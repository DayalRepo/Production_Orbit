package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.platform.rememberInvoicePdfExporter
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.badge.ApprovalStatusBadge
import com.orbitai.erp.ui.form.page.invoiceWorkCatalogue

@Composable
fun CreatedInvoiceScreen(
    record: InvoiceRecord,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenAttachedWork: () -> Unit = {},
) {
    OrbitBackHandler(onBack = onBack)
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val clipboard = LocalClipboardManager.current
    val exportPdf = rememberInvoicePdfExporter()
    var exportNote by remember { mutableStateOf<String?>(null) }
    val catalogue = remember(record.projectType) { invoiceWorkCatalogue(record.projectType) }
    val attached = remember(record, catalogue) {
        catalogue.filter {
            it.id in record.attachedTaskIds || it.id in record.attachedIssueIds
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitIconButton(
                contentDescription = "Back",
                onClick = onBack,
                icon = OrbitIcons.ArrowLeft,
                style = OrbitIconButtonStyle.Neutral,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Created invoice",
                    style = OrbitTheme.typography.titleLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = record.number,
                    style = OrbitTheme.extendedTypography.reference,
                    color = content.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ApprovalStatusBadge(status = record.status)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal)
                .padding(bottom = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            OrbitCard(
                modifier = Modifier.fillMaxWidth(),
                container = content.referenceSurface,
            ) {
                Text(
                    text = "INVOICE",
                    style = OrbitTheme.typography.headlineMedium.copy(
                        fontWeight = OrbitTheme.fontWeights.heading,
                    ),
                    color = content.textPrimary,
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = buildString {
                        append("Issued ${record.issued.formatSlashed()}")
                        append(" · Due ${record.due.formatSlashed()}")
                        if (record.locationLine.isNotBlank()) {
                            append(" · ")
                            append(record.locationLine)
                        }
                    },
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                )

                InvoiceDocSection("Parties") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        PartyBlock("From", record.from, Modifier.weight(1f))
                        PartyBlock("Bill to", record.billTo, Modifier.weight(1f))
                    }
                }

                InvoiceDocSection("Line items") {
                    LineItemsTable(record)
                }

                InvoiceDocSection("Totals") {
                    TotalsBlock(record)
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(
                        text = amountInWordsInr(record.grandTotal),
                        style = OrbitTheme.typography.bodySmall,
                        color = content.textSecondary,
                    )
                }

                InvoiceDocSection("Bank details") {
                    BankBlock(record.bank)
                    if (record.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(spacing.sm))
                        Text(
                            text = record.notes,
                            style = OrbitTheme.typography.bodyMedium,
                            color = content.textSecondary,
                        )
                    }
                }
            }

            if (attached.isNotEmpty()) {
                InvoiceOuterSection("Attached work") {
                    attached.forEach { item ->
                        InvoiceWorkLinkRow(
                            number = item.number,
                            kindLabel = if (item.kind == WorkItemKind.Task) "task" else "issue",
                            onClick = onOpenAttachedWork,
                        )
                    }
                }
            }

            if (record.uploads.isNotEmpty() || record.bank.qrAttachment != null) {
                InvoiceOuterSection("Supporting files") {
                    record.bank.qrAttachment?.let { qr ->
                        FileAttachmentRow(
                            fileName = "QR · ${qr.fileName}",
                            fileSize = qr.fileSize,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    record.uploads.forEach { file ->
                        FileAttachmentRow(
                            fileName = file.fileName,
                            fileSize = file.fileSize,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            exportNote?.let {
                Text(
                    text = it,
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                OrbitButton(
                    label = "Copy invoice",
                    onClick = {
                        clipboard.setText(AnnotatedString(record.invoiceTextSnapshot()))
                        exportNote = "Copied to clipboard"
                    },
                    variant = OrbitButtonVariant.Secondary,
                    size = OrbitButtonSize.Large,
                    icon = OrbitIcons.Copy,
                    modifier = Modifier.weight(1f),
                    shape = OrbitTheme.shapeTokens.card,
                )
                OrbitButton(
                    label = "Export PDF",
                    onClick = {
                        val ok = exportPdf(record.number, record.invoiceTextSnapshot())
                        exportNote = if (ok) {
                            "Opening share sheet…"
                        } else {
                            "PDF export failed"
                        }
                    },
                    variant = OrbitButtonVariant.Primary,
                    size = OrbitButtonSize.Large,
                    icon = OrbitIcons.Download,
                    modifier = Modifier.weight(1f),
                    shape = OrbitTheme.shapeTokens.card,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.InvoiceDocSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing
    Spacer(modifier = Modifier.height(spacing.md))
    OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
    Spacer(modifier = Modifier.height(spacing.md))
    Text(
        text = title.uppercase(),
        style = OrbitTheme.extendedTypography.cardLabel,
        color = OrbitTheme.contentColors.textSecondary,
    )
    Spacer(modifier = Modifier.height(spacing.sm))
    content()
}

@Composable
private fun InvoiceOuterSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
        Text(
            text = title.uppercase(),
            style = OrbitTheme.extendedTypography.sectionLabel,
            color = OrbitTheme.contentColors.textSecondary,
        )
        content()
    }
}

@Composable
private fun PartyBlock(
    title: String,
    party: InvoiceParty,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xxs)) {
        Text(
            text = title.uppercase(),
            style = OrbitTheme.extendedTypography.cardLabel,
            color = content.textSecondary,
        )
        if (party.name.isNotBlank()) {
            Text(party.name, style = OrbitTheme.typography.bodyLarge, color = content.textPrimary)
        }
        if (party.address.isNotBlank()) {
            Text(party.address, style = OrbitTheme.typography.bodySmall, color = content.textSecondary)
        }
        if (party.gstin.isNotBlank()) {
            Text(
                "GSTIN ${party.gstin}",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
        }
    }
}

@Composable
private fun LineItemsTable(record: InvoiceRecord) {
    val content = OrbitTheme.contentColors
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "#",
            style = OrbitTheme.extendedTypography.tableHeader,
            color = content.textTertiary,
            modifier = Modifier.weight(0.12f),
        )
        Text(
            text = "Desc",
            style = OrbitTheme.extendedTypography.tableHeader,
            color = content.textTertiary,
            modifier = Modifier.weight(0.44f),
        )
        Text(
            text = "Qty",
            style = OrbitTheme.extendedTypography.tableHeader,
            color = content.textTertiary,
            modifier = Modifier.weight(0.14f),
        )
        Text(
            text = "Rate",
            style = OrbitTheme.extendedTypography.tableHeader,
            color = content.textTertiary,
            modifier = Modifier.weight(0.15f),
        )
        Text(
            text = "Amt",
            style = OrbitTheme.extendedTypography.tableHeader,
            color = content.textTertiary,
            modifier = Modifier.weight(0.15f),
        )
    }
    Spacer(modifier = Modifier.height(OrbitTheme.spacing.sm))
    record.lines.forEachIndexed { index, line ->
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${index + 1}",
                style = OrbitTheme.extendedTypography.tableNumeric,
                color = content.textPrimary,
                modifier = Modifier.weight(0.12f),
            )
            Text(
                text = line.description,
                style = OrbitTheme.typography.bodySmall,
                color = content.textPrimary,
                modifier = Modifier.weight(0.44f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = line.quantity.toLong().toString(),
                style = OrbitTheme.extendedTypography.tableNumeric,
                color = content.textPrimary,
                modifier = Modifier.weight(0.14f),
            )
            Text(
                text = formatInr(line.rate),
                style = OrbitTheme.extendedTypography.tableNumeric,
                color = content.textPrimary,
                modifier = Modifier.weight(0.15f),
            )
            Text(
                text = formatInr(line.amount),
                style = OrbitTheme.extendedTypography.tableNumeric,
                color = content.textPrimary,
                modifier = Modifier.weight(0.15f),
            )
        }
        Spacer(modifier = Modifier.height(OrbitTheme.spacing.xs))
    }
}

@Composable
private fun TotalsBlock(record: InvoiceRecord) {
    val content = OrbitTheme.contentColors
    Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xs)) {
        TotalRow("Subtotal", formatInr(record.subtotal))
        if (record.applyGst) {
            TotalRow("CGST ${record.cgstPercent.toLong()}%", formatInr(record.cgstAmount))
            TotalRow("SGST ${record.sgstPercent.toLong()}%", formatInr(record.sgstAmount))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "TOTAL",
                style = OrbitTheme.typography.titleMedium,
                color = content.textSecondary,
            )
            Text(
                text = formatInr(record.grandTotal),
                style = OrbitTheme.extendedTypography.metricMedium,
                color = content.textPrimary,
            )
        }
    }
}

@Composable
private fun TotalRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.bodyMedium,
            color = OrbitTheme.contentColors.textSecondary,
        )
        Text(
            text = value,
            style = OrbitTheme.extendedTypography.tableNumeric,
            color = OrbitTheme.contentColors.textPrimary,
        )
    }
}

@Composable
private fun BankBlock(bank: InvoiceBankDetails) {
    val content = OrbitTheme.contentColors
    Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xxs)) {
        Text(bank.accountHolder, style = OrbitTheme.typography.bodyLarge, color = content.textPrimary)
        Text(bank.bankName, style = OrbitTheme.typography.bodyMedium, color = content.textSecondary)
        Text(
            "A/c ${bank.accountNumber}",
            style = OrbitTheme.extendedTypography.reference,
            color = content.textPrimary,
        )
        Text(
            "IFSC ${bank.ifsc}",
            style = OrbitTheme.extendedTypography.reference,
            color = content.textPrimary,
        )
        if (bank.upiId.isNotBlank()) {
            Text(
                "UPI ${bank.upiId}",
                style = OrbitTheme.typography.bodyMedium,
                color = content.textSecondary,
            )
        }
    }
}
