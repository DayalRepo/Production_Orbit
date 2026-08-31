package com.orbitai.erp.core.designsystem.component.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.component.feedback.OrbitLoadingIcon
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeColors
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * How much weight a button carries, and which hue says so.
 *
 * The two that matter are tonal chips drawn from the badge palette: a light blue for the action you
 * are meant to take, a light red for the one that backs out. That is a reversal of an earlier
 * monochrome scheme, and the reason it works is that the *shape* now carries the meaning colour used
 * to carry — a pill with a label is unmistakably a control, so tinting it cannot be confused with a
 * status badge on a card. Reusing the badge tones rather than inventing a button palette also means
 * these colours are already contrast-verified against the glass gradient in both themes.
 *
 * Blue rather than green for the affirmative. Green and red as a pair is the one combination that
 * disappears for the ~8% of men with red-green colour blindness, and "approve versus reject" is
 * exactly the decision that must not depend on hue alone. Blue against red stays distinguishable
 * under every common form of it, and the labels differ anyway.
 */
enum class OrbitButtonVariant {
    /**
     * A light blue tonal chip. The action the screen wants: Approve, Send, Create, Login, Open.
     *
     * One per decision. Two blue chips side by side is a screen that has not decided what it wants
     * the user to do.
     */
    Primary,

    /** A neutral tonal chip, for supporting actions that should not claim the blue. */
    Secondary,

    /** Ring only, no fill. Equal-weight alternatives where neither should be tinted. */
    Outline,

    /** No container at all. Low-emphasis actions, e.g. "View all" on a dashboard card. */
    Text,

    /**
     * A light red tonal chip. Reject, Cancel, and anything that discards or refuses.
     *
     * Filled rather than the outlined-red it used to be. An outlined red label reads as a warning
     * *about* the button; a red chip reads as the negative option, which is what Cancel and Reject
     * actually are. It is a light tint rather than a solid red for the same reason it is not a
     * warning: backing out of a form is an ordinary thing to do, and a saturated red button makes
     * every cancellation feel like a deletion.
     */
    Destructive,
}

enum class OrbitButtonSize { Small, Medium, Large }

/** Which side of the label the glyph sits on. */
enum class OrbitButtonIconPosition {
    /**
     * Before the label. The default, and right whenever the glyph and the label are naming the same
     * thing: the mark is the faster of the two to recognise, so leading it shortens the scan.
     */
    Leading,

    /**
     * After the label.
     *
     * For a glyph that is not naming the action but pointing at its consequence — the arrow on
     * "Open" or "Login". A direction indicator belongs at the end of the phrase it applies to, for
     * the same reason "next →" reads correctly and "→ next" does not.
     */
    Trailing,
}

/**
 * The standard button.
 *
 * ### Layout
 *
 * The visible height comes from [size] — 32/40/48dp — but the touch target is always expanded to
 * `sizing.minTouchTarget`, 48dp on Android and 44pt on iOS. Small and Medium therefore look
 * compact while still occupying a compliant hit area, which matters when a Site Engineer is tapping
 * with gloves on.
 *
 * Height is a minimum, never fixed, and the label is not capped to one line: at 200% font scale the
 * button grows and the label wraps rather than being clipped (WCAG 1.4.4). There is also a minimum
 * width, without which a two-letter label renders as a near-square.
 *
 * The shape is a 12dp rounded rectangle on both platforms, not a pill — see `OrbitShapeTokens`.
 *
 * ### Surface
 *
 * Filled variants get the same translucent treatment as a badge: a gradient fill under a specular
 * highlight along the top edge, plus a hairline rim. See `Modifier.orbitGlass`. Hovering lifts the
 * highlight rather than washing a tint over the button, so a pointer user sees the light move.
 *
 * All of it is contrast-verified against the brightest point of the gradient, hover included —
 * `ControlContrastTest` and `ButtonGlassContrastTest`.
 *
 * ### Accessibility
 *
 * The label is the accessible name, and the glyph is explicitly not described: it restates the
 * label, and "tick icon, Approve" is noise. Descendants are merged so the control is announced as
 * one node rather than as a glyph and a string.
 *
 * @param icon strongly encouraged, and leading by default. Every preset in `:shared`'s
 *   `ui/component/button` supplies one, along with the side it belongs on.
 * @param loading swaps the glyph for a spinning [OrbitLoadingIcon] and blocks input, without
 *   changing the button's colour or width, so the action cannot be double-fired. Pair it with a
 *   label describing the wait ("Sending…").
 */
