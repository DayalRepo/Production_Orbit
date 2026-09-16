package com.orbitai.erp.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.badge.InvoiceStatusBadge

/**
 * On-screen mobile billing plate — same layout anatomy as the desktop A4 PDF.
 */
@Composable
fun MobileInvoiceDocument(
    model: InvoicePdfModel,
    modifier: Modifier = Modifier,
) {
    val invoice = model.invoice
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        container = content.referenceSurface,
        padding = spacing.cardPadding,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "INVOICE",
                        style = OrbitTheme.typography.titleLarge,
                        color = content.textPrimary,
                    )
                    Text(
                        text = invoice.number,
                        style = OrbitTheme.extendedTypography.reference,
                        color = content.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                InvoiceStatusBadge(
                    status = invoice.status,
                    size = OrbitBadgeSize.Small,
                )
            }

            OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Issued ${invoice.issued.formatSlashed()}",
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                )
                Text(
                    text = "Due ${invoice.due.formatSlashed()}",
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                    textAlign = TextAlign.End,
                )
            }

            OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)

            PartiesBlock(
                from = invoice.from,
                billTo = invoice.billTo,
            )

            OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)

            LineItemsBlock(lines = invoice.lines)

            OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                KeyValueRow("Subtotal", formatInr(invoice.subtotal))
                if (invoice.applyGst) {
                    KeyValueRow(
                        "CGST ${invoice.cgstPercent.toLong()}%",
                        formatInr(invoice.cgstAmount),
                    )
                    KeyValueRow(
                        "SGST ${invoice.sgstPercent.toLong()}%",
                        formatInr(invoice.sgstAmount),
                    )
                }
                KeyValueRow("Total", formatInr(invoice.grandTotal), emphasize = true)
            }

            Text(
                text = amountInWordsInr(invoice.grandTotal),
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )

            OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)

            Text(
                text = "Bank details".uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
            )
            if (invoice.bank.accountHolder.isNotBlank()) {
                BankDetailRow("Account holder", invoice.bank.accountHolder)
            }
            if (invoice.bank.bankName.isNotBlank()) {
                BankDetailRow("Bank", invoice.bank.bankName)
            }
            if (invoice.bank.accountNumber.isNotBlank()) {
                BankDetailRow("A/c number", invoice.bank.accountNumber)
            }
            if (invoice.bank.ifsc.isNotBlank()) {
                BankDetailRow("IFSC", invoice.bank.ifsc)
            }
            if (invoice.bank.upiId.isNotBlank()) {
                BankDetailRow("UPI", invoice.bank.upiId)
            }
            if (invoice.notes.isNotBlank()) {
                OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
                Text(
                    text = "Notes".uppercase(),
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = content.textSecondary,
                )
                Text(
                    text = invoice.notes,
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textPrimary,
                )
            }
        }
    }
}

/**
 * From | Bill to as paired rows so NAME / ADDRESS / GSTIN / PHONE headings share one baseline
 * across columns even when one side's value wraps taller.
 */
@Composable
private fun PartiesBlock(
    from: InvoiceParty,
    billTo: InvoiceParty,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(
                text = "From".uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Bill to".uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
                modifier = Modifier.weight(1f),
            )
        }
        PartyFieldRow("Name", from.name, billTo.name)
        PartyFieldRow("Address", from.address, billTo.address)
        PartyFieldRow("GSTIN", from.gstin, billTo.gstin)
        PartyFieldRow("Phone", from.phone, billTo.phone)
    }
}

@Composable
private fun PartyFieldRow(
    heading: String,
    left: String,
    right: String,
) {
    val content = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(
                text = heading.uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textTertiary,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = heading.uppercase(),
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textTertiary,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = left.ifBlank { "—" },
                style = OrbitTheme.typography.bodySmall,
                color = content.textPrimary,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = right.ifBlank { "—" },
                style = OrbitTheme.typography.bodySmall,
                color = content.textPrimary,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Numbered description, then QTY / UNIT / AMOUNT with headings above values.
 * Divider between items for scanability.
 */
@Composable
private fun LineItemsBlock(lines: List<InvoiceLineItem>) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            text = "Line items".uppercase(),
            style = OrbitTheme.extendedTypography.cardLabel,
            color = content.textSecondary,
        )
        lines.forEachIndexed { index, line ->
            if (index > 0) {
                OrbitDivider(color = OrbitTheme.controlColors.controlBorder)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = "${index + 1}. ${line.description}",
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textPrimary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    LineMetric(
                        heading = "QTY",
                        value = line.quantity.toLong().toString(),
                        modifier = Modifier.weight(1f),
                        alignEnd = false,
                    )
                    LineMetric(
                        heading = "UNIT",
                        value = line.displayUnit,
                        modifier = Modifier.weight(1f),
                        alignEnd = false,
                    )
                    LineMetric(
                        heading = "AMOUNT",
                        value = formatInr(line.amount),
                        modifier = Modifier.weight(1.4f),
                        alignEnd = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun LineMetric(
    heading: String,
    value: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xxs),
    ) {
        Text(
            text = heading,
            style = OrbitTheme.extendedTypography.cardLabel,
            color = OrbitTheme.contentColors.textTertiary,
        )
        Text(
            text = value,
            style = OrbitTheme.extendedTypography.tableNumeric,
            color = OrbitTheme.contentColors.textPrimary,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun KeyValueRow(
    label: String,
    value: String,
    emphasize: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = if (emphasize) {
                OrbitTheme.typography.titleMedium
            } else {
                OrbitTheme.typography.bodySmall
            },
            color = OrbitTheme.contentColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = if (emphasize) {
                OrbitTheme.extendedTypography.metricMedium
            } else {
                OrbitTheme.extendedTypography.tableNumeric
            },
            color = OrbitTheme.contentColors.textPrimary,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun BankDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.bodySmall,
            color = OrbitTheme.contentColors.textSecondary,
            modifier = Modifier.width(112.dp),
        )
        Text(
            text = value,
            style = OrbitTheme.typography.bodySmall,
            color = OrbitTheme.contentColors.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}
