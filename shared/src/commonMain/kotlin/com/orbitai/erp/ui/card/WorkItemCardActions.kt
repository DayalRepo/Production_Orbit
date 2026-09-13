package com.orbitai.erp.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.overlay.OrbitBubblePopover
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind

internal val WorkItemCardRoles = listOf(
    UserRole.ProjectManager,
    UserRole.SiteEngineer,
    UserRole.Contractor,
    UserRole.QaQc,
)

internal val PurchaseOrderCardRoles = listOf(
    UserRole.ProjectManager,
    UserRole.ProcurementManager,
    UserRole.WarehouseManager,
)

internal fun createdCardRoles(kind: WorkItemKind): List<UserRole> =
    if (kind == WorkItemKind.PurchaseOrder) PurchaseOrderCardRoles else WorkItemCardRoles

internal fun workItemIsReviewer(role: UserRole): Boolean =
    role == UserRole.ProjectManager || role == UserRole.QaQc

@Composable
internal fun WorkItemOverflowMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    canEdit: Boolean = true,
) {
    var open by remember { mutableStateOf(false) }
    val content = OrbitTheme.contentColors

    Box {
        OrbitIconButton(
            contentDescription = "More",
            onClick = { open = true },
            icon = OrbitIcons.MoreVertical,
            style = OrbitIconButtonStyle.Neutral,
            size = OrbitIconButtonSize.Small,
        )
        OrbitBubblePopover(
            expanded = open,
            onDismiss = { open = false },
            title = "More",
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (canEdit) {
                    OverflowRow("Edit", content.textPrimary) {
                        open = false
                        onEdit()
                    }
                }
                OverflowRow("Delete", OrbitBadgeTone.Red.colors.label) {
                    open = false
                    onDelete()
                }
            }
        }
    }
}

@Composable
private fun OverflowRow(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        style = OrbitTheme.typography.bodyLarge.copy(fontWeight = OrbitTheme.fontWeights.title),
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(
                horizontal = OrbitTheme.spacing.md,
                vertical = OrbitTheme.spacing.sm,
            ),
    )
}

@Composable
internal fun WorkItemRoleActions(
    record: WorkItemRecord,
    viewerRole: UserRole,
    onStart: () -> Unit,
    onView: () -> Unit,
    onUpdate: () -> Unit,
) {
    var confirmStart by remember { mutableStateOf(false) }
    val notStarted = record.status == WorkStatus.Open
    val noun = workItemNoun(record.kind)
    val isOrder = record.kind == WorkItemKind.PurchaseOrder

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        when {
            isOrder && viewerRole == UserRole.ProjectManager -> {
                ActionButton(
                    action = ActionKind.View,
                    onClick = onView,
                    modifier = Modifier.fillMaxWidth(),
                    showIcon = false,
                )
            }
            isOrder && (
                viewerRole == UserRole.ProcurementManager ||
                    viewerRole == UserRole.WarehouseManager
                ) -> {
                if (!record.status.isTerminal) {
                    ActionButton(
                        action = ActionKind.Update,
                        onClick = onUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        showIcon = false,
                    )
                } else {
                    ActionButton(
                        action = ActionKind.View,
                        onClick = onView,
                        modifier = Modifier.fillMaxWidth(),
                        showIcon = false,
                    )
                }
            }
            viewerRole == UserRole.ProjectManager -> {
                if (notStarted) {
                    ActionButton(
                        action = ActionKind.Start,
                        onClick = { confirmStart = true },
                        modifier = Modifier.fillMaxWidth(),
                        showIcon = false,
                    )
                }
                ActionButton(
                    action = ActionKind.View,
                    onClick = onView,
                    modifier = Modifier.fillMaxWidth(),
                    showIcon = false,
                )
            }
            viewerRole == UserRole.SiteEngineer || viewerRole == UserRole.Contractor -> {
                if (!record.status.isTerminal) {
                    ActionButton(
                        action = ActionKind.Update,
                        onClick = onUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        showIcon = false,
                    )
                } else {
                    ActionButton(
                        action = ActionKind.View,
                        onClick = onView,
                        modifier = Modifier.fillMaxWidth(),
                        showIcon = false,
                    )
                }
            }
            viewerRole == UserRole.QaQc -> {
                ActionButton(
                    action = ActionKind.View,
                    onClick = onView,
                    modifier = Modifier.fillMaxWidth(),
                    showIcon = false,
                )
            }
            else -> Unit
        }
    }

    if (confirmStart) {
        OrbitConfirmDialog(
            title = "Start $noun",
            message = "Start ${record.number} now?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                onStart()
                confirmStart = false
            },
            onDismiss = { confirmStart = false },
        )
    }
}
