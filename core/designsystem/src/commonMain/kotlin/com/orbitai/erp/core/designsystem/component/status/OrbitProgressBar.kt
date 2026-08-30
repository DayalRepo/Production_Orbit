package com.orbitai.erp.core.designsystem.component.status

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A determinate progress bar for task completion, budget consumption and material usage.
 *
 * Determinate only — indeterminate loading is the skeleton's job, not this component's.
 *
 * @param progress fraction complete. Values outside `0..1` and `NaN` are clamped rather than
 *   throwing, because these figures are usually computed from server data.
 * @param valueLabel right-aligned readout such as `"68%"` or `"₹4.2L / ₹6L"`. Rendered in the
 *   tabular numeric style so a column of bars aligns.
 */
@Composable
fun OrbitProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    valueLabel: String? = null,
    color: Color = OrbitTheme.colorScheme.primary,
    trackColor: Color = OrbitTheme.colorScheme.surfaceVariant,
    barHeight: Dp = OrbitTheme.sizing.progressBarHeight,
    animate: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    val target = normalizeProgress(progress)

    val fraction = if (animate) {
        animateFloatAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = ProgressAnimationMillis),
            label = "OrbitProgressBar",
        ).value
    } else {
        target
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        if (label != null || valueLabel != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.orEmpty(),
                    style = OrbitTheme.typography.labelMedium,
                    color = OrbitTheme.colorScheme.onSurfaceVariant,
                )
                if (valueLabel != null) {
                    Text(
                        text = valueLabel,
                        style = OrbitTheme.extendedTypography.tableNumeric,
                        color = OrbitTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .progressSemantics(target)
                .background(trackColor, CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(color, CircleShape),
            )
        }
    }
}

/**
 * Clamps a server-supplied fraction into `0..1`, mapping `NaN` to zero.
 *
 * Split out from the composable so the clamping is unit-testable without a Compose test host.
 */
internal fun normalizeProgress(value: Float): Float =
    if (value.isNaN()) 0f else value.coerceIn(0f, 1f)

private const val ProgressAnimationMillis = 350
