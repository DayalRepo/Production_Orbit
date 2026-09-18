package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadge
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.brand.OrbitMark
import com.orbitai.erp.core.designsystem.component.brand.OrbitMarkDefaults
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.markdown.OrbitMarkdown
import com.orbitai.erp.core.designsystem.component.markdown.orbitMarkdownPlainText
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Glass AI briefing card: Orbit mark, Cyan AI badge, [OrbitMarkdown] body, copy on expand.
 */
@Composable
fun OrbitAiInsightCard(
    headline: String,
    markdown: String,
    modifier: Modifier = Modifier,
    title: String = "AI today's brief",
    badgeLabel: String = "AI",
    badgeTone: OrbitBadgeTone = OrbitBadgeTone.Cyan,
    advice: String? = null,
    updatedLabel: String = "Updated just now",
    collapsedMaxLines: Int = 4,
    expandable: Boolean = true,
    glassBoost: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val markColor = OrbitMarkDefaults.color()
    val plain = remember(markdown) { orbitMarkdownPlainText(markdown) }
    val preview = advice?.takeIf { it.isNotBlank() } ?: plain
    var expanded by remember(markdown) { mutableStateOf(false) }
    var overflows by remember(markdown, collapsedMaxLines) { mutableStateOf(false) }

    val spoken = contentDescription ?: buildString {
        append(title)
        append(", ")
        append(headline)
        append(", ")
        append(plain)
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        onClick = if (expanded) null else onClick,
        contentDescription = spoken,
        glassBoost = glassBoost,
        shadowLevel = if (glassBoost) OrbitShadow.Level2 else OrbitShadow.Level1,
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                OrbitMark(
                    size = 28.dp,
                    color = markColor,
                    contentDescription = "Orbit AI",
                )
                Text(
                    text = title.uppercase(),
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = content.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                OrbitBadge(
                    label = badgeLabel,
                    tone = badgeTone,
                    size = OrbitBadgeSize.Compact,
                    emphasis = OrbitBadgeEmphasis.Glass,
                )
            }

            Spacer(Modifier.height(spacing.sm))

            Text(
                text = headline,
                style = OrbitTheme.typography.titleMedium,
                color = content.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(spacing.sm))

            if (expanded) {
                OrbitMarkdown(
                    source = markdown,
                    centered = false,
                )
                Spacer(Modifier.height(spacing.md))
                OrbitDivider(color = control.controlBorder)
                Spacer(Modifier.height(spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = updatedLabel,
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Text(
                        text = "·",
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                    )
                    OrbitCopyButton(
                        value = markdown,
                        label = "Brief",
                        size = OrbitIconButtonSize.Small,
                    )
                }
                Spacer(Modifier.height(spacing.sm))
                Text(
                    text = "Show less",
                    style = OrbitTheme.typography.labelMedium.copy(
                        fontWeight = OrbitTheme.fontWeights.title,
                    ),
                    color = content.iconAccent,
                    modifier = Modifier
                        .orbitHandCursor()
                        .clickable(role = Role.Button) { expanded = false }
                        .semantics { this.contentDescription = "Show less AI briefing" },
                )
            } else {
                Text(
                    text = preview,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textSecondary,
                    maxLines = collapsedMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { layout ->
                        overflows = layout.hasVisualOverflow
                    },
                )
                if (expandable && (overflows || markdown.isNotBlank())) {
                    Spacer(Modifier.height(spacing.sm))
                    Text(
                        text = "Show more",
                        style = OrbitTheme.typography.labelMedium.copy(
                            fontWeight = OrbitTheme.fontWeights.title,
                        ),
                        color = content.iconAccent,
                        modifier = Modifier
                            .orbitHandCursor()
                            .clickable(role = Role.Button) { expanded = true }
                            .semantics { this.contentDescription = "Show more AI briefing" },
                    )
                }
            }
        }
    }
}
