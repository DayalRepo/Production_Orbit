package com.orbitai.erp.ui.form

/**
 * Mock location catalogues for the two demo projects.
 *
 * Villa work is addressed as a villa plus a floor. Apartment / community work is addressed as a
 * tower, a floor, and a unit. Units are filtered by tower so the last dropdown never offers an
 * address that cannot exist on the first.
 */
object SiteLocations {

    val villas = listOf(
        "Villa 12",
        "Villa 18",
        "Villa 24",
        "Villa 31",
        "Villa 42",
    )

    val floors = listOf(
        "Ground",
        "1st floor",
        "2nd floor",
        "3rd floor",
        "Terrace",
    )

    val towers = listOf(
        "Tower A",
        "Tower B",
        "Tower C",
    )

    fun unitsFor(tower: String): List<String> = when (tower) {
        "Tower A" -> listOf("A-101", "A-102", "A-201", "A-202", "A-301")
        "Tower B" -> listOf("B-101", "B-102", "B-201", "B-202", "B-301")
        "Tower C" -> listOf("C-101", "C-102", "C-201", "C-202", "C-301")
        else -> emptyList()
    }
}
