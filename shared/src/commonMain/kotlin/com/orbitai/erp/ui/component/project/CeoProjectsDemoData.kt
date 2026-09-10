package com.orbitai.erp.ui.component.project

/**
 * Demo CEO project cards until live project APIs are wired.
 */
object CeoProjectsDemoData {
    val Projects: List<CeoProjectCardModel> = listOf(
        CeoProjectCardModel(
            id = "proj-skyline",
            type = CeoProjectType.Apartment,
            name = "Skyline Residences",
            place = "Electronic City",
            city = "Bengaluru",
            state = "Karnataka",
            country = "India",
            bannerUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=1200&q=80",
            progressPercent = 62,
            issuesCount = 0,
            units = 216,
            towers = 3,
            floors = 18,
            acresLabel = "2.4 acres",
            startLabel = "Aug 2025",
            targetLabel = "Mar 2027",
            expectedDelayLabel = "On schedule",
        ),
        CeoProjectCardModel(
            id = "proj-palm",
            type = CeoProjectType.Villa,
            name = "Palm Grove Villas",
            place = "Whitefield",
            city = "Bengaluru",
            state = "Karnataka",
            country = "India",
            bannerUrl = "https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=1200&q=80",
            progressPercent = 33,
            issuesCount = 1,
            units = 24,
            towers = null,
            floors = 2,
            acresLabel = "4.5 acres",
            startLabel = "Jan 2025",
            targetLabel = "Dec 2026",
            expectedDelayLabel = "2 months",
        ),
        CeoProjectCardModel(
            id = "proj-harbor",
            type = CeoProjectType.Apartment,
            name = "Harbor Heights",
            place = "Marine Drive",
            city = "Mumbai",
            state = "Maharashtra",
            country = "India",
            bannerUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1200&q=80",
            progressPercent = 78,
            issuesCount = 2,
            units = 340,
            towers = 4,
            floors = 22,
            acresLabel = "3.1 acres",
            startLabel = "Mar 2024",
            targetLabel = "Nov 2026",
            expectedDelayLabel = "On schedule",
        ),
        CeoProjectCardModel(
            id = "proj-lake",
            type = CeoProjectType.Villa,
            name = "Lakeview Estates",
            place = "Sarjapur",
            city = "Bengaluru",
            state = "Karnataka",
            country = "India",
            bannerUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=80",
            progressPercent = 51,
            issuesCount = 0,
            units = 18,
            towers = null,
            floors = 2,
            acresLabel = "6.2 acres",
            startLabel = "Jun 2025",
            targetLabel = "Aug 2027",
            expectedDelayLabel = "1 month",
        ),
    )

    fun projectById(id: String): CeoProjectCardModel? = Projects.find { it.id == id }
}

enum class CeoProjectType {
    Villa,
    Apartment,
}

data class CeoProjectCardModel(
    val id: String,
    val type: CeoProjectType,
    val name: String,
    val place: String,
    val city: String,
    val state: String,
    val country: String,
    val bannerUrl: String,
    val progressPercent: Int,
    val issuesCount: Int,
    val units: Int,
    val towers: Int?,
    val floors: Int,
    val acresLabel: String,
    val startLabel: String,
    val targetLabel: String,
    val expectedDelayLabel: String,
) {
    val addressLine: String
        get() = listOf(place, city, state, country).joinToString(", ")

    val issuesLabel: String
        get() = when (issuesCount) {
            0 -> "No issues"
            1 -> "1 issue"
            else -> "$issuesCount issues"
        }

    val issuesHealthy: Boolean
        get() = issuesCount == 0

    val isOnSchedule: Boolean
        get() = expectedDelayLabel.equals("On schedule", ignoreCase = true)
}
