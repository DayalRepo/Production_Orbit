package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A scrolling gallery of everything in the design system, for reviewing it on a real device.
 *
 * This is a review surface, not a product screen — a phone in the hand is the only place the glass
 * tint, the icon stroke weight and the dark-theme contrast can actually be judged, and a canvas or a
 * screenshot flatters all three. It should be deleted, or moved behind a debug flag, once real
 * screens exist.
 *
 * The groups live in sibling files — [ButtonGalleryPage], [BadgeGalleryPage] — one per component
 * folder in the library. This file is only the frame: the scroll, the insets, the padding and the
 * theme toggle. Adding a component group means adding a file and one line here.
 */
@Composable
fun ComponentGalleryScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing

    // The system bars, then the screen padding. Both are needed and the order matters: the app draws
    // edge to edge, so without the inset the first row sits under the status bar clock and the last
    // under the gesture bar, and applying it after the scroll modifier is what keeps the inset out of
    // the scrolling content — otherwise the top gap scrolls away with everything else.
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.screenVertical,
            ),
        verticalArrangement = Arrangement.spacedBy(spacing.xxl),
    ) {
        // No title, just the toggle. The theme is legible from the screen itself, and a heading
        // naming the design system is the one thing on a review surface that never gets reviewed.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            // Shows the mode you are switching *to*, which is the convention every OS settings
            // toggle uses. Showing the current mode reads as a status light and nobody taps it.
            OrbitIconButton(
                contentDescription = if (isDark) {
                    "Switch to light theme"
                } else {
                    "Switch to dark theme"
                },
                onClick = onToggleTheme,
                icon = if (isDark) OrbitIcons.Sun else OrbitIcons.Moon,
                style = OrbitIconButtonStyle.Neutral,
            )
        }

        DateTimeGalleryPage()
        ProgressGalleryPage()
        MessageBubbleGalleryPage()
        ChecklistGalleryPage()
        AvatarGalleryPage(isDark = isDark, onToggleTheme = onToggleTheme)
        DisplayGalleryPage()
        StateGalleryPage()
        InputGalleryPage()
        AssignGalleryPage()
        ComposerGalleryPage()
        ButtonGalleryPage()
        BadgeGalleryPage()

        // On Android the gesture bar sits directly under the last row of badges.
        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}
