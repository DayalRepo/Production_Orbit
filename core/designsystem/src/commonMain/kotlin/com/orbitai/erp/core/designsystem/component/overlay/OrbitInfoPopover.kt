package com.orbitai.erp.core.designsystem.component.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One line in an [OrbitInfoPopover].
 *
 * The [label] is not drawn. It exists for the spoken description, and dropping it from the visual
 * layout is the deliberate part: nobody needs to be told that "Priya Sharma" is a name or that
 * "+91 98200 41122" is a phone number, and in a bubble this small a caption above every value costs
 * a line of height and a line of reading for information the value already carries. A screen reader
 * user has no such shape or formatting cue, which is exactly why the label survives in the
 * description — "Name, Priya Sharma" is what a sighted user infers in one glance.
 *
 * [OrbitAccountPopover] makes the opposite choice and draws its labels. The difference is that this
 * bubble carries two values a user already knows the shape of, and that one carries four, two of
 * which — a role and an organisation — are genuinely ambiguous next to each other.
 */
@Immutable
data class OrbitInfoField(
    val label: String,
    val value: String,
    /**
     * Puts a copy control on this row.
     *
     * Opt-in per field rather than on by default, because most values are not worth copying and a
     * button beside each one turns a two-line bubble into a column of controls. A phone number is
     * the case this exists for: it is shown precisely so it can end up somewhere else.
     */
    val copyable: Boolean = false,
)

/**
 * A small glass bubble that points at the thing it is describing.
 *
 * ```
 *              /\
 *  -----------/  \-----------
 * | Info               [X]   |
 * |--------------------------|
 * |  Priya Sharma            |
 * |                          |
 * |  +91 98200 41122   [copy]|
 *  --------------------------
 * ```
 *
 * Built for the avatar group — tap a face, read who it is — but it takes an arbitrary list of
 * [OrbitInfoField]s precisely so it does not become "the avatar tooltip". A person, a piece of
 * equipment and a cost line are all things a user will want to identify from a dense list without
 * leaving it, and they differ only in which values they carry.
 *
 * The pointing, placement and animation all belong to [OrbitBubblePopover]; see there for why the
 * pointer is part of the outline and how it tracks its anchor. What this component decides is that
 * the panel holds nothing but bare values.
 *
 * ### Small on purpose
 *
 * Two or three values and a heading. The bubble covers the very thing it is describing — there are
 * other faces under it — so every extra millimetre of panel hides more of the context that made the
 * tap meaningful. Anything that needs more room than this is a detail screen, not a popover. The
 * width is set narrower than a card for the same reason, and a caller with unusually long values can
 * widen it rather than have every bubble pay for the worst case.
 *
 * @param fields drawn in order as plain values, most identifying first. See [OrbitInfoField] for why
 *   the labels are spoken but not shown.
 */
@Composable
fun OrbitInfoPopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    fields: List<OrbitInfoField>,
    modifier: Modifier = Modifier,
    title: String = "Info",
    minWidth: Dp = InfoMinWidth,
    maxWidth: Dp = InfoMaxWidth,
) {
    val spacing = OrbitTheme.spacing

    OrbitBubblePopover(
        expanded = expanded,
        onDismiss = onDismiss,
        title = title,
        minWidth = minWidth,
        maxWidth = maxWidth,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(
                start = spacing.md,
                end = spacing.md,
                top = spacing.none,
                bottom = spacing.xs,
            ),
            // Enough to separate the facts, no more. The lines are different weights and different
            // inks, so they do not need a wide gap to stop reading as one wrapped paragraph — and
            // there is now a rule between them doing the separating structurally.
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            fields.forEachIndexed { index, field ->
                // A rule between every pair of facts, inset from the panel's own padding so it reads
                // as a separator *inside* the bubble rather than as a line cutting the bubble in
                // half. Between the name and the number this is the whole point: they are two
                // different kinds of thing — who, and how to reach them — and without a rule the
                // second line reads as a continuation of the first.
                if (index > 0) {
                    OrbitDivider(inset = spacing.xs, endInset = spacing.xs)
                }
                InfoRow(
                    field = field,
                    // The first value is the subject — the name the user tapped a face to find — and
                    // everything after it is supporting detail. Weight rather than size does the
                    // separating, because a larger first line would push the bubble wider on exactly
                    // the records with the longest names.
                    primary = index == 0,
                )
            }
        }
    }
}

/** One unlabelled value, with a copy control when the field asks for one. */
@Composable
private fun InfoRow(field: OrbitInfoField, primary: Boolean) {
    val content = OrbitTheme.contentColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Every section the same height, whether or not it carries a copy button.
            //
            // Floored at the button's own size, because the button is what made the rows unequal: the
            // number's row stood as tall as a 32dp control while the name's row was one line of text,
            // so the rules either side of the number sat further from their content than the rule
            // above the name did. The gaps were all `xs` and the *sections* were not the same height,
            // which is what the eye actually reads.
            .heightIn(min = OrbitTheme.sizing.iconButtonSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = field.value,
            style = OrbitTheme.typography.bodyLarge,
            fontWeight = if (primary) FontWeight.SemiBold else FontWeight.Medium,
            color = if (primary) content.textPrimary else content.textSecondary,
            // Restores the label for assistive technology. Sighted users read "which field is this"
            // off position and weight; a screen reader user would otherwise get a run of bare
            // strings.
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "${field.label}, ${field.value}" },
        )

        if (field.copyable) {
            OrbitCopyButton(value = field.value, label = field.label)
        }
    }
}

/**
 * The bubble's width, set by the one row that must not wrap.
 *
 * ### Why this is a dp value and not a ratio of the shared popover token
 *
 * It was a ratio, and a ratio cannot state the requirement. What this panel has to guarantee is that
 * a full mobile number and its copy button sit on **one line** with a little air after them — and
 * that is a sum of real widths, not a fraction of a number chosen for a different panel. Pegging it
 * to `popoverMaxWidth` meant the guarantee held only as long as nobody retuned that token for the
 * account bubble, at which point this one would start ellipsising a phone number next to a button
 * offering to copy it.
 *
 * The sum, at the default type scale:
 *
 * ```
 *   24dp  panel padding, 12dp each side
 *  136dp  "+91 98200 41122" at bodyLarge
 *    8dp  gap before the control
 *   32dp  copy button
 *    8dp  the small trailing gap
 *  ------
 *  208dp
 * ```
 *
 * Rounded up to 216dp for headroom at larger font scales and for iOS, whose body tier is a point
 * larger than Android's. `minWidth` is set to the same value rather than lower: every row in here
 * fills the width, so a floor below the ceiling would never be the width that got used, and leaving
 * the two different only invites the belief that the panel shrinks to short content.
 */
private val InfoMaxWidth = 216.dp

private val InfoMinWidth = InfoMaxWidth
