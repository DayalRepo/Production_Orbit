package com.orbitai.erp.core.designsystem.component.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.foundation.orbitElevatedFill
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Bottom sheet for filters, pickers and secondary actions.
 *
 * Uses [OrbitTheme.shapeTokens.sheet] (top corners only) and Level 4 elevation — same family as
 * dialogs and popovers. Scrim tap and back dismiss when [dismissible] is true.
 */
@Composable
fun OrbitBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val contentColors = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.sheet
    val scrim = OrbitTheme.colorScheme.scrim.copy(alpha = SheetScrimAlpha)

    Dialog(
        onDismissRequest = { if (dismissible) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrim)
                    .clickable(
                        enabled = dismissible,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onDismiss,
                    ),
            )

            Column(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .orbitDropShadow(shape = shape, level = OrbitShadow.Level4)
                    .orbitGlass(
                        fill = orbitElevatedFill(OrbitShadow.Level4),
                        shape = shape,
                        highlightAlpha = if (OrbitTheme.isDark) {
                            OrbitGlass.SurfaceHighlightDark
                        } else {
                            OrbitGlass.SurfaceHighlightLight
                        },
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                    )
                    .padding(bottom = spacing.lg),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.sm, bottom = spacing.xs),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .width(SheetHandleWidth)
                            .height(SheetHandleHeight)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(control.controlBorder),
                    )
                }

                if (title != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = spacing.lg, end = spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = title,
                            style = OrbitTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = contentColors.textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        OrbitIconButton(
                            contentDescription = "Close",
                            onClick = onDismiss,
                            icon = OrbitIcons.Cancel,
                            style = OrbitIconButtonStyle.Neutral,
                            size = OrbitIconButtonSize.Small,
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.sm))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    content = content,
                )
            }
        }
    }
}

private val SheetHandleWidth = 36.dp
private val SheetHandleHeight = 4.dp
private const val SheetScrimAlpha = 0.4f

