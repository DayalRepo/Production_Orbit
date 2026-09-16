package com.orbitai.erp.platform

import androidx.compose.runtime.Composable
import com.orbitai.erp.ui.card.InvoicePdfAction
import com.orbitai.erp.ui.card.InvoicePdfModel

/**
 * Writes an invoice-only A4 PDF, then either saves it to Downloads (Download) or opens the share sheet (Share).
 */
@Composable
expect fun rememberInvoicePdfExporter(): (InvoicePdfModel, InvoicePdfAction) -> Boolean
