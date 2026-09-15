package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofStep
import com.orbitai.erp.core.model.ProjectHealth
import com.orbitai.erp.core.model.ProjectType

fun sampleUnitVilla(): UnitRecord = UnitRecord(
    id = "sample-unit-villa",
    numberLabel = "Villa 12",
    projectType = ProjectType.Villas,
    health = ProjectHealth.OnTrack,
    plannedMonths = 14,
    delayMonths = 0,
    issueCount = 0,
    assigneeIds = setOf("u-se-villas", "u-con-villas"),
    stages = listOf(
        OrbitStageProofStep("SR", "Structure", "01/08/2026", "20/08/2026"),
        OrbitStageProofStep("UI", "Unit internal", "21/08/2026", "05/09/2026"),
        OrbitStageProofStep("UE", "Unit external", "06/09/2026", "18/09/2026"),
        OrbitStageProofStep("ED", "Ext. development", "19/09/2026", null),
    ),
    completedStages = 3,
    progress = 0.72f,
    progressDelta = 5f,
)

fun sampleUnitApartment(): UnitRecord = UnitRecord(
    id = "sample-unit-apt",
    numberLabel = "A-101",
    projectType = ProjectType.ApartmentCommunity,
    health = ProjectHealth.AtRisk,
    plannedMonths = 18,
    delayMonths = 5,
    issueCount = 10,
    assigneeIds = setOf("u-se-apt", "u-con-apt"),
    tower = "Tower A",
    floor = "1st floor",
    stages = listOf(
        OrbitStageProofStep("SR", "Structure", "01/07/2026", "15/07/2026"),
        OrbitStageProofStep("CA", "Common area", "16/07/2026", "30/07/2026"),
        OrbitStageProofStep("UI", "Unit internal", "01/08/2026", "20/08/2026"),
        OrbitStageProofStep("UE", "Unit external", "21/08/2026", "10/09/2026"),
        OrbitStageProofStep("ED", "Ext. development", "11/09/2026", null),
        OrbitStageProofStep("BS", "Basement"),
    ),
    completedStages = 4,
    progress = 0.58f,
    progressDelta = -2f,
)

fun sampleUnitVillaDelayed(): UnitRecord = UnitRecord(
    id = "sample-unit-villa-delayed",
    numberLabel = "Villa 24",
    projectType = ProjectType.Villas,
    health = ProjectHealth.Delayed,
    plannedMonths = 12,
    delayMonths = 3,
    issueCount = 4,
    assigneeIds = setOf("u-se-villas"),
    stages = listOf(
        OrbitStageProofStep("SR", "Structure", "01/06/2026", "30/06/2026"),
        OrbitStageProofStep("UI", "Unit internal", "01/07/2026", null),
        OrbitStageProofStep("UE", "Unit external"),
        OrbitStageProofStep("ED", "Ext. development"),
    ),
    completedStages = 1,
    progress = 0.34f,
    progressDelta = -3f,
)
