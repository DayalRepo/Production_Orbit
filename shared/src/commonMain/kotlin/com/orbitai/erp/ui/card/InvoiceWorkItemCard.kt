package com.orbitai.erp.ui.card

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.core.model.UserRole

/**
 * Full [WorkItemCard] for invoice attach pickers and attached-work lists.
 * When [onSelect] is set, the card toggles selection (no action buttons — avoids nested clicks).
 */
@Composable
fun InvoiceWorkItemCard(
    record: WorkItemRecord,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onSelect: (() -> Unit)? = null,
    onView: () -> Unit = {},
) {
    val kindLabel = if (record.kind == WorkItemKind.Issue) "issue" else "task"
    val a11y = buildString {
        append(kindLabel)
        append(' ')
        append(record.numberLabel)
        if (selected) append(", selected")
    }
    val borderColor = when {
        selected -> OrbitTheme.controlColors.actionContainer
        else -> OrbitTheme.controlColors.controlBorder.copy(alpha = 0f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(OrbitTheme.shapeTokens.card)
            .border(
                width = if (selected) OrbitTheme.sizing.borderFocus else OrbitTheme.sizing.hairline,
                color = borderColor,
                shape = OrbitTheme.shapeTokens.card,
            )
            .then(
                if (onSelect != null) {
                    Modifier
                        .semantics {
                            contentDescription = a11y
                            role = Role.Checkbox
                            this.selected = selected
                        }
                        .clickable(onClick = onSelect)
                } else {
                    Modifier
                },
            ),
    ) {
        WorkItemCard(
            record = record,
            viewerRole = UserRole.SiteEngineer,
            onUpdate = onView,
            onEdit = {},
            onDelete = {},
            onView = onView,
            onStart = {},
            showActions = onSelect == null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
