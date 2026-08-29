package com.orbitai.erp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Temporary stand-in for a feature that has not been built yet. Replaced destination by
 * destination as the UI phase progresses.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(OrbitTheme.spacing.xxl),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
            ) {
                Text(
                    text = title,
                    style = OrbitTheme.typography.headlineSmall,
                    color = OrbitTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = OrbitTheme.typography.bodyMedium,
                        color = OrbitTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
