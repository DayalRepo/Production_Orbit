package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Terms checkbox: green glass square with soft shadow; fills green with a tick when checked.
 */
@Composable
fun OrbitGlassCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val green = OrbitBadgeTone.Green.colors
    val dark = OrbitTheme.isDark
    val shape = RoundedCornerShape(6.dp)
    val interaction = remember { MutableInteractionSource() }

    val fill = when {
        checked -> green.solidContainer
        else -> green.container
    }
    val rim = if (checked) green.border else control.controlBorder
    val tick = if (checked) green.onSolidContainer else green.label

    Box(
        modifier = modifier
            .size(22.dp)
            .orbitHandCursor()
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
            .clip(shape)
            .orbitGlass(
                fill = fill,
                shape = shape,
                highlightAlpha = if (dark || checked) 0f else OrbitGlass.SurfaceHighlightLight,
                edge = rim,
                edgeWidth = sizing.hairline,
                sheen = if (checked) 1f else OrbitGlass.Sheen,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) },
            )
            .semantics {
                selected = checked
            },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            OrbitGlyph(
                icon = OrbitIcons.Tick,
                size = sizing.iconSm,
                tint = tick,
                minimumStroke = sizing.iconStrokeLight,
                maximumStroke = sizing.iconStrokeMd,
                contentDescription = null,
            )
        }
    }
}
