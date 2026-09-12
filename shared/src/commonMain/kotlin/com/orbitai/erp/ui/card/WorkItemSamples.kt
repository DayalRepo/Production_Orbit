package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
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
        floor = "First",
        tower = null,
        apartmentUnit = null,
        dateRange = range,
        assigneeIds = setOf("u-se-villas", "u-con-villas"),
        progress = 0.4f,
        progressDelta = 4f,
        severity = null,
        status = WorkStatus.Open,
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
        floor = "First",
        tower = "Tower A",
        apartmentUnit = "A-101",
        dateRange = range,
        assigneeIds = setOf("u-se-apt", "u-con-apt"),
        progress = 0.6f,
        progressDelta = 2f,
        severity = null,
        status = WorkStatus.InProgress,
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
        floor = "Second",
        tower = "Tower B",
        apartmentUnit = "B-201",
        dateRange = range,
        assigneeIds = setOf("u-se-apt", "u-con-apt"),
        progress = 0.75f,
        progressDelta = 6f,
        severity = Severity.Medium,
        status = WorkStatus.Inspection,
    )
}

private fun sampleRange(days: Int): OrbitDateRange {
    val start = orbitToday()
    val end: OrbitCalendarDate = LocalDate
        .fromEpochDays(start.toLocalDate().toEpochDays() + days)
        .toOrbitCalendarDate()
    return if (start <= end) OrbitDateRange(start, end) else OrbitDateRange(end, start)
}
