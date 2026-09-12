package com.orbitai.erp.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

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

@Composable
actual fun rememberCameraPicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val pendingUri = remember { mutableStateOf<Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val uri = pendingUri.value
            pendingUri.value = null
            if (success && uri != null) {
                onPicked(uri.toPickedFile(context, fallbackName = "issueimage.jpg"))
            } else {
                onPicked(null)
            }
        },
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                launchCameraCapture(context, pendingUri, takePicture)
            } else {
                onPicked(null)
            }
        },
    )
    return {
        if (context.hasPermission(Manifest.permission.CAMERA)) {
            launchCameraCapture(context, pendingUri, takePicture)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

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

private fun launchCameraCapture(
    context: Context,
    pendingUri: MutableState<Uri?>,
    launcher: ActivityResultLauncher<Uri>,
) {
    val dir = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File.createTempFile("capture_", ".jpg", dir)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
    pendingUri.value = uri
    launcher.launch(uri)
}

private fun Uri.toPickedFile(
    context: Context,
    fallbackName: String = "file",
): PickedFile {
    val resolver = context.contentResolver
    var name = fallbackName
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
