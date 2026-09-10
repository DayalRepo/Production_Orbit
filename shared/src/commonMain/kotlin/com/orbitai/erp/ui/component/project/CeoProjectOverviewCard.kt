package com.orbitai.erp.ui.component.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgressDefaults
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Project overview summary — name, dates, large segmented donut (thick ring / smaller hole),
 * then expected delay + issues. Layout follows the overview reference; chart stays Orbit segmented.
 */
@Composable
fun CeoProjectOverviewCard(
    model: CeoProjectCardModel,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val semantic = OrbitTheme.semanticColors
    val progress = (model.progressPercent / 100f).coerceIn(0f, 1f)
    val delayColor = if (model.isOnSchedule) {
        semantic.healthOnTrack.content
    } else {
        semantic.healthAtRisk.content
    }
    val issuesColor = if (model.issuesHealthy) {
        semantic.healthOnTrack.content
    } else {
        semantic.healthDelayed.content
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        container = OrbitTheme.controlColors.cardContainer,
        contentDescription = buildString {
            append("Overview, ")
            append(model.name)
            append(", ")
            append(model.progressPercent)
            append(" percent, started ")
            append(model.startLabel)
            append(", target ")
            append(model.targetLabel)
            append(", ")
            append(model.expectedDelayLabel)
            append(", ")
            append(model.issuesLabel)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {},
        ) {
            Text(
                text = "Overview",
                style = OrbitTheme.typography.labelMedium,
                color = content.textSecondary,
                maxLines = 1,
            )

            Spacer(Modifier.height(spacing.xxs))

            Text(
                text = model.name,
                style = OrbitTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = content.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OverviewStack(
                    label = "Started",
                    value = model.startLabel,
                    alignEnd = false,
                    modifier = Modifier.weight(1f),
                )
                OverviewStack(
                    label = "Target",
                    value = model.targetLabel,
                    alignEnd = true,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(spacing.md))
            OrbitDivider(color = OrbitTheme.controlColors.controlBorder)
            Spacer(Modifier.height(spacing.md))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                // Large chart; thicker stroke shrinks the hole without shrinking the ring.
                OrbitDonutProgress(
                    progress = progress,
                    colors = OrbitDonutProgressDefaults.blueColors,
                    size = 168.dp,
                    strokeWidth = 36.dp,
                    segmented = true,
                    caption = "Progress",
                    contentDescription = null,
                    labelStyle = OrbitTheme.extendedTypography.metricMedium,
                    captionStyle = OrbitTheme.extendedTypography.metricCaption,
                )
            }

            Spacer(Modifier.height(spacing.md))
            OrbitDivider(color = OrbitTheme.controlColors.controlBorder)
            Spacer(Modifier.height(spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OverviewStack(
                    label = "Exp. delay",
                    value = model.expectedDelayLabel,
                    valueColor = delayColor,
                    alignEnd = false,
                    modifier = Modifier.weight(1f),
                )
                OverviewStack(
                    label = "Issues",
                    value = model.issuesLabel,
                    valueColor = issuesColor,
                    alignEnd = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun OverviewStack(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = OrbitTheme.contentColors.textPrimary,
    alignEnd: Boolean = false,
) {
    val content = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.labelMedium,
            color = content.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        )
        Spacer(Modifier.height(spacing.xxs))
        Text(
            text = value,
            style = OrbitTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        )
    }
}
