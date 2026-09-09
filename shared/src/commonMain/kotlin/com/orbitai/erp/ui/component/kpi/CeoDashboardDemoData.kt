package com.orbitai.erp.ui.component.kpi

/**
 * Demo CEO dashboard figures until live analytics / AI endpoints are wired.
 */
object CeoDashboardDemoData {
    const val HealthyProjects = 8
    const val AtRiskProjects = 3
    const val CriticalProjects = 1

    const val HealthProgress = 0.82f
    const val HealthProgressDelta = 2.4f

    /** Expected progress vs delay → on-time confidence. */
    const val ConfidencePercent = 100
    const val ConfidenceTrendDelta = 2f

    /** Bills / issues waiting on the PM past the threshold. */
    const val DecisionCount = 4
    const val DecisionTrendDelta = -1f

    /** Open AI tips (waste cut, buy-ahead to avoid delay, etc.). */
    const val AiAdviceCount = 3
    const val AiAdviceTrendDelta = 1f

    val ConfidenceDetailLines: List<String> = listOf(
        "8 of 12 sites on or ahead of schedule",
        "Riverside pour recovery still open",
        "Labour coverage at Bay 4 is the main drag",
    )

    val DecisionDetailLines: List<String> = listOf(
        "INV-2041 awaiting client finance sign-off",
        "Bay 4 cement alternate needs PM lock",
        "Corridor B night-shift redeploy pending",
        "North Gate delay cascade needs owner",
    )

    val AiAdviceDetailLines: List<String> = listOf(
        "Extend Corridor B cement swap to Bay 4",
        "Buy-ahead steel for East Link this week",
        "Cut waste on Plaza A formwork by 6%",
    )

    const val MaterialsSavingsLabel = "₹6.2L"
    const val MaterialsSavingsSupporting = "Material optimisation · MTD"
    /** MTD savings change vs prior month (percentage points for OrbitDelta). */
    const val MaterialsSavingsDelta = 18f
    const val MaterialsSavingsDeltaLabel = "vs last month"
    /** One-line AI optimisation win under the chart. */
    const val MaterialsAiWin = "Corridor B cement swap · ₹1.1L"
    /** Month pace toward materials-savings target. */
    const val MaterialsMonthProgress = 0.68f
    const val MaterialsCtaLabel = "View materials"

    /**
     * Weekly savings index (₹L-scaled demo points) — climbs into Now, then mild forecast ease.
     * Values are chart indices; tooltip formats them as ₹x.xL.
     */
    val SavingsTrendHistory: List<Float> = listOf(
        2.1f, 2.4f, 2.2f, 2.6f, 3.1f, 3.5f, 4.0f, 4.6f, 5.1f, 5.5f, 5.9f, 6.2f,
    )

    val SavingsTrendForecast: List<Float> = listOf(6.0f, 5.7f, 5.4f, 5.2f)

    /** Materials spend index on the same weeks (aligned X with savings). */
    val MaterialsTrendHistory: List<Float> = listOf(
        4.8f, 4.6f, 5.0f, 4.4f, 4.2f, 4.0f, 4.5f, 4.9f, 4.7f, 5.1f, 5.4f, 5.8f,
    )

    val MaterialsTrendForecast: List<Float> = listOf(5.9f, 6.0f, 5.8f, 5.6f)

    val MaterialsTrendXLabels: List<String> = listOf("W1", "W4", "W8", "Now", "+2w", "+4w")

    const val InvoicesOverdueLabel = "₹18.4L"
    const val InvoicesPendingCount = 12
    const val InvoicesAiFlaggedCount = 3
    const val InvoicesTopRiskClient = "Riverside"
    const val InvoicesTopRiskId = "INV-2041"
    const val InvoicesTopRiskAmount = "₹3.8L"
    const val InvoicesTopRiskNote = "highest at risk"

    /** Overdue ₹L mix by aging bucket (weights for the proportion strip). */
    const val InvoicesAging0to30 = 5.2f
    const val InvoicesAging31to60 = 7.1f
    const val InvoicesAging60Plus = 6.1f

    /** Weekly overdue ₹L for the last 8 weeks (tiny sparkline). */
    val InvoicesOverdueTrend: List<Float> = listOf(
        12.4f, 13.1f, 14.0f, 13.6f, 15.2f, 16.8f, 17.5f, 18.4f,
    )

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
