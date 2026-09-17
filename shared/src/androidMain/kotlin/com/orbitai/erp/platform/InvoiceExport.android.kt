package com.orbitai.erp.platform

import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.orbitai.erp.ui.card.InvoiceParty
import com.orbitai.erp.ui.card.InvoicePdfAction
import com.orbitai.erp.ui.card.InvoicePdfModel
import com.orbitai.erp.ui.card.amountInWordsInr
import com.orbitai.erp.ui.card.formatInr
import java.io.File
import java.io.FileOutputStream

private const val PageWidth = 595
private const val PageHeight = 842
private const val Margin = 40f
private const val DownloadChannelId = "orbit_invoice_downloads"
private const val DownloadChannelName = "Invoice downloads"

@Composable
actual fun rememberInvoicePdfExporter(): (InvoicePdfModel, InvoicePdfAction) -> Boolean {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        { model, action ->
            try {
                val dir = File(context.cacheDir, "invoices").also { it.mkdirs() }
                val safeName = model.number.replace(Regex("[^A-Za-z0-9._-]"), "_")
                    .removeSuffix(".pdf")
                val outFile = File(dir, "$safeName.pdf")
                InvoicePdfWriter(context).write(outFile, model)
                require(outFile.exists() && outFile.length() > 0L) { "PDF was not written" }

                when (action) {
                    InvoicePdfAction.Download -> {
                        savePdfToDownloads(context, outFile, "$safeName.pdf")
                    }
                    InvoicePdfAction.Share -> {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            outFile,
                        )
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, model.number)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(
                            Intent.createChooser(share, "Share invoice").apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            },
                        )
                    }
                }
                true
            } catch (t: Exception) {
                postToast(
                    context,
                    t.message?.takeIf { it.isNotBlank() } ?: "Could not save invoice PDF",
                )
                false
            }
        }
    }
}

/** Writes the PDF into public Downloads and posts a download-complete notification. */
private fun savePdfToDownloads(context: Context, source: File, displayName: String) {
    require(source.exists() && source.length() > 0L) { "PDF file is empty" }
    val uniqueName = uniqueDownloadName(displayName)
    val length = source.length()

    val savedUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val pending = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.SIZE, length)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, pending)
            ?: error("Could not create Downloads entry")
        try {
            resolver.openOutputStream(itemUri)?.use { out ->
                source.inputStream().use { input ->
                    val copied = input.copyTo(out)
                    require(copied > 0L) { "Wrote zero bytes" }
                }
            } ?: error("Could not write PDF")
            val published = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
                put(MediaStore.MediaColumns.SIZE, length)
            }
            resolver.update(itemUri, published, null, null)
            itemUri
        } catch (t: Throwable) {
            resolver.delete(itemUri, null, null)
            throw t
        }
    } else {
        @Suppress("DEPRECATION")
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloads.exists()) downloads.mkdirs()
        val dest = File(downloads, uniqueName)
        source.copyTo(dest, overwrite = true)
        require(dest.length() > 0L) { "Copied PDF is empty" }
        MediaScannerConnection.scanFile(
            context,
            arrayOf(dest.absolutePath),
            arrayOf("application/pdf"),
            null,
        )
        @Suppress("DEPRECATION")
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        @Suppress("DEPRECATION")
        dm.addCompletedDownload(
            uniqueName,
            "Invoice PDF",
            true,
            "application/pdf",
            dest.absolutePath,
            dest.length(),
            true,
        )
        Uri.fromFile(dest)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        notifyDownloadComplete(context, uniqueName, savedUri, length)
    }
    postToast(context, "Downloaded $uniqueName")
}

private fun notifyDownloadComplete(
    context: Context,
    fileName: String,
    uri: Uri,
    byteCount: Long,
) {
    ensureDownloadChannel(context)
    val open = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val pending = PendingIntent.getActivity(
        context,
        fileName.hashCode(),
        open,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, DownloadChannelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(fileName)
            .setContentText("Download complete · ${byteCount / 1024} KB")
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
    } else {
        @Suppress("DEPRECATION")
        Notification.Builder(context)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(fileName)
            .setContentText("Download complete · ${byteCount / 1024} KB")
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
    }
    manager.notify(fileName.hashCode(), notification)
}

private fun ensureDownloadChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (manager.getNotificationChannel(DownloadChannelId) != null) return
    manager.createNotificationChannel(
        NotificationChannel(
            DownloadChannelId,
            DownloadChannelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Invoice PDF downloads"
        },
    )
}

private fun uniqueDownloadName(displayName: String): String {
    val base = displayName.removeSuffix(".pdf").ifBlank { "invoice" }
    return "${base}_${System.currentTimeMillis()}.pdf"
}

private fun postToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

