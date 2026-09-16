package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/**
 * Returns a callback that writes a light-plate PDF and opens the platform share / view sheet.
 */
@Composable
expect fun rememberInvoicePdfExporter(): (fileName: String, body: String) -> Boolean
