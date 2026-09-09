package com.orbitai.erp.core.designsystem.component.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitRoleBadge
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.input.OrbitSwitch
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Account identity card behind the app-bar avatar.
 *
 * Layout: name (caps) + role short-form badge + mobile with [OrbitIcons.UserRound], then theme
 * toggle, then sign-out. Organisation / project lines live on the shell screens, not here.
 *
 * Name and phone share Medium + charcoal so the identity block reads as one tone, matching
 * [OrbitInfoPopover].
 */
@Composable
fun OrbitAccountPopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    name: String,
    role: String,
    phone: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Account",
    signOutLabel: String = "Sign out",
    themeDark: Boolean? = null,
    onThemeChange: ((Boolean) -> Unit)? = null,
    minWidth: Dp = OrbitTheme.sizing.popoverMinWidth * AccountWidthRatio,
    maxWidth: Dp = OrbitTheme.sizing.popoverMaxWidth * AccountWidthRatio,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val danger = OrbitBadgeTone.Red.colors.label
    val charcoal = content.textSecondary
    // Full uppercase name — no 18-char hard truncate; the identity block wraps to two lines.
    val displayName = name.trim().uppercase()

    OrbitBubblePopover(
        expanded = expanded,
        onDismiss = onDismiss,
        title = title,
        minWidth = minWidth,
        maxWidth = maxWidth,
        modifier = modifier,
    ) {
        AccountIdentityBlock(
            name = displayName,
            roleBadge = role,
            phone = phone,
            ink = charcoal,
            contentDescription = "Name, $displayName. Role, $role. Mobile, $phone",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
        )

        if (themeDark != null && onThemeChange != null) {
            OrbitDivider(
                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                color = OrbitTheme.controlColors.dividerElevated,
            )
            val mode = if (themeDark) "Dark" else "Light"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = sizing.minTouchTarget)
                    .padding(horizontal = spacing.md)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "$mode theme"
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                OrbitGlyph(
                    icon = if (themeDark) OrbitIcons.Moon else OrbitIcons.Sun,
                    size = AccountGlyphSize,
                    tint = content.iconPrimary,
                    contentDescription = null,
                    minimumStroke = sizing.iconStrokeHairline,
                    maximumStroke = sizing.iconStrokeHairline,
                )
                Text(
                    text = mode,
                    // Same weight/size as the account name so Light/Dark does not shout louder.
                    style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = charcoal,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                OrbitSwitch(
                    checked = themeDark,
                    onCheckedChange = onThemeChange,
                    contentDescription = "$mode theme",
                )
            }
            OrbitDivider(
                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                color = OrbitTheme.controlColors.dividerElevated,
            )
        } else {
            OrbitDivider(
                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                color = OrbitTheme.controlColors.dividerElevated,
            )
        }

        val signOutInteraction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .indication(signOutInteraction, orbitPressIndication())
                .orbitHandCursor()
                .clickable(
                    interactionSource = signOutInteraction,
                    indication = null,
                    role = Role.Button,
                ) {
                    onSignOut()
                    onDismiss()
                }
                .heightIn(min = sizing.minTouchTarget)
                .padding(horizontal = spacing.md)
                .padding(bottom = spacing.xs)
                .semantics { contentDescription = signOutLabel },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitGlyph(
                icon = OrbitIcons.Logout,
                size = AccountGlyphSize,
                tint = danger,
                contentDescription = null,
                minimumStroke = sizing.iconStrokeHairline,
                maximumStroke = sizing.iconStrokeHairline,
            )
            Text(
                text = signOutLabel,
                style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = danger,
            )
        }
    }
}

@Composable
private fun AccountIdentityBlock(
    name: String,
    roleBadge: String,
    phone: String,
    ink: androidx.compose.ui.graphics.Color,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    Row(
        modifier = modifier.semantics(mergeDescendants = true) {
            this.contentDescription = contentDescription
        },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitGlyph(
            icon = OrbitIcons.UserRound,
            size = AccountGlyphSize,
            tint = content.iconPrimary,
            contentDescription = null,
            minimumStroke = sizing.iconStrokeHairline,
            maximumStroke = sizing.iconStrokeHairline,
            modifier = Modifier.padding(top = 2.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Text(
                text = name,
                style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = ink,
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
            )
            if (roleBadge.isNotBlank()) {
                OrbitRoleBadge(label = roleBadge)
            }
            Text(
                text = phone,
                style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private val AccountGlyphSize = 20.dp
private const val AccountWidthRatio = 1.75f
