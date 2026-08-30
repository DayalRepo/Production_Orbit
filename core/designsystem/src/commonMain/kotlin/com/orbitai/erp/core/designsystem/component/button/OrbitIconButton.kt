package com.orbitai.erp.core.designsystem.component.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeColors
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * What an icon button's glyph means, expressed as its colour. The ring is identical in every case.
 *
 * These are semantic and not decorative. An icon button carries no label, so its colour is the only
 * thing distinguishing "send" from "delete" before the tap lands, and picking a hue because it looks
 * good next to its neighbour is how a destructive action ends up looking routine. Every entry except
 * [Neutral] borrows a badge tone, so an icon action, a labelled button and a status pill of the same
 * meaning are the same colour throughout the app.
 *
 * Colour is never the only signal — the glyph itself carries the meaning, and the
 * `contentDescription` carries it for screen readers — which is what keeps this usable for the ~8% of
 * men with a red-green deficiency.
 */
enum class OrbitIconButtonStyle(
    internal val tone: OrbitBadgeTone?,
    internal val lightShade: (OrbitBadgeColors) -> Color,
    internal val darkShade: (OrbitBadgeColors) -> Color,
) {
    /**
     * The button blue. The default, and the right answer for most actions.
     *
     * Same tone as `OrbitButtonVariant.Primary`, so tapping the tick in a list row and tapping
     * Approve on the detail screen look like the same kind of act.
     */
    Accent(OrbitBadgeTone.Blue, { it.border }, { it.border }),

    /**
     * Green, for the affirmative: approve, confirm, mark done.
     *
     * Reach for [Accent] first. Green earns its place where the *outcome* is the point rather than
     * the navigation — a QA pass, a delivery signed for — and loses it if every action is green.
     */
    Positive(OrbitBadgeTone.Green, { it.solidContainer }, { it.solidContainer }),

    /**
     * Red, for anything that destroys or refuses: delete, reject, revoke.
     *
     * The one style worth being strict about. A delete that looks like every other action is a delete
     * someone taps by accident, and on an icon-only control there is no label to catch them.
     */
    Destructive(OrbitBadgeTone.Red, { it.border }, { it.solidContainer }),

    /**
     * The monochrome control foreground.
     *
     * For chrome rather than action: close, back, overflow. If everything is coloured then nothing
     * is, and a dismiss control competing with the action it dismisses is how people tap the wrong
     * one.
     */
    Neutral(null, { it.label }, { it.label }),
}

enum class OrbitIconButtonSize { Small, Medium, Large }

/**
 * An icon-only button: a small glyph inside a ring of clear glass.
 *
 * ### The ring
 *
 * The fill is achromatic and translucent — white on light, grey on dark — so whatever is behind still
 * tints through. That is the difference between glass and frosted plastic, and it is why the ring can
 * be dropped onto a card, a photo or a coloured header without being retuned for each. It is also why
 * the fill is a neutral rather than a tint of the glyph's hue: the colour here is doing semantic work,
 * and a blue ring around a blue glyph doubles the signal while halving the contrast between the two.
 *
 * On a white page a white ring is close to invisible, which is expected rather than a defect. What
 * separates it there is the hairline rim, the highlight along the top edge, and the contact shadow
 * underneath — and the shadow is the only one of the three that reads as *depth* rather than as
 * another line. Without it the highlight tends to be read as a gradient in the background instead of
 * as light catching an edge. The fill starts doing visible work the moment the control sits on
 * anything that is not the page, which is where icon buttons actually live.
 *
 * The glyph is small relative to the ring, roughly half its diameter. The clear space is the point;
 * a glyph crowding its ring reads as a mistake in the padding rather than as an icon under a lens. It
 * renders through [OrbitGlyph] rather than `Icon`, because at 16 to 20dp the authored stroke would
 * scale down to 1.2dp and fall under the floor both platforms set for a stroked icon.
 *
 * ### Accessibility
 *
 * [contentDescription] is required rather than optional, and that is the single most important thing
 * about this component. An icon-only control is silent to TalkBack and VoiceOver without one, and a
 * default value is how a screen ends up shipping with an unlabelled button nobody noticed. Making it
 * a mandatory positional parameter means the compiler asks the question.
 *
 * Describe the action, not the picture: "Delete task", not "Trash icon". Screen readers already
 * announce the role, so "button" in the string is a stutter.
 *
 * The ring is 32/38/44dp but the hit area is `max(ring, sizing.minTouchTarget)` — 48dp on Android,
 * 44pt on iOS — so even the smallest is fully compliant. Two of these side by side keep a gap even
 * when their rings look close together, and that gap is the targets not overlapping.
 *
 * @param selected the on state of a toggle — a bookmark, a pinned filter. Pulls a Neutral glyph up to
 *   the accent tone and lifts the highlight, so the state is carried by colour *and* by light.
 * @param state see [OrbitButtonState]; [OrbitButtonState.Inactive] is the right choice for an
 *   available-but-not-current action, and is announced as such.
 */
