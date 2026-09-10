package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
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
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgressDefaults
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * HEALTH KPI: continuous green donut beside status count rows + delta.
 */
@Composable
fun HealthCard(
    progress: Float,
    healthyProjects: Int,
    atRiskProjects: Int,
    criticalProjects: Int,
    modifier: Modifier = Modifier,
    delta: Float? = null,
    comparisonLabel: String = "vs last week",
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val fraction = progress.coerceIn(0f, 1f)
    val percent = (fraction * 100f).roundToInt()
    val totalProjects = healthyProjects + atRiskProjects + criticalProjects
    val donutColors = OrbitDonutProgressDefaults.greenColors

    data class StatusRow(
        val label: String,
        val count: Int,
    )

    val statusRows = listOf(
        StatusRow("Total Projects", totalProjects),
        StatusRow("Healthy", healthyProjects),
        StatusRow("At risk", atRiskProjects),
        StatusRow("Critical", criticalProjects),
    )

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        container = OrbitTheme.controlColors.cardContainer,
        contentDescription = buildString {
            append("Health, ")
            append(percent)
            append(" percent")
            if (delta != null) {
                append(", ")
                append(if (delta >= 0f) "up " else "down ")
                append(abs(delta).let {
                    val rounded = (it * 10f).toInt() / 10f
                    val whole = rounded.toInt()
                    if (rounded == whole.toFloat()) "$whole" else "$rounded"
                })
                append(" percent ")
                append(comparisonLabel)
            }
            append(", ")
            append(totalProjects)
            append(" projects, ")
            append(healthyProjects)
            append(" healthy, ")
            append(atRiskProjects)
            append(" at risk, ")
            append(criticalProjects)
            append(" critical")
        },
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                OrbitDonutProgress(
                    progress = fraction,
                    colors = donutColors,
                    size = 124.dp,
                    strokeWidth = 18.dp,
                    segmented = false,
                    caption = "HEALTH",
                    contentDescription = null,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    statusRows.forEach { row ->
                        HealthCountRow(
                            label = row.label,
                            count = row.count,
                            metricColor = donutColors.label,
                        )
                    }
                }
            }

            if (delta != null) {
                Spacer(Modifier.height(spacing.md))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    OrbitDelta(
                        value = delta,
                        higherIsBetter = true,
                        contentDescription = "",
                    )
                    Text(
                        text = comparisonLabel,
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthCountRow(
    label: String,
    count: Int,
    metricColor: androidx.compose.ui.graphics.Color,
) {
    val content = OrbitTheme.contentColors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$label:",
            style = OrbitTheme.typography.titleSmall,
            color = content.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count.toString(),
            style = OrbitTheme.extendedTypography.metricMedium.copy(
                fontWeight = FontWeight.Normal,
            ),
            color = metricColor,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.widthIn(min = 28.dp),
        )
    }
}
