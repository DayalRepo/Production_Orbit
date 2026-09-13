package com.orbitai.erp.core.designsystem.component.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.math.roundToInt

/** One page in [OrbitImageCarousel]. */
@Immutable
data class OrbitImageCarouselPage(
    val id: String,
    val title: String,
)

fun orbitImageCarouselPageLabel(index: Int, count: Int): String {
    if (count <= 0) return "0/0"
    val page = index.coerceIn(0, count - 1) + 1
    return "$page/$count"
}

fun orbitImageZoomBy(
    scale: Float,
    delta: Float,
    min: Float = OrbitImageCarouselDefaults.MinScale,
    max: Float = OrbitImageCarouselDefaults.MaxScale,
): Float = (scale + delta).coerceIn(min, max)

fun orbitIsImageFileName(fileName: String): Boolean {
    val ext = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
    return ext in OrbitImageCarouselDefaults.ImageExtensions
}

object OrbitImageCarouselDefaults {
    const val MinScale = 1f
    const val MaxScale = 4f
    const val ZoomStep = 0.5f
    val ImageExtensions = setOf("jpg", "jpeg", "png", "heic", "webp", "gif")
}

/**
 * Full-screen gallery: swipe between photos, pinch or +/− to zoom, close from the X or Close.
 */
@Composable
fun OrbitImageCarousel(
    pages: List<OrbitImageCarouselPage>,
    painterFor: @Composable (OrbitImageCarouselPage) -> Painter?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
) {
    if (pages.isEmpty()) return
    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, pages.lastIndex),
        pageCount = { pages.size },
    )
    val current = pages[pagerState.currentPage]
    var scale by remember(pagerState.currentPage) {
        mutableStateOf(OrbitImageCarouselDefaults.MinScale)
    }
    var offset by remember(pagerState.currentPage) { mutableStateOf(Offset.Zero) }
    val canZoomOut = scale > OrbitImageCarouselDefaults.MinScale + 0.01f
    val canZoomIn = scale < OrbitImageCarouselDefaults.MaxScale - 0.01f
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(OrbitTheme.colorScheme.background)
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = current.title,
                    style = OrbitTheme.typography.titleMedium,
                    fontWeight = OrbitTheme.fontWeights.title,
                    color = content.textPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                OrbitIconButton(
                    contentDescription = "Close",
                    onClick = onDismiss,
                    icon = OrbitIcons.Cancel,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Small,
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                userScrollEnabled = pages.size > 1 && !canZoomOut,
            ) { page ->
                val item = pages[page]
                OrbitZoomableImage(
                    painter = painterFor(item),
                    contentDescription = item.title,
                    scale = if (page == pagerState.currentPage) {
                        scale
                    } else {
                        OrbitImageCarouselDefaults.MinScale
                    },
                    offset = if (page == pagerState.currentPage) offset else Offset.Zero,
                    onTransform = { nextScale, nextOffset ->
                        scale = nextScale
                        offset = if (nextScale <= OrbitImageCarouselDefaults.MinScale + 0.01f) {
                            Offset.Zero
                        } else {
                            nextOffset
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrbitIconButton(
                    contentDescription = "Zoom out",
                    onClick = {
                        scale = orbitImageZoomBy(scale, -OrbitImageCarouselDefaults.ZoomStep)
                        if (scale <= OrbitImageCarouselDefaults.MinScale + 0.01f) {
                            offset = Offset.Zero
                        }
                    },
                    icon = OrbitIcons.MinusSign,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Small,
                    state = if (canZoomOut) OrbitButtonState.Active else OrbitButtonState.Disabled,
                )
                Text(
                    text = "${(scale * 100f).roundToInt()}%",
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = content.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(52.dp),
                )
                OrbitIconButton(
                    contentDescription = "Zoom in",
                    onClick = {
                        scale = orbitImageZoomBy(scale, OrbitImageCarouselDefaults.ZoomStep)
                    },
                    icon = OrbitIcons.PlusSign,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Small,
                    state = if (canZoomIn) OrbitButtonState.Active else OrbitButtonState.Disabled,
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                if (pages.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        pages.indices.forEach { index ->
                            CarouselDot(selected = index == pagerState.currentPage)
                        }
                    }
                }
                Text(
                    text = orbitImageCarouselPageLabel(pagerState.currentPage, pages.size),
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = content.textSecondary,
                )
            }

            OrbitButton(
                label = "Close",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                variant = OrbitButtonVariant.Secondary,
                size = OrbitButtonSize.Medium,
            )
        }
    }
}

@Composable
fun OrbitZoomableImage(
    painter: Painter?,
    contentDescription: String?,
    scale: Float,
    offset: Offset,
    onTransform: (Float, Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaleRef = remember { mutableStateOf(scale) }
    val offsetRef = remember { mutableStateOf(offset) }
    scaleRef.value = scale
    offsetRef.value = offset
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val pointers = event.changes.count { it.pressed }
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()
                        val zoomed = scaleRef.value > OrbitImageCarouselDefaults.MinScale + 0.01f
                        if (pointers >= 2 || zoomed) {
                            val next = (scaleRef.value * zoom).coerceIn(
                                OrbitImageCarouselDefaults.MinScale,
                                OrbitImageCarouselDefaults.MaxScale,
                            )
                            val nextOffset = if (next > OrbitImageCarouselDefaults.MinScale) {
                                offsetRef.value + pan
                            } else {
                                Offset.Zero
                            }
                            onTransform(next, nextOffset)
                            event.changes.forEach { change ->
                                if (change.positionChanged()) change.consume()
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
            )
        } else {
            Text(
                text = contentDescription.orEmpty().ifBlank { "Photo" },
                style = OrbitTheme.typography.bodyMedium,
                color = OrbitTheme.contentColors.textSecondary,
            )
        }
    }
}

@Composable
private fun CarouselDot(selected: Boolean) {
    val content = OrbitTheme.contentColors
    Box(
        modifier = Modifier
            .size(if (selected) 8.dp else 6.dp)
            .clip(CircleShape)
            .background(if (selected) content.iconPrimary else content.iconInactive),
    )
}
