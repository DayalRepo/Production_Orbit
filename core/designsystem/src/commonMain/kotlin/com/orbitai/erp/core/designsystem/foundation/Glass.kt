package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitGlass

/**
 * Draws the glass treatment shared by badges and buttons.
 *
 * Three layers, bottom to top:
 *
 * 1. **The fill**, as a vertical gradient. Its alpha starts at `fill.alpha * sheen` along the top
 *    edge and eases to `fill.alpha` at the bottom. On a translucent fill this reads as a pane
 *    catching light; on an opaque one the sheen is left at 1.0 and the layer is flat.
 * 2. **A white highlight**, strongest at the top edge and fading to nothing. This is what actually
 *    sells the effect — it is the specular line along the top of every button in the reference
 *    images.
 * 3. **A hairline edge**, bright at the top and fading toward the bottom, so the pill reads as
 *    having a thickness rather than being a flat outline.
 *
 * There is no backdrop blur. Compose Multiplatform has no cross-platform way to sample what is
 * behind a composable, so a real frosted pane would mean a `UIVisualEffectView` on iOS and an
 * API-31 `RenderEffect` on Android — two platform implementations for an effect nobody can resolve
 * on a 28dp pill. Translucency plus a specular highlight gets the same read.
 *
 * **The gradients must stay linear two-stop gradients.** Every label and icon shade in
 * `BadgeColors.kt` was tuned by sampling this exact stack down the pill's height, and
 * `BadgeContrastTest` reproduces it. Changing the easing, adding a stop, or raising an alpha
 * without regenerating the palette moves the background out from under text that was measured
 * against it.
 *
 * @param fill base colour. Carry the translucency in this colour's own alpha rather than passing an
 *   opaque colour and dimming it afterwards, since the sheen is applied relative to it.
 * @param edge hairline colour, or null for no edge.
 * @param highlightAlpha peak alpha of the white highlight. Use `OrbitGlass.Badge*`/`Button*`
 *   rather than a literal; those are the values the palette was verified against.
 */
fun Modifier.orbitGlass(
    fill: Color,
    shape: Shape,
    highlightAlpha: Float,
    edge: Color? = null,
    edgeWidth: Dp = Dp.Hairline,
    sheen: Float = OrbitGlass.Sheen,
): Modifier {
    val sheened = fill.copy(alpha = (fill.alpha * sheen).coerceAtMost(1f))
    return this
        .background(Brush.verticalGradient(listOf(sheened, fill)), shape)
        .background(
            // White at zero alpha rather than Color.Transparent: the latter is transparent *black*,
            // and Compose interpolates gradient stops unpremultiplied, so it would drag the middle
            // of the highlight toward grey and dirty the fill.
            Brush.verticalGradient(
                listOf(Color.White.copy(alpha = highlightAlpha), Color.White.copy(alpha = 0f)),
            ),
            shape,
        )
        .then(
            if (edge == null) {
                Modifier
            } else {
                Modifier.border(
                    width = edgeWidth,
                    brush = Brush.verticalGradient(
                        listOf(edge, edge.copy(alpha = edge.alpha * OrbitGlass.EdgeFade)),
                    ),
                    shape = shape,
                )
            },
        )
}
