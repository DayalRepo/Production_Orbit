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
 */
@Composable
fun OrbitOpeningMark(
    modifier: Modifier = Modifier,
    size: Dp = OrbitOpeningLockup.MarkSize,
    color: Color = OrbitMarkDefaults.color(),
    contentDescription: String? = "Orbit.ai",
) {
    OrbitMark(
        modifier = modifier,
        size = size,
        color = color,
        contentDescription = contentDescription,
    )
}

/**
 * Brand-intro horizontal lockup sizes.
 *
 * - Mark: 72dp capital O
 * - Word: 44sp light (300)
 * - Gap: lettermark kerning; whole lockup stays screen-centred
 */
object OrbitOpeningLockup {
    /** Pixel O — brand-intro capital letter size. */
    val MarkSize: Dp = 72.dp

    /**
     * Wordmark size beside the mark.
     */
    val WordSize: TextUnit = 44.sp

    /** Mark-to-type gap — lettermark kerning scaled with the larger lockup. */
    val MarkToWordGap: Dp = 8.dp

    /** Slight positive tracking for a light (300) wordmark. */
    val WordTracking: TextUnit = 0.4.sp
}
