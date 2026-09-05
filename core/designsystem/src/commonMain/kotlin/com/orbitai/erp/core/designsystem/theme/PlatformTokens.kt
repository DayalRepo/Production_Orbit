package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform

/** Android left-aligns top app bar titles; iOS centres navigation bar titles. */
enum class OrbitTitleAlignment { Start, Center }

/**
 * Everything that legitimately differs between Android and iOS, in one place.
 *
 * Adding a platform difference anywhere else in the design system is a smell — it should land here
 * so the divergence stays enumerable and testable.
 */
@Immutable
data class OrbitPlatformTokens(
    val platform: OrbitPlatform,
    val typeScale: OrbitTypeScale,
    val sizing: OrbitSizing,
    val lightContentColors: OrbitContentColors,
    val darkContentColors: OrbitContentColors,
    val topBarTitleAlignment: OrbitTitleAlignment,
)

/**
 * Icon and avatar sizes from `UI System Icons (Android & iOS).xlsx` and
 * `User Profile Avatars (Android & iOS).xlsx`. Where a sheet gives a range, the low end becomes the
 * smaller token and the high end the larger one rather than being averaged away.
 */
internal val AndroidPlatformTokens = OrbitPlatformTokens(
    platform = OrbitPlatform.Android,
    typeScale = AndroidTypeScale,
    sizing = OrbitSizing(
        // Inline 12–16dp, standard/toolbar 24dp, featured 32dp.
        iconXs = 12.dp,
        iconSm = 16.dp,
        iconMd = 24.dp,
        iconLg = 24.dp,
        iconXl = 32.dp,
        iconXxl = 32.dp,

        avatarXs = 24.dp,
        avatarSm = 40.dp,
        avatarMd = 48.dp,
        avatarLg = 64.dp,
        avatarXl = 88.dp,

        minTouchTarget = 48.dp,

        // Android's 48dp targets already carry their own air, so the explicit gap can be modest.
        composerControlGap = 4.dp,
        composerEdgeInset = 6.dp,

        // Six 48dp rows and a sliver of the seventh. Android's taller rows mean fewer options fit,
        // so the panel is given a little more room than iOS to land on a comparable option count.
        dropdownMaxHeight = 296.dp,

        // Shared chrome column: tab bar and bottom nav use the same edge inset.
        bottomNavEdgeInset = 10.dp,
        tabBarEdgeInset = 10.dp,
        tabBarItemGap = 20.dp,
        bottomNavSystemGap = 8.dp,
    ),
    lightContentColors = AndroidLightContentColors,
    darkContentColors = AndroidDarkContentColors,
    topBarTitleAlignment = OrbitTitleAlignment.Start,
)

internal val IosPlatformTokens = OrbitPlatformTokens(
    platform = OrbitPlatform.Ios,
    typeScale = IosTypeScale,
    sizing = OrbitSizing(
        // Inline 12–16pt, standard/toolbar 20–24pt, featured 28–32pt.
        iconXs = 12.dp,
        iconSm = 16.dp,
        // Was 20dp, with iconLg at 24 and iconXl at 28 — an iOS-only ladder from the days when these
        // were sized against SF Symbols' optical scale. The icon spec gives one column for both
        // platforms (16 / 24 / 32), and a shared ladder is worth more here than the half-step: the
        // per-platform difference that actually matters on iOS is the 44pt touch target below, which
        // is unchanged.
        iconMd = 24.dp,
        iconLg = 24.dp,
        iconXl = 32.dp,
        iconXxl = 32.dp,

        avatarXs = 24.dp,
        avatarSm = 32.dp,
        avatarMd = 40.dp,
        avatarLg = 64.dp,
        avatarXl = 80.dp,

        // Apple's minimum is 44pt, Material's is 48dp. Neither is padded to match the other.
        minTouchTarget = 44.dp,

        // More than Android, precisely because the targets are 4pt smaller. Matching the numbers
        // would leave the iOS row visibly tighter than the Android one for the same reason the
        // targets differ — the platform expects controls to sit a little further apart to make up
        // for being a little smaller.
        composerControlGap = 6.dp,
        composerEdgeInset = 8.dp,

        // Six 44pt rows and part of the seventh. Shorter than Android's cap and showing about the
        // same number of options, which is the thing worth matching across platforms — an identical
        // dp height would show iOS users an extra half row for no reason other than arithmetic.
        dropdownMaxHeight = 272.dp,

        // Same shared chrome column as Android, with a touch more edge air for the 44pt targets.
        bottomNavEdgeInset = 12.dp,
        tabBarEdgeInset = 12.dp,
        tabBarItemGap = 22.dp,
        bottomNavSystemGap = 8.dp,
    ),
    lightContentColors = IosLightContentColors,
    darkContentColors = IosDarkContentColors,
    topBarTitleAlignment = OrbitTitleAlignment.Center,
)

internal fun platformTokens(platform: OrbitPlatform): OrbitPlatformTokens = when (platform) {
    OrbitPlatform.Android -> AndroidPlatformTokens
    OrbitPlatform.Ios -> IosPlatformTokens
}
