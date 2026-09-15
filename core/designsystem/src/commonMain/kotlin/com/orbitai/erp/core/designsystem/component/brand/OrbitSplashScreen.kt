package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Opening beat on a transparent canvas (no plate / no themed fill — window colour shows through).
 *
 * Brand mark only, screen-centred:
 * 1. Collapsed at the O centre
 * 2. Expands to the full ring
 * 3. Holds
 * 4. Collapses back to the centre
 * 5. Fades out into the next screen
 */
@Composable
fun OrbitSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val markColor = OrbitMarkDefaults.color()
    val contentAlpha = remember { Animatable(1f) }
    val markSpread = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        markSpread.snapTo(0f)

        markSpread.animateTo(
            1f,
            tween(durationMillis = SpreadExpandMs.toInt(), easing = FastOutSlowInEasing),
        )
        if (!isActive) return@LaunchedEffect

        delay(HoldExpandedMs)
        if (!isActive) return@LaunchedEffect

        markSpread.animateTo(
            0f,
            tween(durationMillis = SpreadCompressMs.toInt(), easing = FastOutSlowInEasing),
        )
        if (!isActive) return@LaunchedEffect

        contentAlpha.animateTo(
            0f,
            tween(durationMillis = FadeOutMs.toInt(), easing = FastOutSlowInEasing),
        )
        if (isActive) onFinished()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        OrbitOpeningMark(
            color = markColor,
            size = OrbitOpeningLockup.SplashMarkSize,
            spread = markSpread.value,
            contentDescription = "Orbit.ai",
            modifier = Modifier.alpha(contentAlpha.value),
        )
    }
}

private const val SpreadExpandMs = 560L
private const val HoldExpandedMs = 900L
private const val SpreadCompressMs = 420L
private const val FadeOutMs = 240L
