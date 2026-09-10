package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionBar
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionSegment
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * CEO invoices entry — hero total + delta, pending / AI-flagged, aging mix, top risk, CTA.
 */
@Composable
fun InvoicesEntryCard(
    modifier: Modifier = Modifier,
    totalAmountLabel: String = CeoDashboardDemoData.InvoicesTotalLabel,
    totalDelta: Float = CeoDashboardDemoData.InvoicesTotalDelta,
    totalDeltaLabel: String = CeoDashboardDemoData.InvoicesTotalDeltaLabel,
    pendingCount: Int = CeoDashboardDemoData.InvoicesPendingCount,
    aiFlaggedCount: Int = CeoDashboardDemoData.InvoicesAiFlaggedCount,
    topRiskClient: String = CeoDashboardDemoData.InvoicesTopRiskClient,
    topRiskId: String = CeoDashboardDemoData.InvoicesTopRiskId,
    topRiskAmount: String = CeoDashboardDemoData.InvoicesTopRiskAmount,
    topRiskNote: String = CeoDashboardDemoData.InvoicesTopRiskNote,
    aging0to30: Float = CeoDashboardDemoData.InvoicesAging0to30,
    aging31to60: Float = CeoDashboardDemoData.InvoicesAging31to60,
    aging60Plus: Float = CeoDashboardDemoData.InvoicesAging60Plus,
    onViewInvoices: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val semantic = OrbitTheme.semanticColors
    val pendingLine = if (pendingCount == 1) {
        "1 pending invoice"
    } else {
        "$pendingCount pending invoices"
    }
    val flaggedLine = if (aiFlaggedCount == 1) {
        "1 AI-flagged"
    } else {
        "$aiFlaggedCount AI-flagged"
    }
    val riskLine = "$topRiskClient · $topRiskId · $topRiskAmount · $topRiskNote"

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
        contentDescription = "Invoices, $totalAmountLabel, $pendingLine, $flaggedLine, $riskLine",
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Text(
                text = "Invoices",
                style = OrbitTheme.typography.titleSmall,
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = totalAmountLabel,
                    style = OrbitTheme.extendedTypography.metricLarge.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                    color = content.textPrimary,
                    maxLines = 1,
                )
                OrbitDelta(
                    value = totalDelta,
                    higherIsBetter = false,
                    contentDescription = "",
                )
                Text(
                    text = totalDeltaLabel,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }

            Spacer(Modifier.height(spacing.xxs))

            Text(
                text = pendingLine,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(spacing.xxs))

            Text(
                text = flaggedLine,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(spacing.md))

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

            Spacer(Modifier.height(spacing.md))

            Text(
                text = "Highest at risk",
                style = OrbitTheme.typography.labelMedium,
                color = content.textSecondary,
                maxLines = 1,
            )

            Spacer(Modifier.height(spacing.xs))

            Text(
                text = riskLine,
                style = OrbitTheme.typography.bodyMedium,
                color = content.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (onViewInvoices != null) {
                Spacer(Modifier.height(spacing.md))
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
