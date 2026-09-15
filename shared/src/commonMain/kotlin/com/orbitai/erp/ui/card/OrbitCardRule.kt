package com.orbitai.erp.ui.card

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Hairline rule used between sections inside glass cards (work items, units, …).
 *
 * Kept as a shared preset so every card uses the same elevated divider colour and vertical rhythm
 * instead of each screen inventing its own padding around [OrbitDivider].
 */
@Composable
fun OrbitCardRule(modifier: Modifier = Modifier) {
    OrbitDivider(
        modifier = modifier.padding(vertical = OrbitTheme.spacing.xxs),
        color = OrbitTheme.controlColors.dividerElevated,
    )
}

/** Empty meta-row value — shared dash for unassigned / missing fields. */
@Composable
fun OrbitCardMetaDash() {
    Text(
        text = "—",
        style = OrbitTheme.typography.bodyMedium.copy(
            fontWeight = OrbitTheme.fontWeights.title,
        ),
        color = OrbitTheme.contentColors.textTertiary,
    )
}
