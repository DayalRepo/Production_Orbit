package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitKpiTile
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Invoices at risk — overdue amount + count, with optional AI-flagged callout.
 */
@Composable
fun InvoicesAtRiskKpiTile(
    overdueAmountLabel: String,
    invoiceCount: Int,
    modifier: Modifier = Modifier,
    aiFlaggedCount: Int = 0,
    deltaPercent: Float? = 8.1f,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    OrbitKpiTile(
        title = "Invoices at risk",
        value = overdueAmountLabel,
        supporting = "$invoiceCount overdue",
        icon = OrbitIcons.NotepadDashed,
        iconTone = OrbitBadgeTone.Orange,
        delta = deltaPercent,
        deltaHigherIsBetter = false,
        deltaDescription = "up $deltaPercent percent overdue vs last month",
        comparisonLabel = "vs last month",
        modifier = modifier,
        onClick = onClick,
        footer = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Open invoices",
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                    )
                    Text(
                        text = "$invoiceCount",
                        style = OrbitTheme.typography.labelLarge,
                        color = content.textPrimary,
                    )
                }
                if (aiFlaggedCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "AI flagged",
                            style = OrbitTheme.extendedTypography.metricCaption,
                            color = content.textTertiary,
                        )
                        Text(
                            text = "$aiFlaggedCount",
                            style = OrbitTheme.typography.labelLarge,
                            color = OrbitTheme.semanticColors.danger.content,
                        )
                    }
                }
            }
        },
    )
}
