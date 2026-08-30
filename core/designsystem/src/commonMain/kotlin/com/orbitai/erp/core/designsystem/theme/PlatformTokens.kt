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
        iconMd = 20.dp,
        iconLg = 24.dp,
        iconXl = 28.dp,
        iconXxl = 32.dp,

        avatarXs = 24.dp,
        avatarSm = 32.dp,
        avatarMd = 40.dp,
        avatarLg = 64.dp,
        avatarXl = 80.dp,

        // Apple's minimum is 44pt, Material's is 48dp. Neither is padded to match the other.
        minTouchTarget = 44.dp,
    ),
    lightContentColors = IosLightContentColors,
    darkContentColors = IosDarkContentColors,
    topBarTitleAlignment = OrbitTitleAlignment.Center,
)

internal fun platformTokens(platform: OrbitPlatform): OrbitPlatformTokens = when (platform) {
    OrbitPlatform.Android -> AndroidPlatformTokens
    OrbitPlatform.Ios -> IosPlatformTokens
}
