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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitMixSegment
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedMixBar
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * HEALTH KPI: score %, delta, and mix bars (green / amber / red) with section bracket on tap.
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
    val semantic = OrbitTheme.semanticColors
    val fraction = progress.coerceIn(0f, 1f)
    val percent = (fraction * 100f).roundToInt()

    val segments = listOf(
        OrbitMixSegment(
            weight = healthyProjects.toFloat(),
            color = semantic.healthOnTrack.content,
            label = "Healthy",
            statusPhrase = "healthy",
        ),
        OrbitMixSegment(
            weight = atRiskProjects.toFloat(),
            color = semantic.healthAtRisk.content,
            label = "At risk",
            statusPhrase = "at risk",
        ),
        OrbitMixSegment(
            weight = criticalProjects.toFloat(),
            color = semantic.healthDelayed.content,
            label = "Critical",
            statusPhrase = "critical",
        ),
    ).filter { it.weight > 0f }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
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
        },
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Text(
                text = "HEALTH",
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
            )
            Spacer(Modifier.height(spacing.xxs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = "$percent%",
                    style = OrbitTheme.extendedTypography.metricLarge,
                    color = content.textPrimary,
                    textAlign = TextAlign.Start,
                )
                if (delta != null) {
                    OrbitDelta(
                        value = delta,
                        higherIsBetter = true,
                        contentDescription = "",
                    )
                    Text(
                        text = comparisonLabel,
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(Modifier.height(spacing.md))

            if (segments.isNotEmpty()) {
                OrbitSegmentedMixBar(
                    segments = segments,
                    segmentCount = OrbitProgressDefaults.SegmentCount,
                    showLegend = false,
                )
            }
        }
    }
}
