package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.datetime.parseOrbitSlashedDate
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One waypoint on a vertical workflow track.
 *
 * @param label stage title. Drawn in the tabular [com.orbitai.erp.core.designsystem.theme.OrbitTypographyTokens.reference] style.
 * @param statusLabel optional subtitle under the title. Vocabulary by stage:
 *   Scheduled — "Not started" / "Started";
 *   In progress — "In progress" / "Submitted";
 *   Inspection — "Inspecting" / "Rework" / "Reviewing" / "Done";
 *   Approval — "Pending" / "Rejected" / "Rework" / "Approved";
 *   Completed — "Complete".
 * @param startedOn already-formatted start date (`dd/mm/yyyy`).
 * @param endedOn already-formatted end date. Shown after the start, separated by a mid-dot.
 */
@Immutable
data class OrbitStep(
    val label: String,
    val statusLabel: String? = null,
    val startedOn: String? = null,
    val endedOn: String? = null,
)

enum class OrbitStepPhase {
    Completed,
    Current,
    Upcoming,
}

@Immutable
data class OrbitStepIndicatorColors(
    val active: Color,
    val inactive: Color,
    val onActive: Color,
    val activeLabel: Color,
    val inactiveLabel: Color,
    val statusLabel: Color,
    val railCompleted: Color,
    val railUpcoming: Color,
    val dateLabel: Color,
    val chevron: Color,
    val headerLabel: Color,
    val summaryLabel: Color,
)

object OrbitStepIndicatorDefaults {

    val colors: OrbitStepIndicatorColors
        @Composable @ReadOnlyComposable get() {
            val content = OrbitTheme.contentColors
            val control = OrbitTheme.controlColors
            val dark = OrbitTheme.isDark
            val active = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50
            // Grey inactive — theme icon ink, not a second blue.
            val inactive = content.iconInactive
            return OrbitStepIndicatorColors(
                active = active,
                inactive = inactive,
                onActive = if (dark) OrbitPalette.Blue20 else Color.White,
                activeLabel = content.textPrimary,
                inactiveLabel = content.textSecondary,
                statusLabel = content.textTertiary,
                railCompleted = active,
                railUpcoming = inactive,
                dateLabel = content.textTertiary,
                chevron = content.iconPrimary,
                headerLabel = content.textPrimary,
                summaryLabel = content.textSecondary,
            )
        }
}

fun orbitStepPhase(index: Int, current: Int): OrbitStepPhase = when {
    index < current -> OrbitStepPhase.Completed
    index == current -> OrbitStepPhase.Current
    else -> OrbitStepPhase.Upcoming
}

/** True when the final stage is marked complete — every mark renders filled. */
fun orbitWorkflowComplete(steps: List<OrbitStep>, currentIndex: Int): Boolean {
    if (steps.isEmpty()) return false
    val lastIndex = steps.lastIndex
    if (currentIndex < lastIndex) return false
    val last = steps[lastIndex]
    return last.statusLabel?.equals("Complete", ignoreCase = true) == true
}

fun orbitStepPhase(index: Int, current: Int, steps: List<OrbitStep>): OrbitStepPhase =
    if (orbitWorkflowComplete(steps, current)) {
        OrbitStepPhase.Completed
    } else {
        orbitStepPhase(index, current)
    }

fun orbitStepCurrentIndex(index: Int, size: Int): Int {
    if (size <= 0) return 0
    return index.coerceIn(0, size - 1)
}

/** Numbered square glyph for stage [index] (0-based). Stages beyond five reuse the fifth mark. */
fun orbitStepSquareIcon(index: Int): ImageVector = when (index.coerceAtLeast(0)) {
    0 -> OrbitIcons.OneSquare
    1 -> OrbitIcons.TwoSquare
    2 -> OrbitIcons.ThreeSquare
    3 -> OrbitIcons.FourSquare
    else -> OrbitIcons.FiveSquare
}

fun orbitStepDigit(index: Int): String = ((index.coerceAtLeast(0) % 9) + 1).toString()

