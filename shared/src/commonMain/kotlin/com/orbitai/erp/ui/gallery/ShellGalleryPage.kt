package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadge
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitListRow
import com.orbitai.erp.core.designsystem.component.feedback.OrbitEmptyState
import com.orbitai.erp.core.designsystem.component.feedback.OrbitSnackbarHost
import com.orbitai.erp.core.designsystem.component.feedback.OrbitSnackbarTone
import com.orbitai.erp.core.designsystem.component.feedback.rememberOrbitSnackbarHostState
import com.orbitai.erp.core.designsystem.component.navigation.OrbitTopAppBar
import com.orbitai.erp.core.designsystem.component.overlay.OrbitBottomSheet
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Screen shell pieces needed before role screens: app bar, list rows, empty, snackbar, sheet.
 * KPI tiles and role cards are intentionally out of scope here.
 */
@Composable
internal fun ShellGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val snackbarHost = rememberOrbitSnackbarHostState()
    var sheetOpen by remember { mutableStateOf(false) }

    GallerySection("Top app bar · back, title, actions") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            OrbitTopAppBar(
                title = "Site updates",
                navigationIcon = {
                    OrbitIconButton(
                        contentDescription = "Back",
                        onClick = {},
                        icon = OrbitIcons.ArrowLeft,
                        style = OrbitIconButtonStyle.Neutral,
                    )
                },
                actions = {
                    OrbitIconButton(
                        contentDescription = "Search",
                        onClick = {},
                        icon = OrbitIcons.Search,
                        style = OrbitIconButtonStyle.Neutral,
                    )
                    OrbitIconButton(
                        contentDescription = "More",
                        onClick = {},
                        icon = OrbitIcons.MoreVertical,
                        style = OrbitIconButtonStyle.Neutral,
                    )
                },
            )
            OrbitTopAppBar(
                title = "Scrolled",
                scrolled = true,
                navigationIcon = {
                    OrbitIconButton(
                        contentDescription = "Back",
                        onClick = {},
                        icon = OrbitIcons.ArrowLeft,
                        style = OrbitIconButtonStyle.Neutral,
                    )
                },
            )
        }
    }

    GallerySection("List row · entity lines") {
        OrbitCard(padding = spacing.none) {
            OrbitListRow(
                title = "RFI-042 · East elevation detail",
                subtitle = "Tower B · Pending QA · Updated today",
                trailing = {
                    OrbitBadge(label = "Open", tone = OrbitBadgeTone.Amber)
                },
                onClick = {},
            )
            OrbitListRow(
                title = "PO-1188 · Cement OPC 53",
                subtitle = "Procurement · 240 bags",
                trailing = {
                    OrbitBadge(label = "Approved", tone = OrbitBadgeTone.Green)
                },
                onClick = {},
            )
            OrbitListRow(
                title = "Disabled line",
                subtitle = "No tap target",
                enabled = false,
                showChevron = false,
            )
        }
    }

    GallerySection("Empty state · zero data + CTA") {
        OrbitCard(padding = spacing.none) {
            OrbitEmptyState(
                title = "No site updates yet",
                message = "Updates from the site engineer will land here once work begins.",
                icon = OrbitIcons.NotepadDashed,
                action = {
                    OrbitButton(
                        label = "Refresh",
                        onClick = {},
                        variant = OrbitButtonVariant.Secondary,
                    )
                },
            )
        }
    }

    GallerySection("Snackbar · host + tones") {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = "Ephemeral feedback when the action is already gone. Prefer in-place ticks when possible.",
                    style = OrbitTheme.typography.bodySmall,
                    color = content.textSecondary,
                )
                GalleryControlFlow {
                    OrbitButton(
                        label = "Neutral",
                        onClick = {
                            snackbarHost.show("Saved draft", tone = OrbitSnackbarTone.Neutral)
                        },
                        variant = OrbitButtonVariant.Secondary,
                    )
                    OrbitButton(
                        label = "Positive",
                        onClick = {
                            snackbarHost.show(
                                "Approved",
                                actionLabel = "Undo",
                                tone = OrbitSnackbarTone.Positive,
                            )
                        },
                        variant = OrbitButtonVariant.Secondary,
                    )
                    OrbitButton(
                        label = "Destructive",
                        onClick = {
                            snackbarHost.show(
                                "Deleted attachment",
                                tone = OrbitSnackbarTone.Destructive,
                            )
                        },
                        variant = OrbitButtonVariant.Destructive,
                    )
                }
            }
            OrbitSnackbarHost(hostState = snackbarHost)
        }
    }

    GallerySection("Bottom sheet · filters / secondary actions") {
        OrbitButton(
            label = "Open sheet",
            onClick = { sheetOpen = true },
            variant = OrbitButtonVariant.Secondary,
        )
        if (sheetOpen) {
            OrbitBottomSheet(
                onDismiss = { sheetOpen = false },
                title = "Filter updates",
            ) {
                Text(
                    text = "Sheet shape is top-corners only. Use for filters and pickers, not for KPI cards.",
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                )
                OrbitButton(
                    label = "Apply",
                    onClick = { sheetOpen = false },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
