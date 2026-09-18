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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitAiInsightCard
import com.orbitai.erp.core.designsystem.component.display.OrbitAlertStrip
import com.orbitai.erp.core.designsystem.component.display.OrbitCompareSide
import com.orbitai.erp.core.designsystem.component.display.OrbitDualCompareTile
import com.orbitai.erp.core.designsystem.component.display.OrbitKpiTile
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionBar
import com.orbitai.erp.core.designsystem.component.progress.OrbitProportionSegment
import com.orbitai.erp.core.designsystem.component.progress.OrbitTrendGraph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.progress.ProgressCard
import com.orbitai.erp.ui.component.progress.ProgressSection

// ─── CEO ─────────────────────────────────────────────────────────────────────

@Composable
fun CeoPortfolioHealthKpi(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        glassBoost = true,
        shadowLevel = OrbitShadow.Level2,
        contentDescription = "Health, ${(KpiSamples.PortfolioHealth * 100).toInt()} percent",
    ) {
        ProgressSection(
            label = "Health",
            progress = KpiSamples.PortfolioHealth,
            delta = 3f,
            comparisonLabel = "vs last week",
            colors = OrbitProgressDefaults.greenColors,
        )
    }
}

/** Combined materials spend trend + savings hero with timeline + axes. */
@Composable
fun CeoMaterialsAndSavingsKpi(modifier: Modifier = Modifier) {
    val chart = OrbitTheme.semanticColors.chartSeries
    TrendKpi(
        title = "Materials & savings",
        value = KpiSamples.MaterialsSavingsValue,
        primaryValues = KpiSamples.savingsTrend,
        secondaryValues = KpiSamples.materialsTrend,
        forecast = KpiSamples.savingsForecast,
        secondaryForecast = KpiSamples.materialsForecast,
        primaryLabel = "Savings",
        secondaryLabel = "Materials",
        xLabels = KpiSamples.monthLabels,
        delta = KpiSamples.MaterialsSavingsDelta,
        comparisonLabel = "vs last month",
        supporting = null,
        icon = OrbitIcons.TrendUp,
        iconTone = OrbitBadgeTone.Teal,
        iconInChip = false,
        primaryLineColor = chart[2], // teal
        secondaryLineColor = chart[5], // orange
        timelineOptions = KpiSamples.timelineOptions,
        seriesForTimeline = { key ->
            when (key) {
                "This week" -> KpiTimelineSeries(
                    primary = KpiSamples.savingsWeek,
                    secondary = KpiSamples.materialsWeek,
                    xLabels = KpiSamples.weekLabels,
                )
                "Last 3 months" -> KpiTimelineSeries(
                    primary = KpiSamples.savingsQuarter,
                    secondary = KpiSamples.materialsQuarter,
                    xLabels = KpiSamples.quarterLabels,
                )
                "Max" -> KpiTimelineSeries(
                    primary = KpiSamples.savingsMax,
                    secondary = KpiSamples.materialsMax,
                    xLabels = KpiSamples.maxLabels,
                )
                else -> KpiTimelineSeries(
                    primary = KpiSamples.savingsTrend,
                    secondary = KpiSamples.materialsTrend,
                    forecast = KpiSamples.savingsForecast,
                    secondaryForecast = KpiSamples.materialsForecast,
                    xLabels = KpiSamples.monthLabels,
                )
            }
        },
        glassBoost = true,
        modifier = modifier,
    )
}

