package com.orbitai.erp.ui.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant

/**
 * The recurring actions of the ERP, each pinned to one variant and one wording.
 *
 * This exists for the same reason `BadgeKind` does. "Approve" appears on a purchase order, a
 * timesheet, an RFI, a material request and a change order, and if each screen picks its own wording
 * and emphasis then approving something looks like five different operations. Pinning them here also
 * makes the destructive ones destructive everywhere, which is the part that actually matters: a
 * Reject styled as a neutral Secondary on one screen is how people reject things by accident.
 *
 * The buttons are text-only. A glyph beside a two-word label competes with it rather than helping —
 * nobody needs a picture to understand "Approve" — and dropping it lets the label centre in the pill,
 * which is what makes a pair of them read as a matched set.
 */
enum class ActionKind(
    val label: String,
    val variant: OrbitButtonVariant,
) {
    Approve("Approve", OrbitButtonVariant.Primary),

    /**
     * The counterpart to Approve, and the reason Destructive exists as a variant: an error-coloured
     * label inside a neutral ring is the only thing separating the two at a glance.
     */
    Reject("Reject", OrbitButtonVariant.Destructive),

    /**
     * Low-emphasis on purpose. Cancel abandons your own unsaved work, and styling an escape hatch as
     * loudly as the action it escapes is how people click the wrong one.
     */
    // Red, the same as Reject. They are never offered together — Cancel pairs with Send or Create,
    // Reject with Approve — so there is no screen on which the two are confusable, and both are the
    // half of a decision that discards what the user was doing. Giving Cancel a neutral outline
    // while Reject went red made the same gesture look like two different kinds of act.
    Cancel("Cancel", OrbitButtonVariant.Destructive),

    Send("Send", OrbitButtonVariant.Primary),
    Login("Login", OrbitButtonVariant.Primary),
    Create("Create", OrbitButtonVariant.Primary),
    Open("Open", OrbitButtonVariant.Primary),
    ;

    /** In-flight wording, so a button that is working says what it is doing. */
    val busyLabel: String
        get() = when (this) {
            Approve -> "Approving"
            Reject -> "Rejecting"
            Cancel -> "Cancelling"
            Send -> "Sending"
            Login -> "Signing in"
            Create -> "Creating"
            Open -> "Opening"
        }
}

/**
 * A button for one of the ERP's standard actions.
 *
 * Prefer this over calling `OrbitButton` with a hand-picked variant. Reach for `OrbitButton` directly
 * only for genuinely one-off actions that no other screen will ever show.
 *
 * @param label overridable for the cases where a screen has better wording than the catalogue —
 *   "Approve all 12" on a batch screen. The variant stays pinned.
 * @param loading swaps in the spinner and, unless [label] was overridden, the matching
 *   [ActionKind.busyLabel], so the button explains the wait instead of just freezing.
 */
@Composable
fun ActionButton(
    action: ActionKind,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    size: OrbitButtonSize = OrbitButtonSize.Medium,
    state: OrbitButtonState = OrbitButtonState.Active,
    loading: Boolean = false,
) {
    OrbitButton(
        label = label ?: if (loading) action.busyLabel else action.label,
        onClick = onClick,
        modifier = modifier,
        variant = action.variant,
        size = size,
        state = state,
        loading = loading,
    )
}
