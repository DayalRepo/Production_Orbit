package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitCreateDialog
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.ui.component.dropdown.WorkSequence
import com.orbitai.erp.ui.component.dropdown.WorkStage
import com.orbitai.erp.ui.form.FormFieldLabel

/**
 * Stage dropdown, then a task dropdown filtered by the chosen stage.
 *
 * Both lists can grow from the pinned add row, matching materials. Optional [severity] is
 * used on raise-issue.
 */
@Composable
fun StageTaskPage(
    projectType: ProjectType,
    stage: WorkStage?,
    onStageSelect: (WorkStage) -> Unit,
    task: String?,
    onTaskSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    customStage: String? = null,
    extraStages: List<String> = emptyList(),
    onCustomStageSelect: (String) -> Unit = {},
    onStageCreated: (String) -> Unit = {},
    extraTasks: List<String> = emptyList(),
    onTaskCreated: (String) -> Unit = {},
    severity: Severity? = null,
    onSeveritySelect: ((Severity) -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val catalogStages = remember(projectType) { WorkSequence.stagesFor(projectType) }
    val catalogLabels = remember(catalogStages) { catalogStages.map { it.optionLabel } }
    val stageOptions = remember(catalogLabels, extraStages) { catalogLabels + extraStages }
    val selectedStageLabel = customStage ?: stage?.optionLabel
    val catalogTasks = remember(projectType, stage, customStage) {
        if (customStage != null) emptyList() else {
            stage?.let { WorkSequence.tasksFor(projectType, it) }.orEmpty()
        }
    }
    val tasks = remember(catalogTasks, extraTasks) { catalogTasks + extraTasks }
    val stageReady = selectedStageLabel != null

    var creatingStage by remember { mutableStateOf(false) }
    var creatingTask by remember { mutableStateOf(false) }
    var duplicateStage by remember { mutableStateOf(false) }
    var duplicateTask by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Stage")
            OrbitDropdownField(
                selected = selectedStageLabel,
                options = stageOptions,
                onSelect = { label ->
                    val catalog = catalogStages.firstOrNull { it.optionLabel == label }
                    if (catalog != null) onStageSelect(catalog) else onCustomStageSelect(label)
                },
                label = "Stage",
                placeholder = "Select a stage",
                searchPlaceholder = "Search stages",
                addLabel = "Add stage",
                onAddRequest = {
                    duplicateStage = false
                    creatingStage = true
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Task")
            OrbitDropdownField(
                selected = task,
                options = tasks,
                onSelect = onTaskSelect,
                label = "Task",
                placeholder = if (!stageReady) {
                    "Select a stage first"
                } else {
                    "Select a task"
                },
                searchPlaceholder = "Search tasks",
                enabled = stageReady,
                addLabel = "Add task",
                onAddRequest = {
                    duplicateTask = false
                    creatingTask = true
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (onSeveritySelect != null) {
            SeverityPicker(
                selected = severity,
                onSelect = onSeveritySelect,
            )
        }
    }

    if (creatingStage) {
        OrbitCreateDialog(
            title = "Add stage",
            info = "Shared across the project. Name it as the work sequence does.",
            label = "Stage name",
            placeholder = "e.g. Waterproofing",
            state = if (duplicateStage) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = stageOptions.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    val catalog = catalogStages.firstOrNull { it.optionLabel.equals(existing, ignoreCase = true) }
                    if (catalog != null) onStageSelect(catalog) else onCustomStageSelect(existing)
                    duplicateStage = true
                    creatingStage = false
                } else {
                    onStageCreated(name)
                    creatingStage = false
                }
            },
            onDismiss = { creatingStage = false },
        )
    }

    if (creatingTask) {
        OrbitCreateDialog(
            title = "Add task",
            info = "Name it as the site uses it on the work sequence.",
            label = "Task name",
            placeholder = "e.g. Slab Concreting",
            state = if (duplicateTask) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = tasks.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    onTaskSelect(existing)
                    duplicateTask = true
                    creatingTask = false
                } else {
                    onTaskCreated(name)
                    creatingTask = false
                }
            },
            onDismiss = { creatingTask = false },
        )
    }
}
