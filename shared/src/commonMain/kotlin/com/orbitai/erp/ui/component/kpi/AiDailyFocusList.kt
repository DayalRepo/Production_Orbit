package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklist
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem

/**
 * AI-built daily task checklist — [OrbitChecklist] only (no outer subtitle chrome).
 */
@Composable
fun AiDailyFocusList(
    title: String,
    initialItems: List<OrbitChecklistItem>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true,
) {
    val items = remember(initialItems) { initialItems.toMutableStateList() }
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    OrbitChecklist(
        title = title,
        items = items.toList(),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        showRemainingBesideBar = false,
        onCheckedChange = { id, checked ->
            val index = items.indexOfFirst { it.id == id }
            if (index >= 0) {
                items[index] = items[index].copy(checked = checked)
            }
        },
        modifier = modifier.fillMaxWidth(),
    )
}
