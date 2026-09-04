package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import com.orbitai.erp.core.designsystem.foundation.WindowSize
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One destination on an [OrbitBottomNavBar].
 *
 * Icons only — no label. [contentDescription] is required so TalkBack / VoiceOver still name the
 * destination; describe the place ("Dashboard"), not the picture ("four circles").
 */
@Immutable
data class OrbitNavItem(
    val id: String,
    val icon: ImageVector,
    val contentDescription: String,
)

/**
 * Resolved sizes for a floating bottom nav at a given available width.
 *
 * Scales height, glyph and active glass disc together so phones stay compact, tablets get roomier
 * targets, and the active lens always clears the outer rim.
 */
@Immutable
internal data class OrbitBottomNavMetrics(
    val height: Dp,
    val glyph: Dp,
    val activeSize: Dp,
    val edgeInset: Dp,
    val pillInset: Dp,
    val clusterGap: Dp,
    val barMaxWidth: Dp,
)

/**
 * Maps available width to bar metrics. Pure so host tests can lock the breakpoints without Compose.
 */
internal fun orbitBottomNavMetrics(
    availableWidth: Dp,
    sizing: OrbitSizing,
): OrbitBottomNavMetrics {
    val scale = when {
        availableWidth < 340.dp -> 0.90f
        availableWidth < 400.dp -> 1.00f
        availableWidth < WindowSize.MediumWidthBreakpoint -> 1.06f
        availableWidth < WindowSize.ExpandedWidthBreakpoint -> 1.14f
        else -> 1.22f
    }
    val height = (sizing.bottomNavHeight.value * scale).dp.coerceIn(56.dp, 76.dp)
    val glyph = (sizing.bottomNavGlyph.value * scale).dp.coerceIn(24.dp, 36.dp)
    val active = (sizing.bottomNavActiveSize.value * scale).dp
        .coerceIn(48.dp, 68.dp)
        .coerceAtMost(height - 8.dp)
    val edgeInset = (sizing.bottomNavEdgeInset.value * scale).dp.coerceIn(8.dp, 20.dp)
    val pillInset = (sizing.bottomNavPillInset.value * scale).dp.coerceIn(10.dp, 24.dp)
    val clusterGap = (sizing.bottomNavClusterGap.value * scale).dp.coerceIn(8.dp, 16.dp)
    val barMaxWidth = when {
        availableWidth < WindowSize.MediumWidthBreakpoint -> availableWidth
        availableWidth < WindowSize.ExpandedWidthBreakpoint -> 560.dp
        else -> 640.dp
    }
    return OrbitBottomNavMetrics(
        height = height,
        glyph = glyph,
        activeSize = active,
        edgeInset = edgeInset,
        pillInset = pillInset,
        clusterGap = clusterGap,
        barMaxWidth = barMaxWidth,
    )
}

/**
 * Floating role bottom navigation: a full-width glass **pill** of primary destinations plus a
 * separate glass **circle** for the trailing action.
 *
 * ```
 *  [  icon  ·  icon  ·  icon  ]     ( )
 * ```
 *
 * Primary icons sit **left / middle / right** inside the pill ([Arrangement.SpaceBetween]). Width,
 * height, glyph and active glass disc scale from the available screen width so phone and tablet
 * layouts stay readable and within touch-target comfort.
 *
 * The selected destination gets a translucent glass disc behind the glyph — no press ripple. Role
 * presets such as [OrbitCeoNavBar] wire a fixed icon set into this layout.
 *
 * @param applyNavigationBarInset when true (default), pads for [WindowInsets.navigationBars] and
 *   then adds [OrbitSizing.bottomNavSystemGap] so the glass sits just above the system chrome.
 */
@Composable
fun OrbitBottomNavBar(
    primaryItems: List<OrbitNavItem>,
    actionItem: OrbitNavItem,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
) {
    require(primaryItems.isNotEmpty()) { "OrbitBottomNavBar needs at least one primary item" }

    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val pillShape = OrbitTheme.shapeTokens.button
    val highlight = if (dark) OrbitGlass.SurfaceHighlightDark else OrbitGlass.SurfaceHighlightLight

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
        val metrics = remember(maxWidth, sizing) { orbitBottomNavMetrics(maxWidth, sizing) }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = metrics.barMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = metrics.edgeInset),
            horizontalArrangement = Arrangement.spacedBy(metrics.clusterGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(metrics.height)
                    .orbitGlassShadow(
                        shape = pillShape,
                        elevation = sizing.bottomNavShadow,
                    )
                    .clip(pillShape)
                    .orbitGlass(
                        fill = control.ringContainer,
                        shape = pillShape,
                        highlightAlpha = highlight,
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                        sheen = if (dark) 1f else OrbitGlass.Sheen,
                    )
                    .padding(horizontal = metrics.pillInset),
                // Left · middle · right — first at the start edge, last at the end, middle between.
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                primaryItems.forEach { item ->
                    NavGlyph(
                        item = item,
                        selected = item.id == selectedId,
                        onClick = { onSelect(item.id) },
                        glyphSize = metrics.glyph,
                        activeSize = metrics.activeSize,
                        modifier = Modifier.size(metrics.height),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(metrics.height)
                    .orbitGlassShadow(
                        shape = CircleShape,
                        elevation = sizing.bottomNavShadow,
                    )
                    .clip(CircleShape)
                    .orbitGlass(
                        fill = control.ringContainer,
                        shape = CircleShape,
                        highlightAlpha = highlight,
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                        sheen = if (dark) 1f else OrbitGlass.Sheen,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                NavGlyph(
                    item = actionItem,
                    selected = actionItem.id == selectedId,
                    onClick = { onSelect(actionItem.id) },
                    glyphSize = metrics.glyph,
                    activeSize = metrics.activeSize,
                    modifier = Modifier.size(metrics.height),
                )
            }
        }
    }
}

@Composable
private fun NavGlyph(
    item: OrbitNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    glyphSize: Dp,
    activeSize: Dp,
    modifier: Modifier = Modifier,
) {
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val dark = OrbitTheme.isDark
    val interaction = remember(item.id) { MutableInteractionSource() }
    val tint = if (selected) content.iconPrimary else content.iconInactive
    val activeHighlight = if (dark) {
        OrbitGlass.RingHighlightDark * OrbitGlass.ButtonHoverLift
    } else {
        OrbitGlass.RingHighlightLight * OrbitGlass.ButtonHoverLift
    }

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
        if (selected) {
            Box(
                modifier = Modifier
                    .size(activeSize)
                    .clip(CircleShape)
                    .orbitGlass(
                        fill = control.ringContainer,
                        shape = CircleShape,
                        highlightAlpha = activeHighlight,
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                        sheen = if (dark) 1f else OrbitGlass.Sheen,
                    ),
            )
        }
        OrbitGlyph(
            icon = item.icon,
            size = glyphSize,
            tint = tint,
            minimumStroke = sizing.bottomNavIconStroke,
            maximumStroke = sizing.bottomNavIconStroke,
            contentDescription = null,
        )
    }
}
