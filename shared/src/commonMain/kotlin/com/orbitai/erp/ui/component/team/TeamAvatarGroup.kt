package com.orbitai.erp.ui.component.team

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarGroup
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarGroupMember
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarSize
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoField
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoPopover
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A person on the project, as the avatar group needs to know them.
 *
 * @param phone stored with its country code already attached rather than as a bare number plus a
 *   separate dialling code. Sites run on subcontractors from several states and the occasional
 *   overseas consultant, so the code is not a constant that can be added at render time — and a
 *   number displayed without one is a number that cannot be dialled from the field.
 * @param role the abbreviation people actually use out loud — "PM", "SE", "Contractor". Not the
 *   HR job title, which nobody says and which does not fit.
 */
@Immutable
data class TeamMember(
    val name: String,
    val phone: String,
    val role: String,
    val avatar: Painter? = null,
)

/**
 * An avatar stack that opens into the team and tells you who each face is.
 *
 * ### Two pieces of state, and why they live here
 *
 * Whether the group is expanded, and which face is selected. Both are hoisted out of
 * `OrbitAvatarGroup` — which stays a pure renderer — and both are held together here because they
 * interact: collapsing the group has to clear the selection, or a popover stays anchored to a face
 * that is no longer on screen and floats over the collapsed stack pointing at nothing.
 *
 * Tapping the same face twice closes the panel rather than reopening it. A tap on a control that is
 * already showing its panel means "I am done with this" far more often than it means "show me that
 * again", and without the toggle the only way out of the panel is the X or a tap on empty space,
 * neither of which is where the finger already is.
 *
 * ### The popover is anchored, not positioned
 *
 * It is passed through `memberOverlay`, so it renders inside the tapped face's own tile and the
 * platform's popup machinery works out where to put it relative to that. The alternative —
 * measuring the tile's window coordinates and placing a panel at them — means re-deriving something
 * the layout already knows, and re-deriving it wrongly the first time the group wraps onto a second
 * row or the page scrolls a pixel.
 *
 * @param onCall optional. When set the popover's phone line is worth showing as a number the user
 *   can act on; dialling itself is a platform intent and does not belong in a shared component, so
 *   the caller supplies it.
 */
@Composable
fun TeamAvatarGroup(
    members: List<TeamMember>,
    modifier: Modifier = Modifier,
    size: OrbitAvatarSize = OrbitAvatarSize.Sm,
    max: Int = 4,
    background: Color = OrbitTheme.colorScheme.background,
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Int?>(null) }

    OrbitAvatarGroup(
        members = members.map { OrbitAvatarGroupMember(name = it.name, painter = it.avatar) },
        modifier = modifier,
        size = size,
        max = max,
        background = background,
        expanded = expanded,
        onToggle = {
            expanded = !expanded
            // Collapsing has to drop the selection with it. A popover left open would still be
            // anchored to a tile that the collapsed layout no longer draws, so it would hang over
            // the stack pointing at whichever face happened to end up underneath.
            if (!expanded) selected = null
        },
        onMemberClick = { index, _ ->
            selected = if (selected == index) null else index
        },
        memberOverlay = { index, _ ->
            val member = members[index]
            OrbitInfoPopover(
                expanded = selected == index,
                onDismiss = { selected = null },
                // Name and number only. The role is deliberately left out: the bubble sits on top
                // of the other faces, so it is kept to the two values somebody actually taps a face
                // to find. Whoever needs the role is on their way to a profile screen, not a
                // popover.
                fields = listOf(
                    OrbitInfoField("Name", member.name),
                    // The number is the one value here worth copying: a name gets read and
                    // remembered, a phone number gets pasted into a dialler or a message. Retyping
                    // twelve digits off a screen with gloves on is where the errors come from.
                    OrbitInfoField("Mobile number", member.phone, copyable = true),
                ),
            )
        },
    )
}
