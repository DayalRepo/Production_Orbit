package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pixel letter O for the brand-intro / opening splash.
 *
 * Default [size] matches the compact gallery / lockup mark. [OrbitSplashScreen] uses
 * [OrbitOpeningLockup.SplashMarkSize] so the centred solo mark reads as the product icon.
 *
 * @param spread 0f = dots packed at the O centre; 1f = default mark geometry.
 */
@Composable
fun OrbitOpeningMark(
    modifier: Modifier = Modifier,
    size: Dp = OrbitOpeningLockup.MarkSize,
    color: Color = OrbitMarkDefaults.color(),
    contentDescription: String? = "Orbit.ai",
    spread: Float = 1f,
) {
    OrbitMark(
        modifier = modifier,
        size = size,
        color = color,
        contentDescription = contentDescription,
        spread = spread,
    )
}

/**
 * Brand-intro mark sizes.
 *
 * - [MarkSize]: compact capital O for gallery samples and chrome next to type
 * - [SplashMarkSize]: centred solo mark on the opening splash
 */
object OrbitOpeningLockup {
    /** Pixel O — compact brand-intro capital letter size. */
    val MarkSize: Dp = 56.dp

    /** Centred splash mark — large enough to read as the product icon alone. */
    val SplashMarkSize: Dp = 88.dp

    /**
     * Wordmark size beside the mark when a typed lockup is needed elsewhere.
     * Heading (SemiBold) rather than display (Bold).
     */
    val WordSize: TextUnit = 32.sp

    /** Mark-to-type gap — lettermark kerning scaled with a horizontal lockup. */
    val MarkToWordGap: Dp = 6.dp

    /** Slight positive tracking so SemiBold letters stay open next to the mark. */
    val WordTracking: TextUnit = 0.2.sp
}
