package com.orbitai.erp.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Weight assignments, gathered so boldness is one edit rather than forty.
 *
 * Starting point, open to revision: SemiBold for display and headings, Medium for titles and labels,
 * Regular for body, Bold for KPI figures. Google Sans Flex is a fairly low-contrast geometric face,
 * so SemiBold reads as a confident heading where Bold tends to look shouty at 32sp; on iOS, where
 * the system face is heavier, Bold headings may suit better. All nine weights are bundled, so
 * changing any of these is a one-line change with no asset work.
 */
@Immutable
data class OrbitFontWeights(
    val display: FontWeight = FontWeight.SemiBold,
    val heading: FontWeight = FontWeight.SemiBold,
    val title: FontWeight = FontWeight.Medium,
    val body: FontWeight = FontWeight.Normal,
    val label: FontWeight = FontWeight.Medium,
    val metric: FontWeight = FontWeight.Bold,
)

/**
 * The Material scale, built from a platform [OrbitTypeScale].
 *
 * Letter spacing is 0 throughout. Google Sans Flex is spaced for UI at these sizes, and Material's
 * default tracking was tuned for Roboto; carrying it over loosens everything slightly. The single
 * exception is [OrbitTypographyTokens.sectionLabel] — see its note.
 *
 * Takes the family as a parameter because resource-backed fonts can only be loaded from a
 * composition. No call site names a font family or a size, so both are changeable in one place.
 */
internal fun orbitTypography(
    sans: FontFamily,
    scale: OrbitTypeScale,
    weights: OrbitFontWeights = OrbitFontWeights(),
): Typography {
    fun style(metrics: OrbitFontMetrics, weight: FontWeight) = TextStyle(
        fontFamily = sans,
        fontWeight = weight,
        fontSize = metrics.size,
        lineHeight = metrics.lineHeight,
        letterSpacing = 0.sp,
    )

    return Typography(
        displayLarge = style(scale.displayLarge, weights.display),
        displayMedium = style(scale.displayMedium, weights.display),
        displaySmall = style(scale.displaySmall, weights.display),

        headlineLarge = style(scale.h1, weights.heading),
        headlineMedium = style(scale.h2, weights.heading),
        headlineSmall = style(scale.h3, weights.heading),

        titleLarge = style(scale.h4, weights.title),
        titleMedium = style(scale.body, weights.title),
        titleSmall = style(scale.small, weights.title),

        bodyLarge = style(scale.body, weights.body),
        bodyMedium = style(scale.small, weights.body),
        bodySmall = style(scale.caption, weights.body),

        labelLarge = style(scale.small, weights.label),
        labelMedium = style(scale.caption, weights.label),
        // Was 11sp. Raised to the caption tier because 12 is the floor on both platforms.
        labelSmall = style(scale.caption, weights.label),
    )
}

/**
 * Styles the Material scale does not cover. Dashboards lean on large KPI numbers and dense
 * tables, both of which need tabular alignment that body text should not have.
 */
@Immutable
data class OrbitTypographyTokens(
    /** Hero KPI figure on a dashboard card. */
    val metricLarge: TextStyle,
    val metricMedium: TextStyle,
    val metricSmall: TextStyle,
    /** Caption under a KPI figure, e.g. "vs. last month". */
    val metricCaption: TextStyle,
    /** Uppercase section divider label, e.g. "MATERIALS". */
    val sectionLabel: TextStyle,
    val tableHeader: TextStyle,
    /** Right-aligned tabular cell for quantities, currency and totals. */
    val tableNumeric: TextStyle,
    /** Reference codes: invoice numbers, PO numbers, audit log IDs. */
    val reference: TextStyle,
    /**
     * Multi-paragraph reading copy — site reports, audit notes, issue descriptions. Line height is
     * 1.5x the font size per WCAG 1.4.12, looser than the scale's UI-label line heights.
     */
    val bodyLongForm: TextStyle,
)

internal fun orbitTypographyTokens(
    sans: FontFamily,
    scale: OrbitTypeScale,
    weights: OrbitFontWeights = OrbitFontWeights(),
): OrbitTypographyTokens = OrbitTypographyTokens(
    metricLarge = TextStyle(
        fontFamily = sans,
        fontWeight = weights.metric,
        fontSize = scale.h1.size,
        lineHeight = scale.h1.lineHeight,
        letterSpacing = 0.sp,
        fontFeatureSettings = TabularNumbers,
    ),
    metricMedium = TextStyle(
        fontFamily = sans,
        fontWeight = weights.metric,
        fontSize = scale.h3.size,
        lineHeight = scale.h3.lineHeight,
        letterSpacing = 0.sp,
        fontFeatureSettings = TabularNumbers,
    ),
    metricSmall = TextStyle(
        fontFamily = sans,
        fontWeight = weights.metric,
        fontSize = scale.h4.size,
        lineHeight = scale.h4.lineHeight,
        letterSpacing = 0.sp,
        fontFeatureSettings = TabularNumbers,
    ),
    metricCaption = TextStyle(
        fontFamily = sans,
        fontWeight = weights.label,
        fontSize = scale.caption.size,
        lineHeight = scale.caption.lineHeight,
        letterSpacing = 0.sp,
    ),
    // The one place tracking is not 0: all-caps text at 12sp sets too tightly without it.
    sectionLabel = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = scale.caption.size,
        lineHeight = scale.caption.lineHeight,
        letterSpacing = 1.sp,
    ),
    tableHeader = TextStyle(
        fontFamily = sans,
        fontWeight = weights.label,
        fontSize = scale.caption.size,
        lineHeight = scale.caption.lineHeight,
        letterSpacing = 0.sp,
    ),
    tableNumeric = TextStyle(
        fontFamily = sans,
        fontWeight = weights.body,
        fontSize = scale.small.size,
        lineHeight = scale.small.lineHeight,
        letterSpacing = 0.sp,
        fontFeatureSettings = TabularNumbers,
        textAlign = TextAlign.End,
    ),
    reference = TextStyle(
        fontFamily = sans,
        fontWeight = weights.label,
        fontSize = scale.caption.size,
        lineHeight = scale.caption.lineHeight,
        letterSpacing = 0.sp,
        fontFeatureSettings = TabularNumbers,
    ),
    bodyLongForm = TextStyle(
        fontFamily = sans,
        fontWeight = weights.body,
        fontSize = scale.body.size,
        lineHeight = (scale.body.size.value * scale.longFormLineHeightRatio).sp,
        letterSpacing = 0.sp,
    ),
)

internal val LocalOrbitTypography = staticCompositionLocalOf {
    orbitTypographyTokens(FontFamily.Default, AndroidTypeScale)
}
