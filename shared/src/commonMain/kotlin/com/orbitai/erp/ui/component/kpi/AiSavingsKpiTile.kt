package com.orbitai.erp.ui.component.kpi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitKpiTile
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressColors
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * AI savings MTD — hero ₹ from material optimization (and related AI saves).
 */
@Composable
fun AiSavingsKpiTile(
    savingsAmountLabel: String,
    modifier: Modifier = Modifier,
    sourceLabel: String = "Material optimization",
    monthProgress: Float = 0.62f,
    deltaPercent: Float? = 18f,
    onClick: (() -> Unit)? = null,
) {
    val green = OrbitTheme.semanticColors.healthOnTrack.content
    val track = OrbitTheme.controlColors.controlContainer

    OrbitKpiTile(
        title = "AI savings",
        value = savingsAmountLabel,
        supporting = "$sourceLabel · MTD",
        icon = OrbitIcons.AiMagic,
        iconTone = OrbitBadgeTone.Teal,
        delta = deltaPercent,
        deltaHigherIsBetter = true,
        deltaDescription = "up $deltaPercent percent vs last month",
        comparisonLabel = "vs last month",
        modifier = modifier,
        onClick = onClick,
        footer = {
            OrbitSegmentedProgress(
                progress = monthProgress.coerceIn(0f, 1f),
                contentDescription = "Month-to-date savings pace",
                colors = OrbitProgressColors(filled = green, track = track),
                segmentCount = OrbitProgressDefaults.ChecklistBarCount,
            )
        },
    )
}
