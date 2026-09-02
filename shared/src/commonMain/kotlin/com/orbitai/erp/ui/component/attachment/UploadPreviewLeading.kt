package com.orbitai.erp.ui.component.attachment

import androidx.compose.runtime.Composable
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading

internal val ImageExtensions = setOf(
    "png", "jpg", "jpeg", "webp", "gif", "bmp", "heic", "heif",
)

internal fun isImageFileName(fileName: String): Boolean =
    fileName.substringAfterLast('.', "").lowercase() in ImageExtensions

/**
 * Returns a preview [OrbitAttachmentLeading] for image uploads when the platform exposes a URI.
 */
@Composable
expect fun imageUploadLeading(previewUri: String?, fileName: String): OrbitAttachmentLeading?
