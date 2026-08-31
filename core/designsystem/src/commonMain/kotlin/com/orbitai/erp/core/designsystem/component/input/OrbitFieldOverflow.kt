package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

/**
 * Softens the edge a single-line field's text runs off, so scrolled-away input is visibly there.
 *
 * ### The problem this solves
 *
 * A single-line `BasicTextField` scrolls horizontally, and neither platform draws anything to say
 * so. Android renders no indicator inside a text field at all; iOS shows its transient scroll
 * indicator only on its own scroll views. The result is that a field containing
 * "drawings/2026/tower-b/level-4/structural-rev-…" looks exactly like a field whose value happens
 * to end there — the user's own text is hidden from them with no signal, and the failure is silent
 * in the worst way: they read what is visible, believe it is the whole value, and submit it.
 *
 * A fade rather than a scrollbar. A scrollbar needs vertical room a 48dp row does not have, and it
 * would be a chrome element in a component that is otherwise entirely text. A softened edge says
 * "continues" using no space at all, which is why it is the convention for horizontally scrolled
 * content everywhere else.
 *
 * ### Which edge, and why it has to be measured
 *
 * The side follows the focus, because that is what determines which end of the text is on screen. A
 * field being typed into keeps the caret in view, so the text scrolls left and the hidden material
 * is behind you: fade the **start**. A field at rest shows the beginning of its value, so the
 * hidden material is ahead: fade the **end**.
 *
 * [overflowed] is measured rather than assumed. Drawing the fade whenever the field merely had a
 * value was tried first, on the theory that a short value would not reach the gradient — and it was
 * wrong on device in a way that is obvious in hindsight. The fade is applied to the text slot,
 * which begins where the text begins, so on every filled field it ate the first character. A value
 * of "Tower B, level 4" rendered with a ghosted T. Whether text has overflowed is not a thing that
 * can be guessed from whether text exists.
 */
/*
 * ### One node, whichever state it is in
 *
 * The fade is a modifier on a single `Box`, never a choice between two of them. Writing it as an
 * `if` that returns a plain slot in one branch and a faded slot in the other is the obvious shape and
 * it carries a bug that is very hard to attribute: the two branches are separate call sites, so the
 * moment `overflowed` flips, Compose disposes the subtree and builds a new one. The `BasicTextField`
 * inside is a *different node* on the other side of that flip, and a text field that has just been
 * recreated is a text field that is no longer focused — the keyboard drops mid-word, exactly when the
 * user types past the width of the field.
 *
 * Keeping the `Box` unconditional and varying only its modifier keeps the node identity stable, so
 * focus, selection and the IME survive the transition.
 */
@Composable
internal fun OrbitFieldOverflowFade(
    overflowed: Boolean,
    atStart: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val fade = with(LocalDensity.current) { FadeDp.dp.toPx() }

    Box(
        modifier = modifier.then(
            if (!overflowed) {
                Modifier
            } else {
                Modifier
                    // Offscreen compositing, so DstIn has something to punch through. Without it the
                    // blend applies against whatever the field is drawn on and erases the fill rather
                    // than the text — the field appears to have a notch cut out of its edge.
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        val (from, to) = if (atStart) {
                            0f to fade
                        } else {
                            size.width to size.width - fade
                        }
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startX = from,
                                endX = to,
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    }
            },
        ),
    ) {
        content()
    }
}

/**
 * Drops focus when the on-screen keyboard goes away.
 *
 * Closing the keyboard with the system back gesture does not clear focus — Compose treats the two
 * as separate, on the reasoning that a hardware keyboard user may still be typing. On a phone that
 * reasoning does not hold, and the visible result is a field left with a caret blinking in it and
 * a lit focus rim, with no keyboard and no way to type. The form looks like it is waiting for input
 * it cannot receive, and the stale rim also means two fields can appear focused at once if the user
 * then taps another.
 *
 * The guard is that this only fires on a *transition* from shown to hidden while this field holds
 * focus. Reacting to "keyboard is hidden" on its own would steal focus the instant a field was
 * tapped, in the frame before the keyboard animates in.
 */
@Composable
internal fun Modifier.orbitReleaseFocusWithKeyboard(): Modifier {
    val focusManager = LocalFocusManager.current
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    var focused by remember { mutableStateOf(false) }
    val windowFocused = LocalWindowInfo.current.isWindowFocused

    LaunchedEffect(focused, windowFocused) {
        if (!focused) return@LaunchedEffect
        var wasOpen = false
        snapshotFlow { imeInsets.getBottom(density) > 0 }.collect { open ->
            if (wasOpen && !open) focusManager.clearFocus()
            wasOpen = open
        }
    }

    return this.onFocusChanged { focused = it.isFocused }
}

/**
 * How far the fade reaches.
 *
 * Short on purpose. Long enough to read as a soft edge rather than a clipped one, short enough that
 * it never fully erases a character — at 16sp a Latin glyph is around 9dp wide, so 12dp touches at
 * most one and a half and leaves the rest legible.
 */
private const val FadeDp = 12
