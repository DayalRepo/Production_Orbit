package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatar
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarGroup
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarGroupMember
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarSize
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoField
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoPopover
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One person who can be assigned to a task.
 *
 * @param username optional login handle shown in the info card; omitted from the dropdown row.
 */
@Immutable
data class OrbitAssignMember(
    val id: String,
    val name: String,
    val role: String,
    val mobile: String,
    val username: String? = null,
    val avatar: Painter? = null,
) {
    internal val monogram: String
        get() = name.trim().split(WHITESPACE)
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}

private val WHITESPACE = Regex("\\s+")

/** Uppercase display name for assign rows — role is omitted from the list UI. */
fun orbitAssignDisplayName(name: String): String = name.trim().uppercase()

internal fun List<OrbitAssignMember>.filterAssignByQuery(query: String): List<OrbitAssignMember> {
    val needle = query.trim()
    if (needle.isEmpty()) return this
    val lower = needle.lowercase()
    return filter { member ->
        member.name.lowercase().contains(lower) ||
            member.role.lowercase().contains(lower) ||
            member.mobile.contains(needle) ||
            member.username?.lowercase()?.contains(lower) == true
    }
}

/** Selected members first, preserving relative order within each group. */
internal fun List<OrbitAssignMember>.sortAssignSelectedFirst(
    selectedIds: Set<String>,
): List<OrbitAssignMember> {
    if (selectedIds.isEmpty()) return this
    val (picked, rest) = partition { it.id in selectedIds }
    return picked + rest
}

/**
 * Multi-select assign field: searchable dropdown, avatar trigger, info card on avatar tap.
 *
 * Built for task assignment where site engineers and contractors are picked separately — pass a
 * distinct [label] per instance ("Site engineer", "Contractor") and filter [members] upstream.
 *
 * Selected members appear as an overlapping avatar group with a `+N` overflow chip; tap the group
 * to expand every face. Tap one face for mobile number in a glass info card.
 */
@Composable
fun OrbitAssignField(
    selectedIds: Set<String>,
    members: List<OrbitAssignMember>,
    onToggle: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Assign members",
    searchPlaceholder: String = "Search by name, role or mobile",
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    avatarStackMax: Int = 4,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }

    var expanded by rememberDropdownExpanded()
    var query by remember { mutableStateOf("") }
    var stackExpanded by remember { mutableStateOf(false) }
    var infoMemberId by remember { mutableStateOf<String?>(null) }

    val density = LocalDensity.current
    var anchorWidth by remember { mutableStateOf(0.dp) }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)
    val glyph = size.pick(sizing.iconSm, sizing.iconMd, sizing.iconMd)

    val selectedMembers = members.filter { it.id in selectedIds }
    val visible = members.filterAssignByQuery(query).sortAssignSelectedFirst(selectedIds)

    fun close() {
        expanded = false
        query = ""
    }

    Box(modifier = modifier) {
        OrbitFieldShell(
            interactionSource = interactionSource,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = minHeight,
            horizontalPadding = padding,
            enabled = enabled,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidth = with(density) { it.width.toDp() } }
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.DropdownList,
                ) {
                    if (expanded) close() else expanded = true
                }
                .semantics {
                    contentDescription = if (selectedMembers.isEmpty()) {
                        "$label, $placeholder"
                    } else {
                        "$label, ${selectedMembers.size} assigned: " +
                            selectedMembers.joinToString(", ") { it.name }
                    }
                },
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (selectedMembers.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = size.pick(
                            OrbitTheme.typography.bodyMedium,
                            OrbitTheme.typography.bodyLarge,
                            OrbitTheme.typography.bodyLarge,
                        ),
                        color = content.textSecondary.copy(
                            if (enabled) 1f else OrbitAlpha.Disabled,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else if (selectedMembers.size == 1) {
                    val member = selectedMembers.first()
                    AssignFieldAvatarTrigger(
                        member = member,
                        infoOpen = infoMemberId == member.id,
                        onAvatarClick = {
                            infoMemberId = if (infoMemberId == member.id) null else member.id
                        },
                        onDismissInfo = { infoMemberId = null },
                    )
                } else {
                    OrbitAvatarGroup(
                        members = selectedMembers.map {
                            OrbitAvatarGroupMember(name = it.name, painter = it.avatar)
                        },
                        size = OrbitAvatarSize.Sm,
                        max = avatarStackMax,
                        background = control.cardContainer,
                        expanded = stackExpanded,
                        onToggle = { stackExpanded = !stackExpanded },
                        onMemberClick = { index, _ ->
                            val id = selectedMembers.getOrNull(index)?.id
                            infoMemberId = if (infoMemberId == id) null else id
                        },
                        memberOverlay = { index, _ ->
                            val member = selectedMembers.getOrNull(index) ?: return@OrbitAvatarGroup
                            OrbitInfoPopover(
                                expanded = infoMemberId == member.id,
                                onDismiss = { infoMemberId = null },
                                fields = assignInfoFields(member),
                            )
                        },
                    )
                }
            }

            OrbitGlyph(
                icon = OrbitIcons.UserAdd,
                size = glyph,
                tint = content.iconInactive.copy(if (enabled) 1f else OrbitAlpha.Disabled),
                contentDescription = null,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }

        OrbitDropdownMenu(
            expanded = expanded,
            onDismiss = ::close,
            width = anchorWidth,
            header = {
                OrbitDropdownHeader(
                    query = query,
                    onQueryChange = { query = it },
                    searchPlaceholder = searchPlaceholder,
                )
            },
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = spacing.sm,
                    vertical = spacing.xs,
                ),
            ) {
                if (visible.isEmpty()) {
                    Text(
                        text = "No matches",
                        style = OrbitTheme.typography.bodyMedium,
                        color = content.textSecondary,
                        modifier = Modifier.padding(
                            horizontal = spacing.sm,
                            vertical = spacing.sm,
                        ),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        visible.forEach { member ->
                            val picked = member.id in selectedIds
                            AssignDropdownRow(
                                member = member,
                                selected = picked,
                                onClick = { onToggle(member.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignFieldAvatarTrigger(
    member: OrbitAssignMember,
    infoOpen: Boolean,
    onAvatarClick: () -> Unit,
    onDismissInfo: () -> Unit,
) {
    val spacing = OrbitTheme.spacing

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Box {
            OrbitAvatar(
                contentDescription = member.name,
                painter = member.avatar,
                initials = member.monogram,
                size = OrbitAvatarSize.Sm,
                onClick = onAvatarClick,
            )
            OrbitInfoPopover(
                expanded = infoOpen,
                onDismiss = onDismissInfo,
                fields = assignInfoFields(member),
            )
        }
    }
}

@Composable
private fun AssignDropdownRow(
    member: OrbitAssignMember,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = spacing.sm, vertical = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitAvatar(
            contentDescription = null,
            painter = member.avatar,
            initials = member.monogram,
            size = OrbitAvatarSize.Sm,
            modifier = Modifier.clearAndSetSemantics {},
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = spacing.xs),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = orbitAssignDisplayName(member.name),
                style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = member.mobile,
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (selected) {
            OrbitGlyph(
                icon = OrbitIcons.Tick,
                size = sizing.iconSm,
                tint = if (selected) content.iconPrimary else content.iconPrimary,
                contentDescription = "Selected",
            )
        }
    }
}

private fun assignInfoFields(member: OrbitAssignMember): List<OrbitInfoField> = buildList {
    add(OrbitInfoField("Name", orbitAssignDisplayName(member.name)))
    add(OrbitInfoField("Mobile number", member.mobile, copyable = true))
}
