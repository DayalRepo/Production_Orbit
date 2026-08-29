package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material window size classes, computed locally rather than via the adaptive artifact so the
 * design system stays dependency-free across Android and iOS.
 */
enum class WindowWidthClass { Compact, Medium, Expanded }

enum class WindowHeightClass { Compact, Medium, Expanded }

/**
 * Which navigation affordance a window of this size should use. Drives whether a role's shell
 * renders a bottom bar (phone), a rail (tablet portrait) or a permanent drawer (tablet landscape).
 */
enum class NavigationLayout { BottomBar, Rail, PermanentDrawer }

@Immutable
data class WindowSize(
    val width: Dp,
    val height: Dp,
    val widthClass: WindowWidthClass,
    val heightClass: WindowHeightClass,
) {
    val isCompact: Boolean get() = widthClass == WindowWidthClass.Compact
    val isMedium: Boolean get() = widthClass == WindowWidthClass.Medium
    val isExpanded: Boolean get() = widthClass == WindowWidthClass.Expanded

    /** True when there is room to show a list and a detail pane side by side. */
    val supportsTwoPane: Boolean get() = widthClass != WindowWidthClass.Compact

    val navigationLayout: NavigationLayout
        get() = when (widthClass) {
            WindowWidthClass.Compact -> NavigationLayout.BottomBar
            WindowWidthClass.Medium -> NavigationLayout.Rail
            WindowWidthClass.Expanded -> NavigationLayout.PermanentDrawer
        }

    /** Sensible dashboard grid column count for this width. */
    val dashboardColumns: Int
        get() = when (widthClass) {
            WindowWidthClass.Compact -> 1
            WindowWidthClass.Medium -> 2
            WindowWidthClass.Expanded -> 4
        }

    companion object {
        val MediumWidthBreakpoint = 600.dp
        val ExpandedWidthBreakpoint = 840.dp
        val MediumHeightBreakpoint = 480.dp
        val ExpandedHeightBreakpoint = 900.dp

        fun of(width: Dp, height: Dp): WindowSize = WindowSize(
            width = width,
            height = height,
            widthClass = when {
                width < MediumWidthBreakpoint -> WindowWidthClass.Compact
                width < ExpandedWidthBreakpoint -> WindowWidthClass.Medium
                else -> WindowWidthClass.Expanded
            },
            heightClass = when {
                height < MediumHeightBreakpoint -> WindowHeightClass.Compact
                height < ExpandedHeightBreakpoint -> WindowHeightClass.Medium
                else -> WindowHeightClass.Expanded
            },
        )
    }
}

val LocalWindowSize = staticCompositionLocalOf {
    WindowSize.of(width = 400.dp, height = 800.dp)
}

/**
 * Measures the available space and publishes it as [LocalWindowSize]. Placed once inside
 * `OrbitTheme`, so screens can read window size without measuring it themselves.
 */
@Composable
fun ProvideWindowSize(content: @Composable () -> Unit) {
    BoxWithConstraints {
        val windowSize = WindowSize.of(width = maxWidth, height = maxHeight)
        CompositionLocalProvider(LocalWindowSize provides windowSize) {
            content()
        }
    }
}