/** Invoice cash accepted + timeline chart + pending / approved / done metrics. */
@Composable
fun CeoInvoicesKpi(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    val chart = OrbitTheme.semanticColors.chartSeries
    val cashLine = chart[4] // green — distinct from materials teal/orange
    var timeline by remember {
        mutableStateOf(
            KpiSamples.timelineOptions.find { it == "This month" }
                ?: KpiSamples.timelineOptions[1],
        )
    }
    val series = remember(timeline) {
        when (timeline) {
            "This week" -> KpiTimelineSeries(KpiSamples.cashWeek, xLabels = KpiSamples.weekLabels)
            "Last 3 months" -> KpiTimelineSeries(KpiSamples.cashQuarter, xLabels = KpiSamples.quarterLabels)
            "Max" -> KpiTimelineSeries(KpiSamples.cashMax, xLabels = KpiSamples.maxLabels)
            else -> KpiTimelineSeries(
                KpiSamples.cashAcceptedSpark,
                xLabels = KpiSamples.monthLabels.take(KpiSamples.cashAcceptedSpark.size),
            )
        }
    }

    OrbitKpiTile(
        title = "Invoices",
        value = KpiSamples.InvoiceMoneyAccepted,
        modifier = modifier,
        icon = OrbitIcons.ReceiptIndianRupee,
        iconTone = OrbitBadgeTone.Green,
        iconInChip = false,
        delta = KpiSamples.InvoiceMoneyDelta,
        comparisonLabel = "vs last month",
        supporting = null,
        glassBoost = true,
        headerTrailing = {
            OrbitDropdownField(
                selected = timeline,
                options = KpiSamples.timelineOptions,
                onSelect = { timeline = it },
                label = "Timeline",
                placeholder = "Month",
                searchable = false,
                size = OrbitFieldSize.Small,
                modifier = Modifier.widthIn(max = 132.dp),
            )
        },
        footer = {
            OrbitTrendGraph(
                values = series.primary,
                lineColor = cashLine,
                primaryLabel = "Accepted",
                secondaryLabel = "",
                xLabels = series.xLabels,
                height = 176.dp,
                showAxes = true,
                interactive = true,
            )
            Spacer(Modifier.height(spacing.md))
            InvoiceStatusMetricsRow()
        },
    )
}

@Composable
private fun InvoiceStatusMetricsRow(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        InvoiceStatusMetric(
            heading = "Pending",
            count = "${KpiSamples.InvoicePendingCount}",
            amount = KpiSamples.InvoicePendingAmount,
            modifier = Modifier.weight(1f),
        )
        InvoiceStatusMetric(
            heading = "Approved",
            count = "${KpiSamples.InvoiceApprovedCount}",
            amount = KpiSamples.InvoiceApprovedAmount,
            modifier = Modifier.weight(1f),
        )
        InvoiceStatusMetric(
            heading = "Done",
            count = "${KpiSamples.InvoiceCompletedCount}",
            amount = KpiSamples.InvoiceCompletedAmount,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InvoiceStatusMetric(
    heading: String,
    count: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    Column(modifier = modifier) {
        Text(
            text = heading.uppercase(),
            style = OrbitTheme.extendedTypography.cardLabel,
            color = content.textTertiary,
            maxLines = 1,
        )
        Text(
            text = count,
            style = OrbitTheme.extendedTypography.metricMedium,
            color = content.textPrimary,
            maxLines = 1,
        )
        Text(
            text = amount,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textSecondary,
            maxLines = 1,
        )
    }
}

@Composable
fun CeoAiTodaysBriefKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "Today's brief",
        headline = KpiSamples.CeoAiHeadline,
        advice = KpiSamples.CeoAiAdvice,
        markdown = KpiSamples.CeoAiMarkdown,
        updatedLabel = "Updated 21m ago",
        glassBoost = true,
        modifier = modifier,
    )
}

@Composable
fun CeoTaskListKpi(modifier: Modifier = Modifier) {
    AiDailyFocusList(
        title = "Today's task list",
        initialItems = KpiSamples.CeoDailyFocus,
        modifier = modifier,
    )
}

@Composable
fun CeoKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        CeoAiTodaysBriefKpi()
        CeoPortfolioHealthKpi()
        CeoTaskListKpi()
        CeoInvoicesKpi()
        CeoMaterialsAndSavingsKpi()
    }
}

// Deprecated aliases — gallery / callers may still reference the old split cards.
@Deprecated("Use CeoMaterialsAndSavingsKpi", ReplaceWith("CeoMaterialsAndSavingsKpi(modifier)"))
@Composable
fun CeoMaterialsSavingsKpi(modifier: Modifier = Modifier) = CeoMaterialsAndSavingsKpi(modifier)

@Deprecated("Use CeoMaterialsAndSavingsKpi", ReplaceWith("CeoMaterialsAndSavingsKpi(modifier)"))
@Composable
fun CeoMaterialsSavingsTrendKpi(modifier: Modifier = Modifier) = CeoMaterialsAndSavingsKpi(modifier)

@Deprecated("Use CeoInvoicesKpi", ReplaceWith("CeoInvoicesKpi(modifier)"))
@Composable
fun CeoInvoicePipelineKpi(modifier: Modifier = Modifier) = CeoInvoicesKpi(modifier)

