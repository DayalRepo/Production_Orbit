package com.orbitai.erp.core.designsystem.component.container

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The surface almost every dashboard element sits on: a glass pane with a rim and a contact shadow.
 *
 * ### Three cues, because no one of them is reliable
 *
 * A card has to read as a raised object, and this draws that three ways at once — a fill a step away
 * from the page, a hairline rim, and a shadow beneath. That looks redundant until each one fails.
 * The shadow is the strongest cue and the first to disappear: on the light theme it is a 16%-alpha
 * black, which is close to invisible on a phone held outdoors at full brightness, which is precisely
 * where this product is used. The fill is the weakest, because the light theme's own surfaces run
 * from `#FFFFFF` to `#F5F5F5` and a card cannot separate from all of them at once. The rim is the
 * one that always works and the one that looks least like anything on its own.
 *
 * ### The fill is not quite opaque
 *
 * White on light and near-black on dark, at 98%. The remaining 2% does almost nothing visually and
 * that is the point: it is enough that a strong edge beneath registers as a faint disturbance, which
 * is the cue that says the card is *in front of* the page rather than being a hole cut in it. Going
 * further would start tinting the text, and a card is mostly text.
 *
 * @param onClick makes the whole card a button. Cards that navigate should set this rather than
 *   putting a chevron in the corner and making the chevron the only target — the card is a 300dp-wide
 *   object and the tap should be too.
 */
@Composable
fun OrbitCard(
    modifier: Modifier = Modifier,
    shape: Shape = OrbitTheme.shapeTokens.card,
    container: Color = OrbitTheme.controlColors.cardContainer,
    elevation: Dp = OrbitTheme.elevation.cardRaised,
    padding: Dp = OrbitTheme.spacing.cardPadding,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            // Level 1 - the resting height for content. See `OrbitShadow`.
            .orbitDropShadow(shape = shape, level = OrbitShadow.Level1)
            .clip(shape)
            .orbitGlass(
                fill = container,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = OrbitTheme.sizing.hairline,
            )
            .then(
                if (onClick == null) {
                    Modifier
                } else {
                    Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .indication(interactionSource, orbitPressIndication())
                },
            )
            .then(
                if (contentDescription == null) {
                    Modifier
                } else {
                    Modifier.semantics { this.contentDescription = contentDescription }
                },
            )
            .padding(padding),
        content = content,
    )
}
