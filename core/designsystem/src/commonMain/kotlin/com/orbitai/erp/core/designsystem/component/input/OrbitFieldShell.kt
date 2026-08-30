package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The glass shell every input field is drawn in.
 *
 * All three fields — [OrbitTextField], [OrbitSearchField], [OrbitMessageField] — share this rather
 * than each drawing its own container, because a field's chrome is the part users read as "this is
 * where I type". If a search box and a message box arrive at their tint by different routes they
 * will drift apart the first time either is adjusted, and the drift is invisible in review because
 * the two are never on screen together.
 *
 * The container is a translucent tint of the foreground under a specular highlight, the same
 * treatment as a badge, so a field on a card and a field on a sheet both sit in their surface rather
 * than punching a hole in it. Focus is carried by the rim rather than by a fill change: brightening
 * the fill would shift the background under text the user is actively reading, and the rim is where
 * the eye already is once a cursor is blinking inside it.
 *
 * Not public. Fields are the API; this is how they are built.
 *
 * ### Why the shell owns the tap
 *
 * The whole container focuses the field, via [onRequestFocus]. This is not a convenience: a
 * `BasicTextField` is only as tall as the text currently in it, so an empty multi-line field is a
 * one-line hit target sitting inside a five-line box. Everything below the first line — most of the
 * visible field, including the whole area the placeholder occupies — is dead to a tap. That is
 * indistinguishable from a broken field, and it is worse on a phone, where the user aims at the
 * middle of the shape rather than at the text baseline.
 *
 * @param focused drives the rim, and only the rim.
 * @param error draws the rim in the theme's error colour. Overrides [focused], because a field that
 *   is both focused and invalid needs to say invalid — the user is looking at it either way.
 * @param onRequestFocus called when the container is tapped anywhere outside its buttons. The field
 *   passes a `FocusRequester.requestFocus`.
 */
@Composable
internal fun OrbitFieldShell(
    focused: Boolean,
    shape: Shape,
    minHeight: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalPadding: Dp = OrbitTheme.spacing.lg,
    verticalPadding: Dp = OrbitTheme.spacing.sm,
    onRequestFocus: (() -> Unit)? = null,
    // Row-scoped, so a field can give its text area `Modifier.weight(1f)` and let the leading and
    // trailing glyphs keep their intrinsic width. Without the scope every field would have to
    // measure around its own icons.
    content: @Composable RowScope.() -> Unit,
) {
    val control = OrbitTheme.controlColors
    val sizing = OrbitTheme.sizing
    val isDark = OrbitTheme.isDark
    val alpha = if (enabled) 1f else OrbitAlpha.Disabled

    val rimTarget = when {
        error -> OrbitTheme.colorScheme.error
        focused -> control.outlineBorder
        else -> control.controlBorder
    }
    val rim by animateColorAsState(
        targetValue = rimTarget.copy(alpha = rimTarget.alpha * alpha),
        animationSpec = tween(FocusMs),
        label = "orbit-field-rim",
    )

    // Focus also lifts the specular highlight, by the same factor a hovered button uses. It is a
    // small move and it is the only thing that reads on a touch screen, where there is no cursor to
    // tell you which field is live.
    val baseHighlight =
        if (isDark) OrbitGlass.ButtonHighlightDark else OrbitGlass.ButtonHighlightLight
    val highlight by animateFloatAsState(
        targetValue = baseHighlight * (if (focused) OrbitGlass.ButtonHoverLift else 1f) * alpha,
        animationSpec = tween(FocusMs),
        label = "orbit-field-highlight",
    )

    // No indication. A field is not a button, and a ripple washing across the glass every time
    // someone puts a cursor in it reads as an accidental press rather than as focus — the rim and
    // the lifted highlight are the feedback.
    val tapSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            // heightIn, never height: a field holds text, so at 200% font scale it must grow rather
            // than clip (WCAG 1.4.4). Multi-line fields rely on this to expand to their line cap.
            .heightIn(min = minHeight)
            .clip(shape)
            .orbitGlass(
                fill = control.controlContainer
                    .copy(alpha = control.controlContainer.alpha * alpha),
                shape = shape,
                highlightAlpha = highlight,
                edge = rim,
                // borderStrong, not border: `border` is the same 1dp as `hairline`, so swapping
                // between them would leave focus carried by tint alone. 2dp is also what both
                // platforms use for a focus indicator, and it is the width that survives being
                // looked at on a bright screen outdoors.
                edgeWidth = if (focused || error) sizing.borderStrong else sizing.hairline,
            )
            .then(
                if (onRequestFocus != null && enabled) {
                    Modifier.clickable(
                        interactionSource = tapSource,
                        indication = null,
                        onClick = onRequestFocus,
                    )
                } else {
                    Modifier
                },
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.md),
        verticalAlignment = verticalAlignment,
    ) {
        content()
    }
}

private const val FocusMs = 140
