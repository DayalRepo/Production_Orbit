package com.orbitai.erp.core.designsystem.component.container

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Quiet glass thumb over a scroll viewport — no track, no arrows.
 *
 * Distinct from [OrbitVerticalScrollbar]: that chrome is a control beside menus and strips. This is a
 * hint *on* the surface (description fields, notes) so the user can tell when their own text
 * continues past the fold. Android draws nothing inside `BasicTextField`; iOS only shows a transient
 * indicator on system scroll views.
 *
 * ### Modifier order
 *
 * Prefer putting the thumb on the **viewport** box and `verticalScroll` on the content inside it:
 *
 * ```
 * OrbitGlassScrollBox(scrollState) {
 *     Column(Modifier.verticalScroll(scrollState)) { … }
 * }
 * ```
 *
 * Or chain on one node — glass **before** scroll:
 *
 * ```
 * Modifier.orbitGlassScrollbar(state).verticalScroll(state)
 * ```
 *
 * After `verticalScroll` the draw measures content height and the thumb scrolls away with the text.
 */
@Composable
fun Modifier.orbitGlassScrollbar(
    scrollState: ScrollState,
    visible: Boolean = true,
    color: Color = OrbitTheme.controlColors.controlContent,
    width: Dp = OrbitGlassScrollbarDefaults.Width,
    inset: Dp = OrbitGlassScrollbarDefaults.Inset,
    minThumbLength: Dp = OrbitGlassScrollbarDefaults.MinThumbLength,
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible && scrollState.maxValue > 0) 1f else 0f,
        animationSpec = tween(OrbitGlassScrollbarDefaults.FadeMs),
        label = "orbit-glass-scrollbar",
    )

    return this.drawWithContent {
        drawContent()
        if (alpha <= 0.01f) return@drawWithContent

        val viewport = size.height
        val content = viewport + scrollState.maxValue
        if (content <= viewport) return@drawWithContent

        val trackInset = inset.toPx()
        val trackLength = viewport - trackInset * 2
        val thumbLength = (trackLength * (viewport / content))
            .coerceAtLeast(minThumbLength.toPx())
            .coerceAtMost(trackLength)
        val progress = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        val thumbTop = trackInset + (trackLength - thumbLength) * progress
        val thumbWidth = width.toPx()

        drawRoundRect(
            brush = Brush.verticalGradient(
                startY = thumbTop,
                endY = thumbTop + thumbLength,
                colors = listOf(
                    color.copy(alpha = color.alpha * OrbitGlassScrollbarDefaults.ThumbTopAlpha * alpha),
                    color.copy(
                        alpha = color.alpha * OrbitGlassScrollbarDefaults.ThumbBottomAlpha * alpha,
                    ),
                ),
            ),
            topLeft = Offset(size.width - thumbWidth - trackInset, thumbTop),
            size = Size(thumbWidth, thumbLength),
            cornerRadius = CornerRadius(thumbWidth / 2f),
        )
    }
}

/**
 * Horizontal twin of [orbitGlassScrollbar] — thumb along the bottom edge when content overflows.
 */
@Composable
fun Modifier.orbitGlassHorizontalScrollbar(
    scrollState: ScrollState,
    visible: Boolean = true,
    color: Color = OrbitTheme.controlColors.controlContent,
    height: Dp = OrbitGlassScrollbarDefaults.Width,
    inset: Dp = OrbitGlassScrollbarDefaults.Inset,
    minThumbLength: Dp = OrbitGlassScrollbarDefaults.MinThumbLength,
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible && scrollState.maxValue > 0) 1f else 0f,
        animationSpec = tween(OrbitGlassScrollbarDefaults.FadeMs),
        label = "orbit-glass-h-scrollbar",
    )

    return this.drawWithContent {
        drawContent()
        if (alpha <= 0.01f) return@drawWithContent

        val viewport = size.width
        val content = viewport + scrollState.maxValue
        if (content <= viewport) return@drawWithContent

        val trackInset = inset.toPx()
        val trackLength = viewport - trackInset * 2
        val thumbLength = (trackLength * (viewport / content))
            .coerceAtLeast(minThumbLength.toPx())
            .coerceAtMost(trackLength)
        val progress = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        val thumbLeft = trackInset + (trackLength - thumbLength) * progress
        val thumbHeight = height.toPx()

        drawRoundRect(
            brush = Brush.horizontalGradient(
                startX = thumbLeft,
                endX = thumbLeft + thumbLength,
                colors = listOf(
                    color.copy(alpha = color.alpha * OrbitGlassScrollbarDefaults.ThumbTopAlpha * alpha),
                    color.copy(
                        alpha = color.alpha * OrbitGlassScrollbarDefaults.ThumbBottomAlpha * alpha,
                    ),
                ),
            ),
            topLeft = Offset(thumbLeft, size.height - thumbHeight - trackInset),
            size = Size(thumbLength, thumbHeight),
            cornerRadius = CornerRadius(thumbHeight / 2f),
        )
    }
}

/**
 * Viewport box with an [orbitGlassScrollbar] overlay. Caller scrolls the content inside.
 *
 * ```
 * OrbitGlassScrollBox {
 *     Column(Modifier.verticalScroll(it)) { longContent() }
 * }
 * ```
 *
 * @param scrollState shared with the child that calls `verticalScroll`.
 */
@Composable
fun OrbitGlassScrollBox(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    visible: Boolean = true,
    content: @Composable BoxScope.(ScrollState) -> Unit,
) {
    Box(
        modifier = modifier.orbitGlassScrollbar(
            scrollState = scrollState,
            visible = visible,
        ),
    ) {
        content(scrollState)
    }
}

object OrbitGlassScrollbarDefaults {
    val Width: Dp = 3.dp
    val Inset: Dp = 2.dp
    val MinThumbLength: Dp = 24.dp
    const val FadeMs: Int = 180

    /**
     * Thumb brightest at the top, matching the specular direction of `Modifier.orbitGlass`.
     * Kept low so the hint is noticed once and then ignored.
     */
    const val ThumbTopAlpha: Float = 0.34f
    const val ThumbBottomAlpha: Float = 0.16f
}
