package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.model.ApprovalStatus
import com.orbitai.erp.core.model.ProjectType
import kotlin.math.roundToLong

/** A single billed row on an invoice. */
data class InvoiceLineItem(
    val id: String,
    val description: String,
    val quantity: Double,
    val rate: Double,
    val hsnSac: String? = null,
    val unit: String = "Nos",
) {
    val amount: Double get() = quantity * rate

    /** Unit of measure shown on the plate (Rate is kept for totals, not shown when redundant). */
    val displayUnit: String
        get() = unit.trim().ifBlank { "Nos" }
}

/** Seller or client party on an invoice. */
data class InvoiceParty(
    val name: String = "",
    val doorNo: String = "",
    val apartmentName: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val gstin: String = "",
    val phone: String = "",
    val email: String = "",
) {
    /** Formatted multi-line address from structured parts. */
    val address: String
        get() = formatInvoiceAddress(
            doorNo = doorNo,
            apartmentName = apartmentName,
            street = street,
            city = city,
            state = state,
            pincode = pincode,
        )
}

fun formatInvoiceAddress(
    doorNo: String = "",
    apartmentName: String = "",
    street: String = "",
    city: String = "",
    state: String = "",
    pincode: String = "",
): String {
    val line1 = listOfNotNull(
        doorNo.takeIf { it.isNotBlank() },
        apartmentName.takeIf { it.isNotBlank() },
    ).joinToString(", ")
    val line2 = street.takeIf { it.isNotBlank() }.orEmpty()
    val line3 = listOfNotNull(
        city.takeIf { it.isNotBlank() },
        state.takeIf { it.isNotBlank() },
        pincode.takeIf { it.isNotBlank() },
    ).joinToString(", ")
    return listOf(line1, line2, line3).filter { it.isNotBlank() }.joinToString("\n")
}

/** Indian bank / UPI payment block. */
data class InvoiceBankDetails(
    val accountHolder: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val ifsc: String = "",
    val upiId: String = "",
    val qrAttachment: InvoiceAttachment? = null,
)

data class InvoiceAttachment(
    val id: String,
    val fileName: String,
    val fileSize: String,
)

/** Immutable created invoice — not a [WorkItemKind]. */
data class InvoiceRecord(
    val id: String,
    val number: String,
    val status: ApprovalStatus,
    val projectType: ProjectType,
    val issued: OrbitCalendarDate,
    val due: OrbitCalendarDate,
    val projectName: String? = null,
    val villa: String? = null,
    val tower: String? = null,
    val apartmentUnit: String? = null,
    val from: InvoiceParty,
    val billTo: InvoiceParty,
    val lines: List<InvoiceLineItem>,
    val applyGst: Boolean,
    val cgstPercent: Double,
    val sgstPercent: Double,
    val bank: InvoiceBankDetails,
    val notes: String = "",
    val placeOfSupply: String = "",
    val attachedTaskIds: List<String> = emptyList(),
    val attachedIssueIds: List<String> = emptyList(),
    val uploads: List<InvoiceAttachment> = emptyList(),
) {
    val subtotal: Double get() = lines.sumOf { it.amount }

    val cgstAmount: Double
        get() = if (applyGst) subtotal * cgstPercent / 100.0 else 0.0

    val sgstAmount: Double
        get() = if (applyGst) subtotal * sgstPercent / 100.0 else 0.0

    val grandTotal: Double get() = subtotal + cgstAmount + sgstAmount

    val locationLine: String
        get() = buildString {
            projectName?.takeIf { it.isNotBlank() }?.let { append(it) }
            val unit = when (projectType) {
                ProjectType.Villas -> villa
                ProjectType.ApartmentCommunity -> listOfNotNull(tower, apartmentUnit)
                    .joinToString(" · ")
                    .ifBlank { null }
            }
            if (!unit.isNullOrBlank()) {
                if (isNotEmpty()) append(" · ")
                append(unit)
            }
        }
}

fun formatInr(amount: Double): String {
    val paise = (amount * 100.0).roundToLong()
    val negative = paise < 0
    val abs = kotlin.math.abs(paise)
    val whole = abs / 100
    val frac = abs % 100
    val grouped = indianGroup(whole)
    val body = "₹ $grouped.${frac.toString().padStart(2, '0')}"
    return if (negative) "-$body" else body
}

fun indianGroup(value: Long): String {
    val s = value.toString()
    if (s.length <= 3) return s
    val last3 = s.takeLast(3)
    val head = s.dropLast(3)
    val parts = mutableListOf<String>()
    var rest = head
    while (rest.length > 2) {
        parts.add(0, rest.takeLast(2))
        rest = rest.dropLast(2)
    }
    if (rest.isNotEmpty()) parts.add(0, rest)
    return parts.joinToString(",") + "," + last3
}

