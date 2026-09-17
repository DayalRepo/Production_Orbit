package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.brand.OrbitNavBrandMark
import com.orbitai.erp.core.designsystem.component.display.OrbitCountBadge
import com.orbitai.erp.core.designsystem.foundation.WindowSize
import com.orbitai.erp.core.designsystem.foundation.orbitCircularPressIndication
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One destination on an [OrbitBottomNavBar].
 *
 * Icons only — [label] is spoken / semantic, not drawn. Selection is colour only.
 * Set [emphasized] on the center Orbit AI slot (nav brand mark).
 */
@Immutable
data class OrbitNavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String = label,
    val emphasized: Boolean = false,
    val badgeCount: Int = 0,
    val badgeLabel: String = "notifications",
)

@Immutable
data class OrbitBottomNavMetrics(
    val height: Dp,
    val glyph: Dp,
    val aiGlyph: Dp,
    val horizontalPadding: Dp,
    val iconStroke: Dp,
    val touchTarget: Dp,
)

/**
 * Scales icon size from full screen width so five equal columns fill the bar.
 * Glyphs take most of each column so spacing between icons stays even, not sparse.
 */
fun orbitBottomNavMetrics(
    availableWidth: Dp,
    sizing: OrbitSizing,
    minTouchTarget: Dp = sizing.minTouchTarget,
): OrbitBottomNavMetrics {
    val edge = sizing.bottomNavEdgeInset
    val usable = (availableWidth - edge * 2).coerceAtLeast(280.dp)
    val column = usable / 5f
    // ~44% of column — slightly smaller glyphs, more even air between slots.
    val glyph = (column.value * 0.44f).dp.coerceIn(24.dp, 30.dp)
    val aiGlyph = (glyph.value + 2f).dp.coerceAtMost(32.dp)
    val heightFloor = maxOf(minTouchTarget + 2.dp, glyph + 12.dp)
    val height = heightFloor.coerceIn(52.dp, 68.dp)
    val widthScale = when {
        availableWidth < 340.dp -> 0.96f
        availableWidth < 400.dp -> 1.00f
        availableWidth < WindowSize.MediumWidthBreakpoint -> 1.04f
        else -> 1.08f
    }
    val stroke = (sizing.bottomNavIconStroke.value * widthScale).dp.coerceIn(1.10.dp, 1.30.dp)
    return OrbitBottomNavMetrics(
        height = height,
        glyph = glyph,
        aiGlyph = aiGlyph,
        horizontalPadding = edge,
        iconStroke = stroke,
        touchTarget = minTouchTarget,
    )
}

/**
 * Full-width role bottom navigation: five icon slots, Orbit AI centered.
 * No chrome container, no labels. Active state is colour only.
 */
@Composable
fun OrbitBottomNavBar(
    items: List<OrbitNavItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
) {
    require(items.size == 5) { "OrbitBottomNavBar needs exactly five items" }
    require(items.count { it.emphasized } == 1 && items[2].emphasized) {
        "Center item (index 2) must be the Orbit AI slot"
    }

    val sizing = OrbitTheme.sizing

    val insetModifier = Modifier
        .then(
            if (applyNavigationBarInset) {
                Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            } else {
                Modifier
            },
        )
        .padding(bottom = sizing.bottomNavSystemGap)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(insetModifier)
            .semantics { contentDescription = "Navigation" },
    ) {
        val metrics = remember(maxWidth, sizing) {
            orbitBottomNavMetrics(maxWidth, sizing, sizing.minTouchTarget)
        }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(metrics.height)
                .padding(horizontal = metrics.horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                NavSlot(
                    item = item,
                    selected = item.id == selectedId,
                    onClick = { onSelect(item.id) },
                    glyphSize = if (item.emphasized) metrics.aiGlyph else metrics.glyph,
                    stroke = metrics.iconStroke,
                    touchTarget = metrics.touchTarget,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavSlot(
    item: OrbitNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    glyphSize: Dp,
    stroke: Dp,
    touchTarget: Dp,
    modifier: Modifier = Modifier,
) {
    val icons = OrbitTheme.contentColors
    val interaction = remember(item.id) { MutableInteractionSource() }
    val tint = if (selected) icons.iconPrimary else icons.iconInactive
    val hit = maxOf(touchTarget, glyphSize + 16.dp)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(hit)
                .clip(CircleShape)
                .orbitHandCursor()
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    role = Role.Tab,
                    onClick = onClick,
                )
                .indication(interaction, orbitCircularPressIndication())
                .semantics(mergeDescendants = true) {
                    contentDescription = item.contentDescription
                    this.selected = selected
                },
            contentAlignment = Alignment.Center,
        ) {
            val showBadge = item.badgeCount > 0
            Box(
                modifier = Modifier.size(glyphSize),
                contentAlignment = Alignment.Center,
            ) {
                if (item.emphasized) {
                    OrbitNavBrandMark(
                        size = glyphSize,
                        color = tint,
                        contentDescription = null,
                    )
                } else {
                    OrbitGlyph(
                        icon = item.icon,
                        size = glyphSize,
                        tint = tint,
                        minimumStroke = stroke,
                        maximumStroke = stroke,
                        contentDescription = null,
                    )
                }
                if (showBadge) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        OrbitCountBadge(
                            count = item.badgeCount,
                            label = item.badgeLabel,
                            compact = true,
                            modifier = Modifier.align(Alignment.TopEnd),
                        )
                    }
                }
            }
        }
    }
}

internal fun List<OrbitNavItem>.withNotificationBadge(
    messageId: String,
    count: Int,
): List<OrbitNavItem> = map { item ->
    if (item.id == messageId) {
        item.copy(badgeCount = count, badgeLabel = "notifications")
    } else {
        item
    }
}

internal fun List<OrbitNavItem>.withInsightBadge(
    orbitId: String,
    count: Int,
): List<OrbitNavItem> = map { item ->
    if (item.id == orbitId) {
        item.copy(badgeCount = count, badgeLabel = "insights")
    } else {
        item
    }
}
