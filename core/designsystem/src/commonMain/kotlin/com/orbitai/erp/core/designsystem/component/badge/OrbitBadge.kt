package com.orbitai.erp.core.designsystem.component.badge

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors

/** How loudly a badge asserts itself. */
enum class OrbitBadgeEmphasis {
    /**
     * Translucent tinted fill under a hairline edge. The default, and the only one that should
     * appear more than a couple of times on a screen.
     */
    Glass,

    /**
     * Opaque saturated fill. Reserve it for the one state on a screen that must not be skimmed
     * past — Blocked, Overdue, Critical. Used more freely it stops meaning anything.
     */
    Solid,

    /** Edge only, no fill. For dense tables where a tint on every row turns the screen to confetti. */
    Outline,
}

enum class OrbitBadgeSize { Small, Medium, Large }

/**
 * A non-interactive status pill: full-radius, optional leading glyph, tinted per [tone].
 *
 * Three things about this component are load-bearing rather than cosmetic.
 *
 * **It takes a tone, not a domain value.** `:core:designsystem` cannot import `WorkStatus` without
 * becoming unreusable, so the enum-to-tone mapping lives in `:shared`. See
 * `ui/status/StatusTokens.kt` for the domain side and `ui/component/badge/` for ready-made
 * wrappers.
 *
 * **The glass effect is translucency, not blur**, and it is shared with buttons — see
 * `Modifier.orbitGlass` for how the three layers stack and why there is no backdrop blur.
 *
 * The contrast consequence of a see-through fill is handled rather than ignored. Because the badge
 * shows what is behind it, every shade in [OrbitBadgeTone] was tuned against the whole glass stack
 * composited over the *most elevated* surface in its theme — the least favourable background a
 * badge can land on — sampled down the pill's height rather than at its edges, since the sheen and
 * the highlight brighten it from opposite directions. `BadgeContrastTest` re-derives it per build.
 *
 * **The height is a floor, never a fixed value.** [OrbitBadgeSize] sets `heightIn(min = ...)` and
 * the label is not capped to one line, so at 200% font scale the pill grows and the text wraps
 * instead of being clipped (WCAG 1.4.4). Keep labels to a word or two and this never shows.
 *
 * @param icon decorative. It is drawn without a content description on purpose: [label] already
 *   states the status, and a screen reader announcing "clock icon, Pending" is noise. The colour is
 *   a deliberately different shade from the label so the pill has an internal hierarchy.
 */
@Composable
fun OrbitBadge(
    label: String,
    modifier: Modifier = Modifier,
    tone: OrbitBadgeTone = OrbitBadgeTone.Slate,
    icon: ImageVector? = null,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
    emphasis: OrbitBadgeEmphasis = OrbitBadgeEmphasis.Glass,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val colors = tone.colors

    val minHeight = when (size) {
        OrbitBadgeSize.Small -> sizing.badgeHeightSm
        OrbitBadgeSize.Medium -> sizing.badgeHeightMd
        OrbitBadgeSize.Large -> sizing.badgeHeightLg
    }
    val iconSize = when (size) {
        OrbitBadgeSize.Small -> sizing.badgeIconSm
        OrbitBadgeSize.Medium -> sizing.badgeIconMd
        OrbitBadgeSize.Large -> sizing.badgeIconLg
    }
    val horizontalPadding = when (size) {
        OrbitBadgeSize.Small -> spacing.sm
        OrbitBadgeSize.Medium -> spacing.md
        OrbitBadgeSize.Large -> spacing.md
    }
    val textStyle = when (size) {
        OrbitBadgeSize.Small -> OrbitTheme.typography.labelMedium
        OrbitBadgeSize.Medium -> OrbitTheme.typography.labelLarge
        OrbitBadgeSize.Large -> OrbitTheme.typography.titleSmall
    }

    val labelColor = when (emphasis) {
        OrbitBadgeEmphasis.Solid -> colors.onSolidContainer
        else -> colors.label
    }
    val iconColor = when (emphasis) {
        OrbitBadgeEmphasis.Solid -> colors.onSolidContainer
        else -> colors.icon
    }

    val surface: Modifier = when (emphasis) {
        OrbitBadgeEmphasis.Glass -> Modifier.orbitGlass(
            fill = colors.container,
            shape = CircleShape,
            highlightAlpha = if (OrbitTheme.isDark) {
                OrbitGlass.BadgeHighlightDark
            } else {
                OrbitGlass.BadgeHighlightLight
            },
            edge = colors.border,
            edgeWidth = sizing.hairline,
        )
        // Opaque, so there is nothing behind it to show through and no sheen to apply; the only
        // glass cue left is a faint highlight to stop it looking like a flat sticker.
        OrbitBadgeEmphasis.Solid -> Modifier.orbitGlass(
            fill = colors.solidContainer,
            shape = CircleShape,
            highlightAlpha = OrbitGlass.ButtonHighlightDark,
            sheen = 1f,
        )
        OrbitBadgeEmphasis.Outline -> Modifier.border(
            width = sizing.border,
            color = colors.label,
            shape = CircleShape,
        )
    }

    Row(
        modifier = modifier
            .heightIn(min = minHeight)
            // Before the surface, so the shadow lands under the fill rather than on top of it. The
            // Outline emphasis is skipped: an outlined badge has no pane to cast a shadow, and giving
            // one to a bare ring makes it look like a hole in the card.
            .then(
                if (emphasis == OrbitBadgeEmphasis.Outline) {
                    Modifier
                } else {
                    Modifier.orbitGlassShadow(
                        shape = CircleShape,
                        elevation = sizing.shadowBadge,
                    )
                },
            )
            .then(surface)
            .padding(horizontal = horizontalPadding, vertical = spacing.xs)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
        Text(text = label, style = textStyle, color = labelColor)
    }
}
