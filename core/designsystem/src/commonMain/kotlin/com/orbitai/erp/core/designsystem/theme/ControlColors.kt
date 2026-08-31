package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * The monochrome palette buttons and icon buttons are built from.
 *
 * Controls are deliberately neutral rather than brand-coloured. Colour in this product carries
 * meaning — a tone on a badge says "overdue", "blocked", "awaiting QA" — and if the buttons are
 * coloured too then a screen has two competing colour languages and neither reads. Black-and-white
 * controls leave the palette free to mean something.
 *
 * Two polarities are in play, and the difference is the point:
 *
 * - **[actionContainer] inverts against the theme.** A filled button is near-black on a light theme
 *   and near-white on a dark one. Inversion is what makes a filled button the loudest thing on the
 *   screen without spending a hue on it.
 * - **[controlContainer] stays with the theme.** An icon button's disc is a faint tint of the
 *   foreground over the current surface, so a toolbar of them recedes instead of reading as a row
 *   of holes punched in the page.
 *
 * Neither extreme is pure. `#FFFFFF` on `#000000` is what the house rules forbid for exactly the
 * reason it looks wrong here: at full contrast a filled button vibrates and its label blooms.
 *
 * `ControlContrastTest` verifies every pair in this file against the glass stack that gets drawn on
 * top of it, so these are not eyeballed values.
 */
@Immutable
data class OrbitControlColors(
    /** Filled-button container. Inverts against the theme. */
    val actionContainer: Color,
    /** Label and glyph on [actionContainer]. */
    val onActionContainer: Color,

    /**
     * Tonal container for icon buttons and the Secondary button. Translucent, so the glass reads
     * and so the same value works on any surface the control lands on.
     */
    val controlContainer: Color,

    /**
     * The one border in the system: `#E8E8E8` on light, `#FFFFFF33` on dark.
     *
     * Every enclosing edge draws this at 1dp — cards, tiles, dividers, tonal control rings, the
     * attachment thumbnail. Having one value rather than a family is the point. A card rim, a
     * divider and a control ring are all answering the same question ("where does this thing stop"),
     * and when each picks its own alpha the screen ends up with four weights of grey line that read
     * as four levels of hierarchy nobody intended.
     *
     * The two sides are not symmetrical, and deliberately so. Light is **opaque**: a flat `#E8E8E8`
     * that renders identically whether it lands on white, on a card, or on an elevated sheet, which
     * is what makes a column of cards look like a column rather than a gradient. Dark is
     * **translucent white at 20%**: on dark the surfaces beneath an edge vary far more (near-black
     * page, `#1A1A1A` card, `#2A2A2A` sheet) and an opaque light grey that works on the page turns
     * into a glowing outline on the sheet. Letting it composite keeps the edge proportional to
     * whatever it sits on.
     */
    val controlBorder: Color,
    /** Glyph or label on [controlContainer], and on no container at all. */
    val controlContent: Color,

    /**
     * Fill for an icon button's ring: white on light, grey on dark.
     *
     * The one value in this file that is neither an inversion nor a tint of the foreground. It is a
     * lens rather than a surface — translucent enough that whatever is behind still tints through,
     * opaque enough to lift the ring off the page — and it is deliberately achromatic so the coloured
     * glyph inside it has nothing to compete with.
     *
     * White on light is close to invisible on a white page, and that is expected rather than a bug:
     * the ring is separated there by its rim and its shadow, and the fill only starts doing visible
     * work once the control sits on a card, an image or any surface that is not the page itself —
     * which is where icon buttons actually live. The alternative, a grey fill on light, makes every
     * toolbar look disabled.
     */
    val ringContainer: Color,

    /**
     * Fill for a card.
     *
     * White on light and near-black on dark, which is what the two themes are *for* — a card is the
     * reading surface and it should be the cleanest thing on screen. Dark uses `#0D0D0D` rather than
     * the page's own `#121212`, so the card sits below the page rather than above it. That inversion
     * is deliberate: on a dark theme there is no usable shadow, so the only way to separate two
     * stacked surfaces is lightness, and making the card *darker* keeps the text on it at higher
     * contrast than lifting it would.
     *
     * Held at 98% rather than fully opaque. See [OrbitCard] for why the missing 2% earns its place.
     */
    val cardContainer: Color,

    /**
     * Ring for the Outline variant.
     *
     * Stronger than [controlBorder] because on an Outline control the ring is the only thing
     * defining the target, whereas on a tonal one the fill already does that.
     */
    val outlineBorder: Color,

    /**
     * Muted inset fill: text inputs, search bars, nested sub-boxes.
     *
     * A shade *recessed* from the card it sits in, where [cardContainer] is a shade raised from the
     * page. That direction is the whole point — a field is a hole you put something into, and giving
     * it the same fill as the card around it leaves the rim doing all the work of saying where it
     * starts.
     */
    val insetContainer: Color,

    /**
     * Subtle divider: list separators, rules inside a card.
     *
     * Lighter than [controlBorder], and it has to be. A border encloses something and is read as its
     * edge; a divider only says "these two rows are different rows", and at border weight a list of
     * eight items turns into eight boxes.
     */
    val dividerSubtle: Color,

    /**
     * Active/focus rim: focused inputs, selected cards.
     *
     * The one place a brand colour appears on an otherwise monochrome control. Focus is the single
     * most important state to be able to spot from across a form — it answers "where will my typing
     * go" — and a neutral rim answers that only by being slightly darker than the seven rims around
     * it, which is not an answer at speed or at a glance.
     */
    val borderFocus: Color,

    /**
     * Interactive container: secondary buttons, badge chips, toggles.
     *
     * Distinct from [cardContainer] because these are things you press, and from [insetContainer]
     * because these are not things you type into. It is the one surface in the matrix that means
     * "actionable" on its own.
     */
    val interactiveContainer: Color,
)

