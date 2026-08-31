package com.orbitai.erp.core.designsystem.foundation

import android.graphics.BlurMaskFilter
import androidx.compose.ui.graphics.Paint

/**
 * Android's mask filter, available since API 1 and unaffected by the API 31 floor on
 * `Modifier.blur`.
 *
 * `NORMAL` blurs both inside and outside the shape, which is the correct mode for a shadow: the
 * silhouette should dissolve at its edge in both directions. `OUTER` would leave a hard core the
 * size of the original shape with a soft fringe around it, which reads as an outline rather than a
 * shadow.
 *
 * The radius is halved because `BlurMaskFilter` treats its argument as a standard deviation, while
 * the design spec's "blur" is the visible extent in the way a design tool reports it — roughly twice
 * the sigma. Passing the spec value straight through produces a shadow about double the intended
 * spread.
 */
internal actual fun Paint.orbitBlur(radiusPx: Float) {
    asFrameworkPaint().maskFilter = BlurMaskFilter(radiusPx / 2f, BlurMaskFilter.Blur.NORMAL)
}
