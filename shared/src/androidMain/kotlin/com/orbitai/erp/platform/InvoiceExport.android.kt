package com.orbitai.erp.platform

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberInvoicePdfExporter(): (fileName: String, body: String) -> Boolean {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        { fileName, body ->
            try {
                val dir = File(context.cacheDir, "invoices").also { it.mkdirs() }
                val safeName = fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
                    .removeSuffix(".pdf")
                val outFile = File(dir, "$safeName.pdf")
                writeInvoicePdf(outFile, body)

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    outFile,
                )
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, fileName)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(share, "Export invoice PDF").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}

private fun writeInvoicePdf(outFile: File, body: String) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    var page = document.startPage(pageInfo)
    var canvas = page.canvas
    // Light print plate: white page, dark ink.
    canvas.drawColor(0xFFFFFFFF.toInt())
    val paint = Paint().apply {
        color = 0xFF1C1C1E.toInt()
        textSize = 11f
        isAntiAlias = true
    }
    val margin = 40f
    var y = margin + 12f
    val lineHeight = 16f
    val maxWidth = pageInfo.pageWidth - margin * 2

    body.lineSequence().forEach { raw ->
        wrapLine(raw, paint, maxWidth).forEach { line ->
            if (y > pageInfo.pageHeight - margin) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                canvas.drawColor(0xFFFFFFFF.toInt())
                y = margin + 12f
            }
            canvas.drawText(line, margin, y, paint)
            y += lineHeight
        }
    }
    document.finishPage(page)
    FileOutputStream(outFile).use { document.writeTo(it) }
    document.close()
}

private fun wrapLine(text: String, paint: Paint, maxWidth: Float): List<String> {
    if (text.isEmpty()) return listOf("")
    val words = text.split(' ')
    val lines = mutableListOf<String>()
    var current = StringBuilder()
    words.forEach { word ->
        val candidate = if (current.isEmpty()) word else "$current $word"
        if (paint.measureText(candidate) <= maxWidth) {
            current = StringBuilder(candidate)
        } else {
            if (current.isNotEmpty()) lines += current.toString()
            current = StringBuilder(word)
        }
    }
    if (current.isNotEmpty()) lines += current.toString()
    return lines.ifEmpty { listOf("") }
}
