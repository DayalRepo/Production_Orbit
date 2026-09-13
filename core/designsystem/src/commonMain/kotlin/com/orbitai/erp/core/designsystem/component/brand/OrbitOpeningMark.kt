package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pixel letter O for the brand-intro / opening splash — sized to the horizontal lockup next to
 * `rbit.ai` (see [OrbitOpeningLockup]).
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
 * Brand-intro horizontal lockup sizes.
 *
 * - Mark: 56dp capital O — sized to sit as the cap of `Orbit.ai`
 * - Word: 32sp heading weight in theme ink, so `rbit.ai` does not overpower the mark
 * - Gap: lettermark kerning; whole lockup stays screen-centred
 */
object OrbitOpeningLockup {
    /** Pixel O — brand-intro capital letter size. */
    val MarkSize: Dp = 56.dp

    /**
     * Wordmark size beside the mark. Heading (SemiBold) rather than display (Bold):
     * Bold at this size next to the pixel O reads as a second logo, not the rest of the word.
     */
    val WordSize: TextUnit = 32.sp

    /** Mark-to-type gap — lettermark kerning scaled with the lockup. */
    val MarkToWordGap: Dp = 6.dp

    /** Slight positive tracking so SemiBold letters stay open next to the mark. */
    val WordTracking: TextUnit = 0.2.sp
}
