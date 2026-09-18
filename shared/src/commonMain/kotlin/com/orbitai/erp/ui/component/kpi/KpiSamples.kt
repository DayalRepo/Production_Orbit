package com.orbitai.erp.ui.component.kpi

import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem

/**
 * Stable mock figures for the KPI gallery and preset previews.
 * Not wired to repositories — replace when dashboards get real data.
 */
object KpiSamples {
    // —— Shared series ——
    val savingsTrend = listOf(2.1f, 2.4f, 2.8f, 3.1f, 3.6f, 4.0f, 4.4f)
    val materialsTrend = listOf(5.2f, 5.0f, 4.8f, 4.9f, 4.6f, 4.5f, 4.3f)
    val savingsForecast = listOf(4.7f, 5.0f)
    val materialsForecast = listOf(4.2f, 4.1f)
    val monthLabels = listOf("Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep")

    val timelineOptions = listOf("This week", "This month", "Last 3 months", "Max")

    val savingsWeek = listOf(3.8f, 3.9f, 4.0f, 4.1f, 4.2f, 4.3f, 4.4f)
    val materialsWeek = listOf(4.6f, 4.55f, 4.5f, 4.48f, 4.4f, 4.35f, 4.3f)
    val weekLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val savingsQuarter = listOf(1.8f, 2.2f, 2.6f, 3.0f, 3.4f, 3.8f, 4.0f, 4.2f, 4.4f)
    val materialsQuarter = listOf(5.8f, 5.5f, 5.2f, 5.0f, 4.9f, 4.7f, 4.6f, 4.5f, 4.3f)
    val quarterLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep")

    val savingsMax = listOf(0.8f, 1.2f, 1.6f, 2.0f, 2.4f, 2.8f, 3.2f, 3.6f, 4.0f, 4.4f)
    val materialsMax = listOf(6.2f, 6.0f, 5.7f, 5.5f, 5.2f, 5.0f, 4.8f, 4.6f, 4.5f, 4.3f)
    val maxLabels = listOf("Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep")

