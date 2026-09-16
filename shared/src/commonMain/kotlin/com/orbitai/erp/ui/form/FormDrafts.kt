package com.orbitai.erp.ui.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.orbitMaterialUsageLineComplete
import com.orbitai.erp.core.model.ApprovalStatus
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.ui.card.InvoiceAttachment
import com.orbitai.erp.ui.card.InvoiceLineItem
import com.orbitai.erp.ui.card.InvoiceParty
import com.orbitai.erp.ui.card.InvoiceRecord
import com.orbitai.erp.ui.card.defaultBankDetails
import com.orbitai.erp.ui.card.defaultSellerParty
import com.orbitai.erp.ui.card.isValidIfsc
import com.orbitai.erp.ui.card.nextInvoiceNumber
import com.orbitai.erp.ui.component.dropdown.WorkStage
import com.orbitai.erp.ui.datetime.orbitToday
import com.orbitai.erp.ui.datetime.toLocalDate
import com.orbitai.erp.ui.datetime.toOrbitCalendarDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import kotlin.random.Random

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

/** Mutable multi-page invoice create state. */
class InvoiceDraft {
    var number by mutableStateOf(nextInvoiceNumber())
    var issued by mutableStateOf(orbitToday())
    var due by mutableStateOf(
        orbitToday().toLocalDate().plus(DatePeriod(days = 15)).toOrbitCalendarDate(),
    )
    var projectName by mutableStateOf<String?>(null)
    var villa by mutableStateOf<String?>(null)
    var tower by mutableStateOf<String?>(null)
    var apartmentUnit by mutableStateOf<String?>(null)
    var from by mutableStateOf(defaultSellerParty())
    var billTo by mutableStateOf(InvoiceParty())
    val lines = mutableStateListOf(InvoiceLineDraft(id = "line-0"))
    var applyGst by mutableStateOf(true)
    var cgstPercent by mutableStateOf(9.0)
    var sgstPercent by mutableStateOf(9.0)
    var bank by mutableStateOf(defaultBankDetails().copy(qrAttachment = null))
    var notes by mutableStateOf("")
    var placeOfSupply by mutableStateOf("Karnataka")
    var attachedTaskIds by mutableStateOf(setOf<String>())
    var attachedIssueIds by mutableStateOf(setOf<String>())
    val uploads = mutableStateListOf<InvoiceAttachment>()

    val dateRange: OrbitDateRange
        get() = OrbitDateRange(issued, due)

    fun setDateRange(range: OrbitDateRange) {
        issued = range.start
        due = range.end
    }

    val subtotal: Double
        get() = completeLines().sumOf { it.amount }

    val cgstAmount: Double
        get() = if (applyGst) subtotal * cgstPercent / 100.0 else 0.0

    val sgstAmount: Double
        get() = if (applyGst) subtotal * sgstPercent / 100.0 else 0.0

    val grandTotal: Double
        get() = subtotal + cgstAmount + sgstAmount

    fun completeLines(): List<InvoiceLineItem> = lines.mapNotNull { it.toLineOrNull() }

    fun replaceLine(id: String, transform: (InvoiceLineDraft) -> InvoiceLineDraft) {
        val index = lines.indexOfFirst { it.id == id }
        if (index >= 0) lines[index] = transform(lines[index])
    }
}

data class InvoiceLineDraft(
    val id: String,
    val description: String = "",
    val quantity: Double = 1.0,
    val rate: Double = 0.0,
    val hsnSac: String = "",
) {
    fun toLineOrNull(): InvoiceLineItem? {
        if (description.isBlank() || quantity <= 0.0 || rate < 0.0) return null
        return InvoiceLineItem(
            id = id,
            description = description.trim(),
            quantity = quantity,
            rate = rate,
            hsnSac = hsnSac.trim().ifBlank { null },
        )
    }
}

fun InvoiceDraft.isReadyForPage(page: Int): Boolean = when (page) {
    0 -> number.isNotBlank() &&
        from.name.isNotBlank() &&
        billTo.name.isNotBlank() &&
        !due.toLocalDate().let { d -> d < issued.toLocalDate() }
    1 -> completeLines().isNotEmpty()
    2 -> bank.accountHolder.isNotBlank() &&
        bank.bankName.isNotBlank() &&
        bank.accountNumber.isNotBlank() &&
        bank.ifsc.isNotBlank() &&
        isValidIfsc(bank.ifsc)
    else -> true
}

fun InvoiceDraft.isReadyToCreate(): Boolean =
    isReadyForPage(0) && isReadyForPage(1) && isReadyForPage(2)

fun InvoiceDraft.toRecord(projectType: ProjectType): InvoiceRecord = InvoiceRecord(
    id = "inv${Random.nextLong()}",
    number = number.trim().ifBlank { nextInvoiceNumber() },
    status = ApprovalStatus.Pending,
    projectType = projectType,
    issued = issued,
    due = due,
    projectName = projectName,
    villa = villa,
    tower = tower,
    apartmentUnit = apartmentUnit,
    from = from,
    billTo = billTo,
    lines = completeLines(),
    applyGst = applyGst,
    cgstPercent = cgstPercent,
    sgstPercent = sgstPercent,
    bank = bank,
    notes = notes.trim(),
    placeOfSupply = placeOfSupply.trim(),
    attachedTaskIds = attachedTaskIds.toList(),
    attachedIssueIds = attachedIssueIds.toList(),
    uploads = uploads.toList(),
)

internal fun WorkItemDraft.replaceMaterialLine(
    id: String,
    transform: (OrbitMaterialUsageLine) -> OrbitMaterialUsageLine,
) {
    val index = materialLines.indexOfFirst { it.id == id }
    if (index >= 0) materialLines[index] = transform(materialLines[index])
}
