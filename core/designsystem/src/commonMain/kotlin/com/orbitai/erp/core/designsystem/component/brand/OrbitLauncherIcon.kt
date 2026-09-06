package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Home-screen / launcher application icon: brand mark centred on a squircle plate.
 *
 * Matches the Android adaptive / mipmap look — use this in Compose previews, about screens,
 * and galleries. The installed launcher assets live under `androidApp/src/main/res`.
 */
@Composable
fun OrbitLauncherIcon(
    modifier: Modifier = Modifier,
    size: Dp = OrbitLauncherIconDefaults.Size,
    darkPlate: Boolean = true,
) {
    val plate = if (darkPlate) {
        OrbitLauncherIconDefaults.DarkPlate
    } else {
        OrbitLauncherIconDefaults.LightPlate
    }
    val mark = if (darkPlate) {
        OrbitLauncherIconDefaults.DarkMark
    } else {
        OrbitLauncherIconDefaults.LightMark
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * OrbitLauncherIconDefaults.CornerFraction))
            .background(plate),
        contentAlignment = Alignment.Center,
    ) {
        OrbitMark(
            size = size * OrbitLauncherIconDefaults.MarkFraction,
            color = mark,
            contentDescription = "Orbit.ai",
        )
    }
}

object OrbitLauncherIconDefaults {
    val Size = 72.dp

    /** Black plate + cyan mark (shipping launcher). */
    val DarkPlate = Color(0xFF000000)
    val DarkMark = Color(0xFFC2E7FF)

    /** Light plate + engineering blue mark. */
    val LightPlate = Color(0xFFFFFFFF)
    val LightMark = Color(0xFF004A77)

    /** Slightly inset so the pixel O breathes inside the squircle. */
    const val MarkFraction = 0.46f

    /** Squircle corner relative to [Size] (Material adaptive-icon feel). */
    const val CornerFraction = 0.22f
}
