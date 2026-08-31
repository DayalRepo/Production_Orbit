package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitElevatedFill
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors

/** How big a field is. The same three steps as buttons, so a form can match its controls. */
enum class OrbitFieldSize { Small, Medium, Large }

/**
 * What a field is currently saying about its contents.
 *
 * Separate from focus, which the field reads from its own interaction source. A field can be
 * focused *and* in error — that is in fact the most common moment for an error to exist, since the
 * user is standing in the field fixing it — so the two cannot share one enum.
 */
enum class OrbitFieldState {
    /** Nothing to report. */
    Default,

    /** The value is wrong or missing. Solid red rim. */
    Error,

    /** The value has been checked and accepted. Solid green rim. */
    Success,
}

/**
 * The pane every input field is drawn on: fill, rim, shadow, focus response.
 *
 * Factored out because the plain field and the search field differ only in shape and in what they
 * put inside, and duplicating the chrome between them is how two inputs on the same screen end up
 * with subtly different rims.
 *
 * ### Solid, not glass — the one component that opts out
 *
 * Everything else in this system is drawn through `Modifier.orbitGlass`: a tonal fill under a white
 * highlight gradient. Fields are not, and they are the deliberate exception.
 *
 * The highlight is a white wash over the top of the shape, and a field is the one component whose
 * content is small dark text the user is actively reading and comparing against what they meant to
 * type. On light it lifted the fill toward pure white and cut the contrast of every stroke sitting
 * on it; on dark it was worse — a visible pale film across the top of the box that read as a
 * rendering artefact rather than as material, and grew more obvious the moment there was text under
 * it. Glass works on a badge because a badge is a coloured object you glance at. It does not work
 * on a surface whose whole job is to disappear behind its own text.
 *
 * So the fill here is flat `cardContainer`: solid white on light, solid near-black on dark. The rim
 * and the contact shadow do the work of separating the field from its surface, which they do the
 * same way on a page or on a card.
 *
 * ### Rim colours are solid too
 *
 * Red for error, green for success, the neutral border otherwise. All opaque, taken from the badge
 * palette's `label` shades, which the generator has already verified past 4.5:1 in both themes — an
 * error rim a low-vision user cannot see is worse than no error rim, because the form then just
 * looks unresponsive.
 *
 * ### Focus is a quiet, separate colour
 *
 * Focus does *not* borrow the accent used by primary buttons. That colour is loud by design — it is
 * the "do the thing" blue — and putting it around a box you are merely typing into made a form of
 * five fields look like it had a button in the middle of it. It also collided with the error rim:
 * focusing a field in error swapped red for blue and hid the error while you fixed it.
 *
 * Instead focus uses `outlineBorder` — the neutral ink at partial strength, a step up from the
 * resting rim in weight but the same family in hue — and only when the field has nothing more
 * important to say. Error and success outrank it and keep their colour while focused.
 *
 * The rim also thickens, by half a point rather than a full one, so the state is never carried by
 * colour alone (WCAG 1.4.1). Both the colour and the width animate, because focus travels between
 * fields as fast as a person can tab and an instant swap on each reads as flicker.
 */
@Composable
internal fun OrbitFieldShell(
    interactionSource: InteractionSource,
    shape: Shape,
    minHeight: Dp,
    horizontalPadding: Dp,
    enabled: Boolean,
    state: OrbitFieldState,
    modifier: Modifier = Modifier,
    // RowScope, so the field's own content can claim the remaining width with `weight(1f)` while
    // the leading glyph and trailing controls size to themselves.
    /**
     * Gap between the field's own slots — leading glyph, value, trailing control.
     *
     * Defaults to the standard inline gap. A field whose leading glyph *names the field* rather than
     * decorating it wants this tighter: at the default gap a magnifier and the word "Search" read as
     * two separate items sharing a box, when they are one label split across a glyph and a word.
     */
    contentGap: Dp? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors

    val focused by interactionSource.collectIsFocusedAsState()

    val targetRim = when {
        !enabled -> control.controlBorder.copy(alpha = control.controlBorder.alpha * OrbitAlpha.Disabled)
        state == OrbitFieldState.Error -> OrbitBadgeTone.Red.colors.label
        state == OrbitFieldState.Success -> OrbitBadgeTone.Green.colors.label
        // The brand blue, which this deliberately did not use for a long time on the grounds that
        // focus should stay monochrome. That held while a screen had two fields on it. On a form with
        // eight, a focus rim that differs from the resting rim only in being darker is not findable
        // at a glance, and "where does my typing go" is the one question a rim has to answer
        // instantly - it is also the state a keyboard-only user navigates entirely by.
        focused -> control.borderFocus
        else -> control.controlBorder
    }
    val rim by animateColorAsState(targetRim, tween(FocusMs), label = "orbit-field-rim")

    // A half-step, not a doubling. The rim going from 1dp to 2dp on focus was a visible jolt that
    // reflowed nothing but looked like it had — the box appeared to grow. 1.5dp is enough to be
    // felt as a change without being read as a different component.
    val width by animateDpAsState(
        targetValue = if (focused || state != OrbitFieldState.Default) {
            sizing.borderFocus
        } else {
            sizing.hairline
        },
        animationSpec = tween(FocusMs),
        label = "orbit-field-rim-width",
    )

    Row(
        modifier = modifier
            // A minimum, never a fixed height: the field holds text at whatever size the user's
            // system is set to, and it has to grow rather than clip it.
            .heightIn(min = minHeight)
            // Level 0 — no shadow at all, which is a statement rather than an omission. A text
            // field is recessed into the form, not raised off it, and the small shadow it used to
            // carry was quietly claiming the opposite. The rim and the fill carry the edge.
            // Flat fill, then rim. No `orbitGlass` — see the note above; the white highlight it
            // paints is exactly what was washing over typed text.
            .background(color = control.insetContainer, shape = shape)
            .border(width = width, color = rim, shape = shape)
            .padding(horizontal = horizontalPadding, vertical = spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(contentGap ?: spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

private const val FocusMs = 120
