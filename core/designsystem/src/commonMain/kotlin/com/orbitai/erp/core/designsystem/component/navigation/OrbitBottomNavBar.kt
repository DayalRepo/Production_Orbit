package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbitai.erp.core.designsystem.component.brand.OrbitNavBrandMark
import com.orbitai.erp.core.designsystem.component.display.OrbitCountBadge
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One destination on an [OrbitBottomNavBar].
 *
 * Solid icon + small uppercase [label]. Active slot draws a full circle behind icon+label.
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
    val activeCircle: Dp,
    val horizontalPadding: Dp,
    val labelGap: Dp,
    val slotPadding: Dp,
    val touchTarget: Dp,
)

/**
 * Full-width five-column metrics. Active circle nearly fills each column; glyphs sit inside it.
 */
fun orbitBottomNavMetrics(
    availableWidth: Dp,
    sizing: OrbitSizing,
    minTouchTarget: Dp = sizing.minTouchTarget,
): OrbitBottomNavMetrics {
    val edge = sizing.bottomNavEdgeInset
    val usable = (availableWidth - edge * 2).coerceAtLeast(280.dp)
    val column = usable / 5f
    val activeCircle = (column.value * 0.90f).dp.coerceIn(56.dp, 72.dp)
    val glyph = (activeCircle.value * 0.40f).dp.coerceIn(22.dp, 28.dp)
    val labelGap = sizing.bottomNavLabelGap
    val height = (activeCircle.value + 6f).dp.coerceIn(62.dp, 78.dp)
    return OrbitBottomNavMetrics(
        height = height,
        glyph = glyph,
        activeCircle = activeCircle,
        horizontalPadding = edge,
        labelGap = labelGap,
        slotPadding = 2.dp,
        touchTarget = minTouchTarget,
    )
}

/**
 * Full-width role bottom navigation: five equal slots, AI centered.
 * Active destination gets a full circle covering its icon and label.
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                NavSlot(
                    item = item,
                    selected = item.id == selectedId,
                    onClick = { onSelect(item.id) },
                    glyphSize = metrics.glyph,
                    activeCircle = metrics.activeCircle,
                    labelGap = metrics.labelGap,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = metrics.height)
                        .fillMaxWidth()
                        .padding(horizontal = metrics.slotPadding),
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
    activeCircle: Dp,
    labelGap: Dp,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    val controls = OrbitTheme.controlColors
    val interaction = remember(item.id) { MutableInteractionSource() }

    // Neutral light/dark ink — no brand blue. Active sits on a theme surface circle.
    val tint = if (selected) content.iconPrimary else content.iconInactive

    val labelStyle = OrbitTheme.typography.labelSmall.copy(
        fontSize = 8.sp,
        lineHeight = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
    )

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
        Box(
            modifier = Modifier
                .size(activeCircle)
                .then(
                    if (selected) {
                        Modifier.background(controls.interactiveContainer, CircleShape)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
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
                Spacer(modifier = Modifier.height(labelGap))
                Text(
                    text = item.label.uppercase(),
                    style = labelStyle,
                    color = tint,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
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
