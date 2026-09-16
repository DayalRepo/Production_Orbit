package com.orbitai.erp.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.progress.OrbitFormPageBar
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.card.InvoiceRecord
import com.orbitai.erp.ui.component.button.ActionButtonRow
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.form.page.InvoiceAttachWorkPage
import com.orbitai.erp.ui.form.page.InvoiceLinesPage
import com.orbitai.erp.ui.form.page.InvoiceMetaPage
import com.orbitai.erp.ui.form.page.InvoicePaymentPage
import com.orbitai.erp.ui.form.page.InvoiceUploadsPage
import com.orbitai.erp.ui.form.page.newInvoiceLineId

private const val InvoicePageCount = 5

@Composable
fun InvoiceForm(
    projectType: ProjectType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    editing: InvoiceRecord? = null,
    onCreate: (InvoiceDraft) -> Unit = {},
) {
    val draft = remember(editing?.id) {
        editing?.toDraft() ?: InvoiceDraft()
    }
    val isEditing = editing != null
    var page by remember { mutableIntStateOf(0) }
    var confirmOpen by remember { mutableStateOf(false) }
    var zeroConfirmOpen by remember { mutableStateOf(false) }
    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val projects = remember(projectType) {
        MockDirectory.OrganisationProjects.filter { it.type == projectType }.map { it.name }
    }
    LaunchedEffect(projects) {
        if (draft.projectName == null && projects.size == 1) {
            draft.projectName = projects.first()
        }
    }

    fun goBack() {
        if (page == 0) onDismiss() else page -= 1
    }

    fun goNext() {
        if (!draft.isReadyForPage(page)) return
        if (page < InvoicePageCount - 1) {
            page += 1
            return
        }
        if (!draft.isReadyToCreate()) return
        if (draft.grandTotal <= 0.0 || draft.completeLines().isEmpty()) {
            zeroConfirmOpen = true
        } else {
            confirmOpen = true
        }
    }

    OrbitBackHandler(onBack = ::goBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .padding(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.screenVertical,
            ),
    ) {
        Text(
            text = (if (isEditing) "Edit invoice" else "Create invoice").uppercase(),
            style = OrbitTheme.typography.headlineSmall.copy(
                fontWeight = OrbitTheme.fontWeights.heading,
            ),
            color = OrbitTheme.contentColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = projectType.displayName.uppercase(),
            style = OrbitTheme.extendedTypography.sectionLabel,
            color = OrbitTheme.contentColors.textTertiary,
        )
        Spacer(modifier = Modifier.height(spacing.md))
        OrbitFormPageBar(
            pageCount = InvoicePageCount,
            currentPage = page,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
        ) {
            when (page) {
                0 -> InvoiceMetaPage(
                    number = draft.number,
                    onNumberChange = { draft.number = it },
                    dateRange = draft.dateRange,
                    onDateRangeChange = draft::setDateRange,
                    projects = projects,
                    projectName = draft.projectName,
                    onProjectSelect = { draft.projectName = it },
                    projectType = projectType,
                    villa = draft.villa,
                    onVillaSelect = { draft.villa = it },
                    tower = draft.tower,
                    onTowerSelect = {
                        draft.tower = it
                        draft.apartmentUnit = null
                    },
                    apartmentUnit = draft.apartmentUnit,
                    onApartmentUnitSelect = { draft.apartmentUnit = it },
                    from = draft.from,
                    onFromChange = { draft.from = it },
                    billTo = draft.billTo,
                    onBillToChange = { draft.billTo = it },
                )
                1 -> InvoiceLinesPage(
                    lines = draft.lines.toList(),
                    onReplaceLine = draft::replaceLine,
                    onAddLine = { draft.lines += InvoiceLineDraft(id = newInvoiceLineId()) },
                    onRemoveLine = { id ->
                        if (draft.lines.size > 1) draft.lines.removeAll { it.id == id }
                    },
                    applyGst = draft.applyGst,
                    onApplyGstChange = { draft.applyGst = it },
                    cgstPercent = draft.cgstPercent,
                    onCgstPercentChange = { draft.cgstPercent = it },
                    sgstPercent = draft.sgstPercent,
                    onSgstPercentChange = { draft.sgstPercent = it },
                    subtotal = draft.subtotal,
                    cgstAmount = draft.cgstAmount,
                    sgstAmount = draft.sgstAmount,
                    grandTotal = draft.grandTotal,
                )
                2 -> InvoicePaymentPage(
                    bank = draft.bank,
                    onBankChange = { draft.bank = it },
                    notes = draft.notes,
                    onNotesChange = { draft.notes = it },
                )
                3 -> InvoiceAttachWorkPage(
                    projectType = projectType,
                    attachedTaskIds = draft.attachedTaskIds,
                    onTaskToggle = { draft.attachedTaskIds = draft.attachedTaskIds.toggle(it) },
                    attachedIssueIds = draft.attachedIssueIds,
                    onIssueToggle = { draft.attachedIssueIds = draft.attachedIssueIds.toggle(it) },
                )
                else -> InvoiceUploadsPage(
                    uploads = draft.uploads.toList(),
                    onAdd = { draft.uploads += it },
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))
        ActionButtonRow(
            dismiss = if (page == 0) ActionKind.Cancel else ActionKind.Back,
            confirm = when {
                page < InvoicePageCount - 1 -> ActionKind.Next
                isEditing -> ActionKind.Edit
                else -> ActionKind.Create
            },
            onDismiss = ::goBack,
            onConfirm = ::goNext,
        )
    }

    if (confirmOpen) {
        OrbitConfirmDialog(
            title = if (isEditing) "Save invoice" else "Create invoice",
            message = if (isEditing) {
                "Save changes to invoice ${draft.number}?"
            } else {
                "Create invoice ${draft.number} for ${formatCreateMessage(draft)}?"
            },
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                confirmOpen = false
                onCreate(draft)
            },
            onDismiss = { confirmOpen = false },
        )
    }
    if (zeroConfirmOpen) {
        OrbitConfirmDialog(
            title = "Zero total",
            message = if (isEditing) {
                "This invoice has no billable amount. Save it anyway?"
            } else {
                "This invoice has no billable amount. Create it anyway?"
            },
            confirmLabel = if (isEditing) "Save" else "Create",
            dismissLabel = "Back",
            onConfirm = {
                zeroConfirmOpen = false
                onCreate(draft)
            },
            onDismiss = { zeroConfirmOpen = false },
        )
    }
}

private fun formatCreateMessage(draft: InvoiceDraft): String =
    draft.billTo.name.ifBlank { "client" }
