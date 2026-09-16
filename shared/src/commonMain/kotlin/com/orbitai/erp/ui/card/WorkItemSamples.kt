package com.orbitai.erp.ui.card

import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.ui.component.dropdown.WorkStage
import com.orbitai.erp.ui.datetime.orbitToday
import com.orbitai.erp.ui.datetime.toLocalDate
import com.orbitai.erp.ui.datetime.toOrbitCalendarDate
import kotlinx.datetime.LocalDate

fun sampleTaskVilla(): WorkItemRecord {
    val range = sampleRange(days = 7)
    return WorkItemRecord(
        id = "sample-task-villa",
        number = "T-1042",
        kind = WorkItemKind.Task,
        projectType = ProjectType.Villas,
        stage = WorkStage.Structure,
        customStage = null,
        task = "Slab Concreting",
        villa = "Villa 12",
        floor = "1st floor",
        tower = null,
        apartmentUnit = null,
        dateRange = range,
        assigneeIds = setOf("u-se-villas", "u-con-villas"),
        progress = 0.4f,
        progressDelta = 4f,
        severity = null,
        status = WorkStatus.Open,
        materials = listOf(
            OrbitMaterialUsageLine("m-v1", "OPC 53 Grade Cement", 40, "Bags"),
            OrbitMaterialUsageLine("m-v2", "20mm Aggregate", 8, "Cubic Metres"),
        ),
        usedMaterials = listOf(
            OrbitMaterialUsageLine("u-v1", "OPC 53 Grade Cement", 36, "Bags"),
            OrbitMaterialUsageLine("u-v2", "20mm Aggregate", 8, "Cubic Metres"),
            OrbitMaterialUsageLine("u-v3", "Binding Wire", 5, "Kg"),
        ),
        labourUsed = 8,
        description = "Ready for the slab pour after formwork and rebar checks.",
        photos = listOf(
            WorkItemPhoto("p-v1", "slab1.jpg", "1.2 MB"),
            WorkItemPhoto("p-v2", "slab2.jpg", "1.4 MB"),
        ),
        checklistTitle = "Pour checklist",
        checklistItems = listOf(
            OrbitChecklistItem("c-v1", "Formwork inspected", checked = true),
            OrbitChecklistItem("c-v2", "Rebar in place", checked = true),
            OrbitChecklistItem("c-v3", "Concrete poured", checked = true),
        ),
        comments = listOf(
            WorkItemComment("u-v1", "Arun · SE", "Ready for the pour tomorrow.", "Yesterday"),
        ),
        requestedMaterials = listOf(
            WorkItemMaterialRequest(
                id = "req-v1",
                material = "Binding Wire",
                quantity = 8,
                unit = "Kg",
                status = MaterialRequestStatus.Ordered,
            ),
        ),
    )
}

