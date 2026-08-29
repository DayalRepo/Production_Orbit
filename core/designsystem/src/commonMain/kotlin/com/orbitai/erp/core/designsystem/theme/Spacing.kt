package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 4dp-based spacing scale. Use these instead of literal dp values so density can be tuned
 * globally (a Site Engineer on a phone in gloves needs larger targets than a CEO on a tablet).
 */
@Immutable
data class OrbitSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val giant: Dp = 48.dp,

    /** Horizontal padding for the outermost content container of a screen. */
    val screenHorizontal: Dp = 16.dp,
    /** Vertical padding at the top/bottom of scrollable screen content. */
    val screenVertical: Dp = 16.dp,
    /** Internal padding for cards and list rows. */
    val cardPadding: Dp = 16.dp,
    /** Gap between sibling cards in a list or grid. */
    val cardGap: Dp = 12.dp,
    /** Gap between labelled form fields. */
    val fieldGap: Dp = 16.dp,
    /** Minimum touch target, enforced on all interactive components. */
    val minTouchTarget: Dp = 48.dp,
)

@Immutable
data class OrbitSizing(
    val iconXs: Dp = 14.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 20.dp,
    val iconLg: Dp = 24.dp,
    val iconXl: Dp = 32.dp,

    val avatarSm: Dp = 24.dp,
    val avatarMd: Dp = 32.dp,
    val avatarLg: Dp = 40.dp,
    val avatarXl: Dp = 56.dp,

    val hairline: Dp = 1.dp,
    val border: Dp = 1.dp,
    val borderStrong: Dp = 2.dp,

    val buttonHeightSm: Dp = 36.dp,
    val buttonHeightMd: Dp = 44.dp,
    val buttonHeightLg: Dp = 52.dp,

    val fieldHeight: Dp = 56.dp,
    val topBarHeight: Dp = 56.dp,
    val listRowMinHeight: Dp = 64.dp,

    /** Width of the persistent navigation rail on medium windows. */
    val navRailWidth: Dp = 80.dp,
    /** Width of the expanded navigation drawer on large windows. */
    val navDrawerWidth: Dp = 280.dp,
    /** Cap on text measure so dashboards stay readable on wide screens. */
    val maxContentWidth: Dp = 1200.dp,
)

internal val LocalOrbitSpacing = staticCompositionLocalOf { OrbitSpacing() }
internal val LocalOrbitSizing = staticCompositionLocalOf { OrbitSizing() }