/** Soft IFSC check: 4 letters, 0, then 6 alphanumerics. */
fun isValidIfsc(value: String): Boolean {
    if (value.isBlank()) return true
    if (value.length != 11) return false
    val upper = value.uppercase()
    return upper.take(4).all { it in 'A'..'Z' } &&
        upper[4] == '0' &&
        upper.drop(5).all { it.isLetterOrDigit() }
}

fun amountInWordsInr(amount: Double): String {
    val rupees = amount.roundToLong().coerceAtLeast(0)
    if (rupees == 0L) return "Rupees: Zero Only"
    return "Rupees: ${numberToWords(rupees)} Only"
}

private fun numberToWords(n: Long): String {
    if (n == 0L) return "Zero"
    val parts = mutableListOf<String>()
    var rem = n
    val crore = rem / 10_000_000
    if (crore > 0) {
        parts += "${belowThousand(crore.toInt())} Crore"
        rem %= 10_000_000
    }
    val lakh = rem / 100_000
    if (lakh > 0) {
        parts += "${belowThousand(lakh.toInt())} Lakh"
        rem %= 100_000
    }
    val thousand = rem / 1_000
    if (thousand > 0) {
        parts += "${belowThousand(thousand.toInt())} Thousand"
        rem %= 1_000
    }
    if (rem > 0) parts += belowThousand(rem.toInt())
    return parts.joinToString(" ")
}

private fun belowThousand(n: Int): String {
    require(n in 0..999)
    if (n == 0) return ""
    val ones = listOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen",
    )
    val tens = listOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety",
    )
    val hundred = n / 100
    val rest = n % 100
    val builder = StringBuilder()
    if (hundred > 0) {
        builder.append(ones[hundred]).append(" Hundred")
        if (rest > 0) builder.append(" ")
    }
    when {
        rest == 0 -> Unit
        rest < 20 -> builder.append(ones[rest])
        else -> {
            builder.append(tens[rest / 10])
            if (rest % 10 != 0) builder.append(" ").append(ones[rest % 10])
        }
    }
    return builder.toString()
}

fun InvoiceRecord.toInvoiceMarkdown(): String = buildString {
    appendLine("# INVOICE")
    append(number)
    append(" · Issued ")
    append(issued.formatSlashed())
    append(" · Due ")
    append(due.formatSlashed())
    if (locationLine.isNotBlank()) {
        append(" · ")
        append(locationLine)
    }
    appendLine()
    appendLine()
    appendLine("## From")
    appendMarkdownParty(from)
    appendLine()
    appendLine("## Bill to")
    appendMarkdownParty(billTo)
    appendLine()
    appendLine("## Line items")
    appendLine("| # | Description | Qty | Rate | Amount |")
    appendLine("| --- | --- | --- | --- | --- |")
    lines.forEachIndexed { index, line ->
        append("| ")
        append(index + 1)
        append(" | ")
        append(line.description.replace('|', '/'))
        append(" | ")
        append(line.quantity.toLong())
        append(" | ")
        append(formatInr(line.rate))
        append(" | ")
        append(formatInr(line.amount))
        appendLine(" |")
    }
    appendLine()
    append("**Subtotal** ")
    appendLine(formatInr(subtotal))
    if (applyGst) {
        append("**CGST ")
        append(cgstPercent.toLong())
        append("%** ")
        appendLine(formatInr(cgstAmount))
        append("**SGST ")
        append(sgstPercent.toLong())
        append("%** ")
        appendLine(formatInr(sgstAmount))
    }
    append("**Total** ")
    appendLine(formatInr(grandTotal))
    append('*')
    append(amountInWordsInr(grandTotal))
    appendLine('*')
    appendLine()
    appendLine("## Bank details")
    if (bank.accountHolder.isNotBlank()) appendLine(bank.accountHolder)
    if (bank.bankName.isNotBlank()) appendLine(bank.bankName)
    if (bank.accountNumber.isNotBlank()) {
        append("A/c ")
        appendLine(bank.accountNumber)
    }
    if (bank.ifsc.isNotBlank()) {
        append("IFSC ")
        appendLine(bank.ifsc)
    }
    if (bank.upiId.isNotBlank()) {
        append("UPI ")
        appendLine(bank.upiId)
    }
    if (notes.isNotBlank()) {
        appendLine()
        appendLine(notes)
    }
}

fun InvoiceRecord.invoiceTextSnapshot(): String = toInvoiceMarkdown()

private fun StringBuilder.appendMarkdownParty(party: InvoiceParty) {
    if (party.name.isNotBlank()) appendLine(party.name)
    if (party.address.isNotBlank()) appendLine(party.address)
    if (party.gstin.isNotBlank()) {
        append("GSTIN ")
        appendLine(party.gstin)
    }
    if (party.phone.isNotBlank()) {
        append("Phone ")
        appendLine(party.phone)
    }
}

private var invoiceSeq = 50

fun nextInvoiceNumber(): String {
    invoiceSeq += 1
    return "INV-2026-${invoiceSeq.toString().padStart(3, '0')}"
}
