package com.orbitai.erp.ui.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.orbitMaterialUsageLineComplete
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.ui.component.dropdown.WorkStage

/**
 * Shared draft for create-task and raise-issue. Hoisted so page changes do not drop answers.
 */
class WorkItemDraft {
    var stage by mutableStateOf<WorkStage?>(null)
    var customStage by mutableStateOf<String?>(null)
    var extraStages by mutableStateOf(emptyList<String>())
    var task by mutableStateOf<String?>(null)
    var extraTasks by mutableStateOf(emptyList<String>())
    val materialLines = mutableStateListOf(OrbitMaterialUsageLine(id = "m0"))
    var extraMaterials by mutableStateOf(emptyList<String>())
    var extraUnits by mutableStateOf(emptyList<String>())
    var checklistTitle by mutableStateOf("")
    val checklistItems = mutableStateListOf<OrbitChecklistItem>()
    var checklistDraft by mutableStateOf("")
    var villa by mutableStateOf<String?>(null)
    var floor by mutableStateOf<String?>(null)
    var tower by mutableStateOf<String?>(null)
    var apartmentUnit by mutableStateOf<String?>(null)
    var dateRange by mutableStateOf<OrbitDateRange?>(null)
    var siteEngineerIds by mutableStateOf(setOf<String>())
    var contractorIds by mutableStateOf(setOf<String>())
}

data class DraftPhoto(
    val id: String,
    val fileName: String,
    val fileSize: String,
)

/** Issue draft: the shared work item plus severity, description, and photos. */
class RaiseIssueDraft {
    val work = WorkItemDraft()
    var severity by mutableStateOf<Severity?>(null)
    var description by mutableStateOf("")
    val photos = mutableStateListOf<DraftPhoto>()
}

class CreateTaskDraft {
    val work = WorkItemDraft()
}

/** Draft for a materials purchase order. */
class MaterialsOrderDraft {
    val materialLines = mutableStateListOf(OrbitMaterialUsageLine(id = "m0"))
    var extraMaterials by mutableStateOf(emptyList<String>())
    var extraUnits by mutableStateOf(emptyList<String>())
    var projectName by mutableStateOf<String?>(null)
    var dateRange by mutableStateOf<OrbitDateRange?>(null)
    var procurementIds by mutableStateOf(setOf<String>())
    var warehouseIds by mutableStateOf(setOf<String>())
}

fun MaterialsOrderDraft.replaceMaterialLine(
    id: String,
    transform: (OrbitMaterialUsageLine) -> OrbitMaterialUsageLine,
) {
    val index = materialLines.indexOfFirst { it.id == id }
    if (index >= 0) materialLines[index] = transform(materialLines[index])
}

fun MaterialsOrderDraft.isReadyToConfirm(): Boolean =
    materialLines.any(::orbitMaterialUsageLineComplete) &&
        !projectName.isNullOrBlank() &&
        dateRange != null &&
        (procurementIds.isNotEmpty() || warehouseIds.isNotEmpty())

fun MaterialsOrderDraft.completeMaterialLines(): List<OrbitMaterialUsageLine> =
    materialLines.filter(::orbitMaterialUsageLineComplete).take(1)

internal fun WorkItemDraft.replaceMaterialLine(
    id: String,
    transform: (OrbitMaterialUsageLine) -> OrbitMaterialUsageLine,
) {
    val index = materialLines.indexOfFirst { it.id == id }
    if (index >= 0) materialLines[index] = transform(materialLines[index])
}
