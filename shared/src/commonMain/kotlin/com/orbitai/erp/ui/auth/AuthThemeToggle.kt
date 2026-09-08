package com.orbitai.erp.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Top-right theme control for auth screens (login / verify).
 */
@Composable
fun AuthThemeToggle(
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = spacing.sm, end = spacing.sm),
        contentAlignment = Alignment.TopEnd,
    ) {
        OrbitIconButton(
            contentDescription = if (isDark) "Switch to light theme" else "Switch to dark theme",
            onClick = { onThemeChange(!isDark) },
            icon = if (isDark) OrbitIcons.Sun else OrbitIcons.Moon,
            style = OrbitIconButtonStyle.Neutral,
        )
    }
}
