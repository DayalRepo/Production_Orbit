package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One face in an [OrbitAvatarGroup].
 *
 * [name] is not optional even when [painter] is, because the group's spoken description is built
 * from the names and a nameless member would leave a hole in it. It also supplies the monogram when
 * there is no photo, so a member with neither a name nor a picture is not a case worth supporting.
 */
data class OrbitAvatarGroupMember(
    val name: String,
    val painter: Painter? = null,
) {
    /**
     * First letters of the first two words. "Priya Sharma" gives PS, "Ravi" gives R.
     *
     * Word-initials rather than the first two characters, because the latter turns every member of a
     * team into a near-identical monogram the moment two people share a first name.
     */
    internal val monogram: String
        get() = name.trim().split(WHITESPACE)
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}

private val WHITESPACE = Regex("\\s+")

/**
 * An overlapping stack of avatars with a count for whoever did not fit.
 *
 * ### The stack is a summary, not a list
 *
 * A row of watchers on an issue can be forty people, and the useful reading is "a handful, and one
 * of them is me" rather than any individual face. That is what fixes the two decisions here. The
 * faces overlap, because the value is in the group having a shape and a shape is what a row of
 * separated circles does not have. And the overflow is a count rather than more faces, because past
 * about five the faces stop being distinguishable at avatar sizes and the row is just wide.
 *
 * ### The ring is load-bearing
 *
 * Each avatar is set in a ring of the page colour (`sizing.avatarStackRing`). Without it two
 * overlapping photographs of similar tone merge into one blob and the count of faces — the entire
 * content of the component — becomes unreadable. This is why [background] exists as a parameter and
 * why getting it wrong is visible immediately: the ring has to match what is actually behind the
 * stack, and a stack on a card needs the card's colour, not the page's.
 *
 * ### Accessibility
 *
 * The whole row is one node reading "Priya Sharma, Ravi Menon and 5 others". Left to the default it
 * would be four unlabelled images and a number, and a screen reader user would have to step through
 * five nodes to learn something a sighted user gets in one glance. The individual avatars are
 * therefore cleared rather than described.
 *
 * ### Collapsed is a summary; expanded is a list
 *
 * Setting [expanded] switches the component between two genuinely different layouts rather than
 * animating a bigger version of the same one, and the split follows from the argument above.
 * Collapsed, the faces overlap, the overflow is a count, and the *whole row* is one target — because
 * overlapping hit areas mean a tap near a seam would otherwise be taken by whichever face happens to
 * be on top rather than the one under the finger.
 *
 * Expanded, the overlap goes away entirely. Every face separates onto its own tile with a real gap,
 * wraps onto as many rows as it needs, and becomes its own target. That is the point of expanding:
 * the user has asked to stop reading a shape and start picking a person, and a person cannot be
 * picked out of a pile. It is also what makes the per-face targets honest — separated tiles have
 * separated hit areas, so [onMemberClick] can be trusted to report the face that was actually
 * touched.
 *
 * The expanded flag is hoisted rather than held internally, for the usual reason plus a specific
 * one: a screen showing several groups will want at most one open at a time, and a component owning
 * its own flag cannot know about its siblings.
 *
 * @param max how many faces to show before collapsing the rest into a count. Four is the default
 *   because a fifth adds roughly the width of a whole avatar for a face nobody can identify anyway.
 *   Ignored while [expanded], where the whole point is that everyone is visible.
 * @param background the colour behind the stack, used for the separating rings.
 * @param onToggle makes the collapsed stack tappable. Null leaves the group inert, which is right
 *   for a summary on a card that is itself a link to somewhere else.
 * @param onMemberClick fires for one face while expanded. Also receives the index, because names
 *   are not unique on a construction project and the caller usually needs to look the member up.
 * @param memberOverlay drawn inside a face's tile, in a `Box` above the avatar. This is the seam an
 *   info popover attaches through: anchoring it to the tile means the platform positions the panel
 *   against the face that was tapped, rather than the caller measuring coordinates and re-deriving
 *   something the layout already knows.
 */
