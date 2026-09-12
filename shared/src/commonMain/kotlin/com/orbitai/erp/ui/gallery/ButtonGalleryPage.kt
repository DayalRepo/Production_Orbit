package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.status.OrbitChip
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One of each button type, then sizes, icon buttons and chips.
 */
@Composable
internal fun ButtonGalleryPage() {
    val spacing = OrbitTheme.spacing

    GallerySection("Buttons") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            GalleryControlFlow {
                OrbitButton(
                    label = "Approve",
                    onClick = {},
                    variant = OrbitButtonVariant.Primary,
                    icon = OrbitIcons.CheckmarkBadge,
                )
                OrbitButton(
                    label = "Secondary",
                    onClick = {},
                    variant = OrbitButtonVariant.Secondary,
                    icon = OrbitIcons.Add,
                )
                OrbitButton(
                    label = "Outline",
                    onClick = {},
                    variant = OrbitButtonVariant.Outline,
                    icon = OrbitIcons.Search,
                )
                OrbitButton(
                    label = "View all",
                    onClick = {},
                    variant = OrbitButtonVariant.Text,
                    icon = OrbitIcons.ArrowRight,
                )
                OrbitButton(
                    label = "Reject",
                    onClick = {},
                    variant = OrbitButtonVariant.Destructive,
                    icon = OrbitIcons.Cancel,
                )
            }
        }
    }

    GallerySection("Button sizes") {
        GalleryControlFlow {
            OrbitButtonSize.entries.reversed().forEach { size ->
                OrbitButton(
                    label = size.name,
                    onClick = {},
                    variant = OrbitButtonVariant.Primary,
                    size = size,
                    icon = OrbitIcons.CheckmarkBadge,
                )
            }
        }
    }

    GallerySection("Icon buttons") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrbitIconButtonSize.entries.forEach { size ->
                    OrbitIconButton(
                        contentDescription = "Upload, ${size.name} size",
                        onClick = {},
                        icon = OrbitIcons.Upload,
                        size = size,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf(
                    OrbitIconButtonStyle.Accent to (OrbitIcons.Mail to "Send"),
                    OrbitIconButtonStyle.Positive to (OrbitIcons.Tick to "Approve"),
                    OrbitIconButtonStyle.Destructive to (OrbitIcons.Delete to "Delete"),
                    OrbitIconButtonStyle.Neutral to (OrbitIcons.Cancel to "Close"),
                ).forEach { (style, glyph) ->
                    OrbitIconButton(
                        contentDescription = glyph.second,
                        onClick = {},
                        icon = glyph.first,
                        style = style,
                    )
                }
            }
        }
    }

    GallerySection("Filter chips") {
        var selected by remember { mutableStateOf("All") }
        val chips = listOf(
            "All" to 24,
            "Open" to 8,
            "Blocked" to 2,
            "Done" to 14,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            chips.forEach { (label, count) ->
                OrbitChip(
                    label = label,
                    selected = selected == label,
                    onClick = { selected = label },
                    count = count,
                )
            }
        }
    }
}
