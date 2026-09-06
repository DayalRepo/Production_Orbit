package com.orbitai.erp.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Zero-data panel: glyph, title, optional message, optional CTA.
 *
 * Icon stays at [OrbitTheme.sizing.emptyStateIcon] (routine empty), not hero size — hero is for
 * success / onboarding moments that should feel ceremonial.
 */
@Composable
fun OrbitEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: ImageVector = OrbitIcons.NotepadDashed,
    action: (@Composable () -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        OrbitGlyph(
            icon = icon,
            size = sizing.emptyStateIcon,
            tint = content.iconInactive,
            contentDescription = null,
            minimumStroke = sizing.iconStrokeHairline,
            maximumStroke = sizing.iconStrokeWidth,
        )
        Text(
            text = title,
            style = OrbitTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = content.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (!message.isNullOrBlank()) {
            Text(
                text = message,
                style = OrbitTheme.typography.bodyMedium,
                color = content.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        action?.invoke()
    }
}