/** Invoice body only — DM Sans type, no attached-work pages. */
private class InvoicePdfWriter(
    context: Context,
) {
    private val document = PdfDocument()
    private val paints = PdfPaints(context)
    private var pageIndex = 0
    private lateinit var page: PdfDocument.Page
    private lateinit var canvas: Canvas
    private var y = Margin + 16f
    private val contentWidth = PageWidth - Margin * 2f

    fun write(outFile: File, model: InvoicePdfModel) {
        newPage()
        drawInvoicePage(model)
        document.finishPage(page)
        FileOutputStream(outFile).use { document.writeTo(it) }
        document.close()
    }

    private fun newPage() {
        if (pageIndex > 0) document.finishPage(page)
        pageIndex += 1
        page = document.startPage(
            PdfDocument.PageInfo.Builder(PageWidth, PageHeight, pageIndex).create(),
        )
        canvas = page.canvas
        canvas.drawColor(0xFFFFFFFF.toInt())
        y = Margin + 16f
    }

    private fun ensure(needed: Float) {
        if (y + needed > PageHeight - Margin) newPage()
    }

    private fun drawInvoicePage(model: InvoicePdfModel) {
        val invoice = model.invoice

        ensure(40f)
        canvas.drawText("INVOICE", Margin, y, paints.title)
        y += 16f
        canvas.drawText(invoice.number, Margin, y, paints.body)
        y += 14f
        drawHairline()
        y += 16f

        ensure(16f)
        canvas.drawText("Issued ${invoice.issued.formatSlashed()}", Margin, y, paints.body)
        val due = "Due ${invoice.due.formatSlashed()}"
        canvas.drawText(due, Margin + contentWidth - paints.body.measureText(due), y, paints.body)
        y += 14f
        drawHairline()
        y += 16f

        val colGap = 24f
        val colWidth = (contentWidth - colGap) / 2f
        val leftX = Margin
        val rightX = Margin + colWidth + colGap
        canvas.drawText("FROM", leftX, y, paints.h2)
        canvas.drawText("BILL TO", rightX, y, paints.h2)
        y += 16f
        y = drawAlignedPartyFields(invoice.from, invoice.billTo, leftX, rightX, colWidth, y)
        y += 12f
        drawHairline()
        y += 16f

        ensure(18f)
        canvas.drawText("LINE ITEMS", Margin, y, paints.h2)
        y += 16f
        invoice.lines.forEachIndexed { index, line ->
            if (index > 0) {
                ensure(10f)
                canvas.drawLine(Margin, y, PageWidth - Margin, y, paints.line)
                y += 14f
            }
            val numbered = "${index + 1}. ${line.description}"
            wrap(numbered, paints.body, contentWidth).forEach { wrapped ->
                ensure(14f)
                canvas.drawText(wrapped, Margin, y, paints.body)
                y += 14f
            }
            ensure(28f)
            val colW = contentWidth / 3f
            canvas.drawText("QTY", Margin, y, paints.caption)
            canvas.drawText("UNIT", Margin + colW, y, paints.caption)
            val amountHeading = "AMOUNT"
            canvas.drawText(
                amountHeading,
                Margin + contentWidth - paints.caption.measureText(amountHeading),
                y,
                paints.caption,
            )
            y += 12f
            canvas.drawText(line.quantity.toLong().toString(), Margin, y, paints.strong)
            canvas.drawText(line.displayUnit, Margin + colW, y, paints.strong)
            val amount = formatInr(line.amount)
            canvas.drawText(
                amount,
                Margin + contentWidth - paints.strong.measureText(amount),
                y,
                paints.strong,
            )
            y += 16f
        }
        y += 4f
        drawHairline()
        y += 14f

        fun totalsRow(label: String, value: String, paint: Paint) {
            ensure(16f)
            canvas.drawText(label, Margin, y, paints.strong)
            val tw = paint.measureText(value)
            canvas.drawText(value, Margin + contentWidth - tw, y, paint)
            y += 16f
        }
        totalsRow("Subtotal", formatInr(invoice.subtotal), paints.mono)
        if (invoice.applyGst) {
            totalsRow("CGST ${invoice.cgstPercent.toLong()}%", formatInr(invoice.cgstAmount), paints.mono)
            totalsRow("SGST ${invoice.sgstPercent.toLong()}%", formatInr(invoice.sgstAmount), paints.mono)
        }
        totalsRow("Total", formatInr(invoice.grandTotal), paints.total)
        y += 4f
        wrap(amountInWordsInr(invoice.grandTotal), paints.words, contentWidth).forEach { line ->
            ensure(14f)
            canvas.drawText(line, Margin, y, paints.words)
            y += 14f
        }
        y += 12f
        drawHairline()
        y += 16f

        ensure(18f)
        canvas.drawText("BANK DETAILS", Margin, y, paints.h2)
        y += 16f
        fun bankRow(label: String, value: String) {
            if (value.isBlank()) return
            ensure(14f)
            canvas.drawText(label, Margin, y, paints.strong)
            val tw = paints.body.measureText(value)
            canvas.drawText(value, Margin + contentWidth - tw, y, paints.body)
            y += 14f
        }
        bankRow("Account holder", invoice.bank.accountHolder)
        bankRow("Bank", invoice.bank.bankName)
        bankRow("A/c number", invoice.bank.accountNumber)
        bankRow("IFSC", invoice.bank.ifsc)
        bankRow("UPI", invoice.bank.upiId)

        if (invoice.notes.isNotBlank()) {
            y += 8f
            drawHairline()
            y += 14f
            ensure(18f)
            canvas.drawText("NOTES", Margin, y, paints.h2)
            y += 16f
            wrap(invoice.notes, paints.body, contentWidth).forEach { line ->
                ensure(14f)
                canvas.drawText(line, Margin, y, paints.body)
                y += 14f
            }
        }
    }

    /** Draw NAME/ADDRESS/GSTIN/PHONE headings on the same baseline for both parties. */
    private fun drawAlignedPartyFields(
        from: InvoiceParty,
        billTo: InvoiceParty,
        leftX: Float,
        rightX: Float,
        colWidth: Float,
        startY: Float,
    ): Float {
        var cursor = startY
        fun field(heading: String, left: String, right: String, drawRuleAfter: Boolean) {
            ensure(14f)
            canvas.drawText(heading.uppercase(), leftX, cursor, paints.caption)
            canvas.drawText(heading.uppercase(), rightX, cursor, paints.caption)
            cursor += 12f
            val leftLines = wrap(left.ifBlank { "—" }, paints.body, colWidth).take(4)
            val rightLines = wrap(right.ifBlank { "—" }, paints.body, colWidth).take(4)
            val rows = maxOf(leftLines.size, rightLines.size)
            ensure(rows * 13f + 10f)
            for (i in 0 until rows) {
                leftLines.getOrNull(i)?.let { canvas.drawText(it, leftX, cursor, paints.body) }
                rightLines.getOrNull(i)?.let { canvas.drawText(it, rightX, cursor, paints.body) }
                cursor += 13f
            }
            cursor += 6f
            if (drawRuleAfter) {
                ensure(8f)
                canvas.drawLine(Margin, cursor, PageWidth - Margin, cursor, paints.line)
                cursor += 10f
            }
        }
        field("Name", from.name, billTo.name, drawRuleAfter = true)
        field("Address", from.address, billTo.address, drawRuleAfter = true)
        field("GSTIN", from.gstin, billTo.gstin, drawRuleAfter = true)
        field("Phone", from.phone, billTo.phone, drawRuleAfter = false)
        return cursor
    }

    private fun drawHairline() {
        canvas.drawLine(Margin, y, PageWidth - Margin, y, paints.line)
    }
}

