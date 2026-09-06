package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.feedback.OrbitLoadingIcon
import com.orbitai.erp.core.designsystem.component.input.OrbitSwitch
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Standalone feedback chrome: spinner and switch, outside BusyButton / account menu.
 */
@Composable
internal fun FeedbackGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val sizing = OrbitTheme.sizing

    GallerySection("Loading icon · button and page sizes") {
        OrbitCard(padding = spacing.md) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                Text(
                    text = "Hugeicons loading-03, linear spin. Small sits in busy buttons; larger for page waits.",
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OrbitLoadingIcon(
                        size = sizing.iconSm,
                        tint = content.iconPrimary,
                    )
                    OrbitLoadingIcon(
                        size = sizing.iconMd,
                        tint = content.iconPrimary,
                    )
                    OrbitLoadingIcon(
                        size = sizing.iconXl,
                        tint = content.iconPrimary,
                    )
                }
            }
        }
    }

    GallerySection("Switch · on and off") {
        OrbitCard(padding = spacing.md) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                var darkPreview by remember { mutableStateOf(true) }
                var alerts by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (darkPreview) "Dark theme" else "Light theme",
                        style = OrbitTheme.typography.bodyMedium,
                        color = content.textPrimary,
                    )
                    OrbitSwitch(
                        checked = darkPreview,
                        onCheckedChange = { darkPreview = it },
                        contentDescription = "Theme preview",
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Site alerts",
                        style = OrbitTheme.typography.bodyMedium,
                        color = content.textPrimary,
                    )
                    OrbitSwitch(
                        checked = alerts,
                        onCheckedChange = { alerts = it },
                        contentDescription = "Site alerts",
                    )
                }
            }
        }
    }
}
