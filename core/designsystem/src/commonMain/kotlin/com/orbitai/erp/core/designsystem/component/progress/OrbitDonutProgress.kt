package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.core.designsystem.theme.colors
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
     * Sixteen is the sweet spot for this diameter and stroke: each bar is still wide enough to read
     * as a bar (not a tick), gaps stay open, and one segment is ~6.25% — close enough to the centre
     * figure that the ring and the number agree at a glance. Twenty (5% each) is fine but denser;
     * twelve looks chunky; twenty-four starts to close the gaps on a phone.
     */
    const val SegmentCount = 16

    /**
     * Gap between neighbouring segments, as a fraction of one segment's slot.
     * Tuned with [SegmentCount] so sixteen bars stay distinct without looking sparse.
     */
    const val GapFraction = 0.16f

    /** First segment starts at 12 o'clock and the ring runs clockwise. */
    const val StartAngle = -90f

    /**
     * Green ring for Health — Done / delta badge solid fills
     * (`#239552` light, `#8BE4B0` dark). Same opposite-direction rule as
     * [blueColors]: darker fill on a light track, pale fill on a dark track.
     */
    val greenColors: OrbitDonutProgressColors
        @Composable @ReadOnlyComposable get() {
            val content = OrbitTheme.contentColors
            val dark = OrbitTheme.isDark
            val green = OrbitBadgeTone.Green.colors
            return OrbitDonutProgressColors(
                filled = green.solidContainer,
                track = if (dark) OrbitPalette.Neutral30 else OrbitPalette.Slate90,
                label = content.textPrimary,
                caption = content.textSecondary,
            )
        }

    /**
     * Blue ring for Progress — light/dark ink from the soft blue container pair
     * (`#004A77` / `#C2E7FF`), matching segmented progress and primary CTAs.
     */
    val blueColors: OrbitDonutProgressColors
        @Composable @ReadOnlyComposable get() {
            val content = OrbitTheme.contentColors
            val dark = OrbitTheme.isDark
            return OrbitDonutProgressColors(
                filled = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50,
                track = if (dark) OrbitPalette.Neutral30 else OrbitPalette.Slate90,
                label = content.textPrimary,
                caption = content.textSecondary,
            )
        }

    /** @deprecated Prefer [greenColors] for Health and [blueColors] for Progress. */
    val monoColors: OrbitDonutProgressColors
        @Composable @ReadOnlyComposable get() = greenColors

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
 * A donut progress ring with a centred percentage (optional caption under it).
 *
 * Draws as a glass object: contact shadow, translucent glass plate (no rim line), a glass lens in
 * the hole, and sheened arcs — the same stack badges and icon rings use.
 *
 * When [segmented] is true (default), the ring is cut into [segmentCount] bars with gaps. Lit count
 * uses the same rounding rules as [OrbitSegmentedProgress] (via [litSegments]). When false, the
 * ring is a continuous track with a single progress arc (Health KPI style).
 *
 * @param progress 0f..1f. Values outside are clamped.
 * @param caption optional name under the percentage (e.g. `"HEALTH"`). Also used in the spoken
 *   description when [contentDescription] is null.
 * @param labelStyle overrides the centre percentage style (default [OrbitTypographyTokens.metricLarge]).
 * @param captionStyle overrides the caption under the percentage (default cardLabel).
 */
@Composable
fun OrbitDonutProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    colors: OrbitDonutProgressColors = OrbitDonutProgressDefaults.greenColors,
    size: Dp = OrbitDonutProgressDefaults.Size,
    strokeWidth: Dp = OrbitDonutProgressDefaults.StrokeWidth,
    segmentCount: Int = OrbitDonutProgressDefaults.SegmentCount,
    segmented: Boolean = true,
    caption: String? = null,
    contentDescription: String? = null,
    labelStyle: TextStyle? = null,
    captionStyle: TextStyle? = null,
) {
    val fraction = progress.coerceIn(0f, 1f)
    val percent = OrbitDonutProgressDefaults.percentLabel(fraction)
    val count = segmentCount.coerceAtLeast(1)
    val lit = litSegments(fraction, count)
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val highlightAlpha = if (dark) {
        OrbitGlass.SurfaceHighlightDark
    } else {
        OrbitGlass.SurfaceHighlightLight
    }
    val sheen = if (dark) 1f else OrbitGlass.Sheen
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
            .orbitGlassShadow(shape = CircleShape, elevation = sizing.shadowButton)
            .clip(CircleShape)
            .orbitGlass(
                fill = control.ringContainer,
                shape = CircleShape,
                // No edge/rim — a hairline circle joins continuous/segment bars into a false ring.
                highlightAlpha = highlightAlpha,
                edge = null,
                sheen = sheen,
            )
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

            if (segmented) {
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
                        cap = StrokeCap.Butt,
                    )
                }
            } else {
                // Continuous ring with square (butt) ends — not rounded pill caps.
                drawGlassArc(
                    color = colors.track,
                    startAngle = OrbitDonutProgressDefaults.StartAngle,
                    sweepAngle = 360f,
                    topLeft = topLeft,
                    arcSize = arcSize,
                    stroke = stroke,
                    sheen = sheen,
                    highlightAlpha = highlightAlpha * 0.35f,
                    cap = StrokeCap.Butt,
                )
                if (fraction > 0f) {
                    drawGlassArc(
                        color = colors.filled,
                        startAngle = OrbitDonutProgressDefaults.StartAngle,
                        sweepAngle = (fraction * 360f).coerceAtLeast(0.5f),
                        topLeft = topLeft,
                        arcSize = arcSize,
                        stroke = stroke,
                        sheen = sheen,
                        highlightAlpha = highlightAlpha,
                        cap = StrokeCap.Butt,
                    )
                }
            }
        }

        // Percent + optional caption; styles scale down when the hole is compact.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percent%",
                style = labelStyle ?: OrbitTheme.extendedTypography.metricLarge.copy(
                    fontWeight = OrbitTheme.fontWeights.body,
                ),
                color = colors.label,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            if (!caption.isNullOrBlank()) {
                Text(
                    text = caption,
                    style = captionStyle ?: OrbitTheme.extendedTypography.cardLabel,
                    color = colors.caption,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * Flat glass sheen on a stroked arc. [StrokeCap.Butt] keeps square ends on continuous and
 * segmented rings (no rounded pill caps).
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
    cap: StrokeCap = StrokeCap.Butt,
) {
    val style = Stroke(width = stroke, cap = cap)
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
