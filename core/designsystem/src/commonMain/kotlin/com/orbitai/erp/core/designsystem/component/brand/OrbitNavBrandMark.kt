package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Orbit pixel mark for the bottom-nav AI / assistant circle — same optical size as sibling stroke
 * glyphs in the bar.
 */
@Composable
fun OrbitNavBrandMark(
    modifier: Modifier = Modifier,
    size: Dp = OrbitNavBrandMarkDefaults.Size,
    color: Color = OrbitMarkDefaults.color(),
    contentDescription: String? = null,
) {
    OrbitMark(
        modifier = modifier,
        size = size,
        color = color,
        contentDescription = contentDescription,
    )
}

object OrbitNavBrandMarkDefaults {
    /** Fallback when a caller does not pass the live nav glyph size. */
    val Size = 30.dp
}