/**
 * Default progress line under the STAGES header, e.g. `"2/5 stages"`.
 * Callers can replace it with copy like `"2/5 stages approved"`.
 */
fun orbitStepProgressSummary(currentIndex: Int, total: Int): String {
    if (total <= 0) return "0/0 stages"
    val n = orbitStepCurrentIndex(currentIndex, total) + 1
    return "$n/$total stages"
}

/**
 * Inclusive days from the earliest start to the latest end (or start, if still open).
 *
 * Null when no stage has a parseable date — the TOTAL row then shows a dash rather than `0 days`,
 * which would look like the work took nothing.
 */
fun orbitStepTotalDays(steps: List<OrbitStep>): Int? {
    val dated = steps.flatMap { step ->
        listOfNotNull(
            parseOrbitSlashedDate(step.startedOn),
            parseOrbitSlashedDate(step.endedOn),
        )
    }
    val first = dated.minOrNull() ?: return null
    val last = dated.maxOrNull() ?: return null
    return first.inclusiveDaysUntil(last)
}

fun orbitStepTotalDaysLabel(steps: List<OrbitStep>): String {
    val days = orbitStepTotalDays(steps) ?: return "—"
    return if (days == 1) "1 day" else "$days days"
}

/**
 * Vertical, collapsible workflow track.
 *
 * Header is always visible: `STAGES · sequence` with a progress summary and disclosure chevron.
 * Collapsed body shows the current stage; expanded lists every stage. Completed marks fill blue;
 * the current mark uses a dashed blue square; upcoming marks stay grey.
 */
@Composable
fun OrbitStepIndicator(
    steps: List<OrbitStep>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    colors: OrbitStepIndicatorColors = OrbitStepIndicatorDefaults.colors,
    sequenceLabel: String = "sequence",
    progressSummary: String? = null,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    if (steps.isEmpty()) return

    val safeCurrent = orbitStepCurrentIndex(currentIndex, steps.size)
    var internalExpanded by remember { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    val setExpanded: (Boolean) -> Unit = onExpandedChange ?: { internalExpanded = it }

    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.card
    val interactionSource = remember { MutableInteractionSource() }
    val current = steps[safeCurrent]
    val summary = progressSummary ?: orbitStepProgressSummary(safeCurrent, steps.size)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) 0f else OrbitGlass.SurfaceHighlightLight,
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
                sheen = if (OrbitTheme.isDark) 1f else OrbitGlass.Sheen,
            )
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = { setExpanded(!isExpanded) },
            )
            .indication(interactionSource, orbitPressIndication())
            .padding(horizontal = spacing.lg, vertical = spacing.md)
            .semantics {
                contentDescription = buildString {
                    append("STAGES, ")
                    append(summary)
                    append(", ")
                    append(current.label)
                    append(if (isExpanded) ", expanded" else ", collapsed")
                }
            },
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        StagesHeader(
            sequenceLabel = sequenceLabel,
            summary = summary,
            expanded = isExpanded,
            colors = colors,
        )

        if (!isExpanded) {
            StepRow(
                step = current,
                phase = orbitStepPhase(safeCurrent, safeCurrent, steps),
                stageIndex = safeCurrent,
                colors = colors,
                showRailBelow = false,
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                StageColumnHeadings(colors = colors)
                steps.forEachIndexed { index, step ->
                    StepRow(
                        step = step,
                        phase = orbitStepPhase(index, safeCurrent, steps),
                        stageIndex = index,
                        colors = colors,
                        showRailBelow = index < steps.lastIndex,
                    )
                }
                StageTotalRow(
                    label = orbitStepTotalDaysLabel(steps),
                    colors = colors,
                )
            }
        }
    }
}

