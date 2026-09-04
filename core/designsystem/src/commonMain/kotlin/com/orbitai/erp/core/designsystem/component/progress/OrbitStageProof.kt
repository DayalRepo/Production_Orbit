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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadge
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.datetime.parseOrbitSlashedDate
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Which work-sequence template a unit follows.
 *
 * Villa omits common area and basement; apartment / building / community include both.
 */
enum class OrbitStageProofKind {
    Villa,
    Building,
}

/**
 * One high-level stage on a [OrbitStageProof] track (Structure, Unit internal, …).
 *
 * @param code short stamp drawn in brackets, e.g. `SR`.
 * @param label full stage name.
 * @param startedOn optional formatted start date (`dd/mm/yyyy`).
 * @param endedOn optional formatted end date.
 */
@Immutable
data class OrbitStageProofStep(
    val code: String,
    val label: String,
    val startedOn: String? = null,
    val endedOn: String? = null,
)

/** Default villa sequence from the work-sequence chart: SR → UI → UE → ED. */
fun orbitStageProofVilla(): List<OrbitStageProofStep> = listOf(
    OrbitStageProofStep("SR", "Structure"),
    OrbitStageProofStep("UI", "Unit internal"),
    OrbitStageProofStep("UE", "Unit external"),
    OrbitStageProofStep("ED", "Ext. development"),
)

/**
 * Default apartment / building / community sequence:
 * SR → CA → UI → UE → ED → BS.
 */
fun orbitStageProofBuilding(): List<OrbitStageProofStep> = listOf(
    OrbitStageProofStep("SR", "Structure"),
    OrbitStageProofStep("CA", "Common area"),
    OrbitStageProofStep("UI", "Unit internal"),
    OrbitStageProofStep("UE", "Unit external"),
    OrbitStageProofStep("ED", "Ext. development"),
    OrbitStageProofStep("BS", "Basement"),
)

fun orbitStageProofDefaults(kind: OrbitStageProofKind): List<OrbitStageProofStep> = when (kind) {
    OrbitStageProofKind.Villa -> orbitStageProofVilla()
    OrbitStageProofKind.Building -> orbitStageProofBuilding()
}

fun orbitStageProofProgressSummary(completed: Int, total: Int): String {
    val safeCompleted = completed.coerceIn(0, total.coerceAtLeast(0))
    val noun = if (total == 1) "stage" else "stages"
    return "$safeCompleted/$total $noun completed"
}

/**
 * Index of the stage currently in progress. When every stage is done, returns the last index
 * so the collapsed card still shows a concrete stage.
 */
fun orbitStageProofCurrentIndex(completedCount: Int, total: Int): Int {
    if (total <= 0) return 0
    if (completedCount >= total) return total - 1
    return completedCount.coerceIn(0, total - 1)
}

fun orbitStageProofPhase(index: Int, completedCount: Int, total: Int): OrbitStepPhase {
    if (total <= 0) return OrbitStepPhase.Upcoming
    if (completedCount >= total) return OrbitStepPhase.Completed
    return when {
        index < completedCount -> OrbitStepPhase.Completed
        index == completedCount -> OrbitStepPhase.Current
        else -> OrbitStepPhase.Upcoming
    }
}

fun orbitStageProofTotalDays(stages: List<OrbitStageProofStep>): Int? {
    val dated = stages.flatMap { step ->
        listOfNotNull(
            parseOrbitSlashedDate(step.startedOn),
            parseOrbitSlashedDate(step.endedOn),
        )
    }
    val first = dated.minOrNull() ?: return null
    val last = dated.maxOrNull() ?: return null
    return first.inclusiveDaysUntil(last)
}

fun orbitStageProofTotalDaysLabel(stages: List<OrbitStageProofStep>): String {
    val days = orbitStageProofTotalDays(stages) ?: return "—"
    return if (days == 1) "1 day" else "$days days"
}

@Immutable
data class OrbitStageProofColors(
    val active: Color,
    val inactive: Color,
    val onActive: Color,
    val activeLabel: Color,
    val inactiveLabel: Color,
    val railCompleted: Color,
    val railUpcoming: Color,
    val dateLabel: Color,
    val chevron: Color,
    val headerLabel: Color,
    val summaryLabel: Color,
)

