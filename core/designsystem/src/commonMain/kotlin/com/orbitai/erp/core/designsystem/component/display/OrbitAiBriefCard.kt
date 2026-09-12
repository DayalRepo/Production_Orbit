package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.brand.OrbitMark
import com.orbitai.erp.core.designsystem.component.brand.OrbitMarkDefaults
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.markdown.OrbitMarkdown
import com.orbitai.erp.core.designsystem.component.markdown.orbitMarkdownPlainText
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Optional action row for an AI brief. Kept for callers that still pass a list; the card no longer
 * renders actions — use a dedicated surface if actions return later.
 */
@Immutable
data class OrbitAiBriefAction(
    val label: String,
    val onClick: (() -> Unit)? = null,
)

/**
 * Freestyle AI brief (no glass card container): `[Orbit mark] [markdown…]` with show more / show less.
 *
 * Expanded footer: divider → `Updated … ·` copy → Show less. Collapsed: Show more only (no copy).
 * Close sits at the top-right while expanded and collapses the open brief.
 *
 * Markdown: `#`/`##`/`###`, bullets, `**bold**`, `*italic*`, `__underline__`, `~~strike~~`, tables.
 */
@Composable
fun OrbitAiBriefCard(
    markdown: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 4,
    updatedLabel: String = "Updated 21m ago",
    @Suppress("UNUSED_PARAMETER")
    actions: List<OrbitAiBriefAction> = emptyList(),
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val linkInk = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50
    val markColor = OrbitMarkDefaults.color()
    val plain = remember(markdown) { orbitMarkdownPlainText(markdown) }
    val bodyStyle = OrbitTheme.typography.bodyMedium.copy(
        fontWeight = OrbitTheme.fontWeights.title,
    )

    var textExpanded by remember(markdown) { mutableStateOf(false) }
    var overflows by remember(markdown, collapsedMaxLines) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = plain },
    ) {
        if (textExpanded) {
            OrbitIconButton(
                contentDescription = "Close brief",
                onClick = { textExpanded = false },
                icon = OrbitIcons.Cancel,
                style = OrbitIconButtonStyle.Neutral,
                size = OrbitIconButtonSize.Small,
                ringed = false,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = spacing.xs, y = (-spacing.xs)),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = if (textExpanded) spacing.xxl else 0.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitMark(
                size = 28.dp,
                color = markColor,
                contentDescription = "Orbit AI",
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                if (textExpanded) {
                    OrbitMarkdown(
                        source = markdown,
                        centered = false,
                    )
                } else {
                    Text(
                        text = plain,
                        style = bodyStyle,
                        color = content.textSecondary,
                        maxLines = collapsedMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { layout ->
                            overflows = layout.hasVisualOverflow
                        },
                    )
                }

                if (textExpanded) {
                    OrbitDivider(color = control.controlBorder)
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
                    Text(
                        text = "Show less",
                        style = OrbitTheme.typography.labelMedium.copy(
                            fontWeight = OrbitTheme.fontWeights.title,
                        ),
                        color = linkInk,
                        modifier = Modifier
                            .orbitHandCursor()
                            .clickable(role = Role.Button) { textExpanded = false }
                            .semantics { contentDescription = "Show less brief" },
                    )
                } else if (overflows) {
                    Text(
                        text = "Show more",
                        style = OrbitTheme.typography.labelMedium.copy(
                            fontWeight = OrbitTheme.fontWeights.title,
                        ),
                        color = linkInk,
                        modifier = Modifier
                            .orbitHandCursor()
                            .clickable(role = Role.Button) { textExpanded = true }
                            .semantics { contentDescription = "Show more brief" },
                    )
                }
            }
        }
    }
}
