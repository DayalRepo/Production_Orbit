package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One page / section on an [OrbitTabBar].
 *
 * @param id stable key for selection callbacks.
 * @param label drawn text — keep it short; the underline is sized to the label width.
 */
@Immutable
data class OrbitTab(
    val id: String,
    val label: String,
)

/**
 * Underline tab bar — also called a **pages bar** or section tabs.
 *
 * Active label is theme primary ink (near-black / near-white); inactive is muted tertiary grey.
 * Weight and size stay the same — only colour changes. A thinner glass underline sits inset under
 * the active label, on the shared hairline track.
 *
 * Horizontal edge inset matches [OrbitSizing.tabBarEdgeInset] / bottom-nav edge so the tab row and
 * floating nav share one column grid on Android and iOS. Label hit height uses the platform
 * [OrbitSizing.minTouchTarget] (48dp Android / 44pt iOS).
 */
@Composable
fun OrbitTabBar(
    tabs: List<OrbitTab>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (tabs.isEmpty()) return

    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val density = LocalDensity.current
    val scroll = rememberScrollState()
    val underlineShape = RoundedCornerShape(sizing.progressSegmentRadius)
    val edgeInset = sizing.tabBarEdgeInset
    val itemGap = sizing.tabBarItemGap
    val rowMinHeight = maxOf(sizing.tabBarMinHeight, sizing.minTouchTarget)

    val selected = tabs.firstOrNull { it.id == selectedId } ?: tabs.first()
    val widths = remember { mutableStateMapOf<String, Dp>() }
    val offsets = remember { mutableStateMapOf<String, Dp>() }

    val indicatorWidth by animateDpAsState(
        targetValue = widths[selected.id] ?: 0.dp,
        animationSpec = tween(IndicatorMs),
        label = "orbit-tab-indicator-width",
    )
    val indicatorOffset by animateDpAsState(
        targetValue = offsets[selected.id] ?: 0.dp,
        animationSpec = tween(IndicatorMs),
        label = "orbit-tab-indicator-offset",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Pages, ${selected.label} selected"
            },
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = edgeInset)
                    .horizontalScroll(scroll)
                    .padding(bottom = spacing.sm)
                    .heightIn(min = rowMinHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selectedTab = tab.id == selected.id
                    val interaction = remember(tab.id) { MutableInteractionSource() }
                    Text(
                        text = tab.label,
                        style = OrbitTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                        color = if (selectedTab) content.textPrimary else content.textTertiary,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(end = if (index < tabs.lastIndex) itemGap else spacing.none)
                            .heightIn(min = rowMinHeight)
                            .onGloballyPositioned { coords ->
                                widths[tab.id] = with(density) { coords.size.width.toDp() }
                                val x = coords.positionInParent().x
                                offsets[tab.id] = with(density) { x.toDp() }
                            }
                            .orbitHandCursor()
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                role = Role.Tab,
                                onClick = { onSelect(tab.id) },
                            )
                            .semantics { contentDescription = tab.label },
                    )
                }
            }

            OrbitDivider(
                modifier = Modifier.align(Alignment.BottomStart),
                color = control.controlBorder,
            )
            val scrolled = with(density) { scroll.value.toDp() }
            val insetWidth = (indicatorWidth - TabUnderlineInset * 2).coerceAtLeast(0.dp)
            if (insetWidth > 0.dp) {
                // Theme ink (near-black / near-white), inset inside the label width on the hairline.
                val underlineFill = control.controlContent
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = indicatorOffset - scrolled + edgeInset + TabUnderlineInset)
                        .width(insetWidth)
                        .height(TabUnderlineHeight)
                        .orbitGlassShadow(shape = underlineShape, elevation = sizing.shadowButton)
                        .clip(underlineShape)
                        .orbitGlass(
                            fill = underlineFill,
                            shape = underlineShape,
                            highlightAlpha = if (dark) {
                                OrbitGlass.SurfaceHighlightDark
                            } else {
                                OrbitGlass.SurfaceHighlightLight
                            },
                            edge = control.controlBorder,
                            edgeWidth = sizing.hairline,
                            sheen = if (dark) 1f else OrbitGlass.Sheen,
                        ),
                )
            }
        }
    }
}

private const val IndicatorMs = 180
private val TabUnderlineHeight = 2.5.dp
private val TabUnderlineInset = 4.dp
