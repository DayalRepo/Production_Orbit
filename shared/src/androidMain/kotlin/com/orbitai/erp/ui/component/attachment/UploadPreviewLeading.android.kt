package com.orbitai.erp.ui.component.attachment

import androidx.compose.runtime.Composable
import coil3.compose.rememberAsyncImagePainter
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading

@Composable
actual fun imageUploadLeading(previewUri: String?, fileName: String): OrbitAttachmentLeading? {
    if (previewUri.isNullOrBlank()) return null
    return OrbitAttachmentLeading.Preview(
        painter = rememberAsyncImagePainter(model = previewUri),
    )
}
