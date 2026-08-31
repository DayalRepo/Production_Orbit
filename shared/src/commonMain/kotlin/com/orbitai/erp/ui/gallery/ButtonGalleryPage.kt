package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
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
        // Every family is shown at all three sizes rather than at one representative size. The
        // sizes are not a scale factor — type, glyph, padding and tracking each move on their own
        // curve — so the only way to see whether Small still reads as the same component as Large
        // is to put them under each other.
        GallerySection("Decision pairs · Large, Medium, Small") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                OrbitButtonSize.entries.reversed().forEach { size ->
                    ActionButtonRow(
                        dismiss = ActionKind.Reject,
                        confirm = ActionKind.Approve,
                        onDismiss = {},
                        onConfirm = {},
                        size = size,
                    )
                }
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
                    size = OrbitButtonSize.Small,
                )
            }
        }

        GallerySection("Single actions · full width, then intrinsic") {
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
                    size = OrbitButtonSize.Small,
                )
                // Unconstrained, so each sizes to its own label. This is the check that the minimum
                // widths are doing their job: a short label must not collapse the pill to a circle.
                GalleryControlFlow {
                    OrbitButtonSize.entries.reversed().forEach { size ->
                        ActionButton(action = ActionKind.Open, onClick = {}, size = size)
                    }
                }
            }
        }

        GallerySection("In flight · ${BusyKind.entries.size} states, three sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                // Left permanently spinning so the animation can actually be watched, which is the
                // only reason a busy sample belongs in a static gallery.
                BusyKind.entries.forEach { kind ->
                    BusyButton(kind = kind, modifier = Modifier.fillMaxWidth())
                }
                // The spinner has to keep pace with the type, or a Small busy button looks like a
                // Medium one that has been squashed.
                GalleryControlFlow {
                    OrbitButtonSize.entries.reversed().forEach { size ->
                        BusyButton(kind = BusyKind.Sending, size = size)
                    }
                }
            }
        }

        GallerySection("Button state and size") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                GalleryControlFlow {
                    OrbitButtonSize.entries.forEach { size ->
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
