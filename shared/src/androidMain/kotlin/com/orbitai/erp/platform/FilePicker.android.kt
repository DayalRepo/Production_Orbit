package com.orbitai.erp.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberImagePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri == null) {
                onPicked(null)
            } else {
                onPicked(uri.toPickedFile(context))
            }
        },
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                pickLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                onPicked(null)
            }
        },
    )
    return {
        val permission = galleryPermission()
        if (permission == null || context.hasPermission(permission)) {
            pickLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            permissionLauncher.launch(permission)
        }
    }
}

@Composable
actual fun rememberDocumentPicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri == null) {
                onPicked(null)
            } else {
                onPicked(uri.toPickedFile(context))
            }
        },
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                pickLauncher.launch(arrayOf("*/*"))
            } else {
                onPicked(null)
            }
        },
    )
    return {
        val permission = legacyStoragePermission()
        if (permission == null || context.hasPermission(permission)) {
            pickLauncher.launch(arrayOf("*/*"))
        } else {
            permissionLauncher.launch(permission)
        }
    }
}

@Composable
actual fun rememberFilePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit = rememberDocumentPicker(onPicked)

private fun galleryPermission(): String? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_IMAGES
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Manifest.permission.READ_EXTERNAL_STORAGE
    else -> null
}

private fun legacyStoragePermission(): String? =
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        Manifest.permission.READ_EXTERNAL_STORAGE
    } else {
        null
    }

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Uri.toPickedFile(context: Context): PickedFile {
    val resolver = context.contentResolver
    var name = "file"
    var size = 0L
    resolver.query(this, null, null, null, null)?.use { cursor ->
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
        resolver.openFileDescriptor(this, "r")?.use { size = it.statSize.coerceAtLeast(0L) }
    }
    val mime = resolver.getType(this).orEmpty()
    val previewUri = if (mime.startsWith("image/")) toString() else null
    return PickedFile(
        id = toString(),
        name = name,
        sizeBytes = size.coerceAtLeast(0L),
        previewUri = previewUri,
    )
}
