package com.orbitai.erp.ui.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A decision pair — two buttons side by side, forced to the same width and the same size.
 *
 * The equal width is the whole point, and it is not decoration. A pair like Reject/Approve or
 * Cancel/Send is a choice between two options, and if one button is wider because its label is
 * longer then the layout is arguing that it is the bigger option. Weighting them equally says they
 * are alternatives; emphasis is left to the variants, where it can be read deliberately rather than
 * inferred from text metrics.
 *
 * [dismiss] is placed first because both platforms put the way out on the left, and because a user
 * scanning left-to-right should meet the safe option before the committing one.
 *
 * Both buttons take the same [size] and the same [state], so a screen cannot end up with a live
 * Approve beside a disabled Reject and no explanation of why.
 */
@Composable
fun ActionButtonRow(
    dismiss: ActionKind,
    confirm: ActionKind,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    size: OrbitButtonSize = OrbitButtonSize.Medium,
    state: OrbitButtonState = OrbitButtonState.Active,
    confirmLoading: Boolean = false,
) {
    // The pair fills its container and splits it evenly, so the two halves are the same height and
    // the same width and together span exactly what a single full-width action would.
    //
    // Equal width is the component rather than a convenience. A choice between two options should not
    // have one button wider because its label is longer, since the layout then argues that it is the
    // bigger option — emphasis belongs to the tone, where it is stated deliberately rather than
    // inferred from text metrics. Filling the width also means a decision pair and the single action
    // below it line up down both edges, which is what stops a form reading as a pile of loose chips.
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActionButton(
            action = dismiss,
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            size = size,
            state = state,
        )
        ActionButton(
            action = confirm,
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            size = size,
            state = state,
            loading = confirmLoading,
        )
    }
}