object OrbitStageProofDefaults {
    val colors: OrbitStageProofColors
        @Composable @ReadOnlyComposable get() {
            val content = OrbitTheme.contentColors
            val dark = OrbitTheme.isDark
            val active = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50
            val inactive = content.iconInactive
            return OrbitStageProofColors(
                active = active,
                inactive = inactive,
                onActive = if (dark) OrbitPalette.Blue20 else Color.White,
                activeLabel = content.textPrimary,
                inactiveLabel = content.textSecondary,
                railCompleted = active,
                railUpcoming = inactive,
                dateLabel = content.textTertiary,
                chevron = content.iconPrimary,
                headerLabel = content.textPrimary,
                summaryLabel = content.textSecondary,
            )
        }
}

/**
 * Stage proof — high-level unit sequence (villa vs building).
 *
 * Matches [OrbitStepIndicator] rhythm: solid rail after done stages, dashed for current/upcoming,
 * coloured number marks, Done / In progress badges on the right, TOTAL days footer, and the
 * current stage shown while collapsed.
 */
@Composable
fun OrbitStageProof(
    kind: OrbitStageProofKind,
    modifier: Modifier = Modifier,
    stages: List<OrbitStageProofStep> = orbitStageProofDefaults(kind),
    completedCount: Int = 0,
    sequenceLabel: String = "SEQUENCE",
    colors: OrbitStageProofColors = OrbitStageProofDefaults.colors,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    if (stages.isEmpty()) return

    var internalExpanded by remember { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    val setExpanded: (Boolean) -> Unit = onExpandedChange ?: { internalExpanded = it }

    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.card
    val interaction = remember { MutableInteractionSource() }
    val summary = orbitStageProofProgressSummary(completedCount, stages.size)
    val currentIndex = orbitStageProofCurrentIndex(completedCount, stages.size)
    val current = stages[currentIndex]
    val mono = OrbitTheme.extendedTypography.reference

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
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = { setExpanded(!isExpanded) },
            )
            .indication(interaction, orbitPressIndication())
            .padding(horizontal = spacing.lg, vertical = spacing.md)
            .semantics {
                contentDescription = buildString {
                    append("STAGEPROOF, ")
                    append(summary)
                    append(", ")
                    append(current.label)
                    append(if (isExpanded) ", expanded" else ", collapsed")
                }
            },
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "STAGEPROOF",
                    style = mono.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.headerLabel,
                )
                Text(text = " · ", style = mono, color = colors.summaryLabel)
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
                    contentDescription = if (isExpanded) "Collapse stage proof" else "Expand stage proof",
                    modifier = if (isExpanded) Modifier.rotate(180f) else Modifier,
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
                color = control.controlBorder,
            )
        }

        if (!isExpanded) {
            StageProofRow(
                index = currentIndex,
                stage = current,
                phase = orbitStageProofPhase(currentIndex, completedCount, stages.size),
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
                stages.forEachIndexed { index, stage ->
                    StageProofRow(
                        index = index,
                        stage = stage,
                        phase = orbitStageProofPhase(index, completedCount, stages.size),
                        colors = colors,
                        showRailBelow = index < stages.lastIndex,
                    )
                }
                StageProofTotalRow(
                    label = orbitStageProofTotalDaysLabel(stages),
                    colors = colors,
                )
            }
        }
    }
}

@Composable
private fun StageProofTotalRow(
    label: String,
    colors: OrbitStageProofColors,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val style = OrbitTheme.extendedTypography.reference.copy(fontWeight = FontWeight.SemiBold)

    OrbitDivider(
        modifier = Modifier.padding(top = spacing.sm, bottom = spacing.sm),
        color = OrbitTheme.controlColors.controlBorder,
    )
    // Same TOTAL / days weighting as [OrbitStepIndicator] StageTotalRow.
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
                .weight(StageProofNameWeight)
                .padding(start = spacing.sm, end = spacing.xxs),
        )
        Text(
            text = label,
            style = style,
            color = colors.summaryLabel,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.weight(StageProofDatesWeight),
        )
    }
}

