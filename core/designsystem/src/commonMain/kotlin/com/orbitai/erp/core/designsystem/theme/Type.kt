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
    /**
     * Bold, where every other heading tier is SemiBold.
     *
     * The display tier is used once per screen at most, on a figure or a title that is meant to be
     * the first thing read. It is the one place the extra step is not shouting, because there is
     * nothing beside it at the same size to shout over.
     */
    val display: FontWeight = FontWeight.Bold,
    val heading: FontWeight = FontWeight.SemiBold,
    val title: FontWeight = FontWeight.Medium,
    val body: FontWeight = FontWeight.Normal,
    val label: FontWeight = FontWeight.Medium,
    /**
     * KPI figures, at 400 rather than the bold you might expect.
     *
     * A dashboard number is already the largest thing on its card, so size alone gives it all the
     * emphasis it needs; adding weight on top makes it shout, and a screen of six shouting cards has
     * no hierarchy left. Regular is also where Google Sans Flex's digits are best drawn — Bold
     * closes the counters on 8 and 9 so a long number reads as a block, while Light thins the strokes
     * enough that a figure set against a glass card starts to look tentative at small sizes.
     */
    val metric: FontWeight = FontWeight.Normal,
)

/**
 * The Material scale, built from a platform [OrbitTypeScale].
 *
 * Letter spacing comes from the scale, per tier, rather than from Material's defaults — those were
 * tuned for Roboto and loosen Google Sans Flex slightly at every size. See
 * [OrbitFontMetrics.tracking] for why it varies by tier instead of being one number.
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
        letterSpacing = metrics.tracking,
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
    /**
     * Uppercase label *inside* a card, e.g. "PROGRESS" above a KPI figure.
     *
     * Smaller and more tightly tracked than [sectionLabel], because it is doing a different job. A
     * section label separates one region of a screen from another and has to hold its own against
     * whitespace; a card label sits a few points above the figure it names, inside a border that has
     * already done the separating, so the same size and tracking make it compete with the number
     * rather than introduce it.
     *
     * This is the only style in the system below the caption size, and it is only safe because it is
     * always set in caps: cap height at this size still exceeds the x-height of caption-size
     * sentence case, so it is not the regression in legibility the number suggests. Do not reach for
     * it for anything with lowercase in it.
     */
    val cardLabel: TextStyle,

    /**
     * The value inside a Large text field.
     *
     * Exists because the body scale runs out: `bodyLarge` is what a Medium field uses, and a Large
     * field set in the same size is not a larger field, it is a taller one. This is one scale step
     * above, so the three field sizes are three type sizes.
     */
    val fieldLarge: TextStyle,
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
    cardLabel = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        // Derived from the caption rather than hardcoded, so it keeps following the platform's base
        // size and the user's font-scale setting instead of pinning itself to one device's idea of
        // small.
        fontSize = scale.caption.size * CardLabelRatio,
        lineHeight = scale.caption.lineHeight * CardLabelRatio,
        letterSpacing = 0.4.sp,
    ),
    fieldLarge = TextStyle(
        fontFamily = sans,
        fontWeight = weights.body,
        // A step above bodyLarge, on the same ratio the rest of the scale uses. The three field
        // sizes need three visibly different type sizes or Large and Medium read as one control at
        // two heights, and the body scale runs out at Large.
        fontSize = scale.body.size * FieldLargeRatio,
        lineHeight = scale.body.lineHeight * FieldLargeRatio,
        letterSpacing = 0.sp,
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

/**
 * How far below the caption size a card label sits.
 *
 * The smallest step that reads as a different tier rather than as the caption rendered slightly
 * wrong. A ratio rather than a size, so it follows the caption tier wherever the platform puts it:
 * against Android's 14sp caption it lands near 13sp and against iOS's 15sp near 14sp, both
 * comfortably clear of the 12sp floor.
 */
private const val CardLabelRatio = 0.92f

/**
 * How far above `bodyLarge` a Large field's value sits.
 *
 * The scale's own ratio, 1.125, which is what keeps this a step *on* the scale rather than a size
 * invented for one component. It also scales with the user's setting like everything else, so a
 * Large field stays a step above a Medium one at 200% instead of the gap collapsing.
 */
private const val FieldLargeRatio = 1.125f

internal val LocalOrbitTypography = staticCompositionLocalOf {
    orbitTypographyTokens(FontFamily.Default, AndroidTypeScale)
}