@Deprecated("Use CeoInvoicesKpi", ReplaceWith("CeoInvoicesKpi(modifier)"))
@Composable
fun CeoInvoiceMoneyAcceptedKpi(modifier: Modifier = Modifier) = CeoInvoicesKpi(modifier)

@Deprecated("Use CeoAiTodaysBriefKpi", ReplaceWith("CeoAiTodaysBriefKpi(modifier)"))
@Composable
fun CeoOrbitPulseKpi(modifier: Modifier = Modifier) = CeoAiTodaysBriefKpi(modifier)

@Deprecated("Use CeoAiTodaysBriefKpi", ReplaceWith("CeoAiTodaysBriefKpi(modifier)"))
@Composable
fun CeoAiExecutiveBriefKpi(modifier: Modifier = Modifier) = CeoAiTodaysBriefKpi(modifier)

@Deprecated("Use CeoTaskListKpi", ReplaceWith("CeoTaskListKpi(modifier)"))
@Composable
fun CeoDailyFocusKpi(modifier: Modifier = Modifier) = CeoTaskListKpi(modifier)

// ─── Project Manager ─────────────────────────────────────────────────────────

@Composable
fun PmProjectProgressKpi(modifier: Modifier = Modifier) {
    ProgressCard(
        label = "Project progress",
        progress = KpiSamples.ProjectProgress,
        delta = KpiSamples.ProjectProgressDelta,
        modifier = modifier,
    )
}

@Composable
fun PmScheduledTasksKpi(modifier: Modifier = Modifier) {
    OrbitDualCompareTile(
        title = "Scheduled tasks",
        left = OrbitCompareSide(
            label = "Due today",
            value = "${KpiSamples.TasksDueToday}",
            barProgress = KpiSamples.TasksDueToday / 20f,
            barColor = OrbitTheme.semanticColors.chartSeries[0],
        ),
        right = OrbitCompareSide(
            label = "This week",
            value = "${KpiSamples.TasksThisWeek}",
            delta = -KpiSamples.TasksOverdue.toFloat(),
            deltaHigherIsBetter = false,
            deltaDescription = "${KpiSamples.TasksOverdue} overdue",
            barProgress = KpiSamples.TasksThisWeek / 30f,
            barColor = OrbitTheme.semanticColors.chartSeries[2],
        ),
        supporting = "${KpiSamples.TasksOverdue} overdue — reschedule or reassign",
        modifier = modifier,
    )
}

@Composable
fun PmScheduledIssuesKpi(modifier: Modifier = Modifier) {
    OrbitAlertStrip(
        title = "Scheduled issues",
        count = "${KpiSamples.OpenIssues}",
        message = "${KpiSamples.CriticalIssues} critical · review and assign",
        badgeLabel = "Open",
        tone = OrbitBadgeTone.Orange,
        icon = OrbitIcons.OctagonAlert,
        supporting = "Tied to raise / assign issue flow",
        modifier = modifier,
    )
}

@Composable
fun PmBudgetSavingsKpi(modifier: Modifier = Modifier) {
    OrbitDualCompareTile(
        title = "Budget / materials savings",
        left = OrbitCompareSide(
            label = "Planned",
            value = KpiSamples.PlannedSavings,
            barProgress = 1f,
            barColor = OrbitTheme.contentColors.textTertiary,
        ),
        right = OrbitCompareSide(
            label = "Actual",
            value = KpiSamples.ActualSavings,
            delta = -15.2f,
            deltaHigherIsBetter = true,
            barProgress = 7.8f / 9.2f,
            barColor = OrbitTheme.semanticColors.chartSeries[4],
        ),
        supporting = "AI materials efficiency vs plan",
        modifier = modifier,
    )
}

@Composable
fun PmAiWeeklyStatusKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "AI today's brief",
        headline = KpiSamples.PmAiHeadline,
        advice = KpiSamples.PmAiAdvice,
        markdown = KpiSamples.PmAiMarkdown,
        updatedLabel = "Updated 1h ago",
        modifier = modifier,
    )
}