fun sampleTaskApartment(): WorkItemRecord {
    val range = sampleRange(days = 5)
    return WorkItemRecord(
        id = "sample-task-apt",
        number = "T-1188",
        kind = WorkItemKind.Task,
        projectType = ProjectType.ApartmentCommunity,
        stage = WorkStage.UnitInternal,
        customStage = null,
        task = "Block Work",
        villa = null,
        floor = "1st floor",
        tower = "Tower A",
        apartmentUnit = "A-101",
        dateRange = range,
        assigneeIds = setOf("u-se-apt", "u-con-apt"),
        progress = 0.6f,
        progressDelta = 2f,
        severity = null,
        status = WorkStatus.InProgress,
        materials = listOf(
            OrbitMaterialUsageLine("m-a1", "Concrete Blocks", 220, "Nos"),
            OrbitMaterialUsageLine("m-a2", "M-Sand", 4, "Cubic Metres"),
        ),
        usedMaterials = listOf(
            OrbitMaterialUsageLine("u-a1", "Concrete Blocks", 210, "Nos"),
            OrbitMaterialUsageLine("u-a2", "M-Sand", 4, "Cubic Metres"),
            OrbitMaterialUsageLine("u-a3", "Cement Putty", 8, "Kg"),
        ),
        labourUsed = 6,
        description = "First course set on A-101. Openings left for the door frames.",
        photos = listOf(
            WorkItemPhoto("p-a1", "blockwork1.jpg", "1.6 MB"),
            WorkItemPhoto("p-a2", "blockwork2.jpg", "1.3 MB"),
        ),
        checklistTitle = "Block work",
        checklistItems = listOf(
            OrbitChecklistItem("c-a1", "Line and level marked", checked = true),
            OrbitChecklistItem("c-a2", "First course set", checked = true),
            OrbitChecklistItem("c-a3", "Openings framed", checked = true),
        ),
        comments = listOf(
            WorkItemComment("u-a1", "Kavya · CONTR", "First course is done on A-101.", "08:40"),
        ),
        requestedMaterials = listOf(
            WorkItemMaterialRequest(
                id = "req-a1",
                material = "Cement Putty",
                quantity = 12,
                unit = "Kg",
                status = MaterialRequestStatus.Ordered,
            ),
        ),
    )
}

fun sampleIssueVilla(): WorkItemRecord {
    val range = sampleRange(days = 3)
    return WorkItemRecord(
        id = "sample-issue-villa",
        number = "I-2031",
        kind = WorkItemKind.Issue,
        projectType = ProjectType.Villas,
        stage = WorkStage.UnitExternal,
        customStage = null,
        task = "External Plastering",
        villa = "Villa 24",
        floor = "Ground",
        tower = null,
        apartmentUnit = null,
        dateRange = range,
        assigneeIds = setOf("u-se-villas"),
        progress = 0.2f,
        progressDelta = -1f,
        severity = Severity.High,
        status = WorkStatus.Rework,
        materials = listOf(
            OrbitMaterialUsageLine("m-i1", "Cement Putty", 12, "Kg"),
        ),
        usedMaterials = listOf(
            OrbitMaterialUsageLine("u-i1", "Cement Putty", 10, "Kg"),
            OrbitMaterialUsageLine("u-i2", "Binding Wire", 2, "Kg"),
        ),
        labourUsed = 3,
        description = "Hairline crack at the reveal marked and surface opened for rework.",
        reworkNote = "Open the crack and redo the reveal plaster.",
        requestedMaterials = listOf(
            WorkItemMaterialRequest(
                id = "req-i1",
                material = "Cement Putty",
                quantity = 6,
                unit = "Kg",
                status = MaterialRequestStatus.LowStock,
            ),
        ),
        checklistTitle = "Rework checks",
        checklistItems = listOf(
            OrbitChecklistItem("c-i1", "Crack marked", checked = true),
            OrbitChecklistItem("c-i2", "Surface prepared", checked = true),
        ),
        photos = listOf(
            WorkItemPhoto("p-i1", "issueimage1.jpg", "2.4 MB"),
            WorkItemPhoto("p-i2", "issueimage2.jpg", "1.8 MB"),
        ),
        comments = listOf(
            WorkItemComment("u-i1", "Meera · QA/QC", "Hairline crack at the reveal. Rework.", "Mon"),
        ),
    )
}

