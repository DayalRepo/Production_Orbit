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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Weighted colour slice for [OrbitSegmentedMixBar].
 */
@Immutable
data class OrbitMixSegment(
    val weight: Float,
    val color: Color,
    val label: String,
    val shortLabel: String = label,
    val statusPhrase: String = label.lowercase(),
)

private val BracketFadeMs = 280

/**
 * Vertical-slat mix bar coloured by weighted segments (e.g. healthy / at risk / critical).
 *
 * Tap a colour band for a rounded section bracket + count label. Tap again to dismiss.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrbitSegmentedMixBar(
    segments: List<OrbitMixSegment>,
    modifier: Modifier = Modifier,
    height: Dp = OrbitTheme.sizing.progressTrackHeight,
    segmentCount: Int = OrbitProgressDefaults.SegmentCount,
    showLegend: Boolean = false,
    selectable: Boolean = true,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val total = segments.sumOf { it.weight.toDouble() }.toFloat().coerceAtLeast(0.0001f)
    val highlightAlpha = if (dark) {
        OrbitProgressDefaults.SlatHighlightDark
    } else {
        OrbitProgressDefaults.SlatHighlightLight
    }

    val gapPx: Float
    val minWidthPx: Float
    val radiusPx: Float
    with(LocalDensity.current) {
        gapPx = sizing.progressSegmentGap.toPx()
        minWidthPx = (sizing.progressSegmentMinWidth.toPx() * 0.65f).coerceAtLeast(1f)
        radiusPx = sizing.progressSegmentRadius.toPx()
    }

    var selectedIndex by remember(segments) { mutableStateOf<Int?>(null) }

    val spoken = contentDescription ?: segments.joinToString { "${it.label} ${it.weight.toInt()}" }
    val trackShape = RoundedCornerShape(sizing.progressSegmentRadius)
    val bracketStroke = if (dark) {
        content.textTertiary.copy(alpha = 0.85f)
    } else {
        control.controlBorder.copy(alpha = 0.9f)
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
                            .pointerInput(segments, total) {
                                detectTapGestures { offset ->
                                    val width = size.width.toFloat()
                                    if (width <= 0f) return@detectTapGestures
                                    val fraction = (offset.x / width).coerceIn(0f, 0.9999f)
                                    var acc = 0f
                                    var hit = segments.lastIndex
                                    for (i in segments.indices) {
                                        acc += segments[i].weight / total
                                        if (fraction < acc) {
                                            hit = i
                                            break
                                        }
                                    }
                                    selectedIndex = if (selectedIndex == hit) null else hit
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

                val count = affordableSegments(width, minWidthPx, gapPx, segmentCount)
                    .coerceAtLeast(1)
                val slatWidth = (width - gapPx * (count - 1)) / count
                if (slatWidth <= 0f) return@Canvas

                val corner = CornerRadius(radiusPx, radiusPx)
                val weights = segments.map { (it.weight / total * count) }

                repeat(count) { index ->
                    val x = index * (slatWidth + gapPx)
                    val mid = index + 0.5f
                    var color = segments.first().color
                    var segmentIdx = 0
                    var acc = 0f
                    for (i in segments.indices) {
                        val next = acc + weights[i]
                        if (mid <= next || i == segments.lastIndex) {
                            color = segments[i].color
                            segmentIdx = i
                            break
                        }
                        acc = next
                    }

                    val dimUnselected = selectedIndex != null && selectedIndex != segmentIdx
                    val drawColor = if (dimUnselected) {
                        color.copy(alpha = color.alpha * 0.32f)
                    } else {
                        color
                    }

                    val at = Offset(x, 0f)
                    val slat = Size(slatWidth, size.height)
                    drawRoundRect(
                        color = drawColor,
                        topLeft = at,
                        size = slat,
                        cornerRadius = corner,
                    )
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(
                                    alpha = if (dimUnselected) highlightAlpha * 0.35f else highlightAlpha,
                                ),
                                Color.White.copy(alpha = 0f),
                            ),
                            startY = 0f,
                            endY = size.height * 0.45f,
                        ),
                        topLeft = at,
                        size = slat,
                        cornerRadius = corner,
                    )
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
                label = "mix-bracket-content",
            ) { index ->
                if (index == null || index !in segments.indices) {
                    Spacer(Modifier.height(0.dp))
                    return@AnimatedContent
                }
                val segment = segments[index]
                val startFrac = segments.take(index).sumOf { it.weight.toDouble() }.toFloat() / total
                val endFrac = startFrac + (segment.weight / total)
                val centerFrac = (startFrac + endFrac) / 2f
                val count = segment.weight.roundToIntSafe()
                val noun = if (count == 1) "project" else "projects"
                val label = "$count $noun ${segment.statusPhrase}"
                val density = LocalDensity.current
                var labelWidthPx by remember(label) { mutableStateOf(0) }

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val trackPx = with(density) { maxWidth.toPx() }
                    val segmentStart = maxWidth * startFrac
                    val segmentWidth = maxWidth * (endFrac - startFrac).coerceAtLeast(0.0001f)
                    val labelOffset = with(density) {
                        val ideal = trackPx * centerFrac - labelWidthPx / 2f
                        val clamped = ideal.coerceIn(0f, (trackPx - labelWidthPx).coerceAtLeast(0f))
                        clamped.toDp()
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Canvas(
                                modifier = Modifier
                                    .offset(x = segmentStart)
                                    .width(segmentWidth)
                                    .height(14.dp),
                            ) {
                                val strokeWidth = 1.25.dp.toPx()
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
                                    .coerceAtMost((bottom - top))
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
                            text = "${segment.weight.toInt()} ${segment.shortLabel}",
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

@Composable
fun OrbitGlassLegendDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
) {
    val sizing = OrbitTheme.sizing
    val shape = CircleShape
    Box(
        modifier = modifier
            .size(size)
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .clip(shape)
            .orbitGlass(
                fill = color.copy(alpha = if (OrbitTheme.isDark) 0.85f else 0.92f),
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.BadgeHighlightDark
                } else {
                    OrbitGlass.BadgeHighlightLight
                },
                edge = color.copy(alpha = 0.55f),
                edgeWidth = sizing.hairline,
                sheen = OrbitGlass.Sheen,
            ),
    )
}

private fun Float.roundToIntSafe(): Int =
    if (this.isNaN()) 0 else (this + 0.5f).toInt().coerceAtLeast(0)
