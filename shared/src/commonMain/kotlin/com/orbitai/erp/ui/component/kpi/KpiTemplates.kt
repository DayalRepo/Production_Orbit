package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitKpiTile
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgressColors
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionBar
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionSegment
import com.orbitai.erp.core.designsystem.component.progress.OrbitSparkline
import com.orbitai.erp.core.designsystem.component.progress.OrbitTrendGraph
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/** Donut health / completion KPI on [OrbitCard]. */
@Composable
fun HealthDonutKpi(
    title: String,
    progress: Float,
    modifier: Modifier = Modifier,
    caption: String? = null,
    colors: OrbitDonutProgressColors = OrbitDonutProgressDefaults.greenColors,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val percent = (progress.coerceIn(0f, 1f) * 100f).toInt()
    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        onClick = onClick,
        contentDescription = contentDescription ?: "$title, $percent percent",
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {},
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = title.uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitDonutProgress(
                progress = progress,
                colors = colors,
                caption = caption,
                contentDescription = null,
            )
        }
    }
}

/** Metric tile with optional sparkline footer. */
@Composable
fun MetricSparklineKpi(
    title: String,
    value: String,
    sparkline: List<Float>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTone: OrbitBadgeTone = OrbitBadgeTone.Blue,
    delta: Float? = null,
    deltaHigherIsBetter: Boolean = true,
    comparisonLabel: String? = null,
    supporting: String? = null,
    sparklineColor: Color = OrbitTheme.contentColors.iconAccent,
    glassBoost: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    OrbitKpiTile(
        title = title,
        value = value,
        modifier = modifier,
        icon = icon,
        iconTone = iconTone,
        supporting = supporting,
        delta = delta,
        deltaHigherIsBetter = deltaHigherIsBetter,
        comparisonLabel = comparisonLabel,
        glassBoost = glassBoost,
        onClick = onClick,
        footer = {
            OrbitSparkline(
                values = sparkline,
                color = sparklineColor,
                height = 48.dp,
            )
        },
    )
}

data class KpiTimelineSeries(
    val primary: List<Float>,
    val secondary: List<Float> = emptyList(),
    val forecast: List<Float> = emptyList(),
    val secondaryForecast: List<Float> = emptyList(),
    val xLabels: List<String> = emptyList(),
)

/** Hero metric + dual-series trend with axes/grid and a compact timeline dropdown in the header. */
@Composable
fun TrendKpi(
    title: String,
    value: String,
    primaryValues: List<Float>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTone: OrbitBadgeTone = OrbitBadgeTone.Teal,
    iconInChip: Boolean = false,
    delta: Float? = null,
    comparisonLabel: String? = "vs last month",
    supporting: String? = null,
    primaryLabel: String = "Savings",
    secondaryLabel: String = "Materials",
    secondaryValues: List<Float> = emptyList(),
    forecast: List<Float> = emptyList(),
    secondaryForecast: List<Float> = emptyList(),
    xLabels: List<String> = emptyList(),
    chartHeight: Dp = 176.dp,
    timelineOptions: List<String> = emptyList(),
    seriesForTimeline: ((String) -> KpiTimelineSeries)? = null,
    primaryLineColor: Color? = null,
    secondaryLineColor: Color? = null,
    glassBoost: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val semantic = OrbitTheme.semanticColors
    val defaultPrimary = primaryLineColor ?: semantic.chartSeries.getOrElse(2) {
        OrbitTheme.contentColors.iconAccent
    }
    val defaultSecondary = secondaryLineColor ?: semantic.chartSeries.getOrElse(1) {
        OrbitTheme.contentColors.textTertiary
    }
    var timeline by remember(timelineOptions) {
        mutableStateOf(
            timelineOptions.find { it.equals("This month", ignoreCase = true) }
                ?: timelineOptions.firstOrNull()
                ?: "This month",
        )
    }
    val active = remember(timeline, primaryValues, secondaryValues, forecast, secondaryForecast, xLabels) {
        seriesForTimeline?.invoke(timeline) ?: KpiTimelineSeries(
            primary = primaryValues,
            secondary = secondaryValues,
            forecast = forecast,
            secondaryForecast = secondaryForecast,
            xLabels = xLabels,
        )
    }

    OrbitKpiTile(
        title = title,
        value = value,
        modifier = modifier,
        icon = icon,
        iconTone = iconTone,
        iconInChip = iconInChip,
        supporting = supporting,
        delta = delta,
        comparisonLabel = comparisonLabel,
        glassBoost = glassBoost,
        onClick = onClick,
        headerTrailing = if (timelineOptions.isNotEmpty()) {
            {
                OrbitDropdownField(
                    selected = timeline,
                    options = timelineOptions,
                    onSelect = { timeline = it },
                    label = "Timeline",
                    placeholder = "Month",
                    searchable = false,
                    size = OrbitFieldSize.Small,
                    modifier = Modifier.widthIn(max = 132.dp),
                )
            }
        } else {
            null
        },
        footer = {
            OrbitTrendGraph(
                values = active.primary,
                lineColor = defaultPrimary,
                secondaryValues = active.secondary,
                secondaryColor = defaultSecondary,
                forecast = active.forecast,
                secondaryForecast = active.secondaryForecast,
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                xLabels = active.xLabels,
                height = chartHeight,
                showAxes = true,
                interactive = true,
            )
        },
    )
}

/** Metric + proportion bar (invoice mix, stock split, material usage). */
@Composable
fun ProportionKpi(
    title: String,
    value: String,
    segments: List<OrbitProportionSegment>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTone: OrbitBadgeTone = OrbitBadgeTone.Blue,
    supporting: String? = null,
    delta: Float? = null,
    comparisonLabel: String? = null,
    glassBoost: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    OrbitKpiTile(
        title = title,
        value = value,
        modifier = modifier,
        icon = icon,
        iconTone = iconTone,
        supporting = supporting,
        delta = delta,
        comparisonLabel = comparisonLabel,
        glassBoost = glassBoost,
        onClick = onClick,
        footer = {
            Spacer(Modifier.height(spacing.xxs))
            OrbitProportionBar(
                segments = segments,
                showLegend = true,
                selectable = true,
                alwaysSelected = true,
            )
        },
    )
}
