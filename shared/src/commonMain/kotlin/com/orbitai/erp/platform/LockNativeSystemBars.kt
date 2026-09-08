package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/**
 * Keeps status / navigation bar icon contrast tied to the **system** night mode only.
 * Call from [com.orbitai.erp.App] so it re-asserts after in-app theme toggles.
 */
@Composable
expect fun LockNativeSystemBars()
