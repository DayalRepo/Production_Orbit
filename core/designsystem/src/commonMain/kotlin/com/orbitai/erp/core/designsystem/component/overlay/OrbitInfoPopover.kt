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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitRoleBadge
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.foundation.orbitPersonDisplayName
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

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
 * [OrbitAccountPopover] draws the same name / role / phone block with a person glyph beside it,
 * plus theme and sign-out. This bubble stays identity-only.
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
 * |  ANANYA KRISHNAMUR…      |
 * |  [CEO]                   |
 * |  +91 98450 11001   [copy]|
 *  --------------------------
 * ```
 *
 * Name and phone share Medium + charcoal. Names are uppercased and capped at
 * [com.orbitai.erp.core.designsystem.foundation.OrbitPersonNameMaxChars] (18).
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
 * @param roleBadge capital short form under the name (CEO, PM, SE, CONTR, …). Null leaves the badge
 *   out for non-person info bubbles.
 */
@Composable
fun OrbitInfoPopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    fields: List<OrbitInfoField>,
    modifier: Modifier = Modifier,
    title: String = "Info",
    roleBadge: String? = null,
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
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            fields.forEachIndexed { index, field ->
                val primary = index == 0
                if (primary && !roleBadge.isNullOrBlank()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        InfoRow(field = field)
                        OrbitRoleBadge(label = roleBadge)
                    }
                } else {
                    InfoRow(field = field)
                }
            }
        }
    }
}

/** One unlabelled value, with a copy control when the field asks for one. */
@Composable
private fun InfoRow(field: OrbitInfoField) {
    val content = OrbitTheme.contentColors
    val display = if (field.label.equals("Name", ignoreCase = true)) {
        orbitPersonDisplayName(field.value)
    } else {
        field.value
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OrbitTheme.sizing.iconButtonSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = display,
            style = OrbitTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = content.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "${field.label}, $display" },
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
