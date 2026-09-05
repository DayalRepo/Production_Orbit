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
     * Hero tier: empty states, success screens, onboarding.
     *
     * Off the interactive ladder entirely. Nothing at this size is tappable — it is illustration, and
     * the touch target rules that shape every smaller tier do not apply. That is why it is 48dp
     * rather than the next multiple up from 32: at hero size the constraint is the composition it
     * sits in, not a thumb.
     */
    val iconHero: Dp = 48.dp,

    /**
     * Uniform stroke weight for outlined icons. Mixing heavy outlines with hairline ones is the
     * fastest way to make an icon set look assembled from three different libraries.
     */
    val iconStrokeWidth: Dp = 1.dp,
    /**
     * Stroke per icon tier, so weight stays *apparently* constant as size changes.
     *
     * A stroke held at 1.5dp across the ladder does not look consistent, it looks like the icon is
     * thinning out: at 16dp a 1.5dp stroke is roughly a tenth of the glyph, and at 48dp the same
     * stroke is a thirty-second of it, so the hero icon reads as a wireframe next to the inline one.
     * Scaling the stroke with the tier — not proportionally, but in steps — keeps the ratio close
     * enough that the two read as the same icon at two sizes.
     *
     * The steps stop climbing at Large. Past about 2.5dp a stroke starts closing the counters on
     * glyphs with tight interiors, and the fix for a hero icon looking thin is more size, not more
     * ink.
     */
    val iconStrokeSm: Dp = 1.dp,
    val iconStrokeMd: Dp = 1.dp,
    val iconStrokeLg: Dp = 1.dp,
    val iconStrokeHero: Dp = 1.dp,

    /**
     * The lighter stroke floor, for a glyph that is not competing with body text.
     *
     * [iconStrokeWidth] is the floor for an icon sitting beside a label, where it has to hold its own
     * against type. A glyph alone inside an icon button has no type next to it and a ring drawing
     * attention to it, so the same weight reads as heavy rather than as legible - the strokes start to
     * close up the counters of a small glyph and the icon turns into a mark. This is the floor those
     * use instead.
     */
    val iconStrokeLight: Dp = 1.dp,

    /**
     * The thinnest stroke the system will draw, for a glyph sharing a row with flat-colour artwork.
     *
     * The one place this exists for is the attachment row's fallback mark, which sits beside the
     * PDF and Sheets images — those are solid shapes with no outline at all, so even
     * [iconStrokeLight] made the single unrecognised file the heaviest thing in the list, which is
     * backwards for the row carrying the least information.
     *
     * Not a general-purpose floor. Below this, strokes start dropping out on low-density screens
     * where a dp is close to a pixel, so anything a user has to *find* rather than merely read
     * should stay at [iconStrokeLight] or above.
     */
    val iconStrokeHairline: Dp = 1.dp,

    val avatarXs: Dp = 24.dp,
    val avatarSm: Dp = 40.dp,
    val avatarMd: Dp = 48.dp,
    val avatarLg: Dp = 64.dp,
    val avatarXl: Dp = 88.dp,
    val avatarBorderWidth: Dp = 1.dp,

    /**
     * The ring drawn in the *page* colour around each avatar in a stack.
     *
     * Thicker than [avatarBorderWidth], and a different thing entirely. The hairline exists so a
     * photo has a closed edge against whatever is behind it; this exists so two overlapping photos
     * do not merge into one shape. That needs a band of background colour, not a line — a hairline
     * between two mid-tone faces disappears at arm's length, and the stack turns into a smear.
     */
    val avatarStackRing: Dp = 2.dp,

    /**
     * How much of each avatar the next one covers, as a fraction of its diameter.
     *
     * A third is the usable range's midpoint. Less and the stack stops reading as a stack and starts
     * reading as a crowded row; more and the covered avatars lose the side of the face, at which
     * point the overlap is hiding the information it exists to summarise.
     */
    val avatarStackOverlap: Float = 0.34f,

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
    /**
     * Panels that float free of the layout: dropdown menus, identity and account bubbles.
     *
     * Several times the deepest in-layout shadow, and that gap is the point rather than an
     * inconsistency. Every other elevation in the system separates a control from the surface it is
     * *resting on*, and a couple of dp is enough to say so. These panels are not resting on anything
     * — they are drawn over arbitrary content, and they have to read as being in front of it before
     * the user has parsed what they say. At button depth a dropdown over a busy list looks like part
     * of the list; the rim alone cannot carry it, because the rim is a hairline and the content
     * behind it has hairlines of its own.
     */
    val shadowOverlay: Dp = 10.dp,

    /**
     * The width every enclosing edge is drawn at, on both platforms and in both themes.
     *
     * One value, applied everywhere, paired with the single `controlBorder` colour. There was
     * briefly a `hairlineFine` at 0.75dp for edges thought not to need the full weight; it is gone,
     * because sub-dp widths are where a border stops being a design decision and becomes a
     * rasterisation one — 0.75dp lands on a pixel boundary at 2x and between two at 3x, so the same
     * rim looked crisp on one phone and soft on the next.
     *
     * Note this is not `Dp.Hairline`, which is one *physical pixel*: a third of a dp at 3x and a
     * whole dp at 1x, so its apparent weight would depend on the device rather than on the design.
     */
    val hairline: Dp = 1.dp,
    val border: Dp = 1.dp,
    val borderStrong: Dp = 2.dp,

    /**
     * The rim of a field that is focused, in error, or validated.
     *
     * A half-step above [hairline] rather than a jump to [borderStrong]. Focus has to be felt
     * without being read as a different component, and 1dp to 2dp was a visible jolt: nothing
     * reflowed, but the box appeared to grow as you tapped it. The width change matters regardless
     * of how small it is, because it is what stops focus and error being carried by hue alone
     * (WCAG 1.4.1) — the colour does the communicating, the width does the compliance.
     *
     * Unlike the hairline, this is safe at a fraction: it is only ever drawn against a solid fill,
     * so there is no sub-pixel row of a translucent edge to go patchy.
     */
    val borderFocus: Dp = 1.5.dp,

    /**
     * The weight of a divider rule, everywhere one is drawn.
     *
     * ### Why a rule is heavier than a border
     *
     * Both were [hairline] and they are not doing the same job. A border *encloses* — it is read as
     * the edge of a shape, and the shape's fill is doing most of the work of saying where it ends, so
     * 1dp is plenty. A rule has no fill behind it and no shape to belong to: it is the only thing
     * asserting that the content above it and the content below it are separate. At 1dp, inside a
     * popover, against a low-contrast `dividerSubtle` ink, it disappeared — the account card read as
     * one eight-line block and the sections it was meant to separate ran together.
     *
     * Back to 1dp, after 1.5 and then 2 both went the wrong way about the same problem. The rules in
     * the popover cards genuinely were hard to see, but the cause was the *ink* — `dividerSubtle` is
     * a hair off white and those cards are white — and thickening a nearly-invisible colour only ever
     * produced a wider smudge. Fixed properly by letting a caller choose a legible colour (see
     * [com.orbitai.erp.core.designsystem.component.container.OrbitDivider]), at which point a
     * hairline is once again the right weight and 2dp reads as a frame cutting a small panel in two.
     *
     * Safe at a fraction for the same reason [borderFocus] is: a rule is drawn as a filled box
     * against a solid surface, not as a translucent stroke around a curve, so there is no sub-pixel
     * edge to go patchy at 3x.
     */
    val dividerThickness: Dp = 1.dp,

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
     *
     * They came down a step when buttons regained their glyphs. The earlier figures were tuned for
     * a text-only pill, where end padding was the only thing standing between a short word and the
     * curve; a leading mark now occupies part of that space and does the same work, so keeping the
     * old values just made every Small button wide enough to look like a Medium one.
     */
    val buttonPaddingSm: Dp = 14.dp,
    val buttonPaddingMd: Dp = 18.dp,
    val buttonPaddingLg: Dp = 24.dp,

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
    val iconButtonGlyphSm: Dp = 18.dp,
    val iconButtonGlyphMd: Dp = 20.dp,
    val iconButtonGlyphLg: Dp = 22.dp,

    /**
     * Minimum heights for a text field, one per size.
     *
     * Minimums applied with `heightIn`, never fixed heights. A field holds text that the user
     * controls the size of, so at 200% font scale it has to grow rather than clip what is being
     * typed into it (WCAG 1.4.4) — a fixed height here is the single most common way a form becomes
     * unusable at large type.
     *
     * All three clear the 48dp touch minimum, unlike the button sizes, where Small deliberately
     * goes under it. A button that is too small to hit can be hit on the second try; a text field
     * that is too small to hit cannot be typed into at all, and there is no dense-table case that
     * justifies one.
     */
    val fieldHeightSm: Dp = 48.dp,
    val fieldHeightMd: Dp = 56.dp,
    val fieldHeightLg: Dp = 64.dp,

    /** Horizontal padding inside a field, paired with the heights above. */
    val fieldPaddingSm: Dp = 12.dp,
    val fieldPaddingMd: Dp = 16.dp,
    val fieldPaddingLg: Dp = 20.dp,

    val listRowMinHeight: Dp = 64.dp,

    /**
     * Visible badge heights. These are minimums applied with `heightIn`, never fixed heights: a
     * badge carries text, so at 200% font scale the pill has to grow rather than crop its label.
     *
     * They are taller than a bare text chip would need because these badges carry a leading icon,
     * and a 20dp pill around a 16dp glyph leaves no optical breathing room at the cap line.
     */
    val badgeHeightXs: Dp = 20.dp,
    val badgeHeightSm: Dp = 24.dp,
    val badgeHeightMd: Dp = 28.dp,
    val badgeHeightLg: Dp = 32.dp,

    /**
     * Badge glyph sizes, paired with the heights above.
     *
     * [badgeIconXs] breaks the pattern of shrinking in step with its pill: it is only 2dp down from
     * [badgeIconSm] where the pill drops 4dp. A glyph scaled proportionally at this tier stops being
     * a recognisable arrow and becomes a smudge, and the label beside it cannot shrink either —
     * caption is already the 12sp floor on both platforms. So the extra height comes out of the
     * padding instead, which is the only part of the chip that has anything to give.
     */
    val badgeIconXs: Dp = 12.dp,
    val badgeIconSm: Dp = 14.dp,
    val badgeIconMd: Dp = 16.dp,
    val badgeIconLg: Dp = 18.dp,
    /** Visible height of a chip; its touch target is expanded to [minTouchTarget]. */
    val chipHeight: Dp = 36.dp,

    /**
     * The numeric count badge, as a diameter for a single digit.
     *
     * It grows into a pill for two or more, so this is a minimum rather than a size. 16dp is the
     * floor at which a digit set at the caption floor of 12sp still has optical room above and below
     * the numeral — below that the badge crops the ascenders of a 4 and the descender of a 9 before
     * the type itself becomes illegible.
     */
    val countBadgeMinSize: Dp = 16.dp,

    /** The count badge with no number in it — presence only, for "there is something here". */
    val countBadgeDot: Dp = 8.dp,

    /** The rail a timeline's events hang off, and the node marking each one. */
    val timelineRailWidth: Dp = 1.dp,
    val timelineNodeSize: Dp = 9.dp,

    /**
     * Distance from the left edge of a timeline row to its text.
     *
     * Wide enough to centre [timelineNodeSize] over the rail and still leave a readable gap before
     * the label. Every row in a timeline shares this, because the rail is only legible as a rail if
     * it is perfectly vertical, and it is only perfectly vertical if no row computes its own gutter.
     */
    val timelineGutter: Dp = 22.dp,

    /**
     * The file-type mark at the head of an attachment row.
     *
     * Sized so the PDF and Sheets artwork stays recognisable — those marks carry a two- or
     * three-letter word inside the page shape, and below about this the word turns into a texture
     * and every file type looks the same at a glance.
     */
    val attachmentMark: Dp = 28.dp,

    /**
     * Minimum height of an attachment row: the mark plus its padding.
     *
     * A minimum rather than a height, because the row carries a filename and the filename has to be
     * able to grow the row at 200% font scale rather than being cropped by it.
     */
    val attachmentRowHeight: Dp = 48.dp,

    /**
     * Minimum height of an attachment row that has no controls on it.
     *
     * Taller than [attachmentRowHeight], which looks backwards until you see the two side by side.
     * An actionable row ends in icon buttons, and those enforce their own touch target, so the row
     * is in practice pushed past its stated minimum by its own controls. A read-only row has nothing
     * doing that, sits at exactly 48dp, and next to its actionable neighbour reads as a squashed
     * version of the same component rather than as a quieter one -- the mark is the same size, the
     * text is the same size, and the only difference is that the padding around them has been
     * pinched.
     *
     * This is the difference put back explicitly, so the two variants line up visually instead of
     * only in the token table. Small on purpose: enough to close the gap, not enough to make a list
     * of read-only attachments taller than the list of editable ones it replaces.
     */
    val attachmentRowHeightReadOnly: Dp = 56.dp,

    /**
     * The glyph in an empty state.
     *
     * Deliberately not much bigger than a toolbar icon. The usual instinct is a large illustration,
     * but an empty inbox on a construction site is a routine condition rather than an event, and
     * sizing it like an event makes the app feel like it is apologising every time a filter matches
     * nothing.
     */
    val emptyStateIcon: Dp = 32.dp,

    /** Height of a skeleton line standing in for a row of text. */
    val skeletonLineHeight: Dp = 12.dp,

    /**
     * Height of a segmented progress track.
     *
     * Tall enough that a slat reads as an object rather than as a texture, which is the entire
     * reason to segment a bar, and no taller — the bar is the second half of the reading, under the
     * figure, and a track that competes with the number inverts the hierarchy the card is built on.
     */
    val progressTrackHeight: Dp = 20.dp,

    /**
     * Gap between two slats.
     *
     * Fixed while the slats themselves flex, so the rhythm stays identical from a 320dp phone to a
     * tablet and only the slat width changes. A proportional gap would make the bar look coarser on
     * a small screen, which is exactly where it can least afford it.
     *
     * Note what changing this does to the slats. The track always spans its container and the count
     * is fixed, so the gap and the slat width are the two halves of one fixed budget -- narrowing
     * the gap is the *only* way to widen the slats without removing some. That is why the last pass
     * moved this alone: 2.5dp to 1.5dp took a slat on a 330dp phone card from 4.4dp to 5.1dp
     * without touching `SegmentCount`.
     *
     * Trimmed a quarter of a dp to widen the slats without touching the count, which has to stay at
     * 50 for the 2%-per-slat arithmetic. Slat width is `(width - gaps) / count`, so every dp taken
     * out of the 49 gaps is a dp shared back across the 50 bars — a small change to the gap is the
     * only lever left once the count is fixed.
     *
     * The floor is `progressSegmentMinWidth` beside it. Below that the gap stops reading as a
     * separation and becomes a seam, and the run collapses into one continuous bar.
     */
    val progressSegmentGap: Dp = 1.25.dp,

    /**
     * Gap between wizard / form page bars.
     *
     * Wider than [progressSegmentGap]: those are 50 KPI slats that share one track, while a form
     * page bar is only a handful of segments and needs clear separation between pages.
     */
    val formPageBarGap: Dp = 4.dp,

    /**
     * Floor for a slat's width, enforced by dropping the slat count rather than by overflowing.
     *
     * Equal to the gap beside it. Below that the bar inverts and the eye starts reading the gaps as
     * the marks — the slats become the space between white lines rather than objects in their own
     * right. It is also roughly where a slat stops surviving the rounding to physical pixels on a
     * 2x screen, after which some slats render a pixel wider than others and the rhythm visibly
     * stutters.
     */
    val progressSegmentMinWidth: Dp = 3.dp,

    /**
     * Width of one bar in an audio waveform, and the gap beside it.
     *
     * Wider and more widely spaced than a progress slat, and deliberately so — the two look alike
     * and are read completely differently. A progress bar is *counted*: you compare a lit run to a
     * dark one, so the slats want to be fine and uniform. A waveform is read as a *silhouette*: the
     * information is in the envelope the bar tops trace, and the eye follows that outline. Bars too
     * fine turn the envelope into noise, and a gap too tight closes it into a solid shape whose top
     * edge is the only thing left to read.
     */
    val waveBarWidth: Dp = 3.dp,
    val waveBarGap: Dp = 2.dp,

    /**
     * Height of the waveform band in the composer and on a voice note.
     *
     * Fixed rather than scaling with the text around it: this is a graphic, not type, and growing it
     * with the font scale would push the composer taller for a user who asked for larger *text*. The
     * controls beside it keep their own touch targets regardless.
     */
    val waveHeight: Dp = 24.dp,

    /**
     * Gap between the composer's controls, and the inset from its rim to the outermost of them.
     *
     * Its own token rather than a reuse of `spacing.xxs`, because the composer is the one row in the
     * product with four independent controls inside a single bounded shape, and it is the only place
     * where the difference between 4dp and 10dp decides whether the row reads as spacious or as
     * crammed. Tying it to a general spacing step means a later adjustment made for cards silently
     * re-crowds this.
     *
     * Set per platform: Android's 48dp touch targets already sit further apart than iOS's 44pt ones,
     * so iOS needs slightly more explicit air to land in the same visual place.
     */
    val composerControlGap: Dp = 6.dp,
    val composerEdgeInset: Dp = 6.dp,

    /**
     * Square thumbnail for a file queued on the message composer.
     *
     * Large enough that a photo still reads as the photo and a PDF mark still shows its letters,
     * small enough that three of them fit beside each other on a compact phone without scrolling.
     * A full attachment row would push the draft off-screen; this is the composer-shaped substitute.
     */
    val composerThumbSize: Dp = 56.dp,

    /**
     * Visible diameter of the X that removes a composer thumbnail.
     *
     * Smaller than the thumbnail on purpose — the badge is a corner affordance, not a second tile —
     * while its hit area still expands to [minTouchTarget].
     */
    val composerThumbRemove: Dp = 20.dp,

    /**
     * Node diameter on a step indicator, and the track that connects them.
     *
     * Kept small relative to the label underneath: the nodes are waypoints, not buttons, and sizing
     * them like chips would make five steps look like a toolbar. The full [minTouchTarget] still
     * wraps each node so a tap lands reliably.
     */
    val stepNodeSize: Dp = 12.dp,
    val stepTrackThickness: Dp = 2.dp,
    val stepNodeRingWidth: Dp = 2.dp,

    /**
     * Vertical step-indicator density. The card itself is the hit target, so the node column and
     * row stay under [minTouchTarget] — packing five stages into a phone card would otherwise force
     * either a scroll or a wall of empty padding.
     */
    val stepGlyphSize: Dp = 24.dp,
    val stepColumnWidth: Dp = 36.dp,
    val stepRowMinHeight: Dp = 36.dp,
    /** Dash and gap retained for any dashed chrome; the stage rail itself is a solid connector. */
    val stepRailDash: Dp = 3.dp,
    val stepRailGap: Dp = 3.dp,
    /** Fallback rail length when a row does not stretch; preferred path fills the row gap. */
    val stepRailLength: Dp = 24.dp,
    /** Light connector weight — matches the dashed / solid stage rails. */
    val stepRailThickness: Dp = 1.5.dp,

    /**
     * The floor and ceiling on a dropdown menu's width.
     *
     * A range rather than a fixed width, so the panel sizes to its longest row and no further —
     * "Upload image" and "Attach file" should decide how wide the menu is, with even margins either
     * side of them. The maximum is what stops a `Popup` from letting `fillMaxWidth` mean *the whole
     * window*; the minimum stops a one-word menu from becoming a sliver.
     */
    val menuMinWidth: Dp = 180.dp,
    val menuMaxWidth: Dp = 260.dp,

    /**
     * The floor and ceiling on an info popover's width.
     *
     * Narrower than the menu range, and narrower than a first guess would make it. A popover is
     * anchored *over* the thing it describes -- in an avatar grid there are other faces directly
     * underneath it -- so unlike a menu, every extra millimetre of panel hides more of the context
     * that made the tap meaningful in the first place. The bubble is deliberately the smallest
     * thing that can hold a name and a phone number on their own lines.
     *
     * The floor exists for the pointer rather than for the text. The pointer has to stay clear of
     * both corner arcs, so a panel much under this has almost no legal span left to slide the
     * pointer along when the bubble is clamped against a screen edge, and it stops being able to
     * track its anchor.
     */
    val popoverMinWidth: Dp = 160.dp,
    val popoverMaxWidth: Dp = 208.dp,

    /**
     * How tall a dropdown's list of options is allowed to get before it scrolls.
     *
     * Capped, and capped at a height that deliberately cuts a row in half rather than landing on a
     * clean boundary. A list that ends flush with the panel's bottom edge looks complete, and a user
     * who believes they have seen every option does not scroll — so on a list of fifteen stages they
     * simply never find the last five. Half a row showing is the cheapest possible "there is more",
     * and it costs no chrome.
     *
     * It is a maximum, not a height: a four-item list is four items tall. A panel padded out to a
     * fixed height leaves dead space under short lists, which reads as options that failed to load.
     *
     * Sized in dp rather than in rows so it holds when the user scales their font up — the list gets
     * fewer, taller rows in the same space rather than growing past the bottom of the screen, which
     * is precisely the case where a fixed row count breaks.
     */
    val dropdownMaxHeight: Dp = 280.dp,

    /**
     * How wide a dialog is allowed to get, and how much of the screen edge it must leave clear.
     *
     * Capped rather than proportional. A confirmation is one or two lines of text, and a line of
     * text much past 60 characters is measurably harder to read because the eye loses its place on
     * the return sweep — on a tablet a proportional dialog would be a single very long line, which
     * is the worst possible shape for the one sentence the user has to actually read before
     * answering.
     *
     * The inset is the guarantee at the other end: on the narrowest phone the dialog still has air
     * around it and reads as an object on top of the screen rather than as a new screen.
     */
    val dialogMaxWidth: Dp = 360.dp,
    val dialogEdgeInset: Dp = 24.dp,

    /**
     * Corner radius of a slat: softened, not pill-shaped.
     *
     * At this width a full radius would consume the slat — a 5dp slat rounded to 2.5dp is a
     * lozenge, and forty lozenges read as a dotted line rather than a bar. One device pixel of
     * softening takes the machined edge off without costing width.
     */
    val progressSegmentRadius: Dp = 1.5.dp,

    /** Cap on text measure so dashboards stay readable on wide screens. */
    val maxContentWidth: Dp = 1200.dp,

    /**
     * Floating role bottom nav — the full pill plus the separate circular action.
     *
     * Height is shared by both clusters so the row reads as one bar. The pill flexes; the circle
     * stays square at the same diameter. Glyphs sit visually centred in both, with no labels.
     */
    val bottomNavHeight: Dp = 64.dp,
    /** Scaled with [bottomNavHeight] so the glyph stays about half the container. */
    val bottomNavGlyph: Dp = 30.dp,
    /**
     * Stroke floor/ceiling for nav glyphs.
     *
     * Thinner than the natural Hugeicons scale at [bottomNavGlyph] — at this size the authored
     * 1.8-unit stroke would land near 2dp and look heavy in a floating glass bar. Cap it here so
     * size can grow without the line weight growing with it.
     */
    val bottomNavIconStroke: Dp = 1.25.dp,
    /**
     * Inset from the pill's curved ends to the outer icon slots.
     *
     * Keeps SpaceEvenly icons optically centred in the glass rather than riding into the radius.
     */
    val bottomNavPillInset: Dp = 14.dp,
    /**
     * Outer horizontal inset for the floating bar.
     *
     * Tighter than [OrbitSpacing.screenHorizontal] so the pill and circle read wider on phone
     * widths without touching the screen edge.
     */
    val bottomNavEdgeInset: Dp = 10.dp,
    /** Gap between the primary pill and the circular action. */
    val bottomNavClusterGap: Dp = 10.dp,
    /**
     * Active-destination glass disc drawn behind the selected glyph.
     *
     * Sized to nearly fill [bottomNavHeight] so it reads clearly inside both the pill slots and the
     * separate action circle, with a few dp of air so it does not collide with the outer rim.
     */
    val bottomNavActiveSize: Dp = 54.dp,
    /**
     * Contact shadow under the pill and circle.
     *
     * Softer than [shadowIconButton] / `elevation.bottomBar` — those were tuned for small rings, and
     * at bar size a 3dp shadow reads as a drop rather than as glass resting on the page.
     */
    val bottomNavShadow: Dp = 2.dp,
    /**
     * Air between the bar and the platform gesture / navigation bar.
     *
     * Small on purpose: enough to separate the glass from the system chrome without floating the
     * bar halfway up the screen.
     */
    val bottomNavSystemGap: Dp = 8.dp,

    /**
     * Horizontal inset for [OrbitTabBar], kept equal to [bottomNavEdgeInset] so the pages bar and
     * floating bottom nav share one column edge on phone widths.
     */
    val tabBarEdgeInset: Dp = 10.dp,

    /** Gap between consecutive tab labels. */
    val tabBarItemGap: Dp = 20.dp,

    /**
     * Minimum label-row height before [minTouchTarget] is applied.
     *
     * The composable takes `max(tabBarMinHeight, minTouchTarget)` so Android lands at 48dp and iOS
     * at 44pt without a second platform branch in the layout.
     */
    val tabBarMinHeight: Dp = 44.dp,
)

internal val LocalOrbitSpacing = staticCompositionLocalOf { OrbitSpacing() }
internal val LocalOrbitSizing = staticCompositionLocalOf { OrbitSizing() }
