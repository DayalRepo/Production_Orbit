package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One tappable option in a set of them: a filled pill that goes solid when chosen.
 *
 * The shared visual behind the time slots, the month pills and the year pills, extracted so those
 * three cannot drift. They are the same act — pick one of a small fixed set — and a calendar in which
 * the time buttons and the month buttons are subtly different objects reads as two components stapled
 * together, which is exactly what it was before this was factored out.
 *
 * @param selected drives the fill, the ink and the weight together. One flag, because a pill that is
 *   filled but not emphasised (or vice versa) is a state nobody asked for.
 * @param enabled a false value strikes the label through rather than hiding the pill, so a grid does
 *   not silently change length between two months and leave the user wondering what they misread.
 */
@Composable
internal fun OrbitChoicePill(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = OrbitTheme.sizing.buttonHeightMd,
) {
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val sizing = OrbitTheme.sizing
    val shape = OrbitTheme.shapeTokens.button
    val highlight = if (OrbitTheme.isDark) OrbitGlass.SurfaceHighlightDark else OrbitGlass.SurfaceHighlightLight

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(height)
            // Clip then indicate, both *before* the glass, and the order is the whole point. The iOS
            // effect is a shrink of everything the node draws, so it has to sit above the glass in the
            // chain to take the pill's fill and rim down with the label — below it, the label would dip
            // inside a fill that stayed put. The clip bounds the Android ripple to the pill instead of
            // letting it square off the corners.
            .clip(shape)
            .indication(interactionSource, orbitPressIndication())
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .orbitGlass(
                fill = if (selected) control.actionContainer else control.cardContainer,
                shape = shape,
                highlightAlpha = highlight,
                edge = if (selected) null else control.controlBorder,
                edgeWidth = OrbitTheme.sizing.hairline,
            )
            .then(
                if (enabled) {
                    Modifier.orbitHandCursor().clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = when {
                selected -> control.onActionContainer
                !enabled -> content.textDisabled
                else -> content.textPrimary
            },
            maxLines = 1,
            textDecoration = if (enabled) null else TextDecoration.LineThrough,
        )
    }
}
