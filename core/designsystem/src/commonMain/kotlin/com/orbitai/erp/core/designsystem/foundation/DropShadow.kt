package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.orbitai.erp.core.designsystem.theme.OrbitElevationLevel
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Applies a blur of [radiusPx] to [paint], using whatever the platform's 2D library provides.
 *
 * This is an `expect` for one reason: `Modifier.blur` — the obvious common-code answer — is a
 * **no-op below Android 12**. The project ships to API 24, so a blur-based shadow would silently
 * render nothing on a large share of devices, and it would do so on exactly the cheaper hardware
 * least likely to be in a reviewer's hand. A mask filter on the paint is supported on every version
 * of both platforms.
 */
internal expect fun Paint.orbitBlur(radiusPx: Float)

/**
 * A soft, downward drop shadow — in the light theme only.
 *
 * ### Why not `Modifier.shadow`
 *
 * The framework's shadow takes a single elevation value and derives everything else from it, which
 * makes the three things this spec cares about — vertical offset, blur radius, opacity — impossible
 * to set independently. It also renders through the platform's own elevation machinery, which on
 * Android means a shadow tuned for Material's light model and on iOS means something visibly
 * different for the same input. Painting it ourselves is what makes a card look like the same card
 * on both platforms.
 *
 * ### Why this does nothing in the dark theme
 *
 * Because a black shadow on a near-black page is not a subtle effect, it is no effect. Depth in the
 * dark theme comes from [OrbitElevationLevel.darkSurface] — a lighter container — and a rim, which
 * are applied by the component rather than here. Returning early is honest about that: the modifier
 * does not pretend to have drawn something invisible, it declines to allocate a layer at all.
 *
 * @param shape must match the shape the content is drawn with, or the shadow will show past a corner.
 * @param level the rung to draw. [OrbitShadow.Level0] draws nothing by definition.
 * @param alpha scales the whole shadow, for animating a control between resting and raised.
 */
@Composable
fun Modifier.orbitDropShadow(
    shape: Shape,
    level: OrbitElevationLevel,
    alpha: Float = 1f,
): Modifier {
    if (OrbitTheme.isDark) return this
    if (level.opacity <= 0f || alpha <= MinimumVisibleAlpha) return this

    val shadowColor = Color.Black.copy(alpha = level.opacity * alpha)

    return drawWithCache {
        val blurPx = level.blur.toPx()
        val offsetPx = level.offsetY.toPx()
        val outline = shape.createOutline(size, layoutDirection, this)

        // Cached across frames. The uncached version built a new [Paint] and [BlurMaskFilter] on
        // every draw, which is fine for a static card and expensive for anything that animates or
        // for a whole screen recomposing at once — exactly what a light-to-dark theme flip does to
        // every elevated surface in the gallery.
        val paint = Paint().apply {
            color = shadowColor
            if (blurPx > 0f) orbitBlur(blurPx)
        }

        onDrawBehind {
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.translate(0f, offsetPx)
                canvas.drawOutline(outline, paint)
                canvas.restore()
            }
        }
    }
}

/** Below this the shadow costs a layer and renders nothing. */
private const val MinimumVisibleAlpha = 0.02f

/**
 * The container fill a surface takes at [level].
 *
 * Light themes hold one fill across the whole ladder — a card and a dialog are both white, and their
 * difference in height is carried entirely by the shadow. Dark themes cannot do that, so the fill
 * *is* the elevation signal and every rung gets its own.
 *
 * Reading the fill through a function rather than hardcoding `cardContainer` is what lets a single
 * component be correct in both themes without an `if (isDark)` at every call site.
 */
@Composable
@ReadOnlyComposable
fun orbitElevatedFill(level: OrbitElevationLevel): Color =
    if (OrbitTheme.isDark) level.darkSurface else OrbitTheme.controlColors.cardContainer