    val cashAcceptedSpark = listOf(8f, 9f, 11f, 10f, 13f, 14f, 16f)
    val cashWeek = listOf(12f, 12.5f, 13f, 14f, 14.5f, 15f, 16f)
    val cashQuarter = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f, 14f, 16f)
    val cashMax = listOf(3f, 4f, 5f, 6f, 8f, 9f, 10f, 12f, 14f, 16f)
    val materialsConsumption = listOf(42f, 48f, 45f, 52f, 49f, 55f, 51f)
    val materialsConsumptionForecast = listOf(53f, 50f)

    // —— CEO ——
    const val PortfolioHealth = 0.78f
    const val MaterialsSavingsValue = "₹ 18.4L"
    const val MaterialsSavingsDelta = 12.5f
    const val InvoicePendingCount = 14
    const val InvoiceApprovedCount = 9
    const val InvoiceCompletedCount = 31
    const val InvoicePendingAmount = "₹ 6.1L"
    const val InvoiceApprovedAmount = "₹ 4.2L"
    const val InvoiceCompletedAmount = "₹ 32.3L"
    const val InvoiceMoneyAccepted = "₹ 42.6L"
    const val InvoiceMoneyDelta = 8.2f
    val CeoAiHeadline = "Savings on track; three invoices need approval"
    val CeoAiAdvice =
        "Materials efficiency is up 12% this month. Approve the pending Villa-12 and Block-A invoices " +
            "to unlock ₹6.1L this week."
    val CeoAiMarkdown =
        """
        ## Portfolio pulse
        - **Materials savings:** ₹18.4L (+12.5% vs last month)
        - **Invoice cash accepted:** ₹42.6L
        - **At risk:** 3 POs past lead time on cement

        Focus procurement on cement reorder and clear the invoice queue today.
        """.trimIndent()

    /** AI-generated “what to do today” — gallery starts with a mix of done / open. */
    val CeoDailyFocus = listOf(
        OrbitChecklistItem("ceo-1", "Approve Villa-12 and Block-A invoices", checked = false),
        OrbitChecklistItem("ceo-2", "Review materials savings vs plan on Tower B", checked = true),
        OrbitChecklistItem("ceo-3", "Sign off cement expedite with procurement", checked = false),
        OrbitChecklistItem("ceo-4", "Scan Orbit pulse for schedule risk flags", checked = false),
    )
    val PmDailyFocus = listOf(
        OrbitChecklistItem("pm-1", "Reschedule waterproofing on Block B", checked = false),
        OrbitChecklistItem("pm-2", "Assign 2 critical issues to QA", checked = false),
        OrbitChecklistItem("pm-3", "Walk overdue tasks with site engineer", checked = true),
        OrbitChecklistItem("pm-4", "Check materials savings vs ₹9.2L plan", checked = false),
    )
    val SeDailyFocus = listOf(
        OrbitChecklistItem("se-1", "Log remaining materials on unit A-101", checked = false),
        OrbitChecklistItem("se-2", "Close curing checklist for slab pour", checked = true),
        OrbitChecklistItem("se-3", "Ask AI for M25 mix bags for 12 m³", checked = false),
    )
    val ContractorDailyFocus = listOf(
        OrbitChecklistItem("co-1", "Attach finished waterproofing to invoice", checked = false),
        OrbitChecklistItem("co-2", "Follow up 4 pending material requests", checked = false),
        OrbitChecklistItem("co-3", "Confirm crew for Villa 8 tomorrow", checked = true),
    )
    val QaDailyFocus = listOf(
        OrbitChecklistItem("qa-1", "Re-inspect Block C expansion joints", checked = false),
        OrbitChecklistItem("qa-2", "Close 2 open inspections before noon", checked = false),
        OrbitChecklistItem("qa-3", "Log critical defect photos", checked = true),
    )
    val ProcDailyFocus = listOf(
        OrbitChecklistItem("proc-1", "Approve PO-204 cement expedite", checked = false),
        OrbitChecklistItem("proc-2", "Hold TMT 12mm — surplus on site", checked = true),
        OrbitChecklistItem("proc-3", "Chase 5 at-risk lead-time orders", checked = false),
    )
    val WhDailyFocus = listOf(
        OrbitChecklistItem("wh-1", "Pick list for 8 incoming deliveries", checked = false),
        OrbitChecklistItem("wh-2", "Flag low-stock cement to procurement", checked = false),
        OrbitChecklistItem("wh-3", "Cycle-count binding wire bay", checked = true),
    )

    // —— PM ——
    const val ProjectProgress = 0.64f
    const val ProjectProgressDelta = 4f
    const val TasksDueToday = 6
    const val TasksThisWeek = 18
    const val TasksOverdue = 3
    const val OpenIssues = 7
    const val CriticalIssues = 2
    const val PlannedSavings = "₹ 9.2L"
    const val ActualSavings = "₹ 7.8L"
    val PmAiHeadline = "Schedule slip on Block B; issues clustered on waterproofing"
    val PmAiAdvice =
        "3 tasks overdue. Reschedule waterproofing inspection and pull two contractors forward on Block B."
    val PmAiMarkdown =
        """
        ## Weekly status
        - Progress **64%** (+4 pts)
        - **3 overdue** tasks · **7** open issues (2 critical)
        - Savings **₹7.8L** vs plan **₹9.2L**

        Recommend: reassign SE capacity to Block B slab curing checks.
        """.trimIndent()

    // —— Site engineer ——
    const val TasksDue = 5
    const val UnitProgress = 0.71f
    const val UnitProgressDelta = 6f
    const val MaterialLoggedKg = 420f
    const val MaterialRemainingKg = 180f
    val SeAiHeadline = "Ask AI about slab mix or plan dimensions"
    val SeAiAdvice =
        "Need cement bags for 12 m³ pour? Open AI for construction calc and plan Q&A."
    val SeAiMarkdown =
        """
        ## Site AI
        - Unit A-101 progress **71%**
        - Materials logged today **420 kg** · remaining **180 kg**

        Try: *“How many bags of OPC 53 for 12 m³ M25?”*
        """.trimIndent()

    // —— Contractor ——
    const val AssignedWork = 12
    const val CompletionRate = 0.58f
    const val CompletionDelta = 3f
    const val MaterialRequestsPending = 4
    const val InvoicesPending = 3
    const val InvoiceMoneyReceived = "₹ 9.8L"
    const val InvoiceReceivedDelta = 14.5f
    val ContractorAiHeadline = "Bill waterproofing finish this week"
    val ContractorAiAdvice =
        "2 completed tasks are ready to attach to an invoice. Pending approvals: ₹2.4L."
    val ContractorAiMarkdown =
        """
        ## Payment brief
        - Assigned **12** · completion **58%**
        - Invoices pending **3** · received **₹9.8L**

        Next: raise invoice for waterproofing finish on Villa 8.
        """.trimIndent()

    // —— QA/QC ——
    const val OpenInspections = 9
    const val ComplianceRate = 0.92f
    const val ComplianceDelta = 2f
    const val CriticalDefects = 3
    val QaAiHeadline = "Recurring hairline cracks at expansion joints"
    val QaAiAdvice =
        "3 critical defects share the same joint detail. Prioritise re-inspection on Block C."
    val QaAiMarkdown =
        """
        ## Inspection summary
        - Open inspections **9** · compliance **92%**
        - Critical defects **3** — expansion joint detailing

        AI pattern: same contractor crew on last four joint failures.
        """.trimIndent()

    // —— Procurement ——
    const val PendingPos = 11
    const val LeadTimeAtRisk = 5
    const val SpendActual = "₹ 28.1L"
    const val SpendPlan = "₹ 30.0L"
    val ProcAiHeadline = "Reorder cement before Thursday shortage"
    val ProcAiAdvice =
        "AI projects a 4-day stockout on OPC 53. Approve PO-204 and consolidate steel with existing vendor."
    val ProcAiMarkdown =
        """
        ## Reorder suggestions
        - Pending POs **11** · lead-time risk **5**
        - Spend **₹28.1L** of **₹30.0L** plan

        1. Cement OPC 53 — expedite
        2. TMT 12mm — hold; surplus on site
        """.trimIndent()

    // —— Warehouse ——
    const val StockHealth = 0.74f
    const val LowStockAlerts = 6
    const val MaterialsEfficiency = "91%"
    const val MaterialsEfficiencyDelta = 3.5f
    const val IncomingDeliveries = 8
    val WhAiHeadline = "Cement and binding wire trending low"
    val WhAiAdvice =
        "Efficiency 91%. Expect shortage on cement by Thu if consumption holds — flag procurement."
    val WhAiMarkdown =
        """
        ## Stock brief
        - Stock health **74%** · low-stock **6**
        - Efficiency **91%** · incoming **8** deliveries

        AI: cement burn rate +12% vs last week after Block B pours.
        """.trimIndent()
}
