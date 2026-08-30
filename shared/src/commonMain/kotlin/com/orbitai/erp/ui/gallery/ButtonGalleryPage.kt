package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionButtonRow
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.component.button.BusyButton
import com.orbitai.erp.ui.component.button.BusyKind

/**
 * Buttons, chips and icon buttons.
 *
 * The decision pairs are shown through `ActionButtonRow` rather than as two loose buttons, because
 * the shared height and the emphasis split are part of the component, and building them by hand here
 * would fail to demonstrate the thing worth demonstrating.
 */
@Composable
internal fun ButtonGalleryPage() {
    val spacing = OrbitTheme.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xxl)) {
        GallerySection("Decision pairs") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                ActionButtonRow(
                    dismiss = ActionKind.Reject,
                    confirm = ActionKind.Approve,
                    onDismiss = {},
                    onConfirm = {},
                )
                ActionButtonRow(
                    dismiss = ActionKind.Cancel,
                    confirm = ActionKind.Send,
                    onDismiss = {},
                    onConfirm = {},
                )
                ActionButtonRow(
                    dismiss = ActionKind.Cancel,
                    confirm = ActionKind.Create,
                    onDismiss = {},
                    onConfirm = {},
                )
            }
        }

        GallerySection("Single actions") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                ActionButton(
                    action = ActionKind.Login,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                ActionButton(
                    action = ActionKind.Open,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        GallerySection("In flight · ${BusyKind.entries.size} states") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                // Left permanently spinning so the animation can actually be watched, which is the
                // only reason a busy sample belongs in a static gallery.
                BusyKind.entries.forEach { kind ->
                    BusyButton(kind = kind, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        GallerySection("Button state and size") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                GalleryControlFlow {
                    com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize.entries
                        .forEach { size ->
                            ActionButton(
                                action = ActionKind.Approve,
                                onClick = {},
                                label = size.name,
                                size = size,
                            )
                        }
                }
                GalleryControlFlow {
                    OrbitButtonState.entries.forEach { state ->
                        ActionButton(
                            action = ActionKind.Approve,
                            onClick = {},
                            label = state.name,
                            state = state,
                        )
                    }
                }
            }
        }

        GallerySection("Icon buttons · clear glass ring") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                // The three sizes. The ring shrinks with the glyph but keeps its clear space, and the
                // glyph's stroke is lifted back to the platform floor at each one, so the three read
                // as one component at three scales rather than as three different weights.
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
                // The four meanings, each with the glyph that goes with it. Pairing them this way is
                // the point: the colour is not a decoration picked per screen, it is the same tone the
                // matching badge and the matching labelled button already use.
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
                // States, shown on the destructive tone because that is where a mistake costs most,
                // and a selected toggle beside them.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OrbitButtonState.entries.forEach { state ->
                        OrbitIconButton(
                            contentDescription = "Delete item, ${state.name}",
                            onClick = {},
                            icon = OrbitIcons.Delete,
                            style = OrbitIconButtonStyle.Destructive,
                            state = state,
                        )
                    }
                    OrbitIconButton(
                        contentDescription = "Bookmark, selected",
                        onClick = {},
                        icon = OrbitIcons.Bookmark,
                        style = OrbitIconButtonStyle.Neutral,
                        selected = true,
                    )
                }
            }
        }
    }
}

/**
 * A filter row of label-and-count chips.
 *
 * Selection is local state because this is a gallery; on a real screen it would be driven by the
 * list's filter. The point being shown is that the count is what makes a filter worth tapping —
 * "Blocked 2" tells you whether to bother before you do.
 */
