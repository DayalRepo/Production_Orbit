package com.orbitai.erp.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.backhandler.BackHandler

@Composable
actual fun OrbitBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
