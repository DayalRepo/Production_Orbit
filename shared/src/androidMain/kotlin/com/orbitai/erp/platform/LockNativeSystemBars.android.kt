package com.orbitai.erp.platform

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

@Composable
actual fun LockNativeSystemBars() {
    val view = LocalView.current
    val systemDark = isSystemInDarkTheme()
    // Read app theme so this SideEffect re-runs after an in-app toggle and undoes any OEM/auto flip.
    @Suppress("UNUSED_VARIABLE")
    val appDark = OrbitTheme.isDark

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !systemDark
            isAppearanceLightNavigationBars = !systemDark
        }
    }
}