@Composable
fun PmKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        PmProjectProgressKpi()
        AiDailyFocusList(
            title = "Today's task list",
            initialItems = KpiSamples.PmDailyFocus,
        )
        PmScheduledTasksKpi()
        PmScheduledIssuesKpi()
        PmBudgetSavingsKpi()
        PmAiWeeklyStatusKpi()
    }
}

// ─── Site Engineer ───────────────────────────────────────────────────────────

@Composable
fun SeTasksDueKpi(modifier: Modifier = Modifier) {
    OrbitKpiTile(
        title = "Tasks due",
        value = "${KpiSamples.TasksDue}",
        modifier = modifier,
        icon = OrbitIcons.CalendarSchedule,
        iconTone = OrbitBadgeTone.Blue,
        supporting = "My queue for today",
    )
}

@Composable
fun SeUnitProgressKpi(modifier: Modifier = Modifier) {
    ProgressCard(
        label = "Unit progress",
        progress = KpiSamples.UnitProgress,
        delta = KpiSamples.UnitProgressDelta,
        modifier = modifier,
    )
}

@Composable
fun SeMaterialUsageKpi(modifier: Modifier = Modifier) {
    val chart = OrbitTheme.semanticColors.chartSeries
    val logged = KpiSamples.MaterialLoggedKg
    val remaining = KpiSamples.MaterialRemainingKg
    ProportionKpi(
        title = "Material usage today",
        value = "${logged.toInt()} kg",
        supporting = "Logged vs remaining on active unit",
        icon = OrbitIcons.Layers01,
        iconTone = OrbitBadgeTone.Teal,
        segments = listOf(
            OrbitProportionSegment(
                weight = logged,
                color = chart[2],
                label = "Logged",
                valueLabel = "${logged.toInt()} kg",
                detailLabel = "Logged · ${logged.toInt()} kg",
                shortLabel = "Logged",
            ),
            OrbitProportionSegment(
                weight = remaining,
                color = chart[6],
                label = "Remaining",
                valueLabel = "${remaining.toInt()} kg",
                detailLabel = "Remaining · ${remaining.toInt()} kg",
                shortLabel = "Left",
            ),
        ),
        modifier = modifier,
    )
}

@Composable
fun SeAiCalcPlanKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "AI today's brief",
        headline = KpiSamples.SeAiHeadline,
        advice = KpiSamples.SeAiAdvice,
        markdown = KpiSamples.SeAiMarkdown,
        badgeLabel = "Ask AI",
        modifier = modifier,
    )
}

@Composable
fun SiteEngineerKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        SeTasksDueKpi()
        AiDailyFocusList(
            title = "Today's task list",
            initialItems = KpiSamples.SeDailyFocus,
        )
        SeUnitProgressKpi()
        SeMaterialUsageKpi()
        SeAiCalcPlanKpi()
    }
}

// ─── Contractor ──────────────────────────────────────────────────────────────

@Composable
fun ContractorAssignedWorkKpi(modifier: Modifier = Modifier) {
    OrbitKpiTile(
        title = "Assigned work",
        value = "${KpiSamples.AssignedWork}",
        modifier = modifier,
        icon = OrbitIcons.NotepadText,
        iconTone = OrbitBadgeTone.Indigo,
        supporting = "Active assignments",
    )
}

@Composable
fun ContractorCompletionKpi(modifier: Modifier = Modifier) {
    ProgressCard(
        label = "Completion rate",
        progress = KpiSamples.CompletionRate,
        delta = KpiSamples.CompletionDelta,
        modifier = modifier,
    )
}

@Composable
fun ContractorMaterialRequestsKpi(modifier: Modifier = Modifier) {
    OrbitAlertStrip(
        title = "Material requests",
        count = "${KpiSamples.MaterialRequestsPending}",
        message = "Pending needs — follow up with warehouse",
        tone = OrbitBadgeTone.Amber,
        badgeLabel = "Pending",
        icon = OrbitIcons.ShoppingCartAdd01,
        modifier = modifier,
    )
}

@Composable
fun ContractorInvoicesPendingKpi(modifier: Modifier = Modifier) {
    OrbitAlertStrip(
        title = "Invoices pending",
        count = "${KpiSamples.InvoicesPending}",
        message = "Awaiting approval or payment",
        tone = OrbitBadgeTone.Orange,
        badgeLabel = "Review",
        icon = OrbitIcons.ReceiptIndianRupee,
        modifier = modifier,
    )
}

