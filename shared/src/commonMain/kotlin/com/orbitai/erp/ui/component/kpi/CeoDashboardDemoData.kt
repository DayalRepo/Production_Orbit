package com.orbitai.erp.ui.component.kpi

/**
 * Demo CEO dashboard figures until live analytics / AI endpoints are wired.
 */
object CeoDashboardDemoData {
    const val HealthyProjects = 8
    const val AtRiskProjects = 3
    const val CriticalProjects = 1

    const val OverdueAmountLabel = "₹18.4L"
    const val OverdueInvoiceCount = 12
    const val AiFlaggedInvoices = 4

    const val RiskCount = 7
    const val DelayRisks = 3
    const val CostRisks = 2
    const val LabourRisks = 2

    const val AiSavingsLabel = "₹6.2L"
    const val SavingsMonthProgress = 0.68f

    const val HealthProgress = 0.82f
    const val HealthProgressDelta = 2.4f

    /**
     * AI-authored markdown brief.
     * Supports headings, bullets, **bold**, *italic*, __underline__, ~~strike~~, and pipe tables.
     */
    val AiBriefMarkdown =
        """
        Here's today's brief across the active portfolio.
        
        Riverside Tower is two days behind on the Level-12 concrete pour after a pump delay, which now risks pushing finishing into next week if formwork is not released by Thursday.
        
        Bay 4 labour attendance is thin on the night shift, so the AI forecast flags a labour shortfall unless two masons are redeployed from Corridor B.
        
        **INV-2041** for ₹3.8L remains stuck in client finance and is AI-flagged as the __highest-value overdue__ item this week.
        
        Material optimisation on Corridor B already saved ₹1.1L by swapping to the alternate cement grade; extending that swap to Bay 4 is the clearest near-term saving.
        
        Overall portfolio health sits at **82%** with eight sites healthy, three at risk, and one critical.
        
        ## Next actions
        
        * Chase client finance on INV-2041
        * Confirm Riverside pour recovery by Thursday
        * Lock Bay 4 cement alternate
        
        ## Site snapshot
        
        | Site | Status | Focus |
        | --- | --- | --- |
        | Riverside Tower | At risk | Pour recovery |
        | Corridor B | Healthy | Material save |
        | Bay 4 | At risk | Labour + cement |
        | North Gate | Critical | Delay cascade |
        """.trimIndent()
}
