package com.orbitai.erp.ui.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
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
    var material by mutableStateOf<String?>(null)
    var quantity by mutableIntStateOf(1)
    var materialUnit by mutableStateOf<String?>(null)
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

/** Issue draft: the shared work item plus a severity. Photos stay on the photos page. */
class RaiseIssueDraft {
    val work = WorkItemDraft()
    var severity by mutableStateOf<Severity?>(null)
}

class CreateTaskDraft {
    val work = WorkItemDraft()
}
