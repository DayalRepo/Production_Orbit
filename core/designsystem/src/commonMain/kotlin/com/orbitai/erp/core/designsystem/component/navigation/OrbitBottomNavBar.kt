package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.brand.OrbitNavBrandMark
import com.orbitai.erp.core.designsystem.component.display.OrbitCountBadge
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One destination on an [OrbitBottomNavBar].
 *
 * Icons only — [label] is spoken / semantic, not drawn. Selection is colour only.
 * Set [emphasized] on the center AI slot (nav brand mark).
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
    val horizontalPadding: Dp,
    val touchTarget: Dp,
)

/**
 * Full-width five equal columns. Glyphs scale with column width and leave even gaps.
 */
fun orbitBottomNavMetrics(
    availableWidth: Dp,
    sizing: OrbitSizing,
    minTouchTarget: Dp = sizing.minTouchTarget,
): OrbitBottomNavMetrics {
    val edge = sizing.bottomNavEdgeInset
    val usable = (availableWidth - edge * 2).coerceAtLeast(280.dp)
    val column = usable / 5f
    // ~52% of column — large icons with even air between the five slots.
    val glyph = (column.value * 0.52f).dp.coerceIn(28.dp, 36.dp)
    val height = maxOf(minTouchTarget, glyph + 16.dp).coerceIn(56.dp, 72.dp)
    return OrbitBottomNavMetrics(
        height = height,
        glyph = glyph,
        horizontalPadding = edge,
        touchTarget = minTouchTarget,
    )
}

/**
 * Full-width role bottom navigation: five icon slots, AI centered.
 * No labels, no active circle — selection is colour only.
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
        "Center item (index 2) must be the Orbit.ai slot"
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                NavSlot(
                    item = item,
                    selected = item.id == selectedId,
                    onClick = { onSelect(item.id) },
                    glyphSize = metrics.glyph,
                    touchTarget = metrics.touchTarget,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = metrics.height)
                        .fillMaxWidth(),
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
    touchTarget: Dp,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    val interaction = remember(item.id) { MutableInteractionSource() }
    val tint = if (selected) content.iconPrimary else content.iconInactive

    Box(
        modifier = modifier
            .orbitHandCursor()
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = item.contentDescription
                this.selected = selected
            },
        contentAlignment = Alignment.Center,
    ) {
        val showBadge = item.badgeCount > 0
        Box(
            modifier = Modifier.size(maxOf(touchTarget * 0.7f, glyphSize + 8.dp)),
            contentAlignment = Alignment.Center,
        ) {
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
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(glyphSize),
                    )
                }
                if (showBadge) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        OrbitCountBadge(
                            count = item.badgeCount,
                            label = item.badgeLabel,
                            compact = true,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp),
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
