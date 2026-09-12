package com.orbitai.erp.core.designsystem.component.container

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Horizontal scrollbar: recessed glass track and a pill thumb. No end arrows.
 */
@Composable
fun OrbitHorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    thickness: Dp = OrbitScrollbarDefaults.Thickness,
    minThumbLength: Dp = OrbitScrollbarDefaults.MinThumbLength,
) {
    val max = scrollState.maxValue
    if (max <= 0) return

    OrbitScrollbarChrome(
        vertical = false,
        thumbFraction = viewportFraction(scrollState.viewportSize, max),
        travelFraction = scrollState.value.toFloat() / max.toFloat(),
        thickness = thickness,
        minThumbLength = minThumbLength,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Vertical scrollbar: recessed glass track and a pill thumb. No end arrows.
 */
@Composable
fun OrbitVerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    thickness: Dp = OrbitScrollbarDefaults.Thickness,
    minThumbLength: Dp = OrbitScrollbarDefaults.MinThumbLength,
) {
    val max = scrollState.maxValue
    if (max <= 0) return

    OrbitScrollbarChrome(
        vertical = true,
        thumbFraction = viewportFraction(scrollState.viewportSize, max),
        travelFraction = scrollState.value.toFloat() / max.toFloat(),
        thickness = thickness,
        minThumbLength = minThumbLength,
        modifier = modifier.fillMaxHeight(),
    )
}

/**
 * The same chrome for a lazy row or column.
 */
@Composable
fun OrbitLazyScrollbar(
    listState: LazyListState,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
    thickness: Dp = OrbitScrollbarDefaults.Thickness,
    minThumbLength: Dp = OrbitScrollbarDefaults.MinThumbLength,
) {
    val info = listState.layoutInfo
    val visible = info.visibleItemsInfo
    if (visible.isEmpty()) return

    val averageItem = visible.sumOf { it.size } / visible.size
    if (averageItem <= 0) return

    val viewport = info.viewportEndOffset - info.viewportStartOffset
    val contentLength = averageItem.toLong() * info.totalItemsCount
    if (contentLength <= viewport) return

    val scrolled = listState.firstVisibleItemIndex.toLong() * averageItem +
        listState.firstVisibleItemScrollOffset
    val travel = (contentLength - viewport).coerceAtLeast(1L)

    OrbitScrollbarChrome(
        vertical = !horizontal,
        thumbFraction = viewport.toFloat() / contentLength.toFloat(),
        travelFraction = (scrolled.toFloat() / travel.toFloat()).coerceIn(0f, 1f),
        thickness = thickness,
        minThumbLength = minThumbLength,
        modifier = modifier.then(
            if (horizontal) Modifier.fillMaxWidth() else Modifier.fillMaxHeight(),
        ),
    )
}

@Composable
private fun OrbitScrollbarChrome(
    vertical: Boolean,
    thumbFraction: Float,
    travelFraction: Float,
    thickness: Dp,
    minThumbLength: Dp,
    modifier: Modifier = Modifier,
) {
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val trackShape = OrbitTheme.shapeTokens.progress
    val thumbShape = OrbitTheme.shapeTokens.progress
    val highlight = if (dark) OrbitGlass.SurfaceHighlightDark else OrbitGlass.SurfaceHighlightLight

    val position by animateFloatAsState(
        targetValue = travelFraction,
        animationSpec = tween(ThumbFollowMs),
        label = "orbit-scrollbar-thumb",
    )

    BoxWithConstraints(
        modifier = modifier
            .then(if (vertical) Modifier.width(thickness) else Modifier.height(thickness))
            .orbitGlassShadow(shape = trackShape, elevation = sizing.shadowBadge)
            .clip(trackShape)
            .orbitGlass(
                fill = control.insetContainer,
                shape = trackShape,
                highlightAlpha = highlight,
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
            )
            .padding(OrbitScrollbarDefaults.TrackInset)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.TopStart,
    ) {
        val trackLength = if (vertical) maxHeight else maxWidth
        val thumbLength = maxOf(trackLength * thumbFraction.coerceIn(0f, 1f), minThumbLength)
        val slack = (trackLength - thumbLength).coerceAtLeast(0.dp)
        val thumbThickness = thickness - OrbitScrollbarDefaults.TrackInset * 2

        Box(
            modifier = Modifier
                .then(
                    if (vertical) {
                        Modifier
                            .offset(y = slack * position)
                            .width(thumbThickness)
                            .height(thumbLength)
                    } else {
                        Modifier
                            .offset(x = slack * position)
                            .height(thumbThickness)
                            .width(thumbLength)
                    },
                )
                .orbitGlassShadow(shape = thumbShape, elevation = sizing.shadowButton)
                .clip(thumbShape)
                .orbitGlass(
                    fill = control.controlContent,
                    shape = thumbShape,
                    highlightAlpha = highlight,
                    edge = control.controlBorder,
                    edgeWidth = sizing.hairline,
                ),
        )
    }
}

/** Whether a plain scroll container has anything hidden, for callers deciding to show a bar at all. */
internal fun viewportFraction(viewportSize: Int, maxValue: Int): Float {
    val content = viewportSize.toLong() + maxValue
    if (content <= 0) return 1f
    return viewportSize.toFloat() / content.toFloat()
}

object OrbitScrollbarDefaults {

    val Thickness: Dp = 8.dp

    val TrackInset: Dp = 1.5.dp

    val MinThumbLength: Dp = 22.dp
}

private const val ThumbFollowMs = 120
