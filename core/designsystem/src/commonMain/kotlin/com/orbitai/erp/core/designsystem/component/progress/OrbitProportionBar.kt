package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One slice of an [OrbitProportionBar] — weight drives relative width.
 */
@Immutable
data class OrbitProportionSegment(
    val weight: Float,
    val color: Color,
    val label: String,
    val valueLabel: String? = null,
)

/**
 * Continuous multi-colour bar with a legend — for portfolio mix (healthy / at risk / critical).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrbitProportionBar(
    segments: List<OrbitProportionSegment>,
    modifier: Modifier = Modifier,
    height: Dp = OrbitTheme.sizing.progressTrackHeight,
    showLegend: Boolean = true,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val total = segments.sumOf { it.weight.toDouble() }.toFloat().coerceAtLeast(0.0001f)
    val radiusPx: Float
    val gapPx: Float
    with(androidx.compose.ui.platform.LocalDensity.current) {
        radiusPx = OrbitTheme.sizing.progressSegmentRadius.toPx()
        gapPx = OrbitTheme.sizing.progressSegmentGap.toPx()
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = spoken },
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
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
                    drawRoundRect(
                        color = segment.color,
                        topLeft = Offset(x, 0f),
                        size = Size(w, size.height),
                        cornerRadius = CornerRadius(radiusPx, radiusPx),
                    )
                    x += w
                    if (index < segments.lastIndex) x += gapPx
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
                        BoxDot(color = segment.color)
                        Text(
                            text = buildString {
                                if (segment.valueLabel != null) {
                                    append(segment.valueLabel)
                                    append(" ")
                                }
                                append(segment.label)
                            },
                            style = OrbitTheme.extendedTypography.metricCaption,
                            color = content.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxDot(color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color),
    )
}