@Composable
fun ContractorInvoiceMoneyReceivedKpi(modifier: Modifier = Modifier) {
    MetricSparklineKpi(
        title = "Invoice money received",
        value = KpiSamples.InvoiceMoneyReceived,
        sparkline = KpiSamples.cashAcceptedSpark,
        icon = OrbitIcons.CircleCheck,
        iconTone = OrbitBadgeTone.Green,
        delta = KpiSamples.InvoiceReceivedDelta,
        comparisonLabel = "vs last month",
        supporting = "Accepted / paid to you",
        sparklineColor = OrbitTheme.semanticColors.chartSeries[4],
        modifier = modifier,
    )
}

@Composable
fun ContractorAiPaymentBriefKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "AI today's brief",
        headline = KpiSamples.ContractorAiHeadline,
        advice = KpiSamples.ContractorAiAdvice,
        markdown = KpiSamples.ContractorAiMarkdown,
        modifier = modifier,
    )
}

@Composable
fun ContractorKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        ContractorAssignedWorkKpi()
        AiDailyFocusList(
            title = "Today's task list",
            initialItems = KpiSamples.ContractorDailyFocus,
        )
        ContractorCompletionKpi()
        ContractorMaterialRequestsKpi()
        ContractorInvoicesPendingKpi()
        ContractorInvoiceMoneyReceivedKpi()
        ContractorAiPaymentBriefKpi()
    }
}

// ─── QA/QC ───────────────────────────────────────────────────────────────────

@Composable
fun QaOpenInspectionsKpi(modifier: Modifier = Modifier) {
    OrbitKpiTile(
        title = "Open inspections",
        value = "${KpiSamples.OpenInspections}",
        modifier = modifier,
        icon = OrbitIcons.BadgeCheck,
        iconTone = OrbitBadgeTone.Cyan,
        supporting = "Inspection queue",
    )
}

@Composable
fun QaComplianceKpi(modifier: Modifier = Modifier) {
    ProgressCard(
        label = "Compliance rate",
        progress = KpiSamples.ComplianceRate,
        delta = KpiSamples.ComplianceDelta,
        modifier = modifier,
    )
}

@Composable
fun QaCriticalDefectsKpi(modifier: Modifier = Modifier) {
    OrbitAlertStrip(
        title = "Critical defects",
        count = "${KpiSamples.CriticalDefects}",
        message = "Severity high — schedule re-inspection",
        tone = OrbitBadgeTone.Red,
        badgeLabel = "Critical",
        icon = OrbitIcons.OctagonAlert,
        modifier = modifier,
    )
}

@Composable
fun QaAiInspectionSummaryKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "AI today's brief",
        headline = KpiSamples.QaAiHeadline,
        advice = KpiSamples.QaAiAdvice,
        markdown = KpiSamples.QaAiMarkdown,
        modifier = modifier,
    )
}

@Composable
fun QaQcKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        QaOpenInspectionsKpi()
        AiDailyFocusList(
            title = "Today's task list",
            initialItems = KpiSamples.QaDailyFocus,
        )
        QaComplianceKpi()
        QaCriticalDefectsKpi()
        QaAiInspectionSummaryKpi()
    }
}

// ─── Procurement ─────────────────────────────────────────────────────────────

@Composable
fun ProcurementPendingPosKpi(modifier: Modifier = Modifier) {
    OrbitKpiTile(
        title = "Pending POs",
        value = "${KpiSamples.PendingPos}",
        modifier = modifier,
        icon = OrbitIcons.File,
        iconTone = OrbitBadgeTone.Blue,
        supporting = "Awaiting approval",
    )
}

@Composable
fun ProcurementLeadTimeRiskKpi(modifier: Modifier = Modifier) {
    OrbitAlertStrip(
        title = "Lead-time risk",
        count = "${KpiSamples.LeadTimeAtRisk}",
        message = "Orders late or at risk of delay",
        tone = OrbitBadgeTone.Orange,
        badgeLabel = "At risk",
        icon = OrbitIcons.StopWatch,
        modifier = modifier,
    )
}

