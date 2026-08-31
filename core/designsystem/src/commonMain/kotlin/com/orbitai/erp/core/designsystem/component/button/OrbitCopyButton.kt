package com.orbitai.erp.core.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import kotlinx.coroutines.delay

/**
 * Puts a value on the clipboard and confirms it in place.
 *
 * ### The tick replaces the glyph rather than joining it
 *
 * Swapping the icon, instead of showing a tick beside it or flashing a toast, is the whole design of
 * this feedback. The button's width does not change, so nothing around it moves at the exact moment
 * the user is looking to confirm something happened. And the confirmation appears at the point of
 * contact — under the finger that just tapped — rather than at the bottom of the screen where a
 * toast would land, which in a popover anchored near the top means the feedback and the action are
 * nowhere near each other.
 *
 * Green, and green specifically for "this worked". It is usually the only colour in the panel around
 * it, which is what makes it register in peripheral vision without the user having to look straight
 * at it. The meaning is not carried by colour alone: the glyph itself changes from a copy mark to a
 * tick, so the confirmation survives greyscale and colour vision deficiency. The spoken description
 * changes with it, because the tick is a purely visual cue and a non-sighted user is exactly the one
 * who cannot check the clipboard by looking.
 *
 * ### The tick is an event, not a state
 *
 * It expires on its own timer. A tick that stayed up would still be claiming the clipboard holds
 * this value long after the user has copied something else, which is worse than no confirmation at
 * all — it is a confirmation that goes quietly stale.
 *
 * The component owns the clipboard write as well as the feedback. Splitting them, so the caller
 * copies and this only draws a tick, is how you end up with a tick that fires on a copy that failed.
 *
 * @param label what is being copied, in the user's words — "Mobile number". Used to build both
 *   descriptions, so callers do not have to phrase the same thing twice.
 */
@Composable
fun OrbitCopyButton(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    size: OrbitIconButtonSize = OrbitIconButtonSize.Small,
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    // Keyed on the flag so a second copy restarts the timer rather than inheriting the remainder of
    // the first one, which would cut the second confirmation short.
    LaunchedEffect(copied) {
        if (copied) {
            delay(CopiedFeedbackMs)
            copied = false
        }
    }

    OrbitIconButton(
        contentDescription = if (copied) "$label copied" else "Copy ${label.lowercase()}",
        onClick = {
            clipboard.setText(AnnotatedString(value))
            copied = true
        },
        icon = if (copied) OrbitIcons.Tick else OrbitIcons.Copy,
        // `Positive` is the system's existing green-for-affirmative style, so this matches every
        // other "this worked" in the product rather than introducing a second green.
        style = if (copied) OrbitIconButtonStyle.Positive else OrbitIconButtonStyle.Neutral,
        size = size,
        modifier = modifier,
    )
}

/**
 * How long the tick stays up.
 *
 * Long enough to be seen if the eye was elsewhere at the moment of the tap, short enough that it is
 * gone before the user could mistake it for the button's permanent state.
 */
private const val CopiedFeedbackMs = 1200L
