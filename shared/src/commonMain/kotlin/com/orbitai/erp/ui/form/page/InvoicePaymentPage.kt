package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.card.InvoiceBankDetails
import com.orbitai.erp.ui.card.isValidIfsc
import com.orbitai.erp.ui.form.FormFieldLabel
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
        FormSection(title = "Bank", showDivider = false) {
            LabeledPaymentField("Account holder") {
                OrbitTextField(
                    value = bank.accountHolder,
                    onValueChange = { onBankChange(bank.copy(accountHolder = it)) },
                    label = "Account holder",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LabeledPaymentField("Bank name") {
                OrbitTextField(
                    value = bank.bankName,
                    onValueChange = { onBankChange(bank.copy(bankName = it)) },
                    label = "Bank name",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LabeledPaymentField("Account number") {
                OrbitTextField(
                    value = bank.accountNumber,
                    onValueChange = {
                        onBankChange(bank.copy(accountNumber = it.filter { ch -> ch.isDigit() }))
                    },
                    label = "Account number",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LabeledPaymentField("IFSC") {
                OrbitTextField(
                    value = bank.ifsc,
                    onValueChange = { onBankChange(bank.copy(ifsc = it.uppercase())) },
                    label = "IFSC",
                    placeholder = "HDFC0001234",
                    state = ifscState,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        FormSection(title = "UPI") {
            LabeledPaymentField("UPI ID") {
                OrbitTextField(
                    value = bank.upiId,
                    onValueChange = { onBankChange(bank.copy(upiId = it, qrAttachment = null)) },
                    label = "UPI ID (optional)",
                    placeholder = "name@bank",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        FormSection(title = "Notes") {
            LabeledPaymentField("Payment notes") {
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
}

@Composable
private fun LabeledPaymentField(
    heading: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm)) {
        FormFieldLabel(heading)
        content()
    }
}