private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
    if (text.isEmpty()) return listOf("")
    val words = text.split(' ', '\n')
    val lines = mutableListOf<String>()
    var current = StringBuilder()
    words.forEach { word ->
        if (word.isEmpty()) return@forEach
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

private class PdfPaints(context: Context) {
    private val regular = loadDmSans(context, "dmsans_regular.ttf")
    private val medium = loadDmSans(context, "dmsans_medium.ttf") ?: regular
    private val bold = loadDmSans(context, "dmsans_bold.ttf") ?: regular

    val title = Paint().apply {
        color = 0xFF1C1C1E.toInt()
        textSize = 20f
        typeface = bold
        isAntiAlias = true
    }
    val h2 = Paint().apply {
        color = 0xFF1C1C1E.toInt()
        textSize = 11f
        typeface = bold
        isAntiAlias = true
    }
    val body = Paint().apply {
        color = 0xFF48484A.toInt()
        textSize = 10f
        typeface = regular
        isAntiAlias = true
    }
    val strong = Paint().apply {
        color = 0xFF1C1C1E.toInt()
        textSize = 11f
        typeface = medium
        isAntiAlias = true
    }
    val mono = Paint().apply {
        color = 0xFF1C1C1E.toInt()
        textSize = 10f
        typeface = regular
        isAntiAlias = true
    }
    val total = Paint().apply {
        color = 0xFF1C1C1E.toInt()
        textSize = 15f
        typeface = bold
        isAntiAlias = true
    }
    val words = Paint().apply {
        color = 0xFF48484A.toInt()
        textSize = 10f
        typeface = regular
        isAntiAlias = true
    }
    val caption = Paint().apply {
        color = 0xFF636366.toInt()
        textSize = 9f
        typeface = medium
        isAntiAlias = true
    }
    val line = Paint().apply {
        color = 0xFFE5E5EA.toInt()
        strokeWidth = 1f
        isAntiAlias = true
    }
}

private fun loadDmSans(context: Context, fileName: String): Typeface? {
    val paths = listOf(
        "composeResources/com.orbitai.erp.core.designsystem.resources/font/$fileName",
        "composeResources/com.orbitai.erp.resources/font/$fileName",
        "font/$fileName",
    )
    paths.forEach { path ->
        try {
            return Typeface.createFromAsset(context.assets, path)
        } catch (_: Exception) {
            // try next
        }
    }
    return Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
}
