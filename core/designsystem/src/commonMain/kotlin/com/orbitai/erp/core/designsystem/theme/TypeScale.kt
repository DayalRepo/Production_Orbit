package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/** A size paired with its line height. Both are `sp`, so both scale with the user's font setting. */
@Immutable
data class OrbitFontMetrics(
    val size: TextUnit,
    val lineHeight: TextUnit,
)

/**
 * The platform type scale.
 *
 * Android and iOS get different values because their conventions differ — a 16sp body on Android
 * against a 17pt body on iOS — and forcing one onto the other is what makes a cross-platform app
 * feel unpolished. Both are built on a 1.125 ratio.
 *
 * Everything is expressed in `sp`, never `dp` or absolute `px`, so Android's font-size slider and
 * (once bridged) iOS Dynamic Type both take effect.
 */
@Immutable
data class OrbitTypeScale(
    val baseSize: TextUnit,
    val scaleRatio: Float,

    /** Above the spec sheet's H1, derived by applying [scaleRatio]. Dashboard hero figures only. */
    val displayLarge: OrbitFontMetrics,
    val displayMedium: OrbitFontMetrics,
    val displaySmall: OrbitFontMetrics,

    val h1: OrbitFontMetrics,
    val h2: OrbitFontMetrics,
    val h3: OrbitFontMetrics,
    val h4: OrbitFontMetrics,
    val body: OrbitFontMetrics,
    val small: OrbitFontMetrics,
    val caption: OrbitFontMetrics,

    /**
     * Hard floor for any text in the product. Nothing in the scale may go below it: Apple treats
     * 11pt as the absolute minimum and Material 11sp, and the spec sheets put Caption at 12 on both
     * platforms, so 12 is the floor everywhere.
     */
    val minimumSize: TextUnit,

    /**
     * Line-height multiplier for long-form reading (WCAG 1.4.12). The scale's own line heights suit
     * UI labels; multi-paragraph content — a site report, an audit note — should use this instead.
     */
    val longFormLineHeightRatio: Float = 1.5f,

    /**
     * Letter spacing for a button label, per button size.
     *
     * Tracking has to move against size, which is why these are three values and not one. A small
     * button's label is set at the same size as a medium one but in a heavier weight, and heavier
     * letters at a short measure crowd each other, so it opens up; a large label is big enough that
     * the default spacing already looks loose, so it tightens slightly. This is also the token most
     * likely to diverge between platforms — the two system fonts do not track identically — which is
     * the reason it belongs here rather than as a constant inside `OrbitButton`.
     */
    val buttonTrackingSm: TextUnit = 0.4.sp,
    val buttonTrackingMd: TextUnit = 0.15.sp,
    val buttonTrackingLg: TextUnit = (-0.1).sp,
)

/**
 * Android — Material Design. Base 16sp, ratio 1.125.
 *
 * H1..Caption are exactly the values in `Size & Line Height (Android).xlsx`.
 */
internal val AndroidTypeScale = OrbitTypeScale(
    baseSize = 16.sp,
    scaleRatio = 1.125f,

    displayLarge = OrbitFontMetrics(46.sp, 54.sp),
    displayMedium = OrbitFontMetrics(40.sp, 48.sp),
    displaySmall = OrbitFontMetrics(36.sp, 44.sp),

    h1 = OrbitFontMetrics(32.sp, 40.sp),
    h2 = OrbitFontMetrics(28.sp, 36.sp),
    h3 = OrbitFontMetrics(24.sp, 32.sp),
    h4 = OrbitFontMetrics(22.sp, 28.sp),
    body = OrbitFontMetrics(16.sp, 24.sp),
    small = OrbitFontMetrics(14.sp, 20.sp),
    caption = OrbitFontMetrics(12.sp, 16.sp),

    minimumSize = 12.sp,
)

/**
 * iOS — Human Interface Guidelines. Base 17pt, ratio 1.125.
 *
 * H1..Caption are exactly the values in `Size & Line Height (iOS).xlsx`. Expressed in `sp` because
 * Compose has no `pt` unit; at iOS's 1x reference scale a point and an `sp` coincide.
 */
internal val IosTypeScale = OrbitTypeScale(
    baseSize = 17.sp,
    scaleRatio = 1.125f,

    displayLarge = OrbitFontMetrics(48.sp, 56.sp),
    displayMedium = OrbitFontMetrics(43.sp, 50.sp),
    displaySmall = OrbitFontMetrics(38.sp, 46.sp),

    h1 = OrbitFontMetrics(34.sp, 41.sp),
    h2 = OrbitFontMetrics(28.sp, 34.sp),
    h3 = OrbitFontMetrics(22.sp, 28.sp),
    h4 = OrbitFontMetrics(20.sp, 25.sp),
    body = OrbitFontMetrics(17.sp, 22.sp),
    small = OrbitFontMetrics(15.sp, 20.sp),
    caption = OrbitFontMetrics(12.sp, 16.sp),

    minimumSize = 12.sp,
)