@Composable
private fun StagesHeader(
    sequenceLabel: String,
    summary: String,
    expanded: Boolean,
    colors: OrbitStepIndicatorColors,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val mono = OrbitTheme.extendedTypography.reference

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "STAGES",
                style = mono.copy(fontWeight = FontWeight.SemiBold),
                color = colors.headerLabel,
            )
            Text(
                text = " · ",
                style = mono,
                color = colors.summaryLabel,
            )
            Text(
                text = sequenceLabel,
                style = mono,
                color = colors.summaryLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            OrbitGlyph(
                icon = OrbitIcons.ChevronDown,
                size = sizing.iconSm,
                tint = colors.chevron,
                contentDescription = if (expanded) "Collapse stages" else "Expand stages",
                modifier = if (expanded) Modifier.rotate(180f) else Modifier,
            )
        }
        Text(
            text = summary,
            style = OrbitTheme.typography.bodySmall,
            color = colors.summaryLabel,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        OrbitDivider(
            modifier = Modifier.padding(top = spacing.sm),
            color = OrbitTheme.controlColors.controlBorder,
        )
    }
}

@Composable
private fun StageColumnHeadings(
    colors: OrbitStepIndicatorColors,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val style = OrbitTheme.extendedTypography.reference.copy(fontWeight = FontWeight.SemiBold)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(sizing.stepColumnWidth))
        Text(
            text = "STAGES",
            style = style,
            color = colors.headerLabel,
            maxLines = 1,
            modifier = Modifier
                .weight(StageNameWeight)
                .padding(start = spacing.sm, end = spacing.xxs),
        )
        Row(
            modifier = Modifier.weight(StageDatesWeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "START",
                style = style,
                color = colors.headerLabel,
                textAlign = TextAlign.Start,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.padding(horizontal = spacing.xxs))
            Text(
                text = "END",
                style = style,
                color = colors.headerLabel,
                textAlign = TextAlign.Start,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StageTotalRow(
    label: String,
    colors: OrbitStepIndicatorColors,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val style = OrbitTheme.extendedTypography.reference.copy(fontWeight = FontWeight.SemiBold)

    OrbitDivider(
        modifier = Modifier.padding(top = spacing.sm, bottom = spacing.sm),
        color = OrbitTheme.controlColors.controlBorder,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(sizing.stepColumnWidth))
        Text(
            text = "TOTAL",
            style = style,
            color = colors.headerLabel,
            maxLines = 1,
            modifier = Modifier
                .weight(StageNameWeight)
                .padding(start = spacing.sm, end = spacing.xxs),
        )
        Text(
            text = label,
            style = style,
            color = colors.summaryLabel,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.weight(StageDatesWeight),
        )
    }
}

@Composable
private fun StepRow(
    step: OrbitStep,
    phase: OrbitStepPhase,
    stageIndex: Int,
    colors: OrbitStepIndicatorColors,
    showRailBelow: Boolean,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val labelColor = when (phase) {
        OrbitStepPhase.Current -> colors.activeLabel
        else -> colors.inactiveLabel
    }
    val railColor = when (phase) {
        OrbitStepPhase.Completed -> colors.railCompleted
        OrbitStepPhase.Current -> colors.railCompleted
        OrbitStepPhase.Upcoming -> colors.railUpcoming
    }
    val railDashed = phase != OrbitStepPhase.Completed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = sizing.stepRowMinHeight),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(sizing.stepColumnWidth)
                .fillMaxHeight(),
        ) {
            Box(
                modifier = Modifier.size(sizing.stepColumnWidth, sizing.stepGlyphSize),
                contentAlignment = Alignment.Center,
            ) {
                StepNumberMark(
                    digit = orbitStepDigit(stageIndex),
                    phase = phase,
                    colors = colors,
                    size = sizing.stepGlyphSize,
                )
            }
            if (showRailBelow) {
                StepRail(
                    color = railColor,
                    dashed = railDashed,
                    thickness = sizing.stepRailThickness,
                    modifier = Modifier
                        .weight(1f)
                        .width(sizing.stepRailThickness)
                        .heightIn(min = sizing.stepRailLength),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = spacing.sm, end = spacing.sm)
                .heightIn(min = sizing.stepGlyphSize)
                .semantics(mergeDescendants = true) {
                    contentDescription = buildString {
                        append(step.label)
                        append(". ")
                        append(
                            if (phase == OrbitStepPhase.Upcoming) {
                                "Not started"
                            } else {
                                step.statusLabel?.takeIf { it.isNotBlank() } ?: "No status"
                            },
                        )
                    }
                },
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = step.label,
                    style = OrbitTheme.extendedTypography.reference.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = labelColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(StageNameWeight)
                        .padding(end = spacing.xxs),
                )
                StepDateTrail(
                    startedOn = step.startedOn,
                    endedOn = step.endedOn,
                    phase = phase,
                    color = colors.dateLabel,
                    modifier = Modifier.weight(StageDatesWeight),
                )
            }
            Text(
                text = if (phase == OrbitStepPhase.Upcoming) {
                    "-"
                } else {
                    step.statusLabel?.takeIf { it.isNotBlank() } ?: "-"
                },
                style = OrbitTheme.typography.bodySmall,
                color = colors.statusLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Numbered stage mark — full circle:
 * - Completed — filled disc, light digit
 * - Current — dashed active border, active digit
 * - Upcoming — dashed grey border, grey digit
 */
@Composable
private fun StepNumberMark(
    digit: String,
    phase: OrbitStepPhase,
    colors: OrbitStepIndicatorColors,
    size: Dp,
) {
    val density = LocalDensity.current
    val stroke = with(density) { 1.5.dp.toPx() }
    val dash = with(density) { 3.dp.toPx() }
    val gap = with(density) { 2.25.dp.toPx() }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        when (phase) {
            OrbitStepPhase.Completed -> Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(colors.active),
            )
            OrbitStepPhase.Current, OrbitStepPhase.Upcoming -> {
                val border = if (phase == OrbitStepPhase.Current) colors.active else colors.inactive
                Canvas(modifier = Modifier.matchParentSize()) {
                    val radius = (this.size.minDimension - stroke) / 2f
                    drawCircle(
                        color = border,
                        radius = radius,
                        style = Stroke(
                            width = stroke,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, gap), 0f),
                        ),
                    )
                }
            }
        }
        Text(
            text = digit,
            style = OrbitTheme.extendedTypography.reference.copy(fontWeight = FontWeight.SemiBold),
            color = when (phase) {
                OrbitStepPhase.Completed -> colors.onActive
                OrbitStepPhase.Current -> colors.active
                OrbitStepPhase.Upcoming -> colors.inactive
            },
            textAlign = TextAlign.Center,
        )
    }
}