/** Issue addressed to Villa 12 — pairs with [sampleTaskVilla] on the unit detail log. */
fun sampleIssueVilla12(): WorkItemRecord {
    val range = sampleRange(days = 2)
    return WorkItemRecord(
        id = "sample-issue-villa-12",
        number = "I-2040",
        kind = WorkItemKind.Issue,
        projectType = ProjectType.Villas,
        stage = WorkStage.Structure,
        customStage = null,
        task = "Slab Formwork",
        villa = "Villa 12",
        floor = "1st floor",
        tower = null,
        apartmentUnit = null,
        dateRange = range,
        assigneeIds = setOf("u-se-villas", "u-con-villas"),
        progress = 0.55f,
        progressDelta = 2f,
        severity = Severity.Medium,
        status = WorkStatus.InProgress,
        materials = listOf(
            OrbitMaterialUsageLine("m-v12i1", "Plywood Shutters", 24, "Nos"),
        ),
        usedMaterials = listOf(
            OrbitMaterialUsageLine("u-v12i1", "Plywood Shutters", 20, "Nos"),
            OrbitMaterialUsageLine("u-v12i2", "Binding Wire", 3, "Kg"),
        ),
        labourUsed = 5,
        description = "Formwork bow at mid-span. Props added; awaiting re-check before pour.",
        checklistTitle = "Formwork checks",
        checklistItems = listOf(
            OrbitChecklistItem("c-v12i1", "Props set", checked = true),
            OrbitChecklistItem("c-v12i2", "Level re-checked", checked = false),
        ),
        photos = listOf(
            WorkItemPhoto("p-v12i1", "formwork1.jpg", "1.9 MB"),
        ),
        comments = listOf(
            WorkItemComment("u-v12i1", "Arun · SE", "Props are in. Recheck tomorrow.", "Today"),
        ),
    )
}

fun sampleOrderVilla(): WorkItemRecord {
    val range = sampleRange(days = 4)
    return WorkItemRecord(
        id = "sample-order-villa",
        number = "PO-1234",
        kind = WorkItemKind.PurchaseOrder,
        projectType = ProjectType.Villas,
        stage = null,
        customStage = null,
        task = "OPC 53 Grade Cement",
        villa = null,
        floor = null,
        tower = null,
        apartmentUnit = null,
        projectName = MockDirectory.VillasProjectName,
        dateRange = range,
        assigneeIds = setOf("u-proc", "u-wh-villas"),
        progress = 0.15f,
        progressDelta = 4f,
        severity = null,
        status = WorkStatus.Open,
        materials = listOf(
            OrbitMaterialUsageLine("m-po-v1", "OPC 53 Grade Cement", 40, "Bags"),
        ),
        description = "Cement for the Prestige Golfshire Villas slab pour.",
        photos = listOf(
            WorkItemPhoto("p-po-v1", "order1.jpg", "1.1 MB"),
            WorkItemPhoto("p-po-v2", "order2.jpg", "1.3 MB"),
        ),
    )
}

fun sampleOrderApartment(): WorkItemRecord {
    val range = sampleRange(days = 6)
    return WorkItemRecord(
        id = "sample-order-apt",
        number = "PO-1450",
        kind = WorkItemKind.PurchaseOrder,
        projectType = ProjectType.ApartmentCommunity,
        stage = null,
        customStage = null,
        task = "Concrete Blocks",
        villa = null,
        floor = null,
        tower = null,
        apartmentUnit = null,
        projectName = MockDirectory.ApartmentProjectName,
        dateRange = range,
        assigneeIds = setOf("u-proc", "u-wh-apt"),
        progress = 0.5f,
        progressDelta = 3f,
        severity = null,
        status = WorkStatus.InProgress,
        materials = listOf(
            OrbitMaterialUsageLine("m-po-a1", "Concrete Blocks", 220, "Nos"),
        ),
        description = "Blocks for Prestige Lakeside Habitat first-course walls.",
        photos = listOf(
            WorkItemPhoto("p-po-a1", "order1.jpg", "1.5 MB"),
        ),
    )
}

