package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/** What the description AI assist can do with the current text. */
enum class OrbitDescriptionAiAction {
    /** Expand terse notes into a fuller handover-style description. */
    RewriteDetail,

    /** Translate the description into another language. */
    Translate,
}

/**
 * The AI assist menu as an elevated dropdown card below the description field.
 *
 * Matching [OrbitDropdownMenu] width and placement so rewrite / translate read as a floating panel
 * on top of the form — the same layer as a stage list — not content inside the text box.
 */
@Composable
fun OrbitDescriptionAiMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRewrite: () -> Unit,
    onTranslate: () -> Unit,
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors

    OrbitDropdownMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        width = width,
        modifier = modifier,
        shape = RectangleShape,
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.sm, vertical = spacing.xxs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ORBIT AI",
                    style = OrbitTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OrbitTheme.contentColors.textPrimary,
                )
                OrbitIconButton(
                    contentDescription = "Close AI menu",
                    onClick = onDismiss,
                    icon = OrbitIcons.Cancel,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Small,
                    ringed = false,
                )
            }
            OrbitDivider(color = control.controlBorder)
        },
    ) {
        AiMenuRow(
            label = "Rewrite in detail",
            description = "Expand notes into a fuller description",
            icon = OrbitIcons.AiGenerate,
            onClick = {
                onDismiss()
                onRewrite()
            },
        )
        OrbitDivider(color = control.controlBorder)
        AiMenuRow(
            label = "Translate",
            description = "Translate into an Indian language",
            icon = OrbitIcons.AiTranslate,
            onClick = {
                onDismiss()
                onTranslate()
            },
        )
    }
}

@Composable
private fun AiMenuRow(
    label: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .indication(interactionSource, orbitPressIndication())
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrbitGlyph(
            icon = icon,
            size = OrbitTheme.sizing.iconSm,
            tint = content.iconPrimary,
            contentDescription = null,
        )
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Text(
                text = label,
                style = OrbitTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = content.textPrimary,
            )
            Text(
                text = description,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
            )
        }
    }
}
