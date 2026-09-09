package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitTrendGraph
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Materials & savings — hero ₹ with side delta, interactive trend, AI win, target pace, CTA.
 */
@Composable
fun MaterialsSavingsCard(
    modifier: Modifier = Modifier,
    savingsAmountLabel: String = CeoDashboardDemoData.MaterialsSavingsLabel,
    supportingLabel: String = CeoDashboardDemoData.MaterialsSavingsSupporting,
    savingsDelta: Float = CeoDashboardDemoData.MaterialsSavingsDelta,
    savingsDeltaLabel: String = CeoDashboardDemoData.MaterialsSavingsDeltaLabel,
    savingsHistory: List<Float> = CeoDashboardDemoData.SavingsTrendHistory,
    savingsForecast: List<Float> = CeoDashboardDemoData.SavingsTrendForecast,
    materialsHistory: List<Float> = CeoDashboardDemoData.MaterialsTrendHistory,
    materialsForecast: List<Float> = CeoDashboardDemoData.MaterialsTrendForecast,
    xLabels: List<String> = CeoDashboardDemoData.MaterialsTrendXLabels,
    aiWinLabel: String = CeoDashboardDemoData.MaterialsAiWin,
    monthProgress: Float = CeoDashboardDemoData.MaterialsMonthProgress,
    ctaLabel: String = CeoDashboardDemoData.MaterialsCtaLabel,
    onViewMaterials: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val dark = OrbitTheme.isDark
    val semantic = OrbitTheme.semanticColors
    val pacePercent = (monthProgress.coerceIn(0f, 1f) * 100f).roundToInt()

    val savingsColor = if (dark) {
        semantic.healthAtRisk.content
    } else {
        semantic.warning.content
    }
    val materialsColor = if (dark) {
        semantic.info.content.copy(alpha = 0.92f)
    } else {
        semantic.neutral.content
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        container = OrbitTheme.controlColors.cardContainer,
        contentDescription = "Materials and savings, $savingsAmountLabel",
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Text(
                text = "Materials & savings",
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
                    text = savingsAmountLabel,
                    style = OrbitTheme.extendedTypography.metricLarge.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                    color = content.textPrimary,
                    maxLines = 1,
                )
                OrbitDelta(
                    value = savingsDelta,
                    higherIsBetter = true,
                    contentDescription = "",
                )
                Text(
                    text = savingsDeltaLabel,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }

            Spacer(Modifier.height(spacing.xxs))

            Text(
                text = supportingLabel,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier.height(spacing.sm))

            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrendLegendDot(color = savingsColor, label = "Savings")
                TrendLegendDot(color = materialsColor, label = "Materials")
            }

            Spacer(Modifier.height(spacing.sm))

            OrbitTrendGraph(
                values = savingsHistory,
                forecast = savingsForecast,
                secondaryValues = materialsHistory,
                secondaryForecast = materialsForecast,
                secondaryColor = materialsColor,
                lineColor = savingsColor,
                fillColor = savingsColor.copy(alpha = if (dark) 0.26f else 0.16f),
                primaryLabel = "Savings",
                secondaryLabel = "Materials",
                xLabels = xLabels,
                height = 156.dp,
                strokeWidth = 1.75.dp,
                showNowMarker = true,
                interactive = true,
                formatValue = ::formatTrendRupeeLakh,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(spacing.sm))

            Text(
                text = aiWinLabel,
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier.height(spacing.md))

            OrbitSegmentedProgress(
                progress = monthProgress,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = "$pacePercent percent of month target",
            )

            Spacer(modifier.height(spacing.xs))

            Text(
                text = "$pacePercent% of month target",
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textTertiary,
                maxLines = 1,
            )

            if (onViewMaterials != null) {
                Spacer(Modifier.height(spacing.md))
                OrbitButton(
                    label = ctaLabel,
                    onClick = onViewMaterials,
                    variant = OrbitButtonVariant.Primary,
                    size = OrbitButtonSize.Medium,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TrendLegendDot(
    color: Color,
    label: String,
) {
    val content = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textSecondary,
            maxLines = 1,
        )
    }
}

private fun formatTrendRupeeLakh(value: Float): String {
    val tenths = (value * 10f).roundToInt() / 10f
    val whole = tenths.toInt()
    val body = if (abs(tenths - whole) < 0.05f) whole.toString() else tenths.toString()
    return "₹${body}L"
}