fun sampleIssueApartment(): WorkItemRecord {
    val range = sampleRange(days = 10)
    return WorkItemRecord(
        id = "sample-issue-apt",
        number = "I-2210",
        kind = WorkItemKind.Issue,
        projectType = ProjectType.ApartmentCommunity,
        stage = WorkStage.CommonArea,
        customStage = null,
        task = "Lobby Area Plastering",
        villa = null,
        floor = "2nd floor",
        tower = "Tower B",
        apartmentUnit = "B-201",
        dateRange = range,
        assigneeIds = setOf("u-se-apt", "u-con-apt"),
        progress = 0.75f,
        progressDelta = 6f,
        severity = Severity.Medium,
        status = WorkStatus.Inspection,
        materials = listOf(
            OrbitMaterialUsageLine("m-l1", "Interior Emulsion", 18, "Kg"),
        ),
        usedMaterials = listOf(
            OrbitMaterialUsageLine("u-l1", "Interior Emulsion", 18, "Kg"),
            OrbitMaterialUsageLine("u-l2", "Primer", 4, "Kg"),
        ),
        labourUsed = 4,
        description = "Patching complete. Primer on. Waiting on the final coat after curing.",
        checklistTitle = "Lobby finish",
        checklistItems = listOf(
            OrbitChecklistItem("c-l1", "Patching complete", checked = true),
            OrbitChecklistItem("c-l2", "Primer applied", checked = true),
            OrbitChecklistItem("c-l3", "Final coat", checked = true),
        ),
        photos = listOf(
            WorkItemPhoto("p-l1", "issueimage1.jpg", "3.1 MB"),
            WorkItemPhoto("p-l2", "issueimage2.jpg", "2.2 MB"),
        ),
        comments = listOf(
            WorkItemComment("u-l1", "Arun · SE", "Ready for inspection after curing.", "Tue"),
        ),
        requestedMaterials = listOf(
            WorkItemMaterialRequest(
                id = "req-l1",
                material = "Primer",
                quantity = 4,
                unit = "Kg",
                status = MaterialRequestStatus.Ordered,
            ),
        ),
    )
}

/** Issue addressed to A-101 — pairs with [sampleTaskApartment] on the unit detail log. */
fun sampleIssueApartment101(): WorkItemRecord {
    val range = sampleRange(days = 4)
    return WorkItemRecord(
        id = "sample-issue-apt-101",
        number = "I-2218",
        kind = WorkItemKind.Issue,
        projectType = ProjectType.ApartmentCommunity,
        stage = WorkStage.UnitInternal,
        customStage = null,
        task = "Door Frame Fixing",
        villa = null,
        floor = "1st floor",
        tower = "Tower A",
        apartmentUnit = "A-101",
        dateRange = range,
        assigneeIds = setOf("u-se-apt"),
        progress = 0.3f,
        progressDelta = -1f,
        severity = Severity.High,
        status = WorkStatus.Blocked,
        materials = listOf(
            OrbitMaterialUsageLine("m-a101i1", "Timber Frames", 4, "Nos"),
        ),
        usedMaterials = listOf(
            OrbitMaterialUsageLine("u-a101i1", "Timber Frames", 2, "Nos"),
            OrbitMaterialUsageLine("u-a101i2", "Cement Putty", 3, "Kg"),
        ),
        labourUsed = 2,
        description = "Frame out of plumb at the kitchen opening. Hold block work until reset.",
        checklistTitle = "Frame checks",
        checklistItems = listOf(
            OrbitChecklistItem("c-a101i1", "Opening measured", checked = true),
            OrbitChecklistItem("c-a101i2", "Frame reset", checked = false),
        ),
        photos = listOf(
            WorkItemPhoto("p-a101i1", "frame1.jpg", "1.7 MB"),
        ),
        comments = listOf(
            WorkItemComment("u-a101i1", "Kavya · CONTR", "Waiting on a replacement frame.", "Yesterday"),
        ),
    )
}

private fun sampleRange(days: Int): OrbitDateRange {
    val start = orbitToday()
    val end: OrbitCalendarDate = LocalDate
        .fromEpochDays(start.toLocalDate().toEpochDays() + days)
        .toOrbitCalendarDate()
    return if (start <= end) OrbitDateRange(start, end) else OrbitDateRange(end, start)
}
