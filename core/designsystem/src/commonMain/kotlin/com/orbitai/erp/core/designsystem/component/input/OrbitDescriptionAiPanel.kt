package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The AI assistant prompt shown beneath a description field after the user picks an action.
 */
@Composable
fun OrbitDescriptionAiPanel(
    action: OrbitDescriptionAiAction,
    prompt: String,
    onPromptChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = when (action) {
        OrbitDescriptionAiAction.RewriteDetail -> "Ask Orbit AI to expand these notes"
        OrbitDescriptionAiAction.Translate -> "Ask Orbit AI which language to translate into"
    },
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    AnimatedVisibility(
        visible = true,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier.fillMaxWidth(),
    ) {
        OrbitCard(padding = spacing.md) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = when (action) {
                            OrbitDescriptionAiAction.RewriteDetail -> "Rewrite with Orbit AI"
                            OrbitDescriptionAiAction.Translate -> "Translate with Orbit AI"
                        },
                        style = OrbitTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = content.textPrimary,
                    )
                    OrbitIconButton(
                        contentDescription = "Close AI assistant",
                        onClick = onDismiss,
                        icon = OrbitIcons.Cancel,
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Small,
                        ringed = false,
                    )
                }
                OrbitTextField(
                    value = prompt,
                    onValueChange = onPromptChange,
                    label = "AI prompt",
                    placeholder = placeholder,
                    modifier = Modifier.fillMaxWidth(),
                    trailing = {
                        OrbitIconButton(
                            contentDescription = "Send to Orbit AI",
                            onClick = onSubmit,
                            icon = OrbitIcons.Sent,
                            style = OrbitIconButtonStyle.Accent,
                            size = OrbitIconButtonSize.Small,
                            ringed = false,
                        )
                    },
                )
            }
        }
    }
}
