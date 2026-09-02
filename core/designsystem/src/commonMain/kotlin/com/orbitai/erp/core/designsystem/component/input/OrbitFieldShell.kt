package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
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
 * The pane every input field is drawn on: glass fill, rim, contact shadow, focus response.
 *
 * Fields share the same raised glass treatment as attachment rows — white on light, near-black on
 * dark — so every input reads as a first-class surface rather than a grey recess in the page.
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
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val isDark = OrbitTheme.isDark

    val focused by interactionSource.collectIsFocusedAsState()

    val errorRim = if (isDark) OrbitPalette.Red70 else OrbitPalette.Red40
    val successRim = if (isDark) OrbitPalette.Green70 else OrbitPalette.Green40

    val targetRim = when {
        !enabled -> control.controlBorder.copy(alpha = control.controlBorder.alpha * OrbitAlpha.Disabled)
        state == OrbitFieldState.Error -> errorRim
        state == OrbitFieldState.Success -> successRim
        focused -> control.controlContent
        else -> control.controlBorder
    }
    val rim by animateColorAsState(targetRim, tween(FocusMs), label = "orbit-field-rim")

    val width by animateDpAsState(
        targetValue = if (focused || state != OrbitFieldState.Default) {
            sizing.borderFocus
        } else {
            sizing.hairline
        },
        animationSpec = tween(FocusMs),
        label = "orbit-field-rim-width",
    )

    val highlight = if (isDark) OrbitGlass.SurfaceHighlightDark else OrbitGlass.SurfaceHighlightLight

    Row(
        modifier = modifier
            .heightIn(min = minHeight)
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = highlight,
                edge = rim,
                edgeWidth = width,
            )
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