@Composable
fun ProcurementSpendVsPlanKpi(modifier: Modifier = Modifier) {
    OrbitDualCompareTile(
        title = "Spend vs plan",
        left = OrbitCompareSide(
            label = "Spend",
            value = KpiSamples.SpendActual,
            barProgress = 28.1f / 30f,
            barColor = OrbitTheme.semanticColors.chartSeries[0],
        ),
        right = OrbitCompareSide(
            label = "Plan",
            value = KpiSamples.SpendPlan,
            barProgress = 1f,
            barColor = OrbitTheme.contentColors.textTertiary,
        ),
        supporting = "Materials budget control",
        modifier = modifier,
    )
}

@Composable
fun ProcurementAiReorderKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "AI today's brief",
        headline = KpiSamples.ProcAiHeadline,
        advice = KpiSamples.ProcAiAdvice,
        markdown = KpiSamples.ProcAiMarkdown,
        modifier = modifier,
    )
}

@Composable
fun ProcurementKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        ProcurementPendingPosKpi()
        AiDailyFocusList(
            title = "Today's task list",
            initialItems = KpiSamples.ProcDailyFocus,
        )
        ProcurementLeadTimeRiskKpi()
        ProcurementSpendVsPlanKpi()
        ProcurementAiReorderKpi()
    }
}

// ─── Warehouse ───────────────────────────────────────────────────────────────

@Composable
fun WarehouseStockHealthKpi(modifier: Modifier = Modifier) {
    HealthDonutKpi(
        title = "Stock health",
        progress = KpiSamples.StockHealth,
        caption = "Healthy",
        colors = OrbitDonutProgressDefaults.greenColors,
        modifier = modifier,
    )
}

@Composable
fun WarehouseLowStockKpi(modifier: Modifier = Modifier) {
    OrbitAlertStrip(
        title = "Low-stock alerts",
        count = "${KpiSamples.LowStockAlerts}",
        message = "SKUs below reorder point",
        tone = OrbitBadgeTone.Amber,
        badgeLabel = "Action",
        icon = OrbitIcons.BatteryLow,
        modifier = modifier,
    )
}

@Composable
fun WarehouseMaterialsEfficiencyKpi(modifier: Modifier = Modifier) {
    OrbitKpiTile(
        title = "Materials efficiency",
        value = KpiSamples.MaterialsEfficiency,
        modifier = modifier,
        icon = OrbitIcons.TrendUp,
        iconTone = OrbitBadgeTone.Green,
        delta = KpiSamples.MaterialsEfficiencyDelta,
        comparisonLabel = "vs last week",
        supporting = "Waste vs plan — savings signal",
    )
}

@Composable
fun WarehouseMaterialsTrendKpi(modifier: Modifier = Modifier) {
    TrendKpi(
        title = "Materials consumption",
        value = "${KpiSamples.materialsConsumption.last().toInt()} t",
        primaryValues = KpiSamples.materialsConsumption,
        forecast = KpiSamples.materialsConsumptionForecast,
        primaryLabel = "Usage",
        secondaryLabel = "",
        xLabels = KpiSamples.monthLabels,
        icon = OrbitIcons.Warehouse,
        iconTone = OrbitBadgeTone.Teal,
        supporting = "Site drawdown over time",
        modifier = modifier,
    )
}

@Composable
fun WarehouseIncomingDeliveriesKpi(modifier: Modifier = Modifier) {
    OrbitKpiTile(
        title = "Incoming deliveries",
        value = "${KpiSamples.IncomingDeliveries}",
        modifier = modifier,
        icon = OrbitIcons.Archive04,
        iconTone = OrbitBadgeTone.Blue,
        supporting = "Expected receipts this week",
    )
}

@Composable
fun WarehouseAiStockBriefKpi(modifier: Modifier = Modifier) {
    OrbitAiInsightCard(
        title = "AI today's brief",
        headline = KpiSamples.WhAiHeadline,
        advice = KpiSamples.WhAiAdvice,
        markdown = KpiSamples.WhAiMarkdown,
        modifier = modifier,
    )
}

@Composable
fun WarehouseKpiColumn(modifier: Modifier = Modifier) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        WarehouseStockHealthKpi()
        AiDailyFocusList(
            title = "Today's task list",
            initialItems = KpiSamples.WhDailyFocus,
        )
        WarehouseLowStockKpi()
        WarehouseMaterialsEfficiencyKpi()
        WarehouseMaterialsTrendKpi()
        WarehouseIncomingDeliveriesKpi()
        WarehouseAiStockBriefKpi()
    }
}
