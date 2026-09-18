package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.display.OrbitAiInsightCard
import com.orbitai.erp.core.designsystem.component.display.OrbitAlertStrip
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressDefaults
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.kpi.AiDailyFocusList
import com.orbitai.erp.ui.component.kpi.CeoKpiColumn
import com.orbitai.erp.ui.component.kpi.ContractorKpiColumn
import com.orbitai.erp.ui.component.kpi.KpiSamples
import com.orbitai.erp.ui.component.kpi.PmKpiColumn
import com.orbitai.erp.ui.component.kpi.ProcurementKpiColumn
import com.orbitai.erp.ui.component.kpi.QaQcKpiColumn
import com.orbitai.erp.ui.component.kpi.SiteEngineerKpiColumn
import com.orbitai.erp.ui.component.kpi.TrendKpi
import com.orbitai.erp.ui.component.kpi.WarehouseKpiColumn
import com.orbitai.erp.ui.component.progress.ProgressCard

/**
 * Gallery for KPI shells and role presets — review glass, type, and AI positioning on device.
 */
@Composable
internal fun KpiGalleryPage() {
    val spacing = OrbitTheme.spacing

    GallerySection("KPI templates") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            ProgressCard(
                label = "Health",
                progress = 0.78f,
                delta = 3f,
                colors = OrbitProgressDefaults.greenColors,
            )
            TrendKpi(
                title = "Materials & savings",
                value = KpiSamples.MaterialsSavingsValue,
                primaryValues = KpiSamples.savingsTrend,
                secondaryValues = KpiSamples.materialsTrend,
                forecast = KpiSamples.savingsForecast,
                secondaryForecast = KpiSamples.materialsForecast,
                xLabels = KpiSamples.monthLabels,
                delta = KpiSamples.MaterialsSavingsDelta,
                comparisonLabel = "vs last month",
                supporting = null,
                iconInChip = false,
                timelineOptions = KpiSamples.timelineOptions,
                glassBoost = true,
            )
            OrbitAiInsightCard(
                title = "Today's brief",
                headline = "Good — room to push savings above plan",
                advice = "AI tip: focus cement waste on Block B pours this week.",
                markdown = KpiSamples.CeoAiMarkdown,
                glassBoost = true,
            )
            AiDailyFocusList(
                title = "Today's task list",
                initialItems = KpiSamples.CeoDailyFocus,
            )
            OrbitAlertStrip(
                title = "Alert strip",
                count = "6",
                message = "Items need attention",
                tone = OrbitBadgeTone.Amber,
                badgeLabel = "Action",
                onClick = {},
            )
        }
    }

    RoleKpiSection(title = "CEO KPIs") { CeoKpiColumn() }
    RoleKpiSection(title = "Project Manager KPIs") { PmKpiColumn() }
    RoleKpiSection(title = "Site Engineer KPIs") { SiteEngineerKpiColumn() }
    RoleKpiSection(title = "Contractor KPIs") { ContractorKpiColumn() }
    RoleKpiSection(title = "QA/QC KPIs") { QaQcKpiColumn() }
    RoleKpiSection(title = "Procurement KPIs") { ProcurementKpiColumn() }
    RoleKpiSection(title = "Warehouse KPIs") { WarehouseKpiColumn() }
}

@Composable
private fun RoleKpiSection(
    title: String,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val spacing = OrbitTheme.spacing
    GallerySection(title) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            OrbitButton(
                label = if (expanded) "Hide role presets" else "Show role presets",
                onClick = { expanded = !expanded },
                variant = OrbitButtonVariant.Secondary,
            )
            if (expanded) {
                content()
            }
        }
    }
}
