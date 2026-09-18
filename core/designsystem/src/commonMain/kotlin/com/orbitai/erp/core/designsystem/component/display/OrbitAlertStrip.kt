package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadge
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Compact alert / queue KPI: severity badge, hero count, one-line action hint.
 *
 * Used for open issues, low stock, pending POs, critical defects — anything that needs a count
 * and a next step without a full chart.
 */
@Composable
fun OrbitAlertStrip(
    title: String,
    count: String,
    message: String,
    modifier: Modifier = Modifier,
    tone: OrbitBadgeTone = OrbitBadgeTone.Amber,
    badgeLabel: String? = null,
    icon: ImageVector? = OrbitIcons.BadgeAlert,
    supporting: String? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val spoken = contentDescription ?: buildString {
        append(title)
        append(", ")
        append(count)
        append(", ")
        append(message)
        if (supporting != null) {
            append(", ")
            append(supporting)
        }
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        onClick = onClick,
        contentDescription = spoken,
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                if (icon != null) {
                    OrbitKpiIconChip(icon = icon, tone = tone)
                }
                Text(
                    text = title.uppercase(),
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = content.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (badgeLabel != null) {
                    OrbitBadge(
                        label = badgeLabel,
                        tone = tone,
                        size = OrbitBadgeSize.Compact,
                        emphasis = OrbitBadgeEmphasis.Glass,
                    )
                }
            }

            Spacer(Modifier.height(spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = count,
                    style = OrbitTheme.extendedTypography.metricLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (showChevron && onClick != null) {
                    OrbitGlyph(
                        icon = OrbitIcons.ArrowRight,
                        size = sizing.iconSm,
                        tint = content.iconPrimary,
                        minimumStroke = sizing.iconStrokeLight,
                        contentDescription = null,
                    )
                }
            }

            Spacer(Modifier.height(spacing.xxs))
            Text(
                text = message,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (supporting != null) {
                Spacer(Modifier.height(spacing.xxs))
                Text(
                    text = supporting,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
