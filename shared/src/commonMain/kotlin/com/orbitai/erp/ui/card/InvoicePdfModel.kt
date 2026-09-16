package com.orbitai.erp.ui.card

/**
 * Shared invoice content for [MobileInvoiceDocument] and desktop PDF export.
 * PDF / share / download use invoice body only — attached work stays on-screen.
 */
data class InvoicePdfModel(
    val invoice: InvoiceRecord,
    val attachedWork: List<WorkItemRecord> = emptyList(),
) {
    val number: String get() = invoice.number
    val files: List<InvoiceAttachment>
        get() = invoice.uploads.distinctBy { it.id }
}

enum class InvoicePdfAction {
    Download,
    Share,
}

fun InvoiceRecord.toPdfModel(
    attachedWork: List<WorkItemRecord> = emptyList(),
): InvoicePdfModel = InvoicePdfModel(
    invoice = copy(bank = bank.copy(qrAttachment = null)),
    attachedWork = attachedWork,
)
