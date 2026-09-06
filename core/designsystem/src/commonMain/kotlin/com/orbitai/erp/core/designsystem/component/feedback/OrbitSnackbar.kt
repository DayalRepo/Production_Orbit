package com.orbitai.erp.core.designsystem.component.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlinx.coroutines.delay

enum class OrbitSnackbarTone {
    Neutral,
    Positive,
    Destructive,
}

/**
 * Short glass toast for approve / save / send feedback that has no in-place home.
 *
 * Prefer swapping a control glyph (see [com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton])
 * when the confirmation can sit under the finger. Use this when the action is already gone —
 * after a sheet closes, after a navigation.
 */
@Composable
fun OrbitSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    tone: OrbitSnackbarTone = OrbitSnackbarTone.Neutral,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = RoundedCornerShape(percent = 50)
    val messageColor = when (tone) {
        OrbitSnackbarTone.Neutral -> content.textPrimary
        OrbitSnackbarTone.Positive -> OrbitBadgeTone.Green.colors.label
        OrbitSnackbarTone.Destructive -> OrbitBadgeTone.Red.colors.label
    }

    Row(
        modifier = modifier
            .orbitDropShadow(shape = shape, level = OrbitShadow.Level3)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
            )
            .padding(horizontal = spacing.md, vertical = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            text = message,
            style = OrbitTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = messageColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (actionLabel != null && onAction != null) {
            OrbitButton(
                label = actionLabel,
                onClick = onAction,
                variant = OrbitButtonVariant.Secondary,
                size = OrbitButtonSize.Small,
            )
        }
    }
}

@Stable
class OrbitSnackbarHostState {
    var current by mutableStateOf<OrbitSnackbarData?>(null)
        private set

    fun show(
        message: String,
        actionLabel: String? = null,
        tone: OrbitSnackbarTone = OrbitSnackbarTone.Neutral,
        durationMs: Long = DefaultDurationMs,
    ) {
        current = OrbitSnackbarData(message, actionLabel, tone, durationMs)
    }

    fun dismiss() {
        current = null
    }

    data class OrbitSnackbarData(
        val message: String,
        val actionLabel: String?,
        val tone: OrbitSnackbarTone,
        val durationMs: Long,
    )

    companion object {
        const val DefaultDurationMs = 3_200L
    }
}

@Composable
fun rememberOrbitSnackbarHostState(): OrbitSnackbarHostState = remember { OrbitSnackbarHostState() }

@Composable
fun OrbitSnackbarHost(
    hostState: OrbitSnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
) {
    val data = hostState.current
    LaunchedEffect(data) {
        val shown = data ?: return@LaunchedEffect
        delay(shown.durationMs)
        if (hostState.current === shown) hostState.dismiss()
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = data != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            val shown = data
            if (shown != null) {
                OrbitSnackbar(
                    message = shown.message,
                    actionLabel = shown.actionLabel,
                    onAction = if (shown.actionLabel != null) {
                        {
                            onAction?.invoke()
                            hostState.dismiss()
                        }
                    } else {
                        null
                    },
                    tone = shown.tone,
                    modifier = Modifier.padding(OrbitTheme.spacing.md),
                )
            }
        }
    }
}
