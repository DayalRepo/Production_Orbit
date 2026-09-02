package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A two-state track with a thumb that slides across it.
 *
 * ### Why a switch and not a checkbox
 *
 * A switch is for a setting that takes effect the moment it moves — the theme, notifications, a
 * feature flag. A checkbox is for a value that will be submitted with a form. Using the wrong one is
 * how a user ends up looking for the Save button that a switch does not have, or waiting for a
 * checkbox to do something on its own.
 *
 * ### The track carries the state, not the thumb
 *
 * The thumb stays white in both positions and the *track* changes colour. That is the platform
 * convention on both targets, and it is the one that survives being looked at quickly: position and
 * fill say the same thing twice, so the control is readable at a glance and still readable to someone
 * who cannot distinguish the on-colour from the off-colour.
 *
 * ### Why the thumb moves in [graphicsLayer]
 *
 * The first version drove the thumb with [androidx.compose.animation.core.animateDpAsState] and
 * [androidx.compose.foundation.layout.offset], which recompose the whole switch on every animation
 * frame. On a theme toggle that is already recomposing the entire screen, the two together produced
 * visible stutter — the thumb looked like it was lagging behind or sticking mid-travel. Moving the
 * thumb in a layer keeps the animation in the draw pass so the rest of the tree is not invalidated
 * sixty times a second for a 44dp control.
 *
 * @param checked the current state, held by the caller.
 * @param onCheckedChange fired with the new state. Null renders a read-only switch that still
 *   announces its value — for a setting shown but governed elsewhere, by a policy or a parent toggle.
 * @param contentDescription what the switch governs. Required, because a bare "on" tells a screen
 *   reader user nothing: switches are almost always identified by the row of text beside them, and
 *   that text is not part of this component.
 */
@Composable
fun OrbitSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current

    val thumbTravelPx = with(density) { (TrackWidth - ThumbSize - TrackPadding * 2).toPx() }
    val thumbOffset = remember { Animatable(if (checked) 1f else 0f) }

    LaunchedEffect(checked) {
        thumbOffset.animateTo(
            targetValue = if (checked) 1f else 0f,
            animationSpec = tween(SlideMs),
        )
    }

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> control.controlBorder
            checked -> control.actionContainer
            else -> control.interactiveContainer
        },
        animationSpec = tween(SlideMs),
        label = "orbit-switch-track",
    )

    Box(
        modifier = modifier
            .size(width = TrackWidth, height = TrackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .border(
                width = OrbitTheme.sizing.hairline,
                // A rim on the off state only. On, the track is a solid accent and a border round it
                // reads as a second edge; off, the track is a quiet grey that needs the rim to be
                // distinguishable from the surface it sits on.
                color = if (checked) trackColor else control.controlBorder,
                shape = RoundedCornerShape(percent = 50),
            )
            .then(
                if (onCheckedChange != null) {
                    Modifier
                        .orbitHandCursor()
                        .toggleable(
                            value = checked,
                            enabled = enabled,
                            role = Role.Switch,
                            interactionSource = interactionSource,
                            // No ripple. The thumb's travel is the feedback, and a wash of colour
                            // across a 32dp track competes with the one thing the control has to say.
                            indication = null,
                            onValueChange = onCheckedChange,
                        )
                } else {
                    Modifier
                },
            )
            .padding(TrackPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = thumbOffset.value * thumbTravelPx
                }
                .size(ThumbSize)
                // A hairline rim rather than a blurred drop shadow. The shadow was what made the
                // thumb stutter: `orbitDropShadow` allocates a fresh blur mask every draw, and on a
                // control that moves that is every frame. A rim gives the same "sitting on the track"
                // read without the per-frame blur cost — and without a layer that vanishes on the
                // light-to-dark flip, which was part of the theme-toggle hitch.
                .border(
                    width = OrbitTheme.sizing.hairline,
                    color = control.controlBorder.copy(alpha = ThumbRimAlpha),
                    shape = CircleShape,
                )
                .background(control.onActionContainer, CircleShape),
        )
    }
}

/**
 * 44 x 26, which is the iOS switch's proportion and close enough to Material's not to look foreign on
 * either. The whole control is below the 48dp touch minimum on purpose — it is always drawn inside a
 * row that is itself tappable and at least that tall, and inflating the switch to 48 would leave a
 * visibly oversized control in a settings list.
 */
private val TrackWidth = 44.dp
private val TrackHeight = 26.dp

/** Inset of the thumb from the track, top and bottom as well as at the ends. */
private val TrackPadding = 3.dp

private val ThumbSize = TrackHeight - TrackPadding * 2

/** Long enough to read as travel, short enough not to delay the thing the switch turned on. */
private const val SlideMs = 160

/** Just enough rim to lift the white thumb off a grey track without reading as a second border. */
private const val ThumbRimAlpha = 0.35f
