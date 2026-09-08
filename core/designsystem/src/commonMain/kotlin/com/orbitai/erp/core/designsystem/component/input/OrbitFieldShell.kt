package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/** How big a field is. The same three steps as buttons, so a form can match its controls. */
enum class OrbitFieldSize { Small, Medium, Large }

/**
 * What a field is currently saying about its contents.
 *
 * Separate from focus, which the field reads from its own interaction source. A field can be
 * focused *and* in error — that is in fact the most common moment for an error to exist, since the
 * user is standing in the field fixing it — so the two cannot share one enum.
 */
enum class OrbitFieldState {
    /** Nothing to report. */
    Default,

    /** The value is wrong or missing. Solid red rim. */
    Error,

    /** The value has been checked and accepted. Solid green rim. */
    Success,
}

/**
 * The pane every input field is drawn on: solid white (light) / black (dark) fill with an opaque
 * colour border — no glass sheen and no contact shadow on the rim.
 *
 * Focus does **not** restyle the rim — a thickening border on tap read as a flash and fought the
 * caret. Error / success still colour the edge. [interactionSource] stays on the signature so
 * callers can observe focus for IME / a11y.
 */
@Composable
internal fun OrbitFieldShell(
    interactionSource: InteractionSource,
    shape: Shape,
    minHeight: Dp,
    horizontalPadding: Dp,
    enabled: Boolean,
    state: OrbitFieldState,
    modifier: Modifier = Modifier,
    contentGap: Dp? = null,
    trailingPadding: Dp? = null,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    // Retained for API stability; focus no longer drives the rim.
    @Suppress("UNUSED_PARAMETER")
    val unusedInteraction = interactionSource

    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val isDark = OrbitTheme.isDark

    val errorRim = if (isDark) OrbitPalette.Red70 else OrbitPalette.Red40
    val successRim = if (isDark) OrbitPalette.Green70 else OrbitPalette.Green40

    val targetRim = when {
        !enabled -> control.controlBorder.copy(alpha = control.controlBorder.alpha * OrbitAlpha.Disabled)
        state == OrbitFieldState.Error -> errorRim
        state == OrbitFieldState.Success -> successRim
        else -> control.controlBorder
    }
    val rim by animateColorAsState(targetRim, tween(FocusMs), label = "orbit-field-rim")

    val width by animateDpAsState(
        targetValue = if (state != OrbitFieldState.Default) {
            sizing.borderFocus
        } else {
            sizing.hairline
        },
        animationSpec = tween(FocusMs),
        label = "orbit-field-rim-width",
    )

    val solidFill = if (isDark) Color.Black else Color.White
    val fill = if (enabled) solidFill else solidFill.copy(alpha = OrbitAlpha.Disabled)

    Row(
        modifier = modifier
            .heightIn(min = minHeight)
            .clip(shape)
            .background(fill, shape)
            .border(width = width, color = rim, shape = shape)
            .padding(
                start = horizontalPadding,
                end = trailingPadding ?: horizontalPadding,
                top = spacing.xs,
                bottom = spacing.xs,
            ),
        horizontalArrangement = Arrangement.spacedBy(contentGap ?: spacing.sm),
        verticalAlignment = verticalAlignment,
    ) {
        content()
    }
}

private const val FocusMs = 120
