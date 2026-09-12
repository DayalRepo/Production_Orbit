package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.dialog.OrbitCreateDialog
import com.orbitai.erp.core.designsystem.component.dialog.OrbitDialog
import com.orbitai.erp.core.designsystem.component.dialog.OrbitRenameDialog
import com.orbitai.erp.core.designsystem.component.feedback.OrbitSkeletonList
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Loading chrome and the dialog family built on [OrbitDialog].
 *
 * Worth watching rather than screenshotting. The skeleton's pulse is the only continuous animation
 * in the library, and the thing to check on a device is that it is slow enough to read as waiting
 * rather than as an alarm — that judgement does not survive a still image.
 *
 * The empty state that used to sit beside it is gone for now; it comes back once the screens define
 * what "empty" means per role, since a Site Engineer with no assigned tasks and a CEO with no
 * projects are not the same condition and should not share one sentence.
 */
@Composable
internal fun StateGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    GallerySection("Skeleton") {
        OrbitCard(padding = spacing.md) {
            OrbitSkeletonList(rows = 3)
        }
    }

    // Confirm / rename / create / bare shell side by side: the differences are the whole design.
    GallerySection("Dialogs") {
        GalleryFlow {
            var open by remember { mutableStateOf<String?>(null) }

            OrbitButton(
                label = "Delete file",
                onClick = { open = "delete" },
                variant = OrbitButtonVariant.Destructive,
            )
            OrbitButton(
                label = "Remove file",
                onClick = { open = "remove" },
                variant = OrbitButtonVariant.Secondary,
            )
            OrbitButton(
                label = "Rename file",
                onClick = { open = "rename" },
                variant = OrbitButtonVariant.Secondary,
            )
            OrbitButton(
                label = "Add stage",
                onClick = { open = "create" },
                variant = OrbitButtonVariant.Secondary,
            )
            OrbitButton(
                label = "Custom shell",
                onClick = { open = "shell" },
                variant = OrbitButtonVariant.Secondary,
            )

            when (open) {
                "delete" -> OrbitConfirmDialog(
                    title = "Delete file",
                    message = "Delete \"boq-revision-3.xlsx\"? This cannot be undone.",
                    destructive = true,
                    onConfirm = { open = null },
                    onDismiss = { open = null },
                )

                "remove" -> OrbitConfirmDialog(
                    title = "Remove file",
                    message = "Remove \"boq-revision-3.xlsx\" from this message? " +
                        "You can attach it again.",
                    onConfirm = { open = null },
                    onDismiss = { open = null },
                )

                "rename" -> OrbitRenameDialog(
                    initialValue = "boq-revision-3.xlsx",
                    title = "Rename file",
                    label = "File name",
                    onConfirm = { open = null },
                    onDismiss = { open = null },
                )

                "create" -> OrbitCreateDialog(
                    title = "Add stage",
                    info = "Stages are shared across the project. Name it as it appears on the work sequence.",
                    label = "Stage name",
                    onCreate = { open = null },
                    onDismiss = { open = null },
                )

                "shell" -> OrbitDialog(
                    onDismiss = { open = null },
                    title = "Dialog shell",
                    content = {
                        Text(
                            text = "Bare OrbitDialog: glass pane, title, close, content slot, actions. " +
                                "Confirm, rename and create are thin wrappers on this.",
                            style = OrbitTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = content.textSecondary,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    actions = {
                        OrbitButton(
                            label = "Cancel",
                            onClick = { open = null },
                            variant = OrbitButtonVariant.Secondary,
                        )
                        OrbitButton(
                            label = "Done",
                            onClick = { open = null },
                            variant = OrbitButtonVariant.Primary,
                        )
                    },
                )

                else -> Unit
            }
        }
    }
}