@Composable
private fun StageProofRow(
    index: Int,
    stage: OrbitStageProofStep,
    phase: OrbitStepPhase,
    colors: OrbitStageProofColors,
    showRailBelow: Boolean,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val labelColor = when (phase) {
        OrbitStepPhase.Current -> colors.activeLabel
        else -> colors.inactiveLabel
    }
    val railColor = when (phase) {
        OrbitStepPhase.Completed, OrbitStepPhase.Current -> colors.railCompleted
        OrbitStepPhase.Upcoming -> colors.railUpcoming
    }
    val railDashed = phase != OrbitStepPhase.Completed
    // Match stages: missing / not-started dates show as "-", joined with a mid-dot.
    val startText = when (phase) {
        OrbitStepPhase.Upcoming -> "-"
        else -> stage.startedOn?.takeIf { it.isNotBlank() } ?: "-"
    }
    val endText = when (phase) {
        OrbitStepPhase.Upcoming -> "-"
        else -> stage.endedOn?.takeIf { it.isNotBlank() } ?: "-"
    }

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
                StageProofNumberMark(
                    digit = orbitStepDigit(index),
                    phase = phase,
                    colors = colors,
                    size = sizing.stepGlyphSize,
                )
            }
            if (showRailBelow) {
                StageProofRail(
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
                .heightIn(min = sizing.stepGlyphSize),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                StageProofCodeChip(code = stage.code)
                Text(
                    text = stage.label,
                    style = OrbitTheme.extendedTypography.reference.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = labelColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                when (phase) {
                    OrbitStepPhase.Completed -> OrbitBadge(
                        label = "Done",
                        tone = OrbitBadgeTone.Green,
                        icon = OrbitIcons.CircleCheck,
                        size = OrbitBadgeSize.Small,
                    )
                    OrbitStepPhase.Current -> OrbitBadge(
                        label = "In progress",
                        tone = OrbitBadgeTone.Blue,
                        icon = OrbitIcons.Progress,
                        size = OrbitBadgeSize.Small,
                    )
                    OrbitStepPhase.Upcoming -> Unit
                }
            }
            // Dates sit under the code + label, indented to line up with the title text.
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.width(CodeChipSize + spacing.xs))
                StageProofDateTrail(
                    startText = startText,
                    endText = endText,
                    color = colors.dateLabel,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StageProofCodeChip(code: String) {
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = RoundedCornerShape(CodeChipCorner)

    Box(
        modifier = Modifier
            .size(CodeChipSize)
            .clip(shape)
            .background(control.interactiveContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = code.uppercase(),
            style = OrbitTheme.extendedTypography.metricCaption.copy(fontWeight = FontWeight.SemiBold),
            color = content.textSecondary,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StageProofDateTrail(
    startText: String,
    endText: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    // Caption size + no ellipsis so full `dd/mm/yyyy` stays visible under the title.
    val style = OrbitTheme.extendedTypography.metricCaption

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Started $startText, ended $endText"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = startText,
            style = style,
            color = color,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
        )
        Text(
            text = " · ",
            style = style,
            color = color,
            maxLines = 1,
        )
        Text(
            text = endText,
            style = style,
            color = color,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
        )
    }
}

@Composable
private fun StageProofNumberMark(
    digit: String,
    phase: OrbitStepPhase,
    colors: OrbitStageProofColors,
    size: Dp,
) {
    val density = LocalDensity.current
    val corner = with(density) { 5.dp.toPx() }
    val stroke = with(density) { 1.5.dp.toPx() }
    val dash = with(density) { 3.dp.toPx() }
    val gap = with(density) { 2.25.dp.toPx() }
    val shape = RoundedCornerShape(5.dp)

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        when (phase) {
            OrbitStepPhase.Completed -> Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(colors.active),
            )
            OrbitStepPhase.Current, OrbitStepPhase.Upcoming -> {
                val border = if (phase == OrbitStepPhase.Current) colors.active else colors.inactive
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(
                        color = border,
                        topLeft = Offset(stroke / 2f, stroke / 2f),
                        size = Size(this.size.width - stroke, this.size.height - stroke),
                        cornerRadius = CornerRadius(corner, corner),
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

@Composable
private fun StageProofRail(
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

/** Same name / dates split as [OrbitStepIndicator] (TOTAL row only). */
private const val StageProofNameWeight = 0.36f
private const val StageProofDatesWeight = 0.64f
/** Fixed stamp for SR / CA / UI / … — same box on every row. */
private val CodeChipSize = 28.dp
private val CodeChipCorner = 4.dp
