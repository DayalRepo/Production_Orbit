package com.orbitai.erp.core.designsystem.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A yes/no question, usually before something irreversible.
 *
 * ### The message says what will happen, and the buttons say Yes and No
 *
 * Splitting those two jobs is the point. The common alternative — a vague message plus verb buttons
 * ("Delete" / "Cancel") — puts the whole decision in two words that a user skims past, and "Cancel"
 * next to a destructive action is genuinely ambiguous: cancel the deletion, or cancel the upload
 * that is running? Naming the object in the message and keeping the answers to a plain Yes and No
 * means the sentence carries the meaning and the buttons carry only the answer.
 *
 * The message must therefore be a complete question naming the thing. "Delete 1.pdf?" — not "Are you
 * sure?", which is the canonical example of a dialog that tells the user nothing they did not
 * already know.
 *
 * ### Yes is the destructive one, and it is styled as such
 *
 * When [destructive] is set the affirmative takes the Destructive variant and No becomes the quiet
 * secondary. That is the right way round even though it makes the dangerous button the loud one:
 * the alternative, hiding Yes in a weak style, means a user who *does* want to delete has to hunt
 * for the control, and hunting under a confirmation is how people click the wrong one. Colour here
 * is a warning label, not a discouragement.
 *
 * No is placed before Yes, so the last button — the one under a right thumb — is deliberate rather
 * than the one you hit by reflex on the way past.
 */
@Composable
fun OrbitConfirmDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Confirm",
    confirmLabel: String = "Yes",
    dismissLabel: String = "No",
    destructive: Boolean = false,
) {
    OrbitDialog(
        onDismiss = onDismiss,
        title = title,
        modifier = modifier,
        // A destructive confirmation does not close on a stray scrim tap. Everywhere else that
        // shortcut is a courtesy; here it would be indistinguishable from an answer.
        dismissible = !destructive,
        content = {
            Text(
                text = message,
                style = OrbitTheme.typography.bodyLarge,
                color = OrbitTheme.contentColors.textPrimary,
            )
        },
        actions = {
            OrbitButton(
                label = dismissLabel,
                onClick = onDismiss,
                variant = OrbitButtonVariant.Secondary,
                size = OrbitButtonSize.Medium,
            )
            OrbitButton(
                label = confirmLabel,
                onClick = onConfirm,
                variant = if (destructive) {
                    OrbitButtonVariant.Destructive
                } else {
                    OrbitButtonVariant.Primary
                },
                size = OrbitButtonSize.Medium,
            )
        },
    )
}

/**
 * Ask for one line of text — renaming a file, in practice.
 *
 * ### It holds its own draft
 *
 * The edited value lives here rather than being hoisted, which is the opposite of the rule
 * everywhere else in this system. The reason is that a rename has a cancel: the caller's copy of
 * the name must not change until Save is pressed, and hoisting the draft means every keystroke
 * writes through to the thing being renamed, so dismissing the dialog leaves the half-typed name
 * behind. A local draft makes cancel actually cancel.
 *
 * ### Save is disabled on an empty or unchanged name
 *
 * Empty because a file with no name is not a rename, it is data loss with extra steps. Unchanged
 * because a Save that does nothing still shows a spinner and a toast and teaches the user that the
 * button lies. Both are disabled rather than hidden, so the button does not move as you type.
 *
 * The keyboard's action key is Done and it commits, since a one-field form has exactly one thing
 * the return key could mean.
 *
 * @param initialValue the current name. The field opens with this in it rather than empty — a
 *   rename is nearly always an edit of a few characters, and an empty box forces retyping the whole
 *   thing.
 */
@Composable
fun OrbitRenameDialog(
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Rename",
    label: String = "Name",
    placeholder: String? = null,
    confirmLabel: String = "Save",
    dismissLabel: String = "Cancel",
) {
    var draft by remember(initialValue) { mutableStateOf(initialValue) }
    val trimmed = draft.trim()
    val canSave = trimmed.isNotEmpty() && trimmed != initialValue

    OrbitDialog(
        onDismiss = onDismiss,
        title = title,
        modifier = modifier,
        content = {
            OrbitTextField(
                value = draft,
                onValueChange = { draft = it },
                label = label,
                placeholder = placeholder,
                size = OrbitFieldSize.Medium,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = { if (canSave) onConfirm(trimmed) },
                ),
            )
        },
        actions = {
            OrbitButton(
                label = dismissLabel,
                onClick = onDismiss,
                variant = OrbitButtonVariant.Secondary,
                size = OrbitButtonSize.Medium,
            )
            OrbitButton(
                label = confirmLabel,
                onClick = { onConfirm(trimmed) },
                variant = OrbitButtonVariant.Primary,
                size = OrbitButtonSize.Medium,
                icon = OrbitIcons.Tick,
                state = if (canSave) OrbitButtonState.Active else OrbitButtonState.Disabled,
            )
        },
    )
}
