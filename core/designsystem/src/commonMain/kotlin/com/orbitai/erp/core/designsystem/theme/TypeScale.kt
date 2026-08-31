package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * A size paired with its line height and tracking. All three are `sp`, so all three scale with the
 * user's font setting.
 *
 * [tracking] lives here rather than being a constant applied to everything because it has to move
 * against size, and in the opposite direction. Large type set at the spacing that suits body text
 * looks gappy — the letters are already far apart in absolute terms — so the display and heading
 * tiers pull in to negative values. Small type has the reverse problem: at caption size the
 * letterforms crowd, so it opens up. A single tracking value across the scale is wrong at both ends
 * and only correct in the middle.
 */
@Immutable
data class OrbitFontMetrics(
    val size: TextUnit,
    val lineHeight: TextUnit,
    val tracking: TextUnit = 0.sp,
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
 * ### Mapping the six-tier spec onto ten slots
 *
 * The spec defines six tiers — display, h1, h2, subheading, body, caption — and this scale carries
 * ten, because Material's `Typography` wants three display slots and four heading slots. The extra
 * slots are filled by the tier nearest them rather than by inventing sizes to fill the gaps:
 *
 * - all three display slots take the single display tier. A product with one display style has one
 *   display size; three near-identical sizes a point apart would be a distinction no reader could
 *   see and every implementer would have to guess between. They stay separate fields so a later
 *   scale can split them without touching call sites.
 * - `h3` takes subheading, and `h4` takes body size at heading weight — which is the shape Material
 *   itself uses, where `titleMedium` and `bodyLarge` are the same size in different weights.
 * - `small` is the step between body and caption, needed by `bodyMedium` and `labelLarge`. It is the
 *   one size here the spec does not state, and it is the midpoint rather than a ratio step, because
 *   at this end of the scale the ratio produces fractions that round onto sizes already in use.
 */
internal val AndroidTypeScale = OrbitTypeScale(
    baseSize = 16.sp,
    scaleRatio = 1.125f,

    displayLarge = OrbitFontMetrics(23.sp, 27.sp, (-0.5).sp),
    displayMedium = OrbitFontMetrics(23.sp, 27.sp, (-0.5).sp),
    displaySmall = OrbitFontMetrics(23.sp, 27.sp, (-0.5).sp),

    h1 = OrbitFontMetrics(20.sp, 24.sp, (-0.25).sp),
    h2 = OrbitFontMetrics(18.sp, 23.sp),
    // Subheading.
    h3 = OrbitFontMetrics(17.sp, 23.sp),
    h4 = OrbitFontMetrics(16.sp, 23.sp),
    body = OrbitFontMetrics(16.sp, 23.sp, 0.15.sp),
    small = OrbitFontMetrics(15.sp, 21.sp, 0.15.sp),
    caption = OrbitFontMetrics(14.sp, 19.sp, 0.2.sp),

    minimumSize = 12.sp,
)

/**
 * iOS — Human Interface Guidelines. Base 17pt, ratio 1.125.
 *
 * The same six tiers as [AndroidTypeScale] one step larger, and mapped onto the ten slots the same
 * way — see that scale's note for why. Expressed in `sp` because Compose has no `pt` unit; at iOS's
 * 1x reference scale a point and an `sp` coincide.
 *
 * The step up is not decoration. iOS sets its body text at 17pt where Material sets 16sp, and an app
 * that ships Android's sizes onto iOS reads as slightly shrunken next to the system UI around it —
 * which is most of what "does not feel native" turns out to mean on inspection.
 */
internal val IosTypeScale = OrbitTypeScale(
    baseSize = 17.sp,
    scaleRatio = 1.125f,

    displayLarge = OrbitFontMetrics(24.sp, 28.sp, (-0.5).sp),
    displayMedium = OrbitFontMetrics(24.sp, 28.sp, (-0.5).sp),
    displaySmall = OrbitFontMetrics(24.sp, 28.sp, (-0.5).sp),

    h1 = OrbitFontMetrics(22.sp, 27.sp, (-0.25).sp),
    h2 = OrbitFontMetrics(19.sp, 24.sp),
    // Subheading.
    h3 = OrbitFontMetrics(18.sp, 24.sp),
    h4 = OrbitFontMetrics(17.sp, 24.sp),
    body = OrbitFontMetrics(17.sp, 24.sp, 0.15.sp),
    small = OrbitFontMetrics(16.sp, 22.sp, 0.15.sp),
    caption = OrbitFontMetrics(15.sp, 20.sp, 0.2.sp),

    minimumSize = 12.sp,
)
