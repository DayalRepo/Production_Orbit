package com.orbitai.erp.ui.component.attachment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.dialog.OrbitRenameDialog

/** Which dialog, if any, the row currently has open. */
private sealed interface AttachmentPrompt {
    data object Rename : AttachmentPrompt
    data object Delete : AttachmentPrompt
    data object Remove : AttachmentPrompt
}

/**
 * A [FileAttachmentRow] whose three trailing controls open the dialogs they imply.
 *
 * ### Why this is a separate component in `:shared`
 *
 * `FileAttachmentRow` — and `OrbitAttachmentRow` under it — take bare callbacks and hold no state,
 * which is what makes them renderable in a gallery, a test and a preview. But "bare callbacks" means
 * every screen that uses the row has to write the same three pieces of dialog state and the same
 * three dialogs, and they will not write them the same way; within a release you have one screen
 * confirming deletion, another deleting immediately, and a third confirming with the wrong verb.
 *
 * So the flow is assembled exactly once, here, and screens get a row that already behaves correctly.
 * The stateless pieces stay stateless underneath and remain available for the rare caller that
 * genuinely needs its own flow — a bulk-select mode, say, where per-row confirmation would be
 * intolerable.
 *
 * ### Each control confirms differently, and the differences are the point
 *
 * The three are graded by how much damage they do and how hard it is to undo:
 *
 * - **Rename** opens straight into an editable field. It is not confirmed first, because a
 *   confirmation before an edit asks you to agree to something you have not written yet; the dialog's
 *   own Cancel is the safety, and the change is trivially reversible by renaming back.
 * - **Delete** confirms as destructive: the panel will not close on a stray tap outside, and Yes is
 *   red. The file is gone afterwards.
 * - **Remove** confirms too, but mildly — ordinary dismissal, no red. Detaching from a draft is
 *   undone by attaching again.
 *
 * The middle case is the one worth defending. A confirmation on *remove* looks like friction on a
 * cheap, reversible action, and normally that is a real objection. It survives here because the
 * cross sits a few millimetres from a red bin on the same row: the cost of a mis-tap is not the
 * removal, it is that the user learns the two small glyphs at that end of the row are dangerous and
 * stops using either. One extra tap keeps the cheap control feeling cheap.
 *
 * The messages name the file rather than saying "this item". A confirmation the user cannot read the
 * subject of is one they answer from memory of what they just tapped, which is exactly the memory
 * that is wrong when the tap was a mistake.
 *
 * @param onRenamed the new name, already trimmed and guaranteed non-empty and different.
 */
@Composable
fun ManagedAttachmentRow(
    fileName: String,
    fileSize: String,
    modifier: Modifier = Modifier,
    preview: Painter? = null,
    onRenamed: ((String) -> Unit)? = null,
    onDeleted: (() -> Unit)? = null,
    onRemoved: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    var prompt by remember { mutableStateOf<AttachmentPrompt?>(null) }
    val dismiss = { prompt = null }

    FileAttachmentRow(
        fileName = fileName,
        fileSize = fileSize,
        modifier = modifier,
        preview = preview,
        onRename = onRenamed?.let { { prompt = AttachmentPrompt.Rename } },
        onDelete = onDeleted?.let { { prompt = AttachmentPrompt.Delete } },
        onRemove = onRemoved?.let { { prompt = AttachmentPrompt.Remove } },
        onClick = onClick,
    )

    when (prompt) {
        AttachmentPrompt.Rename -> OrbitRenameDialog(
            initialValue = fileName,
            title = "Rename file",
            label = "File name",
            onConfirm = { newName ->
                onRenamed?.invoke(newName)
                dismiss()
            },
            onDismiss = dismiss,
        )

        AttachmentPrompt.Delete -> OrbitConfirmDialog(
            title = "Delete file",
            // Says what is lost and that it cannot be undone, in one line. The sentence is the
            // whole safety mechanism: the buttons only say Yes and No.
            message = "Delete \"$fileName\"? This cannot be undone.",
            destructive = true,
            onConfirm = {
                onDeleted?.invoke()
                dismiss()
            },
            onDismiss = dismiss,
        )

        AttachmentPrompt.Remove -> OrbitConfirmDialog(
            title = "Remove file",
            // Deliberately reassuring, where the delete copy is deliberately not. "You can attach it
            // again" is the difference between the two actions, and it is the only thing the user
            // needs in order to answer without thinking about it.
            message = "Remove \"$fileName\" from this message? You can attach it again.",
            onConfirm = {
                onRemoved?.invoke()
                dismiss()
            },
            onDismiss = dismiss,
        )

        null -> Unit
    }
}
