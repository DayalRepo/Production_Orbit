package com.orbitai.erp.core.designsystem.component.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors

/**
 * Who you are signed in as, and the way out.
 *
 * ```
 *              /\
 *  -----------/  \-----------
 * | Account            [X]   |
 * |--------------------------|
 * |  Name                    |
 * |  Priya Sharma            |
 * |  Role                    |
 * |  Project Manager         |
 * |  Number                  |
 * |  +91 98200 41122   [copy]|
 * |  Organisation            |
 * |  Meridian Infra Pvt Ltd  |
 * |--------------------------|
 * |  [->] Sign out           |
 *  --------------------------
 * ```
 *
 * ### The same bubble as the identity card, deliberately
 *
 * This opens from an avatar and so does [OrbitInfoPopover], and on a crew screen the user's own face
 * sits in the same row as everyone else's. Two panels with different chrome hanging off adjacent
 * faces would suggest the faces are different kinds of object, which they are not — the difference
 * is whose face it is. Sharing [OrbitBubblePopover] keeps the claim honest: same panel, same
 * pointer, one extra row because this one is *yours* and you can act on it.
 *
 * ### Unlike the identity card, the labels are drawn
 *
 * [OrbitInfoPopover] hides its labels because a name and a phone number announce what they are by
 * their own shape. That argument runs out here. This panel carries four values, and two of them —
 * "Project Manager" and "Meridian Infra Pvt Ltd" — are proper nouns sitting one above the other with
 * nothing to say which is the job and which is the company. Worse, the fourth line is a *different
 * thing* depending on the user: an organisation for a CEO, a project for a site engineer. An
 * unlabelled line that silently changes meaning between accounts is exactly the line that has to be
 * labelled.
 *
 * So each value gets a caption above it, set in small caps with tracking — the shape of a field name
 * on a form, which is read as a key rather than as a line of prose no matter what word it contains.
 * That shape is what carries the caption's job, which frees the *ink* to do something else.
 *
 * ### The captions take the strong ink and the values take the quiet one
 *
 * The obvious assignment is the other way round: caption quiet, value strong. It was tried and it
 * makes the panel hard to enter. Four proper nouns at full strength in a narrow column is four things
 * shouting the same volume, and the captions — the only part of the panel that tells you what you are
 * looking at — recede exactly when a user scanning for "which project am I on" needs them.
 *
 * Reversing it turns the column into an index. The caps run down the left at full strength and give
 * the eye four fixed landmarks; the values sit under them a shade quieter and are read once the right
 * landmark has been found. The values lose nothing legible by it — a step of ink between primary and
 * secondary is a difference in emphasis, not in contrast, and both clear AA against the panel fill.
 *
 * ### Sign out is red, and it is the only red here
 *
 * Ending a session is the one thing in this panel that changes anything, and on a shared site tablet
 * it is a thing you can do to somebody else by accident. Red — glyph and label together — is what
 * separates it from the four lines above it that merely state facts. The rule above it does the
 * structural half of the same job; colour alone would not survive greyscale, and a rule alone reads
 * as just another section.
 *
 * @param fields identity lines, in reading order. Labels are drawn, so keep them to a word. Mark the
 *   phone [copyable][OrbitInfoField.copyable]; it is the one value here that exists to be pasted
 *   somewhere else.
 * @param signOutLabel exposed for wording and translation, not for repurposing the row. It is the
 *   way out of the session and it sits below a rule that says so.
 */