@Composable
fun OrbitButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OrbitButtonVariant = OrbitButtonVariant.Primary,
    size: OrbitButtonSize = OrbitButtonSize.Medium,
    icon: ImageVector? = null,
    iconPosition: OrbitButtonIconPosition = OrbitButtonIconPosition.Leading,
    state: OrbitButtonState = OrbitButtonState.Active,
    loading: Boolean = false,
) {
    val sizing = OrbitTheme.sizing
    val typeScale = OrbitTheme.typeScale
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.button
    val isDark = OrbitTheme.isDark

    // The badge tone each tinted variant borrows. Blue for the affirmative, Red for backing out;
    // everything else stays neutral and takes the control palette below.
    val tone: OrbitBadgeColors? = when (variant) {
        OrbitButtonVariant.Primary -> OrbitBadgeTone.Blue.colors
        OrbitButtonVariant.Destructive -> OrbitBadgeTone.Red.colors
        else -> null
    }

    val container = when {
        tone != null -> tone.container
        variant == OrbitButtonVariant.Secondary -> control.controlContainer
        else -> Color.Transparent
    }
    val content = when {
        tone != null -> tone.label
        else -> control.controlContent
    }
    val ring = when {
        tone != null -> tone.border
        variant == OrbitButtonVariant.Outline -> control.outlineBorder
        else -> null
    }
    val filled = container != Color.Transparent

    val minHeight = size.pick(sizing.buttonHeightSm, sizing.buttonHeightMd, sizing.buttonHeightLg)
    val minWidth = size.pick(
        sizing.buttonMinWidthSm, sizing.buttonMinWidthMd, sizing.buttonMinWidthLg,
    )
    val glyphSize = size.pick(sizing.buttonIconSm, sizing.buttonIconMd, sizing.buttonIconLg)
    val endPadding = size.pick(
        sizing.buttonPaddingSm, sizing.buttonPaddingMd, sizing.buttonPaddingLg,
    )
    // Three distinct steps, one per size. This is a change: Small used to borrow Medium's type on
    // the grounds that a text-only pill had no glyph sharing the load, so shrinking the label left
    // an empty shape with a caption in it. Now that every button carries a glyph that argument is
    // gone — the mark holds the chip's presence at Small, which frees the label to actually be
    // small and lets the three sizes read as three sizes rather than as three heights of the same
    // button.
    //
    // Both platforms' scales are followed by taking these from the type ramp rather than pinning
    // sp values, so Android's 16dp base and iOS's 17pt base each produce their own three steps and
    // both track the user's font-size setting.
    val textStyle = size.pick(
        OrbitTheme.typography.labelMedium,
        OrbitTheme.typography.labelLarge,
        OrbitTheme.typography.titleMedium,
    ).copy(
        // SemiBold at Small, Medium above it. Stroke weight is what carries a short label at small
        // sizes: "Open" at Medium weight inside a light blue chip reads as a caption on a shape,
        // and the fix is more ink rather than more pixels.
        fontWeight = size.pick(FontWeight.SemiBold, FontWeight.Medium, FontWeight.Medium),
        // Tracking runs the opposite way to weight, and both are corrections for the same thing.
        //
        // The house default is 0, set deliberately for body text. A button label is not body text:
        // it is two or three words with no line to sit on, so the eye reads the whole shape at once
        // and the shape of a short bold word is a blob unless the letters are given air. Hence
        // positive tracking at Small, where the label is heaviest and most cramped, easing to
        // slightly negative at Large, where 18sp letters already have plenty of room and default
        // tracking starts to look like a gap between each pair.
        letterSpacing = size.pick(
            typeScale.buttonTrackingSm,
            typeScale.buttonTrackingMd,
            typeScale.buttonTrackingLg,
        ),
    )

    // Gap scales with the button. At a fixed gap a Large button looks like a Small one with a long
    // label, because the glyph and text stay clamped together while everything else grows.
    val gap = size.pick(spacing.xs, spacing.sm, spacing.sm)

    // Two states, so this is now just "full strength or faded". Disabled fades every layer at once,
    // which is only allowed because WCAG 1.4.3 exempts inactive controls from the contrast minimum;
    // that exemption is also why the old Inactive state could not do the same and needed a
    // per-variant rule about which single layer was safe to fade. Removing it removed the rule.
    val dims = when (state) {
        OrbitButtonState.Active -> Dim(1f, 1f, 1f)
        OrbitButtonState.Disabled -> OrbitAlpha.Disabled.let { Dim(it, it, it) }
    }
    val interactive = state.interactive && !loading

    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    // The badge highlight, not the button one, because these are badge tones on a badge fill — and
    // the tone palette is tuned against this exact gradient. The generator verifies every label at
    // both this level and this level lifted by ButtonHoverLift, so the hovered top edge, which is
    // the brightest a button ever gets, is covered rather than assumed.
    val baseHighlight =
        if (isDark) OrbitGlass.BadgeHighlightDark else OrbitGlass.BadgeHighlightLight
    val highlight by animateFloatAsState(
        targetValue = when {
            !filled -> 0f
            hovered && interactive -> baseHighlight * OrbitGlass.ButtonHoverLift
            else -> baseHighlight
        },
        animationSpec = tween(HoverMs),
        label = "orbit-button-highlight",
    )

    // Two nodes, and the split is deliberate. The outer Box is the touch target and owns the click;
    // the inner Row is the visible button and owns the drawing and the press animation.
    //
    // Doing it on one node does not work. The visible height can be 32dp while the target must be
    // 48dp, and any modifier that only grows the reported layout size leaves the extra 8dp above
    // and below outside the clickable's pointer bounds — a control that looks compliant and is not.
    // Conversely, hanging the ripple off the outer node would draw it across the full 48dp square
    // instead of inside the rounded rectangle.
    //
    // `propagateMinConstraints` is what keeps `Modifier.fillMaxWidth()` working on a caller's
    // `modifier`: the width lands on the wrapper and is handed down, rather than leaving the button
    // centred at its content width inside a full-width box.
    Box(
        modifier = modifier
            .heightIn(min = sizing.minTouchTarget)
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = interactive,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = minHeight)
                .widthIn(min = minWidth)
                // Before the clip, so the shadow falls outside the pill. Only filled variants get
                // one: Outline, Text and Plain draw no pane, and a shadow under a bare label reads as
                // a rendering bug rather than as depth. It fades with the rest of the button.
                .then(
                    if (filled) {
                        Modifier.orbitGlassShadow(
                            shape = shape,
                            elevation = sizing.shadowButton,
                            alpha = dims.container,
                        )
                    } else {
                        Modifier
                    },
                )
                .clip(shape)
                .then(
                    if (filled) {
                        Modifier.orbitGlass(
                            fill = container.copy(alpha = container.alpha * dims.container),
                            shape = shape,
                            highlightAlpha = highlight * dims.container,
                            edge = (ring ?: control.controlBorder).let {
                                it.copy(alpha = it.alpha * dims.container)
                            },
                            // A hairline, and on a pill that is enough. The rim is still where a
                            // tonal chip gets its luminosity — which is why the fill is allowed to
                            // stay deep in the dark theme, rather than being lightened, since the
                            // label sits on that fill and brightening it bleaches every tone toward
                            // white. But a pill has a long edge, so a hairline traces a lot of it;
                            // at 2dp the same rim stopped reading as a lit edge and started reading
                            // as an outline drawn around the button. The icon button's ring is much
                            // shorter and keeps the heavier stroke for exactly that reason.
                            edgeWidth = sizing.hairline,
                            // Every fill here is translucent, so all of them take the badge sheen.
                            sheen = OrbitGlass.Sheen,
                        )
                    } else {
                        Modifier
                    },
                )
                .then(
                    // Only for the unfilled variants. A filled one already has its rim, drawn as
                    // the glass edge, and stacking a second border over it doubles the hairline.
                    if (ring == null || filled) {
                        Modifier
                    } else {
                        Modifier.border(
                            sizing.border,
                            ring.copy(alpha = ring.alpha * dims.ring),
                            shape,
                        )
                    },
                )
                .indication(interactionSource, orbitPressIndication())
                .padding(horizontal = endPadding, vertical = spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val tint = content.copy(alpha = content.alpha * dims.content)
            CompositionLocalProvider(LocalContentColor provides tint) {
                val glyph: (@Composable () -> Unit)? = when {
                    loading -> {
                        { OrbitLoadingIcon(size = glyphSize, tint = tint) }
                    }
                    icon != null -> {
                        {
                            Icon(
                                imageVector = icon,
                                // Decorative: the label already names the action.
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(glyphSize),
                            )
                        }
                    }
                    else -> null
                }

                if (iconPosition == OrbitButtonIconPosition.Leading) glyph?.invoke()
                Text(text = label, style = textStyle, color = tint, textAlign = TextAlign.Center)
                if (iconPosition == OrbitButtonIconPosition.Trailing) glyph?.invoke()
            }
        }
    }
}

private fun <T> OrbitButtonSize.pick(small: T, medium: T, large: T): T = when (this) {
    OrbitButtonSize.Small -> small
    OrbitButtonSize.Medium -> medium
    OrbitButtonSize.Large -> large
}

/** The per-layer opacity a state applies. See the comment where it is built. */
private data class Dim(val container: Float, val ring: Float, val content: Float)

/** Ring alpha for Destructive — present enough to bound the target, faint enough to stay a ring. */
private const val DestructiveRingAlpha = 0.5f

private const val HoverMs = 120
