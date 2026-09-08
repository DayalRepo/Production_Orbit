package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitMixSegment
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedMixBar
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Portfolio Health KPI — heart mark (no chip), title case, score %, delta, multi-colour slats.
 */
@Composable
fun PortfolioHealthKpiTile(
    score: Int,
    healthySites: Int,
    atRiskSites: Int,
    criticalSites: Int,
    modifier: Modifier = Modifier,
    deltaPercent: Float? = 2.4f,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val semantic = OrbitTheme.semanticColors
    val healthyColor = semantic.healthOnTrack.content
    val atRiskColor = semantic.healthAtRisk.content
    val criticalColor = semantic.healthDelayed.content
    val ink = content.textPrimary

    val spoken = buildString {
        append("Portfolio Health, ")
        append(score)
        append(" percent")
        if (deltaPercent != null) {
            append(", ")
            append(if (deltaPercent >= 0f) "up " else "down ")
            append(kotlin.math.abs(deltaPercent))
            append(" percent vs last week")
        }
        append(", ")
        append(healthySites)
        append(" healthy, ")
        append(atRiskSites)
        append(" at risk, ")
        append(criticalSites)
        append(" critical")
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        onClick = onClick,
        contentDescription = spoken,
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                OrbitGlyph(
                    icon = OrbitIcons.Heart,
                    size = sizing.iconMd,
                    tint = ink,
                    minimumStroke = sizing.iconStrokeLight,
                    contentDescription = null,
                )
                Text(
                    text = "Portfolio Health",
                    style = OrbitTheme.typography.titleSmall,
                    color = ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(spacing.sm))

            Text(
                text = "$score%",
                style = OrbitTheme.extendedTypography.metricLarge,
                color = ink,
                maxLines = 1,
            )

            if (deltaPercent != null) {
                Spacer(Modifier.height(spacing.xs))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    OrbitDelta(
                        value = deltaPercent,
                        higherIsBetter = true,
                        contentDescription = "",
                    )
                    Text(
                        text = "vs last week",
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(Modifier.height(spacing.md))

            OrbitSegmentedMixBar(
                segments = listOf(
                    OrbitMixSegment(
                        weight = healthySites.toFloat(),
                        color = healthyColor,
                        label = "Healthy",
                        shortLabel = "HLTH",
                    ),
                    OrbitMixSegment(
                        weight = atRiskSites.toFloat(),
                        color = atRiskColor,
                        label = "At risk",
                        shortLabel = "RSK",
                    ),
                    OrbitMixSegment(
                        weight = criticalSites.toFloat(),
                        color = criticalColor,
                        label = "Critical",
                        shortLabel = "CRTL",
                    ),
                ),
            )
        }
    }
}
