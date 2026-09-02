package com.orbitai.erp.platform

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFilePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri == null) {
                onPicked(null)
                return@rememberLauncherForActivityResult
            }
            val resolver = context.contentResolver
            var name = "file"
            var size = 0L
            resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
            if (size <= 0L) {
                resolver.openFileDescriptor(uri, "r")?.use { size = it.statSize.coerceAtLeast(0L) }
            }
            val mime = resolver.getType(uri).orEmpty()
            val previewUri = if (mime.startsWith("image/")) uri.toString() else null
            onPicked(
                PickedFile(
                    id = uri.toString(),
                    name = name,
                    sizeBytes = size.coerceAtLeast(0L),
                    previewUri = previewUri,
                ),
            )
        },
    )
    return {
        launcher.launch(arrayOf("*/*"))
    }
}
