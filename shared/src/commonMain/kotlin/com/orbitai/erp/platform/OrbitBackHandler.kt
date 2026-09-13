package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/**
 * Hooks the platform back gesture so a Compose screen can pop instead of leaving the app.
 *
 * Android uses the system back button / predictive-back swipe. iOS uses the leading-edge swipe.
 */
@Composable
expect fun OrbitBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