/** Thin vertical connector; solid after completed stages, dashed for current / upcoming. */
@Composable
private fun StepRail(
    color: Color,
    dashed: Boolean,
    thickness: Dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val dash = with(density) { 3.dp.toPx() }
    val gap = with(density) { 2.5.dp.toPx() }
    val stroke = with(density) { thickness.toPx() }
    val overlap = stroke / 2f

    Canvas(modifier = modifier) {
        val x = size.width / 2f
        drawLine(
            color = color,
            start = Offset(x, -overlap),
            end = Offset(x, size.height + overlap),
            strokeWidth = stroke,
            cap = StrokeCap.Butt,
            pathEffect = if (dashed) {
                PathEffect.dashPathEffect(floatArrayOf(dash, gap), 0f)
            } else {
                null
            },
        )
    }
}

@Composable
private fun StepDateTrail(
    startedOn: String?,
    endedOn: String?,
    phase: OrbitStepPhase,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val style = OrbitTheme.extendedTypography.reference

    val startText = when {
        phase == OrbitStepPhase.Upcoming -> "-"
        else -> startedOn ?: "-"
    }
    val endText = when {
        phase == OrbitStepPhase.Upcoming -> "-"
        else -> endedOn ?: "-"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Started $startText, ended $endText"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = startText,
                style = style,
                color = color,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Start,
            )
        }
        Text(
            text = "·",
            style = style,
            color = color,
            modifier = Modifier.padding(horizontal = spacing.xxs),
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = endText,
                style = style,
                color = color,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Start,
            )
        }
    }
}

private const val StageNameWeight = 0.36f
private const val StageDatesWeight = 0.64f
