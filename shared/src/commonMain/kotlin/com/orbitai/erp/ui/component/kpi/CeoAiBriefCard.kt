package com.orbitai.erp.ui.component.kpi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitAiBriefCard

/**
 * CEO AI brief — Orbit mark inline with markdown body + show more / show less.
 */
@Composable
fun CeoAiBriefCard(
    markdown: String,
    modifier: Modifier = Modifier,
    updatedLabel: String = "Updated 21m ago",
) {
    OrbitAiBriefCard(
        markdown = markdown,
        modifier = modifier,
        collapsedMaxLines = 3,
        updatedLabel = updatedLabel,
    )
}
