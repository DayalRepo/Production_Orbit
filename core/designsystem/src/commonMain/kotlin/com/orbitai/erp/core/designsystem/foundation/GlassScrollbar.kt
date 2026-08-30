package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
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

/**
 * Draws a minimal scrollbar over a vertically scrollable composable.
 *
 * This exists because neither platform gives us one here. Android draws no scrollbar inside a
 * `BasicTextField`, and iOS shows a transient system indicator only on its own scroll views — so a
 * multi-line field that has scrolled looks identical to one that has not, and a user has no way to
 * tell that there is more of their own text above the fold. That is the actual problem being solved:
 * not decoration, but the absence of any signal that content is hidden.
 *
 * It is deliberately quiet. The track is never drawn, only the thumb, and only while there is
 * something to scroll — [visible] fades it out entirely when the content fits, so a short note does
 * not get a vestigial bar down its side. The thumb carries the same specular top-to-bottom fade as
 * the rest of the glass so it reads as part of the surface rather than as a control bolted onto it.
 *
 * ### Modifier order matters
 *
 * Apply this *before* `verticalScroll`, not after:
 *
 * ```
 * Modifier.heightIn(max = cap).orbitGlassScrollbar(state, tint).verticalScroll(state)
 * ```
 *
 * Chained after `verticalScroll` the draw lands inside the scroll container, so it is measured
 * against the content rather than the viewport and translated along with it — the thumb comes out
 * nearly full-height and scrolls away with the text, which is the exact opposite of a scrollbar.
 *
 * @param color the thumb colour; pass the theme's control content so it inverts with the theme.
 * @param visible false when the content fits, which fades the thumb out rather than popping it.
 */
@Composable
fun Modifier.orbitGlassScrollbar(
    scrollState: ScrollState,
    color: Color,
    visible: Boolean = true,
    width: Dp = 3.dp,
    inset: Dp = 2.dp,
    minThumbLength: Dp = 24.dp,
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible && scrollState.maxValue > 0) 1f else 0f,
        animationSpec = tween(FadeMs),
        label = "orbit-scrollbar",
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
        // Progress against the scrollable distance rather than the content height, so the thumb
        // lands exactly at the bottom of the track when the field is scrolled to its end.
        val progress = scrollState.value.toFloat() / scrollState.maxValue
        val thumbTop = trackInset + (trackLength - thumbLength) * progress
        val thumbWidth = width.toPx()

        drawRoundRect(
            brush = Brush.verticalGradient(
                startY = thumbTop,
                endY = thumbTop + thumbLength,
                colors = listOf(
                    color.copy(alpha = color.alpha * ThumbTopAlpha * alpha),
                    color.copy(alpha = color.alpha * ThumbBottomAlpha * alpha),
                ),
            ),
            topLeft = Offset(size.width - thumbWidth - trackInset, thumbTop),
            size = Size(thumbWidth, thumbLength),
            cornerRadius = CornerRadius(thumbWidth / 2f),
        )
    }
}

/**
 * The thumb is brightest at its top, matching the specular direction of `Modifier.orbitGlass`.
 *
 * Both values are low because the thumb's job is to be noticed once and then ignored. It is drawn
 * from the control foreground, which is near-black in the light theme and near-white in the dark
 * one, so at anything above roughly a third it stops reading as a hint on the surface and starts
 * competing with the text it is describing.
 */
private const val ThumbTopAlpha = 0.34f
private const val ThumbBottomAlpha = 0.16f

private const val FadeMs = 180
