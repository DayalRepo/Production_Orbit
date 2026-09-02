package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklist
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistEditor
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.component.display.orbitChecklistCanCreate
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.random.Random

/**
 * Checklist creation editor and interactive checklist with strikethrough + progress.
 */
@Composable
internal fun ChecklistGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    val editorItems = remember { mutableStateListOf<OrbitChecklistItem>() }
    var title by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf("") }

    var createdTitle by remember { mutableStateOf<String?>(null) }
    val createdItems = remember { mutableStateListOf<OrbitChecklistItem>() }
    var createdExpanded by remember { mutableStateOf(true) }

    val liveItems = remember {
        mutableStateListOf(
            OrbitChecklistItem("1", "Start new data repository", checked = true),
            OrbitChecklistItem("2", "Define column structure", checked = true),
            OrbitChecklistItem("3", "Create a tab", checked = false),
            OrbitChecklistItem("4", "Edit a filter", checked = false),
            OrbitChecklistItem("5", "Share with the team", checked = true),
            OrbitChecklistItem("6", "Watch the overview", checked = true),
        )
    }

    GallerySection("Checklist · create items") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Text(
                text = "Add a title and items, then Create. The editor stays open so you can add more.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            OrbitChecklistEditor(
                title = title,
                onTitleChange = { title = it },
                items = editorItems.toList(),
                draft = draft,
                onDraftChange = { draft = it },
                onAddItem = {
                    val label = draft.trim()
                    if (label.isNotEmpty()) {
                        editorItems += OrbitChecklistItem(
                            id = "n${Random.nextLong()}",
                            label = label,
                        )
                        draft = ""
                    }
                },
                onRemoveItem = { id -> editorItems.removeAll { it.id == id } },
                onCreate = {
                    if (!orbitChecklistCanCreate(title, editorItems)) return@OrbitChecklistEditor
                    createdTitle = title.trim()
                    createdItems.clear()
                    createdItems.addAll(editorItems.map { it.copy(checked = false) })
                    createdExpanded = true
                    title = ""
                    draft = ""
                    editorItems.clear()
                },
                onCancel = {
                    title = ""
                    draft = ""
                    editorItems.clear()
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (createdTitle != null) {
                OrbitChecklist(
                    title = createdTitle.orEmpty(),
                    items = createdItems.toList(),
                    expanded = createdExpanded,
                    onExpandedChange = { createdExpanded = it },
                    onCheckedChange = { id, checked ->
                        val index = createdItems.indexOfFirst { it.id == id }
                        if (index >= 0) {
                            createdItems[index] = createdItems[index].copy(checked = checked)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    GallerySection("Checklist · interactive") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Text(
                text = "Tap a row to check — strikethrough + progress update.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            OrbitChecklist(
                title = "Getting started checklist",
                items = liveItems.toList(),
                onCheckedChange = { id, checked ->
                    val index = liveItems.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        liveItems[index] = liveItems[index].copy(checked = checked)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
