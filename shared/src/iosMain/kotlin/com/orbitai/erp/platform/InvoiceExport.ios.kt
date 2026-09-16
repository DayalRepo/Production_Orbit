package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberInvoicePdfExporter(): (fileName: String, body: String) -> Boolean {
    return { _, _ -> true }
}
