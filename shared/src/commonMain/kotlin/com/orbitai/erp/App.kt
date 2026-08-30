package com.orbitai.erp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.gallery.ComponentGalleryScreen

@Composable
fun App() {
    // Null means "follow the OS", which is the state the app should ship in. The override only
    // exists so the design system can be reviewed in both themes without leaving the app to change
    // a system setting; it is in-memory on purpose and resets on relaunch.
    var themeOverride by remember { mutableStateOf<Boolean?>(null) }
    val darkTheme = themeOverride ?: isSystemInDarkTheme()

    OrbitTheme(darkTheme = darkTheme) {
        // Placeholder landing surface until sign-in and the role dashboards exist. Session gating
        // lives on the branch that adds authentication.
        ComponentGalleryScreen(
            isDark = darkTheme,
            onToggleTheme = { themeOverride = !darkTheme },
        )
    }
}
