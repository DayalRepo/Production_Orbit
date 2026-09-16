package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.card.InvoiceAttachment
import com.orbitai.erp.ui.card.InvoiceBankDetails
import com.orbitai.erp.ui.card.isValidIfsc
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.attachment.ManagedFileUpload
import com.orbitai.erp.ui.form.FormSection

@Composable
fun InvoicePaymentPage(
    bank: InvoiceBankDetails,
    onBankChange: (InvoiceBankDetails) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val ifscState = when {
        bank.ifsc.isBlank() -> OrbitFieldState.Default
        isValidIfsc(bank.ifsc) -> OrbitFieldState.Default
        else -> OrbitFieldState.Error
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FormSection(title = "Bank details", showDivider = false) {
            OrbitTextField(
                value = bank.accountHolder,
                onValueChange = { onBankChange(bank.copy(accountHolder = it)) },
                label = "Account holder",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitTextField(
                value = bank.bankName,
                onValueChange = { onBankChange(bank.copy(bankName = it)) },
                label = "Bank name",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitTextField(
                value = bank.accountNumber,
                onValueChange = {
                    onBankChange(bank.copy(accountNumber = it.filter { ch -> ch.isDigit() }))
                },
                label = "Account number",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitTextField(
                value = bank.ifsc,
                onValueChange = { onBankChange(bank.copy(ifsc = it.uppercase())) },
                label = "IFSC",
                placeholder = "HDFC0001234",
                state = ifscState,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        FormSection(title = "UPI") {
            OrbitTextField(
                value = bank.upiId,
                onValueChange = { onBankChange(bank.copy(upiId = it)) },
                label = "UPI ID (optional)",
                placeholder = "name@bank",
                modifier = Modifier.fillMaxWidth(),
            )
            ManagedFileUpload(
                modifier = Modifier.fillMaxWidth(),
                browseLabel = "Upload QR",
                cameraLabel = null,
                photosOnly = true,
                dropZoneTitle = "Upload UPI QR image",
                dropZoneHint = "JPEG or PNG, up to 50 MB.",
                onCompleted = { id, name, size ->
                    onBankChange(
                        bank.copy(qrAttachment = InvoiceAttachment(id, name, size)),
                    )
                },
            )
            bank.qrAttachment?.let { qr ->
                FileAttachmentRow(
                    fileName = qr.fileName,
                    fileSize = qr.fileSize,
                    onRemove = { onBankChange(bank.copy(qrAttachment = null)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        FormSection(title = "Terms") {
            OrbitTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = "Payment notes / terms",
                placeholder = "Payment due within 15 days",
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
