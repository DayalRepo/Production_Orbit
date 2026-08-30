package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The contact shadow that goes under every glass surface: badges, chips, buttons, icon buttons.
 *
 * ### Why glass needs a shadow at all
 *
 * `Modifier.orbitGlass` draws a translucent fill, a specular highlight and a rim. All three are
 * *surface* cues — they describe what the pane is made of — and none of them is a *position* cue. A
 * translucent pane with a lit top edge and a pane painted flat onto the page are the same pixels
 * until something says one of them is in front. On a busy or mid-tone background the highlight is
 * quite readily mistaken for a gradient in the background itself.
 *
 * A shadow is the only cheap cue that reads as depth rather than as another line, and it is what
 * makes the rest of the stack land as glass instead of as a decorated outline. It matters most where
 * the fill is faintest, which is why the icon button's ring gets the deepest of the three.
 *
 * ### The two themes are not the same problem
 *
 * On light, the surface is brighter than any plausible shadow, so a soft black at low alpha separates
 * the pane cleanly and anything stronger reads as a drop shadow from a 2007 web page.
 *
 * On dark it is nearly the opposite. The surfaces are `#121212` and up rather than pure black — which
 * is a house rule, not an accident — so there is headroom below them, but only a little, and a shadow
 * has to be much deeper in alpha to register at all across it. Hence roughly triple the alpha for the
 * same visual weight.
 *
 * What must *not* happen on dark is reaching for a light shadow to compensate. A pale halo under the
 * object puts light beneath it while the highlight puts light on top, and two contradictory light
 * directions is precisely what makes a glass effect read as a sticker. Depth on dark comes from going
 * darker, or not at all.
 *
 * ### Ordering
 *
 * Call this *before* the clip and the glass fill. `shadow` is passed `clip = false` so the shadow
 * falls outside the shape; behind a clip it would be cut off at the very edge it is meant to be
 * falling away from, which produces no shadow and a wasted layer.
 *
 * @param elevation use a `sizing.shadow*` token. Zero is honoured and skips the layer.
 * @param alpha scales the shadow with the rest of the component, so a disabled or inactive control
 *   settles onto the page as its other layers fade rather than hovering over it in full depth.
 */
@Composable
fun Modifier.orbitGlassShadow(
    shape: Shape,
    elevation: Dp,
    alpha: Float = 1f,
): Modifier {
    if (elevation <= 0.dp || alpha <= MinimumVisibleAlpha) return this

    val base = if (OrbitTheme.isDark) OrbitGlass.ShadowDark else OrbitGlass.ShadowLight
    // Black in both themes, and the same black. See the class doc: the dark theme's shadow is deeper,
    // never lighter.
    val color = Color.Black.copy(alpha = base * alpha)

    return shadow(
        elevation = elevation * alpha,
        shape = shape,
        clip = false,
        ambientColor = color,
        spotColor = color,
    )
}

/** Below this the shadow costs a layer and renders nothing. */
private const val MinimumVisibleAlpha = 0.02f
