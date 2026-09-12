package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistEditor
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Checklist editor without its own Cancel / Create — the form footer owns those.
 */
@Composable
fun ChecklistPage(
    title: String,
    onTitleChange: (String) -> Unit,
    items: List<OrbitChecklistItem>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        OrbitChecklistEditor(
            title = title,
            onTitleChange = onTitleChange,
            items = items,
            draft = draft,
            onDraftChange = onDraftChange,
            onAddItem = onAddItem,
            onRemoveItem = onRemoveItem,
            onCreate = {},
            onCancel = {},
            modifier = Modifier.fillMaxWidth(),
            showActions = false,
        )
    }
}
