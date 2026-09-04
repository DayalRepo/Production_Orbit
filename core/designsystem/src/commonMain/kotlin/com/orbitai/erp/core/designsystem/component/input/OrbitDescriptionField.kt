package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A multiline description box with a toolbar and expand/collapse height control.
 *
 * The toolbar carries an optional AI assist control on the left and an expand/collapse control on
 * the right. The AI menu and the translate language list open as elevated dropdown cards hung from
 * the toolbar strip — the same popup layer as a stage list — not inside the text area.
 */
@Composable
fun OrbitDescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    collapsedHeight: Dp = DefaultCollapsedHeight,
    expandedHeight: Dp = DefaultExpandedHeight,
    aiAssistEnabled: Boolean = false,
    aiMenuExpanded: Boolean = false,
    onAiMenuExpandedChange: (Boolean) -> Unit = {},
    onAiRewrite: () -> Unit = {},
    onAiTranslate: () -> Unit = {},
    aiPanel: (@Composable (anchorWidth: Dp) -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var menuWidth by remember { mutableStateOf(0.dp) }

    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)
    val base: TextStyle = size.pick(
        OrbitTheme.typography.bodyMedium,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.extendedTypography.fieldLarge,
    )

    val ink = if (enabled) content.textPrimary else content.textPrimary.copy(OrbitAlpha.Disabled)
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    val targetShellHeight = if (expanded) expandedHeight else collapsedHeight
    val shellHeight by animateDpAsState(
        targetValue = targetShellHeight,
        animationSpec = tween(ExpandCollapseMs),
        label = "orbit-description-shell-height",
    )
    val toolbarInset = spacing.xs
    val toolbarVerticalPad = spacing.sm
    val dividerGap = spacing.sm
    val toolbarHeight = sizing.iconButtonSm + toolbarVerticalPad
    val toolbarBlock = toolbarHeight + dividerGap + sizing.hairline
    val scrollViewportHeight = (
        shellHeight - spacing.xs * 2 - toolbarBlock - spacing.xs
        ).coerceAtLeast(MinScrollViewport)

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        OrbitFieldShell(
            interactionSource = interactionSource,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = shellHeight,
            horizontalPadding = 0.dp,
            enabled = enabled,
            state = state,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(toolbarHeight)
                        .padding(
                            start = toolbarInset,
                            end = toolbarInset,
                            top = toolbarVerticalPad / 2,
                        ),
                    horizontalArrangement = if (aiAssistEnabled && enabled) {
                        Arrangement.SpaceBetween
                    } else {
                        Arrangement.End
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (aiAssistEnabled && enabled) {
                        DescriptionToolbarIconButton(
                            contentDescription = "AI assist for $label",
                            icon = OrbitIcons.AiMagic,
                            onClick = { onAiMenuExpandedChange(!aiMenuExpanded) },
                        )
                    }

                    DescriptionToolbarIconButton(
                        contentDescription = if (expanded) "Collapse $label" else "Expand $label",
                        icon = if (expanded) OrbitIcons.Collapse else OrbitIcons.Expand,
                        onClick = { onExpandedChange(!expanded) },
                    )
                }

                OrbitDivider(
                    modifier = Modifier.padding(top = dividerGap, bottom = dividerGap),
                    color = control.controlBorder,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(scrollViewportHeight)
                        .padding(horizontal = padding)
                        .padding(top = spacing.xs),
                    contentAlignment = Alignment.TopStart,
                ) {
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides TextSelectionColors(
                            handleColor = control.actionContainer,
                            backgroundColor = control.actionContainer.copy(alpha = SelectionAlpha),
                        ),
                    ) {
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                                .orbitReleaseFocusWithKeyboard()
                                .semantics { contentDescription = label },
                            enabled = enabled,
                            readOnly = readOnly,
                            textStyle = base.copy(color = ink, fontWeight = FontWeight.Medium),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                            interactionSource = interactionSource,
                            cursorBrush = SolidColor(control.actionContainer),
                        )
                    }

                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = base.copy(fontWeight = FontWeight.Medium),
                            color = hint,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clearAndSetSemantics {},
                        )
                    }
                }
            }
        }

        // Full field width from the left rim — not inset to the AI glyph.
        if (aiAssistEnabled && enabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarBlock)
                    .align(Alignment.TopStart)
                    .onSizeChanged { menuWidth = with(density) { it.width.toDp() } },
            ) {
                OrbitDescriptionAiMenu(
                    expanded = aiMenuExpanded,
                    onDismiss = { onAiMenuExpandedChange(false) },
                    onRewrite = onAiRewrite,
                    onTranslate = onAiTranslate,
                    width = menuWidth,
                )
                aiPanel?.invoke(menuWidth)
            }
        }
    }
}

private val DefaultCollapsedHeight = 300.dp
private val DefaultExpandedHeight = 900.dp
private val MinScrollViewport = 56.dp
private const val SelectionAlpha = 0.28f
private const val ExpandCollapseMs = 200

@Composable
private fun DescriptionToolbarIconButton(
    contentDescription: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(sizing.minTouchTarget)
            .clip(CircleShape)
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        OrbitGlyph(
            icon = icon,
            size = sizing.iconSm,
            tint = content.iconPrimary,
            contentDescription = null,
            minimumStroke = sizing.iconStrokeLight,
        )
    }
}