/**
 * Light theme.
 *
 * The filled container is `#1A1C1E`, the same deep charcoal the house rules specify for primary
 * text, rather than black — see the class doc.
 */
internal val OrbitLightControlColors = OrbitControlColors(
    actionContainer = Color(0xFF1A1C1E),
    onActionContainer = Color(0xFFF7F7F8),
    controlContainer = Color(0x141A1C1E),
    controlBorder = Color(0xFFE2E2E6),
    controlContent = Color(0xFF1A1C1E),
    ringContainer = Color(0xB8FFFFFF),
    cardContainer = Color(0xFFFFFFFF),
    outlineBorder = Color(0x471A1C1E),
    insetContainer = Color(0xFFF2F2F7),
    dividerSubtle = Color(0xFFF2F2F7),
    borderFocus = Color(0xFF007AFF),
    interactiveContainer = Color(0xFFEBEBEF),
)

/** Dark theme, with the polarity of [actionContainer] flipped. */
internal val OrbitDarkControlColors = OrbitControlColors(
    actionContainer = Color(0xFFE3E2E6),
    onActionContainer = Color(0xFF16191D),
    controlContainer = Color(0x1FE3E2E6),
    controlBorder = Color(0xFF3F3F46),
    controlContent = Color(0xFFE3E2E6),
    // Black, not grey — the dark ring deepens where the light one lightens.
    //
    // This was a pale grey (`0x2EA8A8AC`) on the reasoning that glass is a lightening material, and
    // measured on device that produced a #383839 disc on a #121212 page: not a hint of material but
    // a solid light-grey coin, brighter than anything around it, drawing the eye to the container
    // instead of to the glyph it exists to hold. Two rounds of thinning did not fix it, because the
    // problem was the direction rather than the amount.
    //
    // Glass lightens on light because what you see through it is a bright page. On a near-black page
    // the honest analogue is the opposite: the ring should read as a slight well the glyph sits in,
    // which is what a black-based fill gives. The circle is then carried by its rim — already tinted
    // to match the glyph — and by the contact shadow, both of which are position and material cues
    // rather than a wash.
    ringContainer = Color(0x33000000),
    cardContainer = Color(0xFF1C1C1E),
    outlineBorder = Color(0x5CE3E2E6),
    insetContainer = Color(0xFF2C2C2E),
    dividerSubtle = Color(0xFF2C2C2E),
    borderFocus = Color(0xFF0A84FF),
    interactiveContainer = Color(0xFF3A3A3C),
)

val OrbitTheme.controlColors: OrbitControlColors
    @Composable @ReadOnlyComposable get() =
        if (isDark) OrbitDarkControlColors else OrbitLightControlColors
