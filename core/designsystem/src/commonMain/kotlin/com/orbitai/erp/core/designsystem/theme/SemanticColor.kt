package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A foreground/background pair. ERP surfaces show a lot of status at a glance, so every
 * semantic colour ships with a matching container to keep contrast correct in both themes.
 */
@Immutable
data class ColorPair(
    val content: Color,
    val container: Color,
)

/**
 * Domain colours that Material 3 does not model: task/issue lifecycle, defect severity,
 * project health (RAG), and stock levels.
 */
@Immutable
data class OrbitSemanticColors(
    val success: ColorPair,
    val warning: ColorPair,
    val danger: ColorPair,
    val info: ColorPair,
    val neutral: ColorPair,

    val statusOpen: ColorPair,
    val statusInProgress: ColorPair,
    val statusBlocked: ColorPair,
    val statusInReview: ColorPair,
    val statusCompleted: ColorPair,
    val statusOverdue: ColorPair,
    val statusCancelled: ColorPair,

    val severityLow: ColorPair,
    val severityMedium: ColorPair,
    val severityHigh: ColorPair,
    val severityCritical: ColorPair,

    val healthOnTrack: ColorPair,
    val healthAtRisk: ColorPair,
    val healthDelayed: ColorPair,

    val stockHealthy: ColorPair,
    val stockLow: ColorPair,
    val stockOut: ColorPair,

    /** Highlight for AI-generated suggestions, forecasts and auto-assignments. */
    val aiAccent: ColorPair,

    val chartSeries: List<Color>,
)

internal val OrbitLightSemanticColors = OrbitSemanticColors(
    success = ColorPair(OrbitPalette.Green40, OrbitPalette.Green90),
    warning = ColorPair(OrbitPalette.Orange40, OrbitPalette.Orange90),
    danger = ColorPair(OrbitPalette.Red40, OrbitPalette.Red90),
    info = ColorPair(OrbitPalette.Blue40, OrbitPalette.Blue90),
    neutral = ColorPair(OrbitPalette.Neutral30, OrbitPalette.Neutral94),

    statusOpen = ColorPair(OrbitPalette.Slate40, OrbitPalette.Slate90),
    statusInProgress = ColorPair(OrbitPalette.Blue40, OrbitPalette.Blue90),
    statusBlocked = ColorPair(OrbitPalette.Red40, OrbitPalette.Red90),
    statusInReview = ColorPair(OrbitPalette.Violet40, OrbitPalette.Violet90),
    statusCompleted = ColorPair(OrbitPalette.Green40, OrbitPalette.Green90),
    statusOverdue = ColorPair(OrbitPalette.Orange40, OrbitPalette.Orange90),
    statusCancelled = ColorPair(OrbitPalette.Neutral50, OrbitPalette.Neutral94),

    severityLow = ColorPair(OrbitPalette.Teal40, OrbitPalette.Teal90),
    severityMedium = ColorPair(OrbitPalette.Amber40, OrbitPalette.Amber90),
    severityHigh = ColorPair(OrbitPalette.Orange40, OrbitPalette.Orange90),
    severityCritical = ColorPair(OrbitPalette.Red40, OrbitPalette.Red90),

    healthOnTrack = ColorPair(OrbitPalette.Green40, OrbitPalette.Green90),
    healthAtRisk = ColorPair(OrbitPalette.Amber40, OrbitPalette.Amber90),
    healthDelayed = ColorPair(OrbitPalette.Red40, OrbitPalette.Red90),

    stockHealthy = ColorPair(OrbitPalette.Green40, OrbitPalette.Green90),
    stockLow = ColorPair(OrbitPalette.Amber40, OrbitPalette.Amber90),
    stockOut = ColorPair(OrbitPalette.Red40, OrbitPalette.Red90),

    aiAccent = ColorPair(OrbitPalette.Violet40, OrbitPalette.Violet90),

    chartSeries = listOf(
        OrbitPalette.Blue40,
        OrbitPalette.Amber70,
        OrbitPalette.Teal40,
        OrbitPalette.Violet40,
        OrbitPalette.Green40,
        OrbitPalette.Orange40,
        OrbitPalette.Slate60,
        OrbitPalette.Red40,
    ),
)

internal val OrbitDarkSemanticColors = OrbitSemanticColors(
    success = ColorPair(OrbitPalette.Green70, OrbitPalette.Green30),
    warning = ColorPair(OrbitPalette.Orange70, OrbitPalette.Orange30),
    danger = ColorPair(OrbitPalette.Red70, OrbitPalette.Red30),
    info = ColorPair(OrbitPalette.Blue80, OrbitPalette.Blue30),
    neutral = ColorPair(OrbitPalette.Neutral80, OrbitPalette.Neutral22),

    statusOpen = ColorPair(OrbitPalette.Slate80, OrbitPalette.Slate30),
    statusInProgress = ColorPair(OrbitPalette.Blue80, OrbitPalette.Blue30),
    statusBlocked = ColorPair(OrbitPalette.Red70, OrbitPalette.Red30),
    statusInReview = ColorPair(OrbitPalette.Violet70, OrbitPalette.Violet30),
    statusCompleted = ColorPair(OrbitPalette.Green70, OrbitPalette.Green30),
    statusOverdue = ColorPair(OrbitPalette.Orange70, OrbitPalette.Orange30),
    statusCancelled = ColorPair(OrbitPalette.Neutral60, OrbitPalette.Neutral22),

    severityLow = ColorPair(OrbitPalette.Teal70, OrbitPalette.Teal30),
    severityMedium = ColorPair(OrbitPalette.Amber70, OrbitPalette.Amber30),
    severityHigh = ColorPair(OrbitPalette.Orange70, OrbitPalette.Orange30),
    severityCritical = ColorPair(OrbitPalette.Red70, OrbitPalette.Red30),

    healthOnTrack = ColorPair(OrbitPalette.Green70, OrbitPalette.Green30),
    healthAtRisk = ColorPair(OrbitPalette.Amber70, OrbitPalette.Amber30),
    healthDelayed = ColorPair(OrbitPalette.Red70, OrbitPalette.Red30),

    stockHealthy = ColorPair(OrbitPalette.Green70, OrbitPalette.Green30),
    stockLow = ColorPair(OrbitPalette.Amber70, OrbitPalette.Amber30),
    stockOut = ColorPair(OrbitPalette.Red70, OrbitPalette.Red30),

    aiAccent = ColorPair(OrbitPalette.Violet70, OrbitPalette.Violet30),

    chartSeries = listOf(
        OrbitPalette.Blue60,
        OrbitPalette.Amber80,
        OrbitPalette.Teal70,
        OrbitPalette.Violet70,
        OrbitPalette.Green70,
        OrbitPalette.Orange70,
        OrbitPalette.Slate80,
        OrbitPalette.Red70,
    ),
)

internal val LocalOrbitSemanticColors = staticCompositionLocalOf { OrbitLightSemanticColors }
