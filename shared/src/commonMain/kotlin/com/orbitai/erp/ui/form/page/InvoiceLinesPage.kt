package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.input.OrbitQuantityField
import com.orbitai.erp.core.designsystem.component.input.OrbitSwitch
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.card.amountInWordsInr
import com.orbitai.erp.ui.card.formatInr
import com.orbitai.erp.ui.form.FormFieldLabel
import com.orbitai.erp.ui.form.FormSection
import com.orbitai.erp.ui.form.InvoiceLineDraft
import kotlin.random.Random

@Composable
fun InvoiceLinesPage(
    lines: List<InvoiceLineDraft>,
    onReplaceLine: (String, (InvoiceLineDraft) -> InvoiceLineDraft) -> Unit,
    onAddLine: () -> Unit,
    onRemoveLine: (String) -> Unit,
    applyGst: Boolean,
    onApplyGstChange: (Boolean) -> Unit,
    cgstPercent: Double,
    onCgstPercentChange: (Double) -> Unit,
    sgstPercent: Double,
    onSgstPercentChange: (Double) -> Unit,
    subtotal: Double,
    cgstAmount: Double,
    sgstAmount: Double,
    grandTotal: Double,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FormSection(title = "Line items", showDivider = false) {
            lines.forEach { line ->
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        FormFieldLabel("Line item", modifier = Modifier.weight(1f))
                        if (lines.size > 1) {
                            OrbitIconButton(
                                contentDescription = "Remove line",
                                onClick = { onRemoveLine(line.id) },
                                icon = OrbitIcons.Cancel,
                                style = OrbitIconButtonStyle.Neutral,
                            )
                        }
                    }
                    OrbitTextField(
                        value = line.description,
                        onValueChange = { value ->
                            onReplaceLine(line.id) { it.copy(description = value) }
                        },
                        label = "Description",
                        placeholder = "Slab concreting",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        OrbitQuantityField(
                            value = line.quantity.toInt().coerceAtLeast(1),
                            onValueChange = { qty ->
                                onReplaceLine(line.id) { it.copy(quantity = qty.toDouble()) }
                            },
                            label = "Qty",
                            range = 1..999_999,
                            modifier = Modifier.weight(1f),
                        )
                        OrbitTextField(
                            value = if (line.rate == 0.0) "" else line.rate.trimZeros(),
                            onValueChange = { raw ->
                                val parsed = raw.filter { it.isDigit() || it == '.' }.toDoubleOrNull()
                                onReplaceLine(line.id) { it.copy(rate = parsed ?: 0.0) }
                            },
                            label = "Rate (₹)",
                            placeholder = "0",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Text(
                        text = "Amount ${formatInr(line.quantity * line.rate)}",
                        style = OrbitTheme.extendedTypography.tableNumeric,
                        color = content.textSecondary,
                    )
                }
            }
            OrbitButton(
                label = "Add line",
                onClick = onAddLine,
                variant = OrbitButtonVariant.Secondary,
                size = OrbitButtonSize.Medium,
                icon = OrbitIcons.Add,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        FormSection(title = "GST & totals") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Text(
                    text = "Apply GST",
                    style = OrbitTheme.typography.bodyLarge,
                    color = content.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                OrbitSwitch(
                    checked = applyGst,
                    onCheckedChange = onApplyGstChange,
                    contentDescription = "Apply GST",
                )
            }
            if (applyGst) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    OrbitTextField(
                        value = cgstPercent.trimZeros(),
                        onValueChange = { raw ->
                            onCgstPercentChange(
                                raw.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0,
                            )
                        },
                        label = "CGST %",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                    OrbitTextField(
                        value = sgstPercent.trimZeros(),
                        onValueChange = { raw ->
                            onSgstPercentChange(
                                raw.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0,
                            )
                        },
                        label = "SGST %",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                TotalsRow("Subtotal", formatInr(subtotal))
                if (applyGst) {
                    TotalsRow("CGST", formatInr(cgstAmount))
                    TotalsRow("SGST", formatInr(sgstAmount))
                }
                TotalsRow("Total", formatInr(grandTotal), emphasize = true)
                Text(
                    text = amountInWordsInr(grandTotal),
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun TotalsRow(
    label: String,
    value: String,
    emphasize: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = if (emphasize) {
                OrbitTheme.typography.titleMedium
            } else {
                OrbitTheme.typography.bodyMedium
            },
            color = OrbitTheme.contentColors.textSecondary,
        )
        Text(
            text = value,
            style = if (emphasize) {
                OrbitTheme.extendedTypography.metricMedium
            } else {
                OrbitTheme.extendedTypography.tableNumeric
            },
            color = OrbitTheme.contentColors.textPrimary,
        )
    }
}

private fun Double.trimZeros(): String {
    val asLong = toLong()
    return if (this == asLong.toDouble()) asLong.toString() else toString()
}

fun newInvoiceLineId(): String = "line-${Random.nextLong()}"
