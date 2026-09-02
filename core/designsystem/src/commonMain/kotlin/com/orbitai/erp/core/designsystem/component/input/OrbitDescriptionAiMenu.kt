package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.foundation.orbitElevatedFill
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/** What the description AI assist can do with the current text. */
enum class OrbitDescriptionAiAction {
    /** Expand terse notes into a fuller handover-style description. */
    RewriteDetail,

    /** Translate the description into another language. */
    Translate,
}

/**
 * The AI assist menu as a dropdown card anchored to the toolbar AI button.
 *
 * Compose this inside a [androidx.compose.foundation.layout.Box] that wraps only the anchor
 * control so the panel drops from the button rather than from the field corner.
 */
@Composable
fun OrbitDescriptionAiMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRewrite: () -> Unit,
    onTranslate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = remember { MutableTransitionState(false) }
    transition.targetState = expanded
    if (!expanded && transition.isIdle && !transition.currentState) return

    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.card

    val density = LocalDensity.current
    val gapPx = with(density) { BelowAnchorGap.roundToPx() }
    val edgeMarginPx = with(density) { EdgeMargin.roundToPx() }
    val positionProvider = remember(gapPx, edgeMarginPx) {
        BelowAnchorStartPositionProvider(gapPx, edgeMarginPx)
    }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(
            visibleState = transition,
            enter = fadeIn(tween(OpenMs)) +
                expandVertically(tween(OpenMs), expandFrom = Alignment.Top),
            exit = fadeOut(tween(CloseMs)) +
                shrinkVertically(tween(CloseMs), shrinkTowards = Alignment.Top),
        ) {
            Column(
                modifier = modifier
                    .width(IntrinsicSize.Max)
                    .widthIn(min = sizing.menuMinWidth, max = AiMenuMaxWidth)
                    .orbitDropShadow(shape = shape, level = OrbitShadow.Level2)
                    .orbitGlass(
                        fill = orbitElevatedFill(OrbitShadow.Level2),
                        shape = shape,
                        highlightAlpha = if (OrbitTheme.isDark) {
                            OrbitGlass.SurfaceHighlightDark
                        } else {
                            OrbitGlass.SurfaceHighlightLight
                        },
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                    )
                    .padding(vertical = spacing.xs),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.sm, vertical = spacing.xxs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Orbit AI",
                        style = OrbitTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OrbitTheme.contentColors.textPrimary,
                    )
                    OrbitIconButton(
                        contentDescription = "Close AI menu",
                        onClick = onDismiss,
                        icon = OrbitIcons.Cancel,
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Small,
                        ringed = false,
                    )
                }
                OrbitDivider(color = control.controlBorder)
                AiMenuRow(
                    label = "Rewrite in detail",
                    description = "Expand notes into a fuller description",
                    icon = OrbitIcons.AiMagic,
                    onClick = {
                        onDismiss()
                        onRewrite()
                    },
                )
                OrbitDivider(color = control.controlBorder)
                AiMenuRow(
                    label = "Translate",
                    description = "Translate into an Indian language",
                    icon = OrbitIcons.AiMagic,
                    onClick = {
                        onDismiss()
                        onTranslate()
                    },
                )
            }
        }
    }
}

@Composable
private fun AiMenuRow(
    label: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .indication(interactionSource, orbitPressIndication())
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrbitGlyph(
            icon = icon,
            size = OrbitTheme.sizing.iconSm,
            tint = content.iconPrimary,
            contentDescription = null,
        )
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Text(
                text = label,
                style = OrbitTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = content.textPrimary,
            )
            Text(
                text = description,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
            )
        }
    }
}

/** Wider than a compact attach menu — room for a title row and two-line action labels. */
private val AiMenuMaxWidth = 300.dp

private val BelowAnchorGap = 4.dp
private val EdgeMargin = 12.dp

/**
 * Drops the card below the AI button with left edges aligned; flips above when there is no room.
 */
private class BelowAnchorStartPositionProvider(
    private val gapPx: Int,
    private val edgeMarginPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val below = anchorBounds.bottom + gapPx
        val above = anchorBounds.top - popupContentSize.height - gapPx

        val fitsBelow = below + popupContentSize.height <= windowSize.height - edgeMarginPx
        val fitsAbove = above >= edgeMarginPx

        val y = if (!fitsBelow && fitsAbove) above else below

        val maxX = (windowSize.width - popupContentSize.width - edgeMarginPx).coerceAtLeast(edgeMarginPx)
        val x = anchorBounds.left.coerceIn(edgeMarginPx.coerceAtMost(maxX), maxX)

        return IntOffset(x, y)
    }
}

private const val OpenMs = 180
private const val CloseMs = 130
