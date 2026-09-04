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
 * Bubble title is **Account** with the close control. Body uses quiet uppercase field labels and
 * normal-case values. Theme row and red sign-out sit below rules. Same shell as [OrbitInfoPopover].
 */
@Composable
fun OrbitAccountPopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    name: String,
    role: String,
    phone: String,
    tenancy: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Account",
    tenancyLabel: String = "Organisation",
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

    OrbitBubblePopover(
        expanded = expanded,
        onDismiss = onDismiss,
        title = title,
        minWidth = minWidth,
        maxWidth = maxWidth,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            AccountField(label = "Name", value = name)
            AccountField(label = "Mobile", value = phone)
            AccountField(label = "Role", value = role)
            AccountField(label = tenancyLabel, value = tenancy)
        }

        if (themeDark != null && onThemeChange != null) {
            OrbitDivider(
                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                color = OrbitTheme.controlColors.dividerElevated,
            )
            val mode = if (themeDark) "Dark" else "Light"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md)
                    .heightIn(min = sizing.iconButtonSm)
                    .semantics(mergeDescendants = true) { contentDescription = "$mode theme" },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                OrbitGlyph(
                    icon = if (themeDark) OrbitIcons.Moon else OrbitIcons.Sun,
                    size = AccountGlyphSize,
                    tint = content.iconPrimary,
                    contentDescription = null,
                )
                Text(
                    text = mode,
                    style = OrbitTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = content.textPrimary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                OrbitSwitch(
                    checked = themeDark,
                    onCheckedChange = onThemeChange,
                    contentDescription = "$mode theme",
                )
            }
        }

        OrbitDivider(
            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
            color = OrbitTheme.controlColors.dividerElevated,
        )

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
                .heightIn(min = sizing.iconButtonSm)
                .padding(horizontal = spacing.md)
                .padding(bottom = spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitGlyph(
                icon = OrbitIcons.Logout,
                size = AccountGlyphSize,
                tint = danger,
                contentDescription = null,
            )
            Text(
                text = signOutLabel,
                style = OrbitTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = danger,
            )
        }
    }
}

@Composable
private fun AccountField(
    label: String,
    value: String,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val caption = OrbitTheme.extendedTypography.reference

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "$label, $value"
            },
        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
    ) {
        Text(
            text = label.uppercase(),
            style = caption,
            color = content.textTertiary,
            maxLines = 1,
        )
        Text(
            text = value,
            style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = content.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val AccountGlyphSize = 18.dp
private const val AccountWidthRatio = 1.3f

