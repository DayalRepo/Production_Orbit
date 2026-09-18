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
import com.orbitai.erp.core.designsystem.theme.OrbitElevationLevel
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The surface almost every dashboard element sits on: a glass pane with a rim and a contact shadow.
 *
 * @param glassBoost stronger specular highlight for elevated dashboard KPI cards (light + dark).
 * @param shadowLevel drop-shadow rung; [OrbitShadow.Level2] reads more “lifted” on light theme.
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
    glassBoost: Boolean = false,
    shadowLevel: OrbitElevationLevel = OrbitShadow.Level1,
    content: @Composable ColumnScope.() -> Unit,
) {
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }
    val highlight = when {
        glassBoost && OrbitTheme.isDark -> OrbitGlass.KpiHighlightDark
        glassBoost -> OrbitGlass.KpiHighlightLight
        OrbitTheme.isDark -> OrbitGlass.SurfaceHighlightDark
        else -> OrbitGlass.SurfaceHighlightLight
    }
    // Slightly more translucent when boosted so the page shows through the sheen.
    val fill = if (glassBoost) {
        container.copy(alpha = (container.alpha * 0.92f).coerceIn(0.85f, 1f))
    } else {
        container
    }

    Column(
        modifier = modifier
            .orbitDropShadow(shape = shape, level = shadowLevel)
            .clip(shape)
            .orbitGlass(
                fill = fill,
                shape = shape,
                highlightAlpha = highlight,
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
