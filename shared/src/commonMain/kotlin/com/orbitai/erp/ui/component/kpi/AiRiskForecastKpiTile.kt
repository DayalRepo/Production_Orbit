package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.display.OrbitKpiTile
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * AI risk forecast — count of near-term risks with category breakdown.
 */
@Composable
fun AiRiskForecastKpiTile(
    riskCount: Int,
    delayRisks: Int,
    costRisks: Int,
    labourRisks: Int,
    modifier: Modifier = Modifier,
    horizonLabel: String = "Next 14 days",
    deltaPercent: Float? = -12f,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    OrbitKpiTile(
        title = "AI risk forecast",
        value = "$riskCount",
        supporting = horizonLabel,
        icon = OrbitIcons.BadgeAlert,
        iconTone = OrbitBadgeTone.Amber,
        delta = deltaPercent,
        deltaHigherIsBetter = false,
        deltaDescription = "risk count changed $deltaPercent percent",
        comparisonLabel = "vs prior window",
        modifier = modifier,
        onClick = onClick,
        footer = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                RiskRow(label = "Delay", value = delayRisks)
                RiskRow(label = "Cost", value = costRisks)
                RiskRow(label = "Labour", value = labourRisks)
            }
        },
    )
}

@Composable
private fun RiskRow(label: String, value: Int) {
    val content = OrbitTheme.contentColors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "$value",
            style = OrbitTheme.typography.labelLarge,
            color = content.textPrimary,
        )
    }
}
