package com.orbitai.erp.core.designsystem.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.theme.ColorPair
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A stepped bar indicator for defect severity and task priority.
 *
 * Encodes magnitude twice — by how many segments are filled and by hue — so severity survives
 * greyscale printing of a QA report and colour-deficient vision. That redundancy is the reason this
 * exists instead of just using a coloured [OrbitStatusBadge].
 *
 * @param level how many segments are filled, coerced into `0..segments`.
 * @param segments total segments drawn. Defaults to four, matching `Severity` and `Priority`.
 */
@Composable
fun OrbitSeverityIndicator(
    level: Int,
    label: String,
    colors: ColorPair,
    modifier: Modifier = Modifier,
    segments: Int = DefaultSegments,
    showLabel: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val filled = level.coerceIn(0, segments)

    Row(
        modifier = if (showLabel) modifier else modifier.semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.xxs),
            verticalAlignment = Alignment.Bottom,
        ) {
            repeat(segments) { index ->
                // Segments ramp in height so the shape reads as a level even in one colour.
                val heightFraction = MinSegmentFraction +
                    (1f - MinSegmentFraction) * (index + 1) / segments
                Box(
                    modifier = Modifier
                        .width(sizing.severitySegmentWidth)
                        .height(sizing.severitySegmentHeight * heightFraction)
                        .background(
                            color = if (index < filled) colors.content else colors.container,
                            shape = RoundedCornerShape(percent = 30),
                        ),
                )
            }
        }
        if (showLabel) {
            Text(
                text = label,
                style = OrbitTheme.typography.labelMedium,
                color = OrbitTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private const val DefaultSegments = 4

/** Shortest segment as a fraction of full height, so segment one is still visible. */
private const val MinSegmentFraction = 0.45f
