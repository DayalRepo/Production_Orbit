package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.ui.graphics.Paint
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter

/**
 * Skia's mask filter, which is what Compose draws through on iOS.
 *
 * Same halving as the Android side, and for the same reason: `makeBlur` takes a sigma, the spec
 * quotes a visible blur extent. Keeping the conversion identical on both platforms is the whole
 * point of doing this by hand rather than through each platform's own elevation system — a card at
 * Level 1 should cast the same shadow on an iPhone as on a Pixel.
 */
internal actual fun Paint.orbitBlur(radiusPx: Float) {
    asFrameworkPaint().maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, radiusPx / 2f)
}
