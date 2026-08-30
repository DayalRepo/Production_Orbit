package com.orbitai.erp.core.designsystem.component.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The house spinner: the Hugeicons `loading-03` glyph, rotating.
 *
 * Used instead of `CircularProgressIndicator` so that a button in flight still looks like it came
 * from this design system rather than from stock Material. The glyph is a ring of graduated dashes,
 * which is what makes the rotation legible — a closed circle would spin invisibly.
 *
 * Rotation is linear and continuous. There is no easing, because an eased spin reads as stuttering
 * rather than as smooth, and no progress semantics, because none of the callers know a duration.
 *
 * @param size defaults to `sizing.iconSm`, which is the size that sits correctly against label text
 *   in a button. Pass a larger value for a standalone page-level spinner.
 */
@Composable
fun OrbitLoadingIcon(
    modifier: Modifier = Modifier,
    size: Dp = OrbitTheme.sizing.iconSm,
    tint: Color = LocalContentColor.current,
) {
    val transition = rememberInfiniteTransition(label = "orbit-loading")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SpinDurationMs, easing = LinearEasing),
        ),
        label = "orbit-loading-rotation",
    )

    Icon(
        imageVector = OrbitIcons.Loading,
        // Decorative. A spinner sits next to a label that has already been swapped to describe the
        // wait ("Sending…"), and the button itself is disabled while in flight, so announcing the
        // glyph as well would just be repetition.
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation },
    )
}

/**
 * One revolution per 900ms.
 *
 * Slower than Material's 1332ms sweep on purpose: this glyph has eight discrete dashes, and at
 * Material's rate the dash pattern reads as drifting rather than spinning.
 */
private const val SpinDurationMs = 900