@Composable
fun OrbitAccountPopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    fields: List<OrbitInfoField>,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Account",
    signOutLabel: String = "Sign out",
    // Wider than the shared bounds, for the same reason the identity bubble is narrower: the panel
    // is sized by what it holds. Every line here is a caption and a value on one row, so the width
    // has to carry the longest of each at once — and the fourth value is an organisation name, the
    // longest string in the whole component.
    minWidth: Dp = OrbitTheme.sizing.popoverMinWidth * AccountWidthRatio,
    maxWidth: Dp = OrbitTheme.sizing.popoverMaxWidth * AccountWidthRatio,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing

    // The same red the destructive button and the error rim use, so signing out is recognisably the
    // same class of act as anything else in the product painted this colour.
    val danger = OrbitBadgeTone.Red.colors.label

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
            // Tighter than the identity card's spacing, because each entry here is already two lines
            // and the caption binds itself to the value below it. The gap that has to read as "new
            // fact" is the one between pairs, and it only works if it is clearly larger than the gap
            // inside a pair — which is zero.
            // Down a step from the identity card's gap. Four two-line entries stacked at the wider
            // rhythm made this panel tall enough to cover the row of faces it opened from, and a
            // panel that hides its own anchor is one the user cannot orient against. The caps
            // captions give each pair a hard top edge, so the pairs stay separable at the tighter
            // gap in a way plain sentence-case lines would not.
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            fields.forEachIndexed { index, field ->
                // A rule between each identity section. Without one, four caption-and-value pairs at
                // this gap read as a single eight-line block, and the caps alone have to carry the
                // whole job of saying where one fact ends and the next begins. The rule makes the
                // boundary structural, which is what lets the gap stay tight enough to keep the panel
                // from covering the faces it opened from.
                //
                // Inset both ends past the column's own padding, so it separates rows inside the
                // panel rather than looking like the panel has been sliced into pieces.
                if (index > 0) {
                    OrbitDivider(inset = spacing.xs, endInset = spacing.xs)
                }
                AccountRow(field)
            }
        }

        // The same rule, at the same inset and with the same gap either side, as the ones between the
        // identity sections above. It was full-bleed with no gap beneath it, on the reasoning that the
        // boundary between "facts" and "an action" is a stronger one than the boundaries between
        // facts — but the effect was one rule that did not line up with the three above it and a
        // sign-out row sitting tighter to its rule than any other row, so the panel read as having
        // been assembled from two different cards.
        //
        // The inset is stated as `md + xs` because this rule is a sibling of the padded column rather
        // than a child of it, so it has to reproduce that column's padding to land on the same pixel
        // as the rules inside it.
        OrbitDivider(
            inset = spacing.md + spacing.xs,
            endInset = spacing.md + spacing.xs,
        )

        Spacer(modifier = Modifier.height(spacing.xs))

        val signOutInteraction = remember { MutableInteractionSource() }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                // The whole row is the target, not the label inside it. A tappable strip the width
                // of the panel is hard to miss with a thumb; a control sized to its own text is one
                // that gets tapped just past its right edge and does nothing.
                .indication(signOutInteraction, orbitPressIndication())
                .orbitHandCursor()
                .clickable(
                    interactionSource = signOutInteraction,
                    indication = null,
                    role = Role.Button,
                ) {
                    // Dismissing alongside the action, rather than leaving it to the caller, because
                    // a panel still sitting open over a screen that is mid sign-out invites a second
                    // tap on the same row.
                    onSignOut()
                    onDismiss()
                }
                // Floors the row at the platform's minimum touch target even when the text is set
                // small, so the one control in the panel is never the hardest thing in it to hit.
                .heightIn(min = sizing.minTouchTarget)
                .padding(horizontal = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Icon(
                imageVector = OrbitIcons.Logout,
                // The row's own text names the action, and a second reading of it from the icon is
                // noise on a control this short.
                contentDescription = null,
                tint = danger,
                modifier = Modifier.size(sizing.iconSm),
            )
            Text(
                text = signOutLabel,
                style = OrbitTheme.typography.bodyMedium,
                // Heavier than the values above it. This is the only line in the panel that is a
                // control rather than a statement, and weight is what says so where colour alone
                // would not survive greyscale.
                fontWeight = FontWeight.SemiBold,
                color = danger,
            )
        }
    }
}

/**
 * A caption and the value under it.
 *
 * ### Stacked, after side-by-side was tried and failed
 *
 * Caption left and value right is the tidier arrangement on paper and it does not survive contact
 * with the content. A popover is a few hundred dp wide at most, and once a tracked caps caption has
 * taken its share of the row there is not enough left for the two longest values this panel carries:
 * a phone number with its country code, and an organisation name. Both ellipsised. A truncated
 * organisation name is merely annoying; a truncated phone number next to a copy button is a control
 * that appears to offer something it cannot deliver.
 *
 * Stacking gives every value the full width of the panel, which is the only way all four fit. The
 * cost is height, and it is paid back by setting the pair tight — the caption sits directly on its
 * value with no gap, so a pair reads as one object and the panel as four objects rather than eight
 * lines.
 *
 * ### Caption quiet, value strong
 *
 * An earlier pass ran these the other way round, on the theory that the captions were the landmarks
 * a scanning eye needs. On device it read as a list of headings with the answers whispered
 * underneath — the caps were doing the landmark job perfectly well on their own, and the ink spent
 * on them was taken from the only lines anybody came to read.
 *
 * Both lines sit in one semantics node. Read separately a screen reader gives "Role" and "Project
 * Manager" as two unrelated stops, which is the same problem the drawn caption exists to solve for
 * sighted users, reintroduced one layer down.
 */
@Composable
private fun AccountRow(field: OrbitInfoField) {
    val content = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Every section the same height, copy button or not — see the note on the info card's row
            // for why. Here the two-line caption-and-value pair is usually already taller than this,
            // so the floor only ever bites on a short row, which is exactly the one that used to be
            // shorter than its neighbours.
            .heightIn(min = OrbitTheme.sizing.iconButtonSm)
            .semantics(mergeDescendants = true) {
                contentDescription = "${field.label}, ${field.value}"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                // Uppercased here rather than at the call site so a caller writes "Role" and gets
                // the right thing. Passing "ROLE" through would put the caps into the accessibility
                // tree as well, where a screen reader is entitled to spell them out letter by letter.
                text = field.label.uppercase(),
                // Tracked caps, one tier below the value and in the quieter ink. The caps carry
                // "this is a field name" by shape alone, so neither size nor ink has to spend
                // anything saying it again.
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clearAndSetSemantics {},
            )
            Text(
                text = field.value,
                style = OrbitTheme.typography.bodyMedium,
                // A step above the caption, not two. The caps above are already separating the pair,
                // so the value does not need bold on top of that to be found.
                fontWeight = FontWeight.Medium,
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }

        if (field.copyable) {
            OrbitCopyButton(value = field.value, label = field.label)
        }
    }
}

/**
 * How much of the shared popover width the account panel takes.
 *
 * A multiplier on the shared token rather than its own dp pair, so the two bubbles stay proportional
 * if the base moves.
 *
 * Raised from 1.05, which was chosen on the reasoning that stacking each value under its caption
 * already gives it the full width so the panel needed only enough extra to keep an organisation name
 * off the rim. That was true of the width the *text* needs and missed what the panel now contains:
 * four sections each floored to a control's height, separated by rules inset from the padding. At the
 * old width the longer values — a full organisation name, a number with its country code — were
 * ellipsising while the rules made the panel read as a denser object than it used to.
 */
private const val AccountWidthRatio = 1.2f
