package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.math.roundToInt

private val BracketFadeMs = 280

/**
 * One slice of an [OrbitProportionBar] — weight drives relative width.
 */
@Immutable
data class OrbitProportionSegment(
    val weight: Float,
    val color: Color,
    val label: String,
    val valueLabel: String? = null,
    /** Optional full selection line, e.g. `Materials or Stock · ₹5L · 27%`. */
    val detailLabel: String? = null,
    /** Compact legend name, e.g. `Materials` instead of `Materials or Stock`. */
    val shortLabel: String = label,
)

/**
 * Continuous multi-colour “wizard” bar with small gaps between segments.
 *
 * Each segment gets a thin glass highlight. Tap (when [selectable]) dims the others, draws a
 * Health-style U-bracket under the band, and shows [OrbitProportionSegment.detailLabel] centered
 * on that bucket.
 *
 * @param alwaysSelected when true, a bucket is always active (defaults to the heaviest / [initialSelectedIndex]);
 *   taps switch buckets instead of toggling off.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrbitProportionBar(
    segments: List<OrbitProportionSegment>,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    showLegend: Boolean = true,
    selectable: Boolean = false,
    alwaysSelected: Boolean = false,
    initialSelectedIndex: Int? = null,
    gap: Dp = 2.dp,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val dark = OrbitTheme.isDark
    val total = segments.sumOf { it.weight.toDouble() }.toFloat().coerceAtLeast(0.0001f)
    val highlightAlpha = if (dark) {
        OrbitProgressDefaults.SlatHighlightDark
    } else {
        OrbitProgressDefaults.SlatHighlightLight
    }
    val radiusPx: Float
    val gapPx: Float
    val borderPx: Float
    with(LocalDensity.current) {
        // Square ends — no corner radius on invoice expense segments.
        radiusPx = 0f
        gapPx = gap.toPx()
        borderPx = sizing.hairline.toPx().coerceAtLeast(1f)
    }

    val defaultIndex = remember(segments, initialSelectedIndex) {
        when {
            initialSelectedIndex != null && initialSelectedIndex in segments.indices ->
                initialSelectedIndex
            segments.isEmpty() -> 0
            else -> segments.indices.maxByOrNull { segments[it].weight } ?: 0
        }
    }
    var selectedIndex by remember(segments, alwaysSelected, defaultIndex) {
        mutableStateOf<Int?>(
            when {
                alwaysSelected -> defaultIndex
                else -> null
            },
        )
    }

    val spoken = contentDescription ?: segments.joinToString(separator = ", ") { segment ->
        buildString {
            append(segment.label)
            if (segment.valueLabel != null) {
                append(" ")
                append(segment.valueLabel)
            }
        }
    }

    val trackShape = RoundedCornerShape(0.dp)
    val bracketStroke = if (dark) {
        content.textTertiary.copy(alpha = 0.9f)
    } else {
        content.textSecondary.copy(alpha = 0.72f)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = spoken },
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .orbitGlassShadow(shape = trackShape, elevation = sizing.shadowBadge)
                .then(
                    if (selectable && segments.isNotEmpty()) {
                        Modifier
                            .orbitHandCursor()
                            .pointerInput(segments, total, gapPx, alwaysSelected) {
                                detectTapGestures { offset ->
                                    val width = size.width.toFloat()
                                    if (width <= 0f) return@detectTapGestures
                                    val hit = hitSegmentIndex(
                                        x = offset.x,
                                        width = width,
                                        segments = segments,
                                        total = total,
                                        gapPx = gapPx,
                                    )
                                    selectedIndex = when {
                                        alwaysSelected -> hit
                                        selectedIndex == hit -> null
                                        else -> hit
                                    }
                                }
                            }
                    } else {
                        Modifier
                    },
                ),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
            ) {
                val width = size.width
                if (width <= 0f || segments.isEmpty()) return@Canvas
                val gaps = gapPx * (segments.size - 1).coerceAtLeast(0)
                val usable = (width - gaps).coerceAtLeast(0f)
                var x = 0f
                segments.forEachIndexed { index, segment ->
                    val w = usable * (segment.weight / total)
                    if (w > 0f) {
                        val dim = selectedIndex != null && selectedIndex != index
                        val draw = if (dim) {
                            segment.color.copy(alpha = segment.color.alpha * 0.32f)
                        } else {
                            segment.color
                        }
                        val at = Offset(x, 0f)
                        val slat = Size(w, size.height)
                        val corner = CornerRadius(radiusPx, radiusPx)
                        drawRoundRect(
                            color = draw,
                            topLeft = at,
                            size = slat,
                            cornerRadius = corner,
                        )
                        // Thin top-edge glass line — same cue as segmented progress slats.
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(
                                        alpha = if (dim) highlightAlpha * 0.35f else highlightAlpha,
                                    ),
                                    Color.White.copy(alpha = 0f),
                                ),
                                startY = 0f,
                                endY = size.height * 0.55f,
                            ),
                            topLeft = at,
                            size = slat,
                            cornerRadius = corner,
                        )
                        // Rounded corner border so each wizard segment reads as its own pane.
                        drawRoundRect(
                            color = Color.White.copy(
                                alpha = when {
                                    dim && dark -> 0.08f
                                    dim -> 0.16f
                                    dark -> 0.22f
                                    else -> 0.42f
                                },
                            ),
                            topLeft = at,
                            size = slat,
                            cornerRadius = corner,
                            style = Stroke(width = borderPx),
                        )
                        x += w
                        if (index < segments.lastIndex) x += gapPx
                    }
                }
            }
        }

        val selected = selectedIndex
        AnimatedVisibility(
            visible = selectable &&
                selected != null &&
                selected >= 0 &&
                selected < segments.size,
            enter = fadeIn(tween(BracketFadeMs, easing = FastOutSlowInEasing)) +
                expandVertically(tween(BracketFadeMs, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(BracketFadeMs, easing = FastOutSlowInEasing)) +
                shrinkVertically(tween(BracketFadeMs, easing = FastOutSlowInEasing)),
        ) {
            AnimatedContent(
                targetState = selectedIndex,
                transitionSpec = {
                    fadeIn(tween(BracketFadeMs, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(BracketFadeMs, easing = FastOutSlowInEasing))
                },
                label = "proportion-bracket-content",
            ) { index ->
                if (index == null || index !in segments.indices) {
                    Spacer(modifier.height(0.dp))
                    return@AnimatedContent
                }
                val segment = segments[index]
                val pct = ((segment.weight / total) * 100f).roundToInt()
                val label = segment.detailLabel
                    ?: buildString {
                        append(segment.label)
                        if (segment.valueLabel != null) {
                            append(" · ")
                            append(segment.valueLabel)
                        }
                        append(" · ")
                        append(pct)
                        append('%')
                    }
                val density = LocalDensity.current
                var labelWidthPx by remember(label) { mutableStateOf(0) }

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val trackPx = with(density) { maxWidth.toPx() }
                    val gaps = gapPx * (segments.size - 1).coerceAtLeast(0)
                    val usable = (trackPx - gaps).coerceAtLeast(0f)
                    var startPx = 0f
                    for (i in 0 until index) {
                        startPx += usable * (segments[i].weight / total) + gapPx
                    }
                    val segmentWidthPx = usable * (segment.weight / total)
                    val segmentStart = with(density) { startPx.toDp() }
                    val segmentWidth = with(density) { segmentWidthPx.toDp() }
                    val centerPx = startPx + segmentWidthPx / 2f
                    val labelOffset = with(density) {
                        val ideal = centerPx - labelWidthPx / 2f
                        val clamped = ideal.coerceIn(0f, (trackPx - labelWidthPx).coerceAtLeast(0f))
                        clamped.toDp()
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Canvas(
                                modifier = Modifier
                                    .offset(x = segmentStart)
                                    .width(segmentWidth.coerceAtLeast(8.dp))
                                    .height(14.dp),
                            ) {
                                val strokeWidth = if (dark) 1.25.dp.toPx() else 1.6.dp.toPx()
                                val stroke = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                )
                                val inset = strokeWidth / 2f
                                val left = inset
                                val right = size.width - inset
                                val top = inset
                                val bottom = size.height - inset
                                val radius = (6.dp.toPx()).coerceAtMost((right - left) / 2f)
                                    .coerceAtMost(bottom - top)
                                val path = Path().apply {
                                    moveTo(left, top)
                                    lineTo(left, bottom - radius)
                                    quadraticTo(left, bottom, left + radius, bottom)
                                    lineTo(right - radius, bottom)
                                    quadraticTo(right, bottom, right, bottom - radius)
                                    lineTo(right, top)
                                }
                                drawPath(path = path, color = bracketStroke, style = stroke)
                            }
                        }
                        Spacer(Modifier.height(spacing.xxs))
                        Text(
                            text = label,
                            style = OrbitTheme.extendedTypography.metricCaption,
                            color = content.textPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier
                                .offset(x = labelOffset)
                                .wrapContentWidth(unbounded = true)
                                .onSizeChanged { labelWidthPx = it.width },
                        )
                    }
                }
            }
        }

        if (showLegend) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                segments.forEach { segment ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        OrbitGlassLegendDot(color = segment.color)
                        Text(
                            text = segment.shortLabel,
                            style = OrbitTheme.extendedTypography.metricCaption,
                            color = content.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

private fun hitSegmentIndex(
    x: Float,
    width: Float,
    segments: List<OrbitProportionSegment>,
    total: Float,
    gapPx: Float,
): Int {
    val gaps = gapPx * (segments.size - 1).coerceAtLeast(0)
    val usable = (width - gaps).coerceAtLeast(0f)
    var cursor = 0f
    var hit = segments.lastIndex
    for (i in segments.indices) {
        val w = usable * (segments[i].weight / total)
        if (x <= cursor + w || i == segments.lastIndex) {
            hit = i
            break
        }
        cursor += w + gapPx
    }
    return hit
}
