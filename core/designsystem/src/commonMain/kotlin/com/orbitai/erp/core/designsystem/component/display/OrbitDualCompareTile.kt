package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Side-by-side comparison KPI: two labelled figures (planned vs actual, submitted vs paid, etc.).
 *
 * Domain-agnostic — callers supply labels and values. Optional deltas and thin progress bars sit
 * under each column without nesting another card.
 */
@Immutable
data class OrbitCompareSide(
    val label: String,
    val value: String,
    val delta: Float? = null,
    val deltaHigherIsBetter: Boolean = true,
    val deltaDescription: String = "",
    val barProgress: Float? = null,
    val barColor: Color? = null,
)

@Composable
fun OrbitDualCompareTile(
    title: String,
    left: OrbitCompareSide,
    right: OrbitCompareSide,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val spoken = contentDescription ?: buildString {
        append(title)
        append(", ")
        append(left.label)
        append(" ")
        append(left.value)
        append(", ")
        append(right.label)
        append(" ")
        append(right.value)
        if (supporting != null) {
            append(", ")
            append(supporting)
        }
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        onClick = onClick,
        contentDescription = spoken,
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Text(
                text = title.uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.Top,
            ) {
                CompareColumn(
                    side = left,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .width(OrbitTheme.sizing.hairline)
                        .height(56.dp)
                        .align(Alignment.CenterVertically)
                        .background(OrbitTheme.controlColors.controlBorder),
                )
                CompareColumn(
                    side = right,
                    modifier = Modifier.weight(1f),
                )
            }
            if (supporting != null) {
                Spacer(Modifier.height(spacing.sm))
                Text(
                    text = supporting,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun CompareColumn(
    side: OrbitCompareSide,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    Column(modifier = modifier) {
        Text(
            text = side.label,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(spacing.xxs))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Text(
                text = side.value,
                style = OrbitTheme.extendedTypography.metricMedium,
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            if (side.delta != null) {
                OrbitDelta(
                    value = side.delta,
                    higherIsBetter = side.deltaHigherIsBetter,
                    contentDescription = side.deltaDescription,
                )
            }
        }
        if (side.barProgress != null) {
            Spacer(Modifier.height(spacing.sm))
            val fraction = side.barProgress.coerceIn(0f, 1f)
            val fill = side.barColor ?: OrbitTheme.contentColors.iconAccent
            val trackShape = RoundedCornerShape(percent = 50)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(trackShape)
                    .background(control.controlContainer),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(6.dp)
                        .clip(trackShape)
                        .background(fill),
                )
            }
        }
    }
}
