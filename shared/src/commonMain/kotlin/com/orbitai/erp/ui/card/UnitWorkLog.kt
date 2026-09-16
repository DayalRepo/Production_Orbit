package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.orbitMaterialUsageLineComplete
import com.orbitai.erp.core.model.ProjectType

/**
 * Whether this work item is addressed to [unit] (tasks and issues only; orders have no unit fields).
 */
fun WorkItemRecord.belongsToUnit(unit: UnitRecord): Boolean {
    if (kind == WorkItemKind.PurchaseOrder) return false
    if (projectType != unit.projectType) return false
    return when (unit.projectType) {
        ProjectType.Villas -> {
            villa?.takeIf { it.isNotBlank() } == unit.numberLabel
        }
        ProjectType.ApartmentCommunity -> {
            val unitMatch = apartmentUnit?.takeIf { it.isNotBlank() } == unit.numberLabel
            val towerMatch = unit.tower.isNullOrBlank() ||
                tower?.takeIf { it.isNotBlank() } == unit.tower
            unitMatch && towerMatch
        }
    }
}

/** Tasks and issues for [unit], newest-looking first by number then id. */
fun List<WorkItemRecord>.forUnit(unit: UnitRecord): List<WorkItemRecord> =
    filter { it.belongsToUnit(unit) }
        .sortedWith(compareByDescending<WorkItemRecord> { it.number }.thenByDescending { it.id })

fun List<WorkItemRecord>.unitTasks(unit: UnitRecord): List<WorkItemRecord> =
    forUnit(unit).filter { it.kind == WorkItemKind.Task }

fun List<WorkItemRecord>.unitIssues(unit: UnitRecord): List<WorkItemRecord> =
    forUnit(unit).filter { it.kind == WorkItemKind.Issue }

/**
 * Aggregated used materials for [unit]. Prefers each item's [WorkItemRecord.usedMaterials];
 * falls back to issued [WorkItemRecord.materials] when the usage log is empty.
 * Same material + unit collapses into one row with summed quantity.
 */
fun List<WorkItemRecord>.unitUsedMaterials(unit: UnitRecord): List<OrbitMaterialUsageLine> {
    val lines = forUnit(unit).flatMap { item ->
        val used = item.usedMaterials.filter(::orbitMaterialUsageLineComplete)
        if (used.isNotEmpty()) used else item.materials.filter(::orbitMaterialUsageLineComplete)
    }
    if (lines.isEmpty()) return emptyList()

    val merged = linkedMapOf<String, OrbitMaterialUsageLine>()
    lines.forEach { line ->
        val material = line.material.orEmpty().trim()
        val unitLabel = line.unit.orEmpty().trim()
        val key = "${material.lowercase()}\u0000${unitLabel.lowercase()}"
        val existing = merged[key]
        merged[key] = if (existing == null) {
            OrbitMaterialUsageLine(
                id = "agg-$key",
                material = material,
                quantity = line.quantity,
                unit = unitLabel,
            )
        } else {
            existing.copy(quantity = existing.quantity + line.quantity)
        }
    }
    return merged.values.toList()
}

/** Location subtitle under the unit title (apartment tower · floor; blank for villas). */
val UnitRecord.locationSubtitle: String
    get() = when (projectType) {
        ProjectType.Villas -> listOfNotNull(floor?.takeIf { it.isNotBlank() })
            .joinToString(" · ")
        ProjectType.ApartmentCommunity -> listOfNotNull(
            tower?.takeIf { it.isNotBlank() },
            floor?.takeIf { it.isNotBlank() },
        ).joinToString(" · ")
    }

/**
 * Gallery / preview catalogue of work items that can be filtered onto a unit detail log.
 * Includes dedicated Villa 12 and A-101 issues so both demo units have a real log.
 */
fun unitWorkLogSamples(): List<WorkItemRecord> = listOf(
    sampleTaskVilla(),
    sampleIssueVilla12(),
    sampleTaskApartment(),
    sampleIssueApartment101(),
    sampleIssueVilla(),
    sampleIssueApartment(),
)
