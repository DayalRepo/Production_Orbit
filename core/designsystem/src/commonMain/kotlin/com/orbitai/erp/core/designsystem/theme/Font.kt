package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.resources.Res
import com.orbitai.erp.core.designsystem.resources.googlesansflex_black
import com.orbitai.erp.core.designsystem.resources.googlesansflex_bold
import com.orbitai.erp.core.designsystem.resources.googlesansflex_extrabold
import com.orbitai.erp.core.designsystem.resources.googlesansflex_extralight
import com.orbitai.erp.core.designsystem.resources.googlesansflex_light
import com.orbitai.erp.core.designsystem.resources.googlesansflex_medium
import com.orbitai.erp.core.designsystem.resources.googlesansflex_regular
import com.orbitai.erp.core.designsystem.resources.googlesansflex_semibold
import com.orbitai.erp.core.designsystem.resources.googlesansflex_thin
import org.jetbrains.compose.resources.Font

/**
 * Google Sans Flex, all nine weights, bundled from `composeResources/font/`.
 *
 * These are the static instances Google Fonts serves per weight, not the six-axis variable font.
 * Variable-axis selection needs Android API 26 and `minSdk` is 24, so on Android 7 devices every
 * weight would collapse to Regular. Statics render identically on every supported device.
 *
 * All nine ship because weight choices are still open — nothing needs re-exporting when the UI
 * settles on, say, Medium for titles. The cost is roughly 1.1 MB of assets; if that matters more
 * than the flexibility, drop Thin, ExtraLight, ExtraBold and Black and reclaim about 500 KB.
 *
 * Licensed under the SIL Open Font License 1.1 — see `licenses/OFL-Google-Sans-Flex.txt`.
 */
@Composable
internal fun orbitFontFamily(): FontFamily = FontFamily(
    Font(Res.font.googlesansflex_thin, FontWeight.Thin),
    Font(Res.font.googlesansflex_extralight, FontWeight.ExtraLight),
    Font(Res.font.googlesansflex_light, FontWeight.Light),
    Font(Res.font.googlesansflex_regular, FontWeight.Normal),
    Font(Res.font.googlesansflex_medium, FontWeight.Medium),
    Font(Res.font.googlesansflex_semibold, FontWeight.SemiBold),
    Font(Res.font.googlesansflex_bold, FontWeight.Bold),
    Font(Res.font.googlesansflex_extrabold, FontWeight.ExtraBold),
    Font(Res.font.googlesansflex_black, FontWeight.Black),
)

/**
 * Tabular figures, for quantities, currency and totals that must align on the decimal in a column.
 *
 * Google Sans Flex ships a `tnum` feature, so numeric styles stay in the product typeface instead of
 * switching to a monospace face that would not match anything else on the screen.
 */
internal const val TabularNumbers = "tnum"
