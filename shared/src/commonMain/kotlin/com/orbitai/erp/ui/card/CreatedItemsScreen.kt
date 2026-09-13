package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.form.CreateTaskForm
import com.orbitai.erp.ui.form.MaterialsOrderForm
import com.orbitai.erp.ui.form.RaiseIssueForm

@Composable
fun CreatedItemsScreen(
    items: List<WorkItemRecord>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Created items",
) {
    val liveItems = remember(items) {
        mutableStateListOf<WorkItemRecord>().also { it.addAll(items) }
    }
    var deleteId by remember { mutableStateOf<String?>(null) }
    var editId by remember { mutableStateOf<String?>(null) }
    var openId by remember { mutableStateOf<String?>(null) }
    var openRole by remember { mutableStateOf(UserRole.SiteEngineer) }

    fun replace(id: String, next: WorkItemRecord) {
        val index = liveItems.indexOfFirst { it.id == id }
        if (index >= 0) liveItems[index] = next
    }

    fun open(slide: CreatedCardSlide) {
        openId = slide.record.id
        openRole = slide.role
    }

    val editing = editId?.let { id -> liveItems.firstOrNull { it.id == id } }
    if (editing != null) {
        when (editing.kind) {
            WorkItemKind.Issue -> RaiseIssueForm(
                projectType = editing.projectType,
                editing = editing,
                onDismiss = { editId = null },
                onRaise = { draft ->
                    replace(editing.id, editing.applyIssueDraft(draft))
                    editId = null
                },
                modifier = modifier,
            )
            WorkItemKind.PurchaseOrder -> MaterialsOrderForm(
                projectType = editing.projectType,
                editing = editing,
                onDismiss = { editId = null },
                onCreate = { draft ->
                    replace(editing.id, editing.applyMaterialsOrderDraft(draft))
                    editId = null
                },
                modifier = modifier,
            )
            WorkItemKind.Task -> CreateTaskForm(
                projectType = editing.projectType,
                editing = editing,
                onDismiss = { editId = null },
                onCreate = { draft ->
                    replace(editing.id, editing.applyWorkDraft(draft))
                    editId = null
                },
                modifier = modifier,
            )
        }
        return
    }

    val openRecord = openId?.let { id -> liveItems.firstOrNull { it.id == id } }
    if (openRecord != null && openRecord.kind == WorkItemKind.PurchaseOrder) {
        CreatedOrderScreen(
            record = openRecord,
            viewerRole = openRole,
            onBack = { openId = null },
            onRecordChange = { next -> replace(next.id, next) },
            onOrderDone = {
                replace(openRecord.id, openRecord.withStatus(WorkStatus.InReview))
                openId = null
            },
            onReceived = {
                replace(openRecord.id, openRecord.withStatus(WorkStatus.Completed))
                openId = null
            },
            modifier = modifier,
        )
        return
    }
    if (openRecord != null) {
        CreatedTaskScreen(
            record = openRecord,
            viewerRole = openRole,
            onBack = { openId = null },
            onRecordChange = { next -> replace(next.id, next) },
            onSubmit = {
                replace(openRecord.id, openRecord.withStatus(WorkStatus.InReview))
                openId = null
            },
            onApprove = {
                replace(openRecord.id, openRecord.withStatus(WorkStatus.Completed))
                openId = null
            },
            onCancel = {
                replace(openRecord.id, openRecord.withStatus(WorkStatus.Cancelled))
                openId = null
            },
            onRework = { note ->
                replace(openRecord.id, openRecord.withRework(note))
                openId = null
            },
            modifier = modifier,
        )
        return
    }

    OrbitBackHandler(onBack = onBack)

    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .padding(vertical = spacing.screenVertical),
    ) {
        OrbitIconButton(
            contentDescription = "Back",
            onClick = onBack,
            icon = OrbitIcons.ArrowLeft,
            style = OrbitIconButtonStyle.Neutral,
            modifier = Modifier.padding(horizontal = spacing.screenHorizontal),
        )
        CreatedCardCarousel(
            slides = createdCardSlides(liveItems),
            onUpdate = { slide -> open(slide) },
            onEdit = { slide -> editId = slide.record.id },
            onDelete = { slide -> deleteId = slide.record.id },
            onView = { slide -> open(slide) },
            onStart = { slide ->
                replace(slide.record.id, slide.record.withStatus(WorkStatus.InProgress))
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }

    val removing = deleteId?.let { id -> liveItems.firstOrNull { it.id == id } }
    if (removing != null) {
        val noun = workItemNoun(removing.kind)
        OrbitConfirmDialog(
            title = "Delete $noun",
            message = "Delete ${removing.number}? This cannot be undone.",
            destructive = true,
            onConfirm = {
                liveItems.removeAll { it.id == removing.id }
                deleteId = null
                if (liveItems.isEmpty()) onBack()
            },
            onDismiss = { deleteId = null },
        )
    }
}
