package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 4dp-based spacing scale. Use these instead of literal dp values so density can be tuned
 * globally (a Site Engineer on a phone in gloves needs larger targets than a CEO on a tablet).
 *
 * Spacing is platform-independent; sizes that differ by platform live in [OrbitSizing].
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
)

/**
 * Sizes, including the ones that differ per platform.
 *
 * Defaults are the Android values; [IosPlatformTokens] overrides the icon, avatar and touch-target
 * fields. Read this through `OrbitTheme.sizing`, which resolves the running platform for you.
 *
 * Note that no container height here should be applied with `Modifier.height` on anything holding
 * text — use `heightIn(min = ...)` so the container grows when the user scales text to 200%
 * (WCAG 1.4.4).
 */
@Immutable
data class OrbitSizing(
    // Icons — inline, standard/toolbar, featured.
    val iconXs: Dp = 12.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 24.dp,
    val iconLg: Dp = 24.dp,
    val iconXl: Dp = 32.dp,
    val iconXxl: Dp = 32.dp,

    /**
     * Uniform stroke weight for outlined icons. Mixing heavy outlines with hairline ones is the
     * fastest way to make an icon set look assembled from three different libraries.
     */
    val iconStrokeWidth: Dp = 1.5.dp,

    val avatarXs: Dp = 24.dp,
    val avatarSm: Dp = 40.dp,
    val avatarMd: Dp = 48.dp,
    val avatarLg: Dp = 64.dp,
    val avatarXl: Dp = 88.dp,
    val avatarBorderWidth: Dp = 1.dp,

    /** Minimum hit area for anything interactive: 48dp on Android, 44pt on iOS. */
    val minTouchTarget: Dp = 48.dp,

    /**
     * Contact-shadow depth under each kind of glass surface.
     *
     * Ordered by how much the component's own fill already does. A badge is a solid-enough tint that
     * it barely needs help; a button is larger and sits on more varied backgrounds; an icon button's
     * ring is the faintest fill in the system and leans on its shadow hardest. All three are contact
     * shadows - the millimetre between a pane of glass and the paper under it - not the lift of a
     * floating action button.
     */
    val shadowBadge: Dp = 1.dp,
    val shadowButton: Dp = 2.dp,
    val shadowIconButton: Dp = 3.dp,

    val hairline: Dp = 1.dp,
    val border: Dp = 1.dp,
    val borderStrong: Dp = 2.dp,

    /**
     * Visible button heights, applied as `heightIn(min = ...)` so a scaled label grows the pill.
     *
     * Small stays under [minTouchTarget] deliberately — it is for dense table rows, where a 48dp
     * pill would not fit — and `OrbitButton` expands its hit area to compensate.
     */
    val buttonHeightSm: Dp = 32.dp,
    val buttonHeightMd: Dp = 40.dp,
    val buttonHeightLg: Dp = 48.dp,

    /**
     * Button glyph sizes.
     *
     * Each is a little smaller than the paired label's cap height would suggest. An icon drawn at
     * the same nominal size as the text reads as heavier than the text, because the glyph fills its
     * box while a letter does not, and the pair then looks unbalanced.
     *
     * None goes below 16dp. The icon stroke is authored in viewport units so it scales with the
     * glyph, and below 16dp it renders under 1dp and turns hairline against its label.
     */
    val buttonIconSm: Dp = 16.dp,
    val buttonIconMd: Dp = 18.dp,
    val buttonIconLg: Dp = 20.dp,

    /**
     * Horizontal padding at each end of a button, paired with the heights above.
     *
     * Not derived from the spacing scale, because a button's end padding is a function of its own
     * height rather than of the layout grid — the label needs to sit visually centred in the pill,
     * and the ratio that achieves that does not follow 4dp steps. These are generous for the same
     * reason the minimum widths below are: a pill needs more end padding than a rounded rectangle
     * to look evenly weighted, because its corners curve away from the text.
     */
    val buttonPaddingSm: Dp = 18.dp,
    val buttonPaddingMd: Dp = 22.dp,
    val buttonPaddingLg: Dp = 28.dp,

    /**
     * Minimum width of a button.
     *
     * A floor, not a target. Buttons size to their labels — a row of them should look like a row of
     * words, not a row of identical slabs — and these values exist only for the degenerate case: a
     * pill's corner radius is half its height, so a two-character label on an unconstrained pill
     * comes out as a circle.
     *
     * They are deliberately low enough that a real label like "Approve" or "Cancel" never touches
     * them; end padding is what sets the width in practice.
     */
    val buttonMinWidthSm: Dp = 72.dp,
    val buttonMinWidthMd: Dp = 88.dp,
    val buttonMinWidthLg: Dp = 104.dp,

    /**
     * Icon button ring diameters, and the glyphs inside them.
     *
     * The ring is generous relative to the glyph — 16/20/24dp of clear space around it — which is
     * what makes it read as a lens over the surface rather than as a badge crowding its contents. A
     * tight ring looks like a bug in the padding.
     *
     * These are the visible circle, not the hit area; the hit area is `max(diameter, minTouchTarget)`,
     * which is why the small ring is allowed to be 36dp.
     */
    val iconButtonSm: Dp = 32.dp,
    val iconButtonMd: Dp = 38.dp,
    val iconButtonLg: Dp = 44.dp,

    /**
     * The glyph inside the ring.
     *
     * Small relative to the ring, which is deliberate: the clear space is the component. These sizes
     * would draw a stroke of 1.2 to 1.5dp if the vector were left to scale on its own, so they are
     * always rendered through `OrbitGlyph`, which lifts the stroke back to the platform floor.
     */
    val iconButtonGlyphSm: Dp = 16.dp,
    val iconButtonGlyphMd: Dp = 18.dp,
    val iconButtonGlyphLg: Dp = 20.dp,

    val fieldHeight: Dp = 56.dp,
    val topBarHeight: Dp = 56.dp,
    val listRowMinHeight: Dp = 64.dp,

    /**
     * Visible badge heights. These are minimums applied with `heightIn`, never fixed heights: a
     * badge carries text, so at 200% font scale the pill has to grow rather than crop its label.
     *
     * They are taller than a bare text chip would need because these badges carry a leading icon,
     * and a 20dp pill around a 16dp glyph leaves no optical breathing room at the cap line.
     */
    val badgeHeightSm: Dp = 24.dp,
    val badgeHeightMd: Dp = 28.dp,
    val badgeHeightLg: Dp = 32.dp,

    /** Badge glyph sizes, paired with the heights above. */
    val badgeIconSm: Dp = 14.dp,
    val badgeIconMd: Dp = 16.dp,
    val badgeIconLg: Dp = 18.dp,
    /** Visible height of a chip; its touch target is expanded to [minTouchTarget]. */
    val chipHeight: Dp = 36.dp,
    val indicatorDotSm: Dp = 6.dp,
    val indicatorDotMd: Dp = 8.dp,
    val indicatorDotLg: Dp = 12.dp,
    /** One segment of a severity indicator. */
    val severitySegmentWidth: Dp = 5.dp,
    val severitySegmentHeight: Dp = 14.dp,
    val progressBarHeight: Dp = 6.dp,
    val progressBarHeightLg: Dp = 10.dp,

    /** Width of the persistent navigation rail on medium windows. */
    val navRailWidth: Dp = 80.dp,
    /** Width of the expanded navigation drawer on large windows. */
    val navDrawerWidth: Dp = 280.dp,
    /** Cap on text measure so dashboards stay readable on wide screens. */
    val maxContentWidth: Dp = 1200.dp,
)

internal val LocalOrbitSpacing = staticCompositionLocalOf { OrbitSpacing() }
internal val LocalOrbitSizing = staticCompositionLocalOf { OrbitSizing() }
