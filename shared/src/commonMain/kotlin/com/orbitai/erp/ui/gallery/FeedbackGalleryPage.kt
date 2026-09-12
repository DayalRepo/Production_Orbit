package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.feedback.OrbitLoadingIcon
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Standalone feedback chrome: the spinner, outside BusyButton.
 */
@Composable
internal fun FeedbackGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val sizing = OrbitTheme.sizing

    GallerySection("Loading icon") {
        OrbitCard(padding = spacing.md) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
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
}
