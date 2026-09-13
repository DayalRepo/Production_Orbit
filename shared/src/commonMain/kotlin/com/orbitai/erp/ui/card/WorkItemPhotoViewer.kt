package com.orbitai.erp.ui.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.designsystem.component.media.OrbitImageCarousel
import com.orbitai.erp.core.designsystem.component.media.OrbitImageCarouselPage
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.avatar_01
import com.orbitai.erp.resources.avatar_02
import com.orbitai.erp.resources.avatar_03
import com.orbitai.erp.resources.avatar_04
import com.orbitai.erp.resources.avatar_05
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun workItemPhotoPainter(photo: WorkItemPhoto): Painter {
    val res = remember(photo.id, photo.fileName) { workItemPhotoPreviewRes(photo) }
    return painterResource(res)
}

internal fun workItemPhotoPreviewRes(photo: WorkItemPhoto) = when (photo.id) {
    "p-v1", "p-i1", "p-l1" -> Res.drawable.avatar_03
    "p-v2", "p-i2", "p-a1" -> Res.drawable.avatar_04
    else -> when (photo.fileName.hashCode().mod(3)) {
        0 -> Res.drawable.avatar_01
        1 -> Res.drawable.avatar_02
        else -> Res.drawable.avatar_05
    }
}

@Composable
internal fun WorkItemPhotoViewer(
    photos: List<WorkItemPhoto>,
    startIndex: Int,
    onDismiss: () -> Unit,
) {
    if (photos.isEmpty()) return
    OrbitImageCarousel(
        pages = photos.map { OrbitImageCarouselPage(id = it.id, title = it.fileName) },
        painterFor = { page ->
            photos.firstOrNull { it.id == page.id }?.let { workItemPhotoPainter(it) }
        },
        onDismiss = onDismiss,
        startIndex = startIndex,
    )
}
