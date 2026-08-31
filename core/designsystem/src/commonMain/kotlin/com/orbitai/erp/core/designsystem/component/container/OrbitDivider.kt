package com.orbitai.erp.core.designsystem.component.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A rule between rows or sections.
 *
 * ### Why it takes an inset rather than always being full-bleed
 *
 * In a list of rows that each begin with an avatar or an icon, a full-bleed rule cuts under that
 * leading element and visually detaches it from its own row — the eye groups by enclosure, so the
 * line reads as a lid on the row above rather than as a separator between two. Insetting the rule to
 * where the *text* starts fixes that, and it is why every platform's list divider does it. Full-bleed
 * is still right between sections, where there is no leading column to align to.
 *
 * ### It is not part of the reading order
 *
 * A divider is a drawing of a grouping that is already expressed by the content, so it is cleared
 * from the semantics tree. Left in, a settings screen with eight sections announces eight anonymous
 * nodes between the things a person is actually looking for.
 *
 * @param inset distance from the leading edge. Pass `spacing.cardPadding + sizing.avatarSm +
 *   spacing.md` to line up with text in an avatar row; leave at zero for a section break.
 */
@Composable
fun OrbitDivider(
    modifier: Modifier = Modifier,
    inset: Dp = OrbitTheme.spacing.none,
    /**
     * Pulls the rule in from the trailing edge as well.
     *
     * Separate from [inset] rather than folded into it, because the two are asked for by different
     * situations. A list whose rows have a leading glyph insets only the start, so the rules line up
     * under the text and the glyph column reads as a continuous gutter. A rule *between* rows of
     * plain text wants to be short of both edges, so it reads as a separator inside the panel rather
     * than as a line cutting the panel in half.
     */
    endInset: Dp = OrbitTheme.spacing.none,
    thickness: Dp = OrbitTheme.sizing.dividerThickness,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = inset, end = endInset)
            .height(thickness)
            .background(OrbitTheme.controlColors.dividerSubtle)
            .clearAndSetSemantics {},
    )
}

/**
 * The vertical rule, for separating side-by-side figures inside one card.
 *
 * Takes its height from the row it sits in rather than a fixed value, so that a card whose text has
 * been scaled to 200% gets a rule that grows with it instead of a stub floating beside a tall column.
 * That means the parent row has to have a height for this to be visible — inside an
 * `IntrinsicSize.Min` row it will match the tallest sibling, which is almost always what is wanted.
 */
@Composable
fun OrbitVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = OrbitTheme.sizing.dividerThickness,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
            .background(OrbitTheme.controlColors.dividerSubtle)
            .clearAndSetSemantics {},
    )
}
