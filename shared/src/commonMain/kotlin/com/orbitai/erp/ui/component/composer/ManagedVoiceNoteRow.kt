package com.orbitai.erp.ui.component.composer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.media.OrbitVoiceNoteRow

/** Which of the row's two destructive controls is awaiting an answer. */
private sealed interface ClipPrompt {
    data object Delete : ClipPrompt
    data object Remove : ClipPrompt
}

/**
 * An [OrbitVoiceNoteRow] whose bin and cross confirm before they act.
 *
 * The audio counterpart to `ManagedAttachmentRow`, and it exists for the same reason: the design
 * system row takes bare callbacks so it stays renderable in a gallery and a test, which means the
 * confirmation flow has to be assembled exactly once somewhere above it or every screen invents its
 * own.
 *
 * ### Why a voice note needs this more than a file does
 *
 * A file you deleted by accident is usually still somewhere — in the email it arrived on, on the
 * drive it came from, in someone's phone. A voice note recorded on site is the only copy in
 * existence and it is gone the instant it is dropped. It is also the attachment most likely to be
 * dismissed by mistake, because the row's controls sit at the end of a strip the user has just been
 * tapping to scrub through playback, so a finger is already travelling across the row.
 *
 * That is also why the copy differs from the file dialogs' rather than being reused. "This cannot be
 * undone" is accurate for a file and understates the case here, where the recording cannot be
 * retrieved from anywhere else either; the delete message says so plainly.
 *
 * The split between the two controls is the same as for files, and it survives for the same reason:
 * **remove** takes the clip off the draft and is undone by attaching it again, so it confirms mildly
 * and says so; **delete** destroys the recording, so it confirms as destructive and will not close
 * on a stray tap outside.
 *
 * Playback state stays hoisted — [playing], [progress] and [onPlayPause] pass straight through —
 * because only one clip in a list may play at a time and a row owning its own flag cannot know about
 * its siblings.
 */
@Composable
fun ManagedVoiceNoteRow(
    amplitudes: List<Float>,
    progress: Float,
    duration: String,
    playing: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Voice note",
    onDeleted: (() -> Unit)? = null,
    onRemoved: (() -> Unit)? = null,
) {
    var prompt by remember { mutableStateOf<ClipPrompt?>(null) }
    val dismiss = { prompt = null }

    OrbitVoiceNoteRow(
        amplitudes = amplitudes,
        progress = progress,
        duration = duration,
        playing = playing,
        onPlayPause = onPlayPause,
        modifier = modifier,
        label = label,
        onDelete = onDeleted?.let { { prompt = ClipPrompt.Delete } },
        onRemove = onRemoved?.let { { prompt = ClipPrompt.Remove } },
    )

    when (prompt) {
        ClipPrompt.Delete -> OrbitConfirmDialog(
            title = "Delete voice note",
            // Names the length, since that is the only handle the user has on which clip this is —
            // a voice note has no filename, and in a list of three the duration is what tells them
            // apart. "There is no other copy" rather than the file dialogs' "cannot be undone",
            // because the stronger statement is the true one here.
            message = "Delete this $duration voice note? There is no other copy.",
            destructive = true,
            onConfirm = {
                onDeleted?.invoke()
                dismiss()
            },
            onDismiss = dismiss,
        )

        ClipPrompt.Remove -> OrbitConfirmDialog(
            title = "Remove voice note",
            message = "Remove this $duration voice note from your message? " +
                "The recording is kept.",
            onConfirm = {
                onRemoved?.invoke()
                dismiss()
            },
            onDismiss = dismiss,
        )

        null -> Unit
    }
}
