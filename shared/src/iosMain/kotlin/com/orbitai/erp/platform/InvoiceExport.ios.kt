package com.orbitai.erp.platform

import androidx.compose.runtime.Composable
import com.orbitai.erp.ui.card.InvoicePdfAction
import com.orbitai.erp.ui.card.InvoicePdfModel

@Composable
actual fun rememberInvoicePdfExporter(): (InvoicePdfModel, InvoicePdfAction) -> Boolean {
    return { _, _ -> true }
}