@Composable
fun OrbitAvatarGroup(
    members: List<OrbitAvatarGroupMember>,
    modifier: Modifier = Modifier,
    size: OrbitAvatarSize = OrbitAvatarSize.Sm,
    max: Int = 4,
    background: androidx.compose.ui.graphics.Color = OrbitTheme.colorScheme.background,
    expanded: Boolean = false,
    onToggle: (() -> Unit)? = null,
    onMemberClick: ((index: Int, member: OrbitAvatarGroupMember) -> Unit)? = null,
    memberOverlay: (@Composable (index: Int, member: OrbitAvatarGroupMember) -> Unit)? = null,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors

    val diameter: Dp = when (size) {
        OrbitAvatarSize.Xs -> sizing.avatarXs
        OrbitAvatarSize.Sm -> sizing.avatarSm
        OrbitAvatarSize.Md -> sizing.avatarMd
        OrbitAvatarSize.Lg -> sizing.avatarLg
        OrbitAvatarSize.Xl -> sizing.avatarXl
    }

    val ring = sizing.avatarStackRing
    // The ring sits outside the avatar, so the tile each face occupies is wider than the face.
    val tile = diameter + ring * 2
    // Negative spacing is how the overlap happens: each tile is pulled back over the one before it.
    val step = -(tile * sizing.avatarStackOverlap)

    val shown = members.take(max.coerceAtLeast(1))
    val overflow = members.size - shown.size

    val spoken = buildString {
        append(shown.joinToString(", ") { it.name })
        if (overflow > 0) {
            if (shown.isNotEmpty()) append(" and ")
            append(overflow)
            append(if (overflow == 1) " other" else " others")
        }
    }

    if (expanded) {
        FlowRow(
            modifier = modifier,
            // A real, positive gap now, where the collapsed stack uses a negative one. The overlap
            // existed to give the group a shape; once the user is picking a person it is only in
            // the way, and separated faces are what make the per-face hit areas trustworthy.
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            members.forEachIndexed { index, member ->
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(tile)
                            .background(background, CircleShape)
                            .then(
                                if (onMemberClick != null) {
                                    Modifier
                                        .orbitHandCursor()
                                        .clickable(
                                            role = Role.Button,
                                            onClick = { onMemberClick(index, member) },
                                        )
                                } else {
                                    Modifier
                                },
                            )
                            .padding(ring)
                            // Each face is its own named node now. Expanded, the group is no longer
                            // one summary to be read in a breath — it is a list being chosen from,
                            // and a screen reader user needs the same per-person granularity a
                            // sighted user has just gained.
                            .semantics(mergeDescendants = true) { contentDescription = member.name },
                        contentAlignment = Alignment.Center,
                    ) {
                        OrbitAvatar(
                            contentDescription = null,
                            painter = member.painter,
                            initials = member.monogram,
                            size = size,
                        )
                    }
                    memberOverlay?.invoke(index, member)
                }
            }

            // The way back. Without it expanding is a one-way door: the collapsed stack is one big
            // target precisely because the faces overlap, and expanding replaces that target with
            // per-face ones — so the gesture that opened the group no longer exists anywhere, and
            // the user is left with a grid they cannot put away.
            //
            // It takes the last tile rather than sitting above or beside the grid, so it lands where
            // the "+3" chip was a moment earlier. That is the position the eye already associates
            // with "the rest of this group", and reusing it means the row's width does not jump when
            // the control appears. It flows with the faces, so on a group that wraps it ends up
            // after the final face on the last row rather than stranded.
            if (onToggle != null) {
                val collapseInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(tile)
                        .clip(CircleShape)
                        .indication(collapseInteraction, orbitPressIndication())
                        .background(background, CircleShape)
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = collapseInteraction,
                            indication = null,
                            role = Role.Button,
                            onClick = onToggle,
                        )
                        .padding(ring)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Collapse group"
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(diameter)
                            // The same neutral fill the overflow chip and the monogram fallback
                            // use. This is a control rather than a person, and tinting it would
                            // make it the most prominent thing in a row of faces.
                            .background(control.interactiveContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        OrbitGlyph(
                            icon = OrbitIcons.Cancel,
                            size = if (size == OrbitAvatarSize.Xs) sizing.iconXs else sizing.iconSm,
                            tint = content.textSecondary,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
        return
    }

    val expandInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .then(
                if (onToggle != null) {
                    Modifier
                        // A stadium, not a rectangle: the collapsed row is exactly one face tall and
                        // capped by a circular face at each end, so this clip follows the silhouette
                        // the eye sees. Unclipped, the ripple squares off the two rounded ends and
                        // the group looks briefly like a rectangular button.
                        .clip(RoundedCornerShape(percent = 50))
                        .indication(expandInteraction, orbitPressIndication())
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = expandInteraction,
                            indication = null,
                            role = Role.Button,
                            onClick = onToggle,
                        )
                } else {
                    Modifier
                },
            )
            .semantics(mergeDescendants = true) {
                contentDescription = spoken
                if (onToggle != null) {
                    // Labels what the tap does, not what the row is. "Show all members" is the only
                    // thing a screen reader user cannot infer from the names just read to them.
                    onClick(label = "Show all members") { onToggle(); true }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(step),
    ) {
        shown.forEach { member ->
            Box(
                modifier = Modifier
                    .size(tile)
                    .background(background, CircleShape)
                    .padding(ring)
                    .clearAndSetSemantics {},
                contentAlignment = Alignment.Center,
            ) {
                OrbitAvatar(
                    contentDescription = null,
                    painter = member.painter,
                    initials = member.monogram,
                    size = size,
                )
            }
        }

        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .size(tile)
                    .background(background, CircleShape)
                    .padding(ring)
                    .clearAndSetSemantics {},
                contentAlignment = Alignment.Center,
            ) {
                    Box(
                        modifier = Modifier
                            .size(diameter)
                            .clip(CircleShape)
                            .background(control.interactiveContainer, CircleShape)
                            .border(sizing.hairline, control.controlBorder, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+$overflow",
                            style = when (size) {
                                OrbitAvatarSize.Xs, OrbitAvatarSize.Sm -> OrbitTheme.typography.labelSmall
                                OrbitAvatarSize.Md -> OrbitTheme.typography.labelLarge
                                OrbitAvatarSize.Lg -> OrbitTheme.typography.titleMedium
                                OrbitAvatarSize.Xl -> OrbitTheme.typography.headlineSmall
                            },
                            fontWeight = FontWeight.SemiBold,
                            color = content.textSecondary,
                        )
                    }
            }
        }
    }
}
