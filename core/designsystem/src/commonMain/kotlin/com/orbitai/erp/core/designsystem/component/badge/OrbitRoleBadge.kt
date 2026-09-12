package com.orbitai.erp.core.designsystem.component.badge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Container + label colours for [OrbitRoleBadge].
 *
 * Opposite-direction pair so the chip stays high-contrast on both themes: charcoal black fill with
 * white type on light, off-white fill with charcoal type on dark. Fills are slightly translucent so
 * [orbitGlass] can sheen and rim them like other glass chips.
 */
@Immutable
data class OrbitRoleBadgeColors(
    val container: Color,
    val label: Color,
    val edge: Color,
)

object OrbitRoleBadgeDefaults {
    /**
     * Light: charcoal glass `#1C1C1E` + white type.
     * Dark: off-white glass `#F2F2F7` + charcoal type.
     */
    val colors: OrbitRoleBadgeColors
        @Composable
        @ReadOnlyComposable
        get() = if (OrbitTheme.isDark) {
            OrbitRoleBadgeColors(
                container = Color(0xE6F2F2F7),
                label = Color(0xFF1C1C1E),
                edge = Color(0x8CFFFFFF),
            )
        } else {
            OrbitRoleBadgeColors(
                container = Color(0xE61C1C1E),
                label = Color(0xFFFFFFFF),
                edge = Color(0x8CFFFFFF),
            )
        }

    /** Full pill, matching status badges. */
    val shape
        @Composable
        @ReadOnlyComposable
        get() = OrbitTheme.shapeTokens.roleBadge
}

/**
 * Capital role short form (CEO, PM, SE, CONTR, …) in a rounded rectangle chip.
 *
 * Deliberately not [OrbitBadge]: status chips are tinted by tone; this chip is a
 * black/white identity mark with the same pill, glass sheen and badge shadow.
 */
@Composable
fun OrbitRoleBadge(
    label: String,
    modifier: Modifier = Modifier,
    colors: OrbitRoleBadgeColors = OrbitRoleBadgeDefaults.colors,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val shape = OrbitRoleBadgeDefaults.shape
    val highlight = if (OrbitTheme.isDark) {
        OrbitGlass.BadgeHighlightDark
    } else {
        OrbitGlass.BadgeHighlightLight
    }

    Box(
        modifier = modifier
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .orbitGlass(
                fill = colors.container,
                shape = shape,
                highlightAlpha = highlight,
                edge = colors.edge,
                edgeWidth = sizing.hairline,
            )
            .heightIn(min = sizing.badgeHeightXs)
            .padding(horizontal = spacing.sm, vertical = spacing.xxs)
            .semantics { contentDescription = "Role, $label" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.labelMedium.copy(fontWeight = OrbitTheme.fontWeights.title),
            color = colors.label,
            maxLines = 1,
        )
    }
}
