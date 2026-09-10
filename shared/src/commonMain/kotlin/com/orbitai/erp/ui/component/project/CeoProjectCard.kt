package com.orbitai.erp.ui.component.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Project list card — 16:9 banner, name / address, then type-specific metrics (2 per row).
 *
 * Villa: Progress, Units, Floors, Acres + Issues footer.
 * Apartment / community / building: Progress, Units, Towers, Floors, Acres + Issues footer.
 *
 * Metric figures use DM Sans via [OrbitTheme.extendedTypography.metricMedium].
 */
@Composable
fun CeoProjectCard(
    model: CeoProjectCardModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val semantic = OrbitTheme.semanticColors
    val issuesColor = if (model.issuesHealthy) {
        semantic.healthOnTrack.content
    } else {
        semantic.healthDelayed.content
    }

    val metrics = buildList {
        add(ProjectMetric("Progress", "${model.progressPercent}%"))
        add(ProjectMetric("Units", model.units.toString()))
        when (model.type) {
            CeoProjectType.Villa -> {
                add(ProjectMetric("Floors", model.floors.toString()))
                add(ProjectMetric("Acres", model.acresLabel))
            }
            CeoProjectType.Apartment -> {
                add(ProjectMetric("Towers", (model.towers ?: 0).toString()))
                add(ProjectMetric("Floors", model.floors.toString()))
                add(ProjectMetric("Acres", model.acresLabel))
            }
        }
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.none,
        container = OrbitTheme.controlColors.cardContainer,
        onClick = onClick,
        contentDescription = "${model.name}, ${model.addressLine}, ${model.progressPercent} percent",
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(OrbitTheme.controlColors.insetContainer),
            ) {
                AsyncImage(
                    model = model.bannerUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
            ) {
                Text(
                    text = model.name,
                    style = OrbitTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = content.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(spacing.xxs))

                Text(
                    text = model.addressLine,
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(spacing.md))

                OrbitDivider(color = OrbitTheme.controlColors.controlBorder)

                Spacer(Modifier.height(spacing.md))

                MetricGrid(metrics = metrics)

                Spacer(Modifier.height(spacing.md))

                Text(
                    text = "Issues",
                    style = OrbitTheme.typography.labelMedium,
                    color = content.textSecondary,
                    maxLines = 1,
                )
                Spacer(Modifier.height(spacing.xxs))
                Text(
                    text = model.issuesLabel,
                    style = OrbitTheme.extendedTypography.metricSmall,
                    color = issuesColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private data class ProjectMetric(
    val label: String,
    val value: String,
)

@Composable
private fun MetricGrid(metrics: List<ProjectMetric>) {
    val spacing = OrbitTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        metrics.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                row.forEach { metric ->
                    MetricCell(
                        label = metric.label,
                        value = metric.value,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    Column(modifier = modifier) {
        Text(
            text = label,
            style = OrbitTheme.typography.labelMedium,
            color = content.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(OrbitTheme.spacing.xxs))
        Text(
            text = value,
            style = OrbitTheme.extendedTypography.metricMedium,
            color = content.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
