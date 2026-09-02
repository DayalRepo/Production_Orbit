package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The header above a month grid: back, a month picker, a year picker, forward.
 *
 * Month and year open **inside** the calendar panel — an inline grid below the header row — rather
 * than in a popup that floats over the rest of the app. That keeps the picker contained within the
 * glass calendar shell the user already has open.
 */
@Composable
internal fun OrbitMonthNavigator(
    month: OrbitYearMonth,
    bounds: OrbitCalendarBounds,
    onMonthChange: (OrbitYearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors

    var openPanel by remember { mutableStateOf<PickerPanel?>(null) }

    val canGoBack = !bounds.isEntirelyPast(month.plusMonths(-1))
    val canGoForward = !bounds.isBeyondEnd(month.plusMonths(1))

    val years = remember(bounds) {
        (bounds.today.year..bounds.lastSelectableYear).toList()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            OrbitIconButton(
                icon = OrbitIcons.ArrowRight,
                contentDescription = "Previous month",
                onClick = {
                    openPanel = null
                    onMonthChange(month.plusMonths(-1))
                },
                state = if (canGoBack) OrbitButtonState.Active else OrbitButtonState.Disabled,
                size = OrbitIconButtonSize.Small,
                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
            )

            MonthYearTrigger(
                label = OrbitMonthNames.full(month.month),
                unitLabel = "Month",
                expanded = openPanel == PickerPanel.Month,
                onClick = {
                    openPanel = if (openPanel == PickerPanel.Month) null else PickerPanel.Month
                },
                modifier = Modifier.weight(MonthWeight),
            )

            MonthYearTrigger(
                label = month.year.toString(),
                unitLabel = "Year",
                expanded = openPanel == PickerPanel.Year,
                onClick = {
                    openPanel = if (openPanel == PickerPanel.Year) null else PickerPanel.Year
                },
                modifier = Modifier.weight(YearWeight),
            )

            OrbitIconButton(
                icon = OrbitIcons.ArrowRight,
                contentDescription = "Next month",
                onClick = {
                    openPanel = null
                    onMonthChange(month.plusMonths(1))
                },
                state = if (canGoForward) OrbitButtonState.Active else OrbitButtonState.Disabled,
                size = OrbitIconButtonSize.Small,
            )
        }

        AnimatedVisibility(
            visible = openPanel == PickerPanel.Month,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            InlineChoicePanel {
                ChoiceGrid(
                    options = (1..12).map { OrbitMonthNames.short(it) },
                    selectedIndex = month.month - 1,
                    enabled = { index ->
                        val candidate = OrbitYearMonth(month.year, index + 1)
                        !bounds.isEntirelyPast(candidate) && !bounds.isBeyondEnd(candidate)
                    },
                    onSelect = { index ->
                        onMonthChange(OrbitYearMonth(month.year, index + 1))
                        openPanel = null
                    },
                    columns = GridColumns,
                )
            }
        }

        AnimatedVisibility(
            visible = openPanel == PickerPanel.Year,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            InlineChoicePanel {
                ChoiceGrid(
                    options = years.map { it.toString() },
                    selectedIndex = years.indexOf(month.year).coerceAtLeast(0),
                    enabled = { true },
                    onSelect = { index ->
                        val candidate = OrbitYearMonth(years[index], month.month)
                        onMonthChange(
                            if (bounds.isEntirelyPast(candidate)) bounds.today.yearMonth else candidate,
                        )
                        openPanel = null
                    },
                    columns = GridColumns,
                    cellSpacing = YearGridSpacing,
                )
            }
        }
    }
}

@Composable
private fun MonthYearTrigger(
    label: String,
    unitLabel: String,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = RoundedCornerShape(percent = 50)
    val highlight = if (OrbitTheme.isDark) OrbitGlass.SurfaceHighlightDark else OrbitGlass.SurfaceHighlightLight
    val interactionSource = remember { MutableInteractionSource() }

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) ChevronFlipped else 0f,
        label = "orbit-monthyear-chevron",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .semantics(mergeDescendants = true) {
                contentDescription = "$unitLabel: $label"
            }
            .clip(shape)
            .indication(interactionSource, orbitPressIndication())
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = highlight,
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
            )
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = content.textPrimary,
            maxLines = 1,
        )
        Box(modifier = Modifier.size(spacing.xxs))
        OrbitGlyph(
            icon = OrbitIcons.ChevronDown,
            size = sizing.iconSm,
            tint = content.iconInactive,
            contentDescription = null,
            modifier = Modifier.graphicsLayer { rotationZ = chevronRotation },
        )
    }
}

@Composable
private fun InlineChoicePanel(content: @Composable () -> Unit) {
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.field
    val highlight = if (OrbitTheme.isDark) OrbitGlass.SurfaceHighlightDark else OrbitGlass.SurfaceHighlightLight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = highlight,
                edge = control.controlBorder,
                edgeWidth = OrbitTheme.sizing.hairline,
            )
            .padding(vertical = spacing.sm),
    ) {
        content()
    }
}

@Composable
private fun ChoiceGrid(
    options: List<String>,
    selectedIndex: Int,
    enabled: (Int) -> Boolean,
    onSelect: (Int) -> Unit,
    columns: Int = GridColumns,
    cellSpacing: Dp = MonthGridSpacing,
) {
    val spacing = OrbitTheme.spacing

    Column(
        modifier = Modifier.padding(horizontal = spacing.md),
        verticalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        options.indices.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                row.forEach { index ->
                    OrbitChoicePill(
                        label = options[index],
                        selected = index == selectedIndex,
                        enabled = enabled(index),
                        onClick = { onSelect(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - row.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private enum class PickerPanel { Month, Year }

private const val ChevronFlipped = 180f
private const val MonthWeight = 3f
private const val YearWeight = 2f
private const val GridColumns = 3
private val MonthGridSpacing = 8.dp
private val YearGridSpacing = 10.dp
