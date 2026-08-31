package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import kotlin.math.abs

/**
 * A change since some earlier reading: an arrow, and how much.
 *
 * ### Direction and sentiment are two different things
 *
 * The arrow points the way the number moved. The colour says whether that is good news. Those
 * coincide often enough that most implementations collapse them into one, and then the first time
 * somebody puts this on *open defects* or *cost variance* the dashboard turns green while the
 * project catches fire. [higherIsBetter] keeps them apart: a rising defect count still points up,
 * and is still red.
 *
 * ### The fill is translucent, and that is what keeps it a footnote
 *
 * A delta sits beside a large figure that is the actual subject of the card, so it has to be
 * findable without out-shouting the number it annotates. The fill is the badge tone's `container` —
 * a low-alpha tint rather than a solid block — so the card shows through it and the chip reads as a
 * tinted pane rather than a sticker. A solid green pill at this size pulls the eye before the
 * percentage does, which is precisely backwards.
 *
 * Both states are drawn identically apart from hue, so a green delta and a red one carry equal
 * weight and only the meaning changes. A red chip given extra emphasis would make every dip look
 * like an incident.
 *
 * ### Accessibility
 *
 * The whole chip announces one sentence — "up 14 percent vs last week" — assembled from
 * [contentDescription] rather than being read out as a glyph followed by a bare number. The arrow is
 * decorative in the accessibility tree because the word "up" already carries it.
 *
 * Direction is never colour alone: the arrow points, and the sign is in the text. Green and red are
 * the two hues that a substantial minority of users cannot tell apart, so a red-versus-green pill
 * with no other cue is close to the worst case in the whole system.
 *
 * @param value the signed change, in percentage points. `-4.2f` renders as "4.2%" with a down arrow;
 *   the minus sign is dropped from the label because the arrow already carries it and "↓ -4.2%" is a
 *   double negative that reads as an increase on a quick glance.
 * @param higherIsBetter whether a rise is good news. Set `false` for metrics where it is not:
 *   overdue tasks, defect counts, cost variance, rework hours.
 */
@Composable
fun OrbitDelta(
    value: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    higherIsBetter: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing

    val rising = value >= 0f
    val good = rising == higherIsBetter
    val tone = if (good) OrbitBadgeTone.Green else OrbitBadgeTone.Red
    val palette = tone.colors

    val shape = OrbitTheme.shapeTokens.badge

    Row(
        modifier = modifier
            .clip(shape)
            // The same glass stack a badge uses, so a delta and a status pill on one card look like
            // members of the same family rather than two different ideas about what a pill is.
            .orbitGlass(
                fill = palette.container,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.BadgeHighlightDark
                } else {
                    OrbitGlass.BadgeHighlightLight
                },
                edge = palette.border,
                edgeWidth = sizing.hairline,
            )
            // `heightIn`, never `height`: at 200% font scale the label has to be able to grow the
            // chip rather than being clipped by it (WCAG 1.4.4).
            .heightIn(min = sizing.badgeHeightXs)
            .padding(horizontal = spacing.xs, vertical = spacing.none)
            // One announcement for the chip, not three. Assembled by the caller because only the
            // caller knows what the comparison is against.
            .clearAndSetSemantics { this.contentDescription = contentDescription },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xxs),
    ) {
        OrbitGlyph(
            icon = if (rising) OrbitIcons.TrendUp else OrbitIcons.TrendDown,
            size = sizing.badgeIconXs,
            tint = palette.icon,
            minimumStroke = sizing.iconStrokeWidth,
            contentDescription = null,
        )
        Text(
            // Magnitude only. The arrow is the sign; repeating it in the text reads as a double
            // negative on a falling value.
            text = "${abs(value).formatDelta()}%",
            style = OrbitTheme.extendedTypography.metricCaption,
            color = palette.label,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * One decimal place, and only when it says something.
 *
 * "14%" rather than "14.0%", because the trailing zero implies a precision the weekly rollup does
 * not have and costs a character in a chip that is already narrow. A genuine 4.2 keeps its decimal.
 */
private fun Float.formatDelta(): String {
    val rounded = (this * 10f).toInt() / 10f
    val whole = rounded.toInt()
    return if (rounded == whole.toFloat()) "$whole" else "$rounded"
}
