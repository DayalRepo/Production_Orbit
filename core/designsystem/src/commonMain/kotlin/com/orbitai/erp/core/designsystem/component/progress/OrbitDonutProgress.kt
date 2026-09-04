package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.math.roundToInt

/**
 * Colours for an [OrbitDonutProgress] ring.
 *
 * Taken as a parameter so `:core:designsystem` stays free of product vocabulary — callers pass
 * a light-green health pair, the linear progress blue, or any other fill.
 */
@Immutable
data class OrbitDonutProgressColors(
    /** Lit segment fill. */
    val filled: Color,
    /** Unlit segment track. */
    val track: Color,
    /** Percentage figure in the hole. */
    val label: Color,
    /** Optional name under the percentage (e.g. Health / Progress). */
    val caption: Color,
)

object OrbitDonutProgressDefaults {

    /** Compact enough that two rings sit cleanly in one phone row. */
    val Size: Dp = 140.dp

    /**
     * Ring thickness (segment “height” radially). Proportioned to the smaller diameter so bars stay
     * readable — hole diameter is `size - 2 * strokeWidth`.
     */
    val StrokeWidth: Dp = 26.dp

    /**
     * How many arc segments the ring is cut into.
     *
     * Twenty makes each piece worth exactly **5%** of the ring (`100 / 20`). The figure in the centre
     * is still the true rounded percentage; the segments only approximate that fraction visually —
     * see [litSegments].
     */
    const val SegmentCount = 20

    /**
     * Gap between neighbouring segments, as a fraction of one segment's slot.
     * Modest so twenty bars stay wide enough to read.
     */
    const val GapFraction = 0.14f

    /** First segment starts at 12 o'clock and the ring runs clockwise. */
    const val StartAngle = -90f

    /**
     * Health — deep green on light (readable on white), soft green on dark.
     */
    val greenColors: OrbitDonutProgressColors
        @Composable @ReadOnlyComposable get() = colors(
            filled = if (OrbitTheme.isDark) OrbitPalette.Green80 else OrbitPalette.Green40,
            track = if (OrbitTheme.isDark) OrbitPalette.Neutral30 else OrbitPalette.Slate90,
        )

    /**
     * Progress — same blue + track as [OrbitProgressDefaults] / [OrbitSegmentedProgress] (Blue50 on
     * light, Blue80 on dark).
     */
    val blueColors: OrbitDonutProgressColors
        @Composable @ReadOnlyComposable get() {
            val bar = OrbitProgressDefaults.colors
            return colors(filled = bar.filled, track = bar.track)
        }

    @Composable
    @ReadOnlyComposable
    fun colors(
        filled: Color,
        track: Color = if (OrbitTheme.isDark) OrbitPalette.Neutral30 else OrbitPalette.Slate90,
    ): OrbitDonutProgressColors {
        val content = OrbitTheme.contentColors
        return OrbitDonutProgressColors(
            filled = filled,
            track = track,
            label = content.textPrimary,
            caption = content.textSecondary,
        )
    }

    /** Whole-number percent for the centre figure. Values outside 0..1 are clamped. */
    fun percentLabel(progress: Float): Int =
        (progress.coerceIn(0f, 1f) * 100f).roundToInt()
}

/**
 * A segmented donut progress ring: [OrbitDonutProgressDefaults.SegmentCount] curved bars with gaps,
 * a centred percentage, and an optional name beneath it.
 *
 * Lit count uses the same rounding rules as [OrbitSegmentedProgress] (via [litSegments]): above zero
 * always lights at least one piece; below 100% always leaves at least one dark.
 *
 * Segments are stroked arcs with [StrokeCap.Butt] so they follow the ring and stay clearly separated
 * — no drop shadow, no round caps that merge the gaps.
 *
 * @param progress 0f..1f. Values outside are clamped.
 * @param caption optional line under the percent (e.g. `"Health"`, `"Progress"`).
 */
@Composable
fun OrbitDonutProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    colors: OrbitDonutProgressColors = OrbitDonutProgressDefaults.greenColors,
    size: Dp = OrbitDonutProgressDefaults.Size,
    strokeWidth: Dp = OrbitDonutProgressDefaults.StrokeWidth,
    segmentCount: Int = OrbitDonutProgressDefaults.SegmentCount,
    caption: String? = null,
    contentDescription: String? = null,
) {
    val fraction = progress.coerceIn(0f, 1f)
    val percent = OrbitDonutProgressDefaults.percentLabel(fraction)
    val count = segmentCount.coerceAtLeast(1)
    val lit = litSegments(fraction, count)
    val dark = OrbitTheme.isDark
    val highlightAlpha = if (dark) {
        OrbitGlass.SurfaceHighlightDark
    } else {
        OrbitGlass.SurfaceHighlightLight
    }
    val sheen = if (dark) 1f else OrbitGlass.Sheen
    val spacing = OrbitTheme.spacing
    val spoken = contentDescription
        ?: buildString {
            if (!caption.isNullOrBlank()) {
                append(caption)
                append(", ")
            }
            append(percent)
            append(" percent")
        }

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
                this.contentDescription = spoken
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx().coerceAtMost(this.size.minDimension * 0.4f)
            val inset = stroke / 2f
            val arcSize = Size(
                width = this.size.width - inset * 2f,
                height = this.size.height - inset * 2f,
            )
            val topLeft = Offset(inset, inset)
            val slotSweep = 360f / count
            val gapSweep = slotSweep * OrbitDonutProgressDefaults.GapFraction
            val segSweep = (slotSweep - gapSweep).coerceAtLeast(0.5f)

            repeat(count) { index ->
                val start = OrbitDonutProgressDefaults.StartAngle +
                    index * slotSweep +
                    gapSweep / 2f
                val color = if (index < lit) colors.filled else colors.track
                drawGlassArc(
                    color = color,
                    startAngle = start,
                    sweepAngle = segSweep,
                    topLeft = topLeft,
                    arcSize = arcSize,
                    stroke = stroke,
                    sheen = sheen,
                    highlightAlpha = if (index < lit) highlightAlpha else highlightAlpha * 0.35f,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = "$percent%",
                style = OrbitTheme.extendedTypography.metricMedium,
                color = colors.label,
                textAlign = TextAlign.Center,
            )
            if (!caption.isNullOrBlank()) {
                Text(
                    text = caption,
                    style = OrbitTheme.typography.labelMedium,
                    color = colors.caption,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Flat glass sheen on a stroked arc. [StrokeCap.Butt] keeps radial cuts clean so gaps stay open.
 */
private fun DrawScope.drawGlassArc(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    topLeft: Offset,
    arcSize: Size,
    stroke: Float,
    sheen: Float,
    highlightAlpha: Float,
) {
    val style = Stroke(width = stroke, cap = StrokeCap.Butt)
    val sheened = color.copy(alpha = (color.alpha * sheen).coerceAtMost(1f))

    drawArc(
        brush = Brush.verticalGradient(listOf(sheened, color)),
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = style,
    )
    if (highlightAlpha > 0f) {
        drawArc(
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = highlightAlpha),
                    Color.White.copy(alpha = 0f),
                ),
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style,
        )
    }
}
