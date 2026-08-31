package com.orbitai.erp.core.designsystem.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Adds a new entry to a list the user is choosing from.
 *
 * ```
 *  ------------------------------------
 * | Add stage                     [X]  |
 * |------------------------------------|
 * | Stages are shared across the       |
 * | project. Name it as it appears on  |
 * | the work sequence.                 |
 * |                                    |
 * |  [ Slab Concreting              ]  |
 * |                                    |
 * |            [ Cancel ]  [   Add  ]  |
 *  ------------------------------------
 * ```
 *
 * ### The explanatory line is not filler
 *
 * A user reaching this dialog has just failed to find their stage in a list of a hundred, and the
 * conclusion they are one tap from drawing is "it does not exist, I will make it". Often it does
 * exist under a different wording, and the thing they are about to create is a near-duplicate that
 * everybody on the project then has to disambiguate for the rest of the job. One sentence saying
 * where the value goes and how it should be written is the cheapest available guard against that,
 * and it is why the dialog has a body at all rather than being a bare text box.
 *
 * It is kept to a sentence or two. Anything longer is not read at the moment someone is mid-task
 * with a name already in their head.
 *
 * ### Add is disabled until there is something to add
 *
 * Rather than accepting the tap and then explaining that the field is empty. A blank name is the
 * only invalid input this dialog can receive, and it is visible from the field itself — so the
 * disabled button is not hiding a rule the user cannot see, which is the usual objection to
 * disabling a submit. Duplicates are the caller's business, because only the caller holds the list.
 *
 * ### Cancel sits before Add
 *
 * Same order as the confirm dialog, for the same reason: the affirmative goes last, nearest the
 * thumb, and the two dialogs must not disagree about which side is which — a user who learns the
 * position in one and finds it reversed in the other will eventually tap the wrong one without
 * looking.
 *
 * @param info the short guidance line. Null for the rare list where nothing needs saying.
 * @param onCreate the trimmed name. Persisting it — and refusing it if it collides with an existing
 *   entry, or if the backend rejects it — belongs to the caller; this component knows about one text
 *   field.
 */
@Composable
fun OrbitCreateDialog(
    title: String,
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    info: String? = null,
    label: String = "Name",
    placeholder: String? = null,
    confirmLabel: String = "Add",
    dismissLabel: String = "Cancel",
    state: OrbitFieldState = OrbitFieldState.Default,
) {
    val spacing = OrbitTheme.spacing

    var draft by remember { mutableStateOf("") }
    val trimmed = draft.trim()
    val canCreate = trimmed.isNotEmpty()

    OrbitDialog(
        onDismiss = onDismiss,
        title = title,
        modifier = modifier,
        content = {
            if (info != null) {
                Text(
                    text = info,
                    // The quiet tier at regular weight. This is guidance, and it has to be legible
                    // without competing with the field the user came here to fill in — the heading
                    // above and the label below are both set heavier, so anything but regular here
                    // gives a dialog with no plain text in it at all.
                    style = OrbitTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = OrbitTheme.contentColors.textSecondary,
                )
            }

            OrbitTextField(
                value = draft,
                onValueChange = { draft = it },
                label = label,
                placeholder = placeholder,
                size = OrbitFieldSize.Medium,
                state = state,
                modifier = Modifier.fillMaxWidth(),
                // Done rather than Next: this field is the whole form, so the keyboard should offer
                // to finish rather than to move to a field that does not exist.
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (canCreate) onCreate(trimmed) },
                ),
            )
        },
        actions = {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                OrbitButton(
                    label = dismissLabel,
                    onClick = onDismiss,
                    variant = OrbitButtonVariant.Secondary,
                )
                OrbitButton(
                    label = confirmLabel,
                    onClick = { onCreate(trimmed) },
                    variant = OrbitButtonVariant.Primary,
                    state = if (canCreate) OrbitButtonState.Active else OrbitButtonState.Disabled,
                )
            }
        },
    )
}
