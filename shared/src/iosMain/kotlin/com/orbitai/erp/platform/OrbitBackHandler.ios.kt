package com.orbitai.erp.platform

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@Composable
actual fun OrbitBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    val state = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = state,
        isBackEnabled = enabled,
        onBackCompleted = onBack,
    )
}
