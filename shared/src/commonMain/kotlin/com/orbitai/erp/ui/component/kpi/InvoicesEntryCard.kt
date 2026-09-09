package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionBar
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionSegment
import com.orbitai.erp.core.designsystem.component.progress.OrbitSparkline
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * CEO invoices entry card — aging mix, overdue sparkline, and CTAs.
 */
@Composable
fun InvoicesEntryCard(
    modifier: Modifier = Modifier,
    overdueLabel: String = CeoDashboardDemoData.InvoicesOverdueLabel,
    pendingCount: Int = CeoDashboardDemoData.InvoicesPendingCount,
    aiFlaggedCount: Int = CeoDashboardDemoData.InvoicesAiFlaggedCount,
    topRiskClient: String = CeoDashboardDemoData.InvoicesTopRiskClient,
    topRiskId: String = CeoDashboardDemoData.InvoicesTopRiskId,
    topRiskAmount: String = CeoDashboardDemoData.InvoicesTopRiskAmount,
    topRiskNote: String = CeoDashboardDemoData.InvoicesTopRiskNote,
    aging0to30: Float = CeoDashboardDemoData.InvoicesAging0to30,
    aging31to60: Float = CeoDashboardDemoData.InvoicesAging31to60,
    aging60Plus: Float = CeoDashboardDemoData.InvoicesAging60Plus,
    overdueTrend: List<Float> = CeoDashboardDemoData.InvoicesOverdueTrend,
    onViewInvoices: (() -> Unit)? = null,
    onChaseInvoice: ((invoiceId: String) -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val semantic = OrbitTheme.semanticColors
    val summary = "$overdueLabel overdue · $pendingCount pending"
    val riskLine = "$topRiskClient · $topRiskId · $topRiskAmount · $topRiskNote"
    val chaseLabel = "Chase $topRiskId"
    val flaggedLine = if (aiFlaggedCount == 1) {
        "1 AI-flagged this week"
    } else {
        "$aiFlaggedCount AI-flagged this week"
    }

    val agingSegments = remember(aging0to30, aging31to60, aging60Plus, semantic) {
        listOf(
            OrbitProportionSegment(
                weight = aging0to30,
                color = semantic.healthOnTrack.content,
                label = "0–30 days",
                shortLabel = "0–30",
                valueLabel = "₹${aging0to30}L",
                detailLabel = "0–30 days · ₹${aging0to30}L",
            ),
            OrbitProportionSegment(
                weight = aging31to60,
                color = semantic.healthAtRisk.content,
                label = "31–60 days",
                shortLabel = "31–60",
                valueLabel = "₹${aging31to60}L",
                detailLabel = "31–60 days · ₹${aging31to60}L",
            ),
            OrbitProportionSegment(
                weight = aging60Plus,
                color = semantic.healthDelayed.content,
                label = "60+ days",
                shortLabel = "60+",
                valueLabel = "₹${aging60Plus}L",
                detailLabel = "60+ days · ₹${aging60Plus}L",
            ),
        )
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        container = OrbitTheme.controlColors.cardContainer,
        contentDescription = "Invoices, $summary, $flaggedLine, $riskLine",
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Text(
                text = "Invoices",
                style = OrbitTheme.typography.titleSmall,
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier.height(spacing.sm))

            Text(
                text = summary,
                style = OrbitTheme.typography.bodyMedium,
                color = content.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier.height(spacing.xxs))

            Text(
                text = flaggedLine,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier.height(spacing.md))

            Text(
                text = "Overdue aging",
                style = OrbitTheme.typography.labelMedium,
                color = content.textSecondary,
                maxLines = 1,
            )

            Spacer(Modifier.height(spacing.sm))

            OrbitProportionBar(
                segments = agingSegments,
                height = 10.dp,
                showLegend = true,
                selectable = true,
                alwaysSelected = false,
                contentDescription = agingSegments.joinToString { segment ->
                    "${segment.label} ${segment.valueLabel}"
                },
            )

            Spacer(modifier.height(spacing.md))

            Text(
                text = riskLine,
                style = OrbitTheme.typography.bodySmall,
                color = content.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(spacing.md))

            Text(
                text = "Overdue (8w)",
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textTertiary,
                maxLines = 1,
            )
            Spacer(modifier.height(spacing.xs))
            OrbitSparkline(
                values = overdueTrend,
                color = semantic.healthDelayed.content,
                height = 40.dp,
                strokeWidth = 1.75.dp,
                modifier = Modifier.fillMaxWidth(),
            )

            if (onChaseInvoice != null) {
                Spacer(modifier.height(spacing.sm))
                OrbitButton(
                    label = chaseLabel,
                    onClick = { onChaseInvoice(topRiskId) },
                    variant = OrbitButtonVariant.Text,
                    size = OrbitButtonSize.Small,
                    pressIndication = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (onViewInvoices != null) {
                Spacer(modifier.height(if (onChaseInvoice != null) spacing.xs else spacing.md))
                OrbitButton(
                    label = "View invoices",
                    onClick = onViewInvoices,
                    variant = OrbitButtonVariant.Primary,
                    size = OrbitButtonSize.Medium,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
