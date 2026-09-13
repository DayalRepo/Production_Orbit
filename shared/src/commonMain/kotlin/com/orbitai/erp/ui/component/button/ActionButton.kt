package com.orbitai.erp.ui.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonIconPosition
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The recurring actions of the ERP, each pinned to one variant and one wording.
 *
 * This exists for the same reason `BadgeKind` does. "Approve" appears on a purchase order, a
 * timesheet, an RFI, a material request and a change order, and if each screen picks its own wording
 * and emphasis then approving something looks like five different operations. Pinning them here also
 * makes the destructive ones destructive everywhere, which is the part that actually matters: a
 * Reject styled as a neutral Secondary on one screen is how people reject things by accident.
 *
 * ### Every action carries a glyph
 *
 * These were text-only for a while, on the reasoning that nobody needs a picture to understand
 * "Approve" and a glyph beside a two-word label competes with it. That is true of the glyph as
 * *identification* and misses what it does here. A tinted chip with a mark in it is a recognisable
 * object; a tinted chip with only a word in it is a coloured rectangle, and a form full of them
 * reads as a form full of rectangles. The glyph is what makes the control look like a control.
 *
 * ### Which side it sits on depends on what it is saying
 *
 * Most sit **before** the label, because the glyph and the word are naming the same thing and the
 * mark is the faster of the two to recognise — a red ✕ is identified before "Cancel" is read, so
 * putting it first shortens the scan rather than interrupting it.
 *
 * [Login], [Open] and [Next] are the exception and sit **after**. Their arrow is not naming the
 * action, it is pointing at where the action takes you, and a direction indicator belongs at the
 * end of the phrase it applies to for the same reason "next →" reads correctly and "→ next" does
 * not.
 *
 * Two glyphs are shared deliberately. Reject and Cancel both take `cancel-01`: same gesture,
 * discard what is in front of you, and never offered together. Login and Open both take
 * `arrow-right-01`: both take you somewhere else rather than changing anything where you are.
 *
 * Approve takes `checkmark-badge-02` rather than a bare tick. A bare tick is also the mark for
 * "done" and "selected", and Approve is neither — it is a decision someone with authority is
 * making. The badge around the tick is what distinguishes conferring approval from ticking a box.
 */
enum class ActionKind(
    val label: String,
    val variant: OrbitButtonVariant,
    val icon: ImageVector?,
    val iconPosition: OrbitButtonIconPosition = OrbitButtonIconPosition.Leading,
) {
    Approve("Approve", OrbitButtonVariant.Primary, OrbitIcons.CheckmarkBadge),

    /**
     * The counterpart to Approve, and the reason Destructive exists as a variant: an error-coloured
     * label inside a neutral ring is the only thing separating the two at a glance.
     */
    Reject("Reject", OrbitButtonVariant.Destructive, OrbitIcons.Cancel),

    /**
     * Low-emphasis on purpose. Cancel abandons your own unsaved work, and styling an escape hatch as
     * loudly as the action it escapes is how people click the wrong one.
     */
    // Red, the same as Reject. They are never offered together — Cancel pairs with Send or Create,
    // Reject with Approve — so there is no screen on which the two are confusable, and both are the
    // half of a decision that discards what the user was doing. Giving Cancel a neutral outline
    // while Reject went red made the same gesture look like two different kinds of act.
    Cancel("Cancel", OrbitButtonVariant.Destructive, OrbitIcons.Cancel),

    Send("Send", OrbitButtonVariant.Primary, OrbitIcons.Sent),
    Create("Create", OrbitButtonVariant.Primary, OrbitIcons.Add),

    Login(
        "Login",
        OrbitButtonVariant.Primary,
        OrbitIcons.ArrowRight,
        OrbitButtonIconPosition.Trailing,
    ),
    Open(
        "Open",
        OrbitButtonVariant.Primary,
        OrbitIcons.ArrowRight,
        OrbitButtonIconPosition.Trailing,
    ),

    /**
     * The quiet half of a wizard pair. Secondary so it never competes with [Next] on the same row.
     */
    Back("Back", OrbitButtonVariant.Secondary, OrbitIcons.ArrowLeft),

    Next(
        "Next",
        OrbitButtonVariant.Primary,
        OrbitIcons.ArrowRight,
        OrbitButtonIconPosition.Trailing,
    ),

    /** Commits an issue the same way [Create] commits a task. */
    Raise("Raise", OrbitButtonVariant.Primary, OrbitIcons.OctagonAlert),

    /** Opens the edit screen for a created task or issue. Text-only on purpose. */
    Update("Update", OrbitButtonVariant.Primary, null),

    /** Project manager starts work that is still in Created. */
    Start("Start", OrbitButtonVariant.Primary, OrbitIcons.Play),

    /** Opens a created card read-only. */
    View(
        "View",
        OrbitButtonVariant.Primary,
        OrbitIcons.ArrowRight,
        OrbitButtonIconPosition.Trailing,
    ),

    /** Site engineer / contractor hands work up for review. */
    Submit("Submit", OrbitButtonVariant.Primary, OrbitIcons.Sent),

    /** QA/QC sends the work back. Pairs with [Approve]. */
    Rework("Rework", OrbitButtonVariant.Destructive, OrbitIcons.Repeat),

    /** Commits a Need request. Pairs with [Cancel]. */
    Done("Done", OrbitButtonVariant.Primary, OrbitIcons.Tick),

    /** Places a materials order. Pairs with [Cancel]. */
    Confirm("Confirm", OrbitButtonVariant.Primary, OrbitIcons.Tick),

    /** Warehouse marks a purchase order received. */
    Receive("Received", OrbitButtonVariant.Primary, OrbitIcons.Tick),
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
            Back -> "Returning"
            Next -> "Continuing"
            Raise -> "Raising"
            Update -> "Updating"
            Start -> "Starting"
            View -> "Viewing"
            Submit -> "Submitting"
            Rework -> "Sending back"
            Done -> "Saving"
            Confirm -> "Confirming"
            Receive -> "Receiving"
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
    size: OrbitButtonSize = OrbitButtonSize.Large,
    state: OrbitButtonState = OrbitButtonState.Active,
    loading: Boolean = false,
    showIcon: Boolean = true,
) {
    OrbitButton(
        label = label ?: if (loading) action.busyLabel else action.label,
        onClick = onClick,
        modifier = modifier,
        variant = action.variant,
        size = size,
        // Skipped while loading: the spinner takes the glyph's slot, so passing both would be a
        // mark and a spinner competing for one position. [ActionKind.Update] is text-only.
        icon = if (loading || !showIcon) null else action.icon,
        iconPosition = action.iconPosition,
        state = state,
        loading = loading,
        shape = OrbitTheme.shapeTokens.card,
    )
}
