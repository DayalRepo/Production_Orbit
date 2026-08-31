package com.orbitai.erp.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.dialog.OrbitRenameDialog
import com.orbitai.erp.core.designsystem.component.feedback.OrbitSkeletonList
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * What a screen shows while it is still loading.
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

    GallerySection("Skeleton · a list still loading") {
        OrbitCard(padding = spacing.md) {
            OrbitSkeletonList(rows = 3)
        }
    }

    // Three dialogs side by side, because the differences between them are the whole design and
    // none of them are visible one at a time: the destructive one is red and refuses a scrim tap,
    // the mild one is neither, and the rename one opens straight into a field with Save inert until
    // the name has actually changed.
    GallerySection("Dialogs · glass, over a dimmed screen") {
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

                else -> Unit
            }
        }
    }
}
