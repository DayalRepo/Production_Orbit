package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class OrbitElevation(
    val none: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,

    val card: Dp = 1.dp,
    val cardRaised: Dp = 3.dp,
    val topBar: Dp = 0.dp,
    val topBarScrolled: Dp = 3.dp,
    val bottomBar: Dp = 3.dp,
    val dialog: Dp = 6.dp,
    val menu: Dp = 3.dp,
    val fab: Dp = 6.dp,
)

internal val LocalOrbitElevation = staticCompositionLocalOf { OrbitElevation() }

/**
 * One rung of the elevation ladder: how far off the surface a thing sits, and what that looks like.
 *
 * ### Two parameter sets, because the two themes achieve depth differently
 *
 * A drop shadow is light that a raised object blocks. That works on a pale page, where the blocked
 * light is visibly missing. On a `#121214` page there is no light to block — a black shadow on a
 * near-black background is *nothing*, and turning up its opacity only produces a slightly different
 * shade of nothing. Dark themes therefore convey height the other way round: a raised surface is
 * *lighter*, as if it were closer to the light rather than casting away from it.
 *
 * So each tier carries a light-mode shadow ([offsetY], [blur], [opacity]) and a dark-mode surface
 * ([darkSurface]). Only one of the two is ever used, decided by the theme.
 *
 * ### The shadow parameters move together, and not all in the same direction
 *
 * Going up a tier: the offset grows, the blur grows faster, and the opacity — counter-intuitively —
 * *also* grows. The rule usually quoted is "higher means softer and more transparent", and that is
 * true of the shadow's *peak density*, which is what a bigger blur radius spreads out. Total ink has
 * to go up or a modal would cast a fainter shadow than a chip; the blur is what keeps the result
 * soft rather than dark.
 *
 * All of it stays between 4% and 16% black. Past that a shadow stops looking like absent light and
 * starts looking like grey paint, which is the single most common way a UI reads as cheap.
 *
 * @param offsetY how far down the shadow sits. Always positive: the light is above, always, and a
 *   shadow that wraps evenly on all four sides reads as a glow — a state — rather than as height.
 * @param blur the softening radius. Grows faster than the offset, so higher things are hazier.
 * @param opacity black alpha at the shadow's densest point.
 * @param darkSurface the container fill this tier takes in the dark theme, replacing the shadow.
 * @param darkBorder whether the dark theme should also draw a hairline rim. Needed at the low tiers,
 *   where the tonal step off the background is small enough to be lost on a dim screen.
 */
@Immutable
data class OrbitElevationLevel(
    val offsetY: Dp,
    val blur: Dp,
    val opacity: Float,
    val darkSurface: Color,
    val darkBorder: Boolean,
)

/**
 * The elevation ladder, in five rungs.
 *
 * Five rather than Material's fifteen. Every extra rung is a decision someone has to make and a
 * distinction a user has to see, and past about five the distinctions stop being visible: the
 * difference between a 9dp and a 10dp shadow is not something anyone reads as a difference in
 * height, it is just two shadows.
 *
 * The rungs are named for what sits on them rather than for their depth, because the question at a
 * call site is never "how many dp" — it is "is this a card or a dialog".
 */
@Immutable
object OrbitShadow {

    /**
     * Flat. Inset boxes, search fields, dividers.
     *
     * Not "a very small shadow" but the deliberate absence of one. These elements are *recessed*
     * into their container, not raised off it, and a shadow would claim the opposite. The dark theme
     * still gets a rim, because an inset box on a dark page has no fill contrast to define it.
     */
    val Level0 = OrbitElevationLevel(
        offsetY = 0.dp,
        blur = 0.dp,
        opacity = 0f,
        darkSurface = Color(0xFF2C2C2E),
        darkBorder = true,
    )

    /** Standard cards, tiles, list containers. The default resting height for content. */
    val Level1 = OrbitElevationLevel(
        offsetY = 2.dp,
        blur = 4.dp,
        opacity = 0.05f,
        darkSurface = Color(0xFF1C1C1E),
        darkBorder = true,
    )

    /** Icon buttons, active cards, dropdown panels. Raised, but still attached to the page. */
    val Level2 = OrbitElevationLevel(
        offsetY = 4.dp,
        blur = 8.dp,
        opacity = 0.08f,
        darkSurface = Color(0xFF252528),
        darkBorder = true,
    )

    /** Floating action buttons and navigation bars — chrome that outranks the content under it. */
    val Level3 = OrbitElevationLevel(
        offsetY = 6.dp,
        blur = 14.dp,
        opacity = 0.12f,
        darkSurface = Color(0xFF2C2C2E),
        darkBorder = false,
    )

    /**
     * Sheets, modals, dialogs, popovers.
     *
     * The only tier that is allowed to sit over arbitrary content, and the reason its numbers jump
     * rather than step: everything below it is part of the page, and this is not. A shadow that
     * merely continued the progression would put a dialog one rung above a card, when the thing the
     * user has to understand is that the dialog is somewhere else entirely.
     *
     * No rim in the dark theme. At `#3A3A3C` the tonal step off a `#1C1C1E` card is large enough to
     * carry the edge on its own, and a rim on top of that reads as a stroke rather than as depth.
     */
    val Level4 = OrbitElevationLevel(
        offsetY = 12.dp,
        blur = 24.dp,
        opacity = 0.16f,
        darkSurface = Color(0xFF3A3A3C),
        darkBorder = false,
    )
}