@Composable
fun OrbitIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    style: OrbitIconButtonStyle = OrbitIconButtonStyle.Accent,
    size: OrbitIconButtonSize = OrbitIconButtonSize.Medium,
    state: OrbitButtonState = OrbitButtonState.Active,
    selected: Boolean = false,
) {
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.avatar
    val isDark = OrbitTheme.isDark

    // A selected Neutral toggle borrows Accent whole — its tone *and* its shade — so "on" is visible
    // without needing a second glyph. Borrowing only the tone would pair blue with Neutral's own
    // shade, which is the deep label value, and the toggle would go dark rather than lighting up.
    val effective = if (selected) OrbitIconButtonStyle.Accent else style
    val tone = effective.tone?.colors

    val diameter = when (size) {
        OrbitIconButtonSize.Small -> sizing.iconButtonSm
        OrbitIconButtonSize.Medium -> sizing.iconButtonMd
        OrbitIconButtonSize.Large -> sizing.iconButtonLg
    }
    val glyphSize = when (size) {
        OrbitIconButtonSize.Small -> sizing.iconButtonGlyphSm
        OrbitIconButtonSize.Medium -> sizing.iconButtonGlyphMd
        OrbitIconButtonSize.Large -> sizing.iconButtonGlyphLg
    }

    // The lightest shade each tone has that still clears the bar — which is why the shade lives on the
    // enum, per style and per theme, rather than being one property read off the tone. A light blue and
    // a light red are what an icon button wants; the deep `label` shades are tuned for small text and
    // on a glyph they read as ink rather than as light. But "the lightest shade" is not the same field
    // in every tone or in every theme:
    //
    //  - `border` is the lightest that clears for Blue in both themes, and for Red on light.
    //  - Green's border shade does not clear — a saturated green is simply a bright colour, and it
    //    reaches only 2.9:1 against a white ring — so Positive takes the next shade down.
    //  - Red's border shade does not clear on dark either, at 2.91:1 over a grey ring on the most
    //    elevated surface, so dark Destructive takes `solidContainer`, which is *lighter* there. The
    //    dark palette is inverted, so the shade that is a step safer is a step brighter.
    //
    // The bar is 3:1, not 4.5:1. A glyph is a graphical object rather than text, so WCAG 1.4.11 is
    // what applies (1.4.3 governs text), and holding glyphs to the text minimum is what forced the
    // near-navy blue this replaces. `ControlContrastTest` checks every tone against the ring fill
    // composited over each surface, which is the background the glyph actually has.
    //
    // `border` carries alpha, because on a badge it is a rim over a tint. Here it is ink, so the alpha
    // is dropped; left in, the glyph would half-dissolve into the ring behind it.
    val shade = if (isDark) effective.darkShade else effective.lightShade
    val content = tone?.let { shade(it).copy(alpha = 1f) } ?: control.controlContent

    // Inactive fades the ring and not the glyph. The ring carries no information, so dimming it is
    // free; the glyph is the entire content, and fading it as well composites two dimmed layers and
    // lands well under the 4.5:1 that a still-tappable control owes. Disabled is exempt from that
    // minimum (WCAG 1.4.3) and fades both.
    val ringAlpha = when (state) {
        OrbitButtonState.Active -> 1f
        OrbitButtonState.Inactive -> OrbitAlpha.Inactive
        OrbitButtonState.Disabled -> OrbitAlpha.Disabled
    }
    val contentAlpha = when (state) {
        OrbitButtonState.Disabled -> OrbitAlpha.Disabled
        else -> 1f
    }

    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    val baseHighlight =
        if (isDark) OrbitGlass.RingHighlightDark else OrbitGlass.RingHighlightLight
    val highlight by animateFloatAsState(
        targetValue = when {
            hovered && state.interactive -> baseHighlight * OrbitGlass.ButtonHoverLift
            selected -> baseHighlight * OrbitGlass.ButtonHoverLift
            else -> baseHighlight
        } * ringAlpha,
        animationSpec = tween(HoverMs),
        label = "orbit-icon-button-highlight",
    )

    // Outer node is the touch target and owns the click; inner node is the ring and owns the paint
    // and the press animation. A 32dp ring has to be tappable across 48dp, and a modifier that only
    // grew the reported layout size would leave the outer band of that target outside the clickable's
    // pointer bounds. Keeping the indication on the inner node is what stops an Android ripple
    // spilling out into a 48dp square.
    Box(
        modifier = modifier
            .size(maxOf(diameter, sizing.minTouchTarget))
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = state.interactive,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
                if (state == OrbitButtonState.Inactive) stateDescription = "Not selected"
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(diameter)
                // The deepest of the three shadow tokens, because the ring's fill is the faintest
                // surface in the system and leans on its shadow hardest. See
                // `Modifier.orbitGlassShadow`.
                .orbitGlassShadow(
                    shape = shape,
                    elevation = sizing.shadowIconButton,
                    alpha = ringAlpha,
                )
                .clip(shape)
                .orbitGlass(
                    // White on light, grey on dark, and translucent in both, so whatever is behind
                    // still tints through. A lens rather than a surface.
                    fill = control.ringContainer,
                    shape = shape,
                    highlightAlpha = highlight,
                    edge = control.controlBorder.copy(
                        alpha = control.controlBorder.alpha * ringAlpha,
                    ),
                    // A hairline now that the ring has a fill. The fill is what defines the circle,
                    // so the rim's job shrinks to catching the light along the top edge, and at 2dp
                    // it was drawing a ring around a ring.
                    edgeWidth = sizing.hairline,
                )
                .indication(interactionSource, orbitPressIndication()),
            contentAlignment = Alignment.Center,
        ) {
            val tint = content.copy(alpha = content.alpha * contentAlpha)
            CompositionLocalProvider(LocalContentColor provides tint) {
                OrbitGlyph(
                    icon = icon,
                    size = glyphSize,
                    tint = tint,
                    // The button itself carries the description; describing the glyph too would make
                    // screen readers announce the action twice.
                    contentDescription = null,
                )
            }
        }
    }
}

private const val HoverMs = 120
