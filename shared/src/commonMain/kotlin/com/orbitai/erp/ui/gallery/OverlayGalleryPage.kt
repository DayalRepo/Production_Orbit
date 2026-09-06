package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.overlay.OrbitBubblePopover
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoField
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoPopover
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Popovers and the copy control they share — reviewed on their own, not only through avatars.
 */
@Composable
internal fun OverlayGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    GallerySection("Info popover · name, role badge, copyable mobile") {
        Box {
            var open by remember { mutableStateOf(false) }
            OrbitButton(
                label = "Open info",
                onClick = { open = true },
                variant = OrbitButtonVariant.Secondary,
            )
            OrbitInfoPopover(
                expanded = open,
                onDismiss = { open = false },
                roleBadge = GalleryAccountSamples.SiteRole,
                fields = listOf(
                    OrbitInfoField("Name", GalleryAccountSamples.SiteName),
                    OrbitInfoField(
                        "Mobile number",
                        GalleryAccountSamples.SitePhone,
                        copyable = true,
                    ),
                ),
            )
        }
    }

    GallerySection("Bubble popover · shell with custom body") {
        Box {
            var open by remember { mutableStateOf(false) }
            OrbitButton(
                label = "Open bubble",
                onClick = { open = true },
                variant = OrbitButtonVariant.Secondary,
            )
            OrbitBubblePopover(
                expanded = open,
                onDismiss = { open = false },
                title = "Note",
            ) {
                Text(
                    text = "Pointer, glass, close and dismiss belong here. " +
                        "Info and account panels only fill the body.",
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md),
                )
            }
        }
    }

    GallerySection("Copy button · idle then confirm tick") {
        OrbitCard(padding = spacing.md) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = "Tap to copy. The glyph swaps to a tick, then returns.",
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                )
                GalleryControlFlow {
                    OrbitCopyButton(
                        value = GalleryAccountSamples.CeoPhone,
                        label = "Mobile number",
                        size = OrbitIconButtonSize.Small,
                    )
                    OrbitCopyButton(
                        value = "RFI-042",
                        label = "Reference",
                        size = OrbitIconButtonSize.Medium,
                    )
                }
            }
        }
    }
}
