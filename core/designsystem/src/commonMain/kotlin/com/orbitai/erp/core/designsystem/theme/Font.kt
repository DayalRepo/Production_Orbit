package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.resources.Res
import com.orbitai.erp.core.designsystem.resources.dmsans_bold
import com.orbitai.erp.core.designsystem.resources.dmsans_medium
import com.orbitai.erp.core.designsystem.resources.dmsans_regular
import com.orbitai.erp.core.designsystem.resources.dmsans_semibold
import org.jetbrains.compose.resources.Font

/**
 * DM Sans — the product typeface for UI copy and KPI figures.
 *
 * Static weights from `composeResources/font/` so every supported Android API (minSdk 24) can
 * select Regular / Medium / SemiBold / Bold. Those four cover [OrbitFontWeights]; lighter or
 * heavier requests fall to the nearest bundled face.
 *
 * Licensed under the SIL Open Font License 1.1 — see `licenses/OFL-DM-Sans.txt`.
 */
@Composable
internal fun orbitFontFamily(): FontFamily = FontFamily(
    Font(Res.font.dmsans_regular, FontWeight.Thin),
    Font(Res.font.dmsans_regular, FontWeight.ExtraLight),
    Font(Res.font.dmsans_regular, FontWeight.Light),
    Font(Res.font.dmsans_regular, FontWeight.Normal),
    Font(Res.font.dmsans_medium, FontWeight.Medium),
    Font(Res.font.dmsans_semibold, FontWeight.SemiBold),
    Font(Res.font.dmsans_bold, FontWeight.Bold),
    Font(Res.font.dmsans_bold, FontWeight.ExtraBold),
    Font(Res.font.dmsans_bold, FontWeight.Black),
)

/**
 * Tabular figures, for quantities, currency and totals that must align on the decimal in a column.
 *
 * DM Sans ships a `tnum` feature, so numeric styles stay in the product typeface instead of
 * switching to a monospace face that would not match anything else on the screen.
 */
internal const val TabularNumbers = "tnum"
