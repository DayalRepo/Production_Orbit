package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.brand.OrbitLauncherIcon
import com.orbitai.erp.core.designsystem.component.brand.OrbitNavBrandMark
import com.orbitai.erp.core.designsystem.component.brand.OrbitOpeningMark
import com.orbitai.erp.core.designsystem.component.brand.OrbitSplashScreen
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Brand mark surfaces: launcher plate, opening splash letter O, nav AI glyph, and splash replay.
 */
@Composable
internal fun BrandGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    GallerySection("Brand · marks") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            Text(
                text = "Three reusable mark components — launcher plate, splash letter O, and " +
                    "bottom-nav AI glyph. Shared geometry lives in OrbitMark.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                verticalAlignment = Alignment.Bottom,
            ) {
                BrandSample(label = "Launcher") {
                    OrbitLauncherIcon(size = 64.dp)
                }
                BrandSample(label = "Opening") {
                    OrbitOpeningMark()
                }
                BrandSample(label = "Nav AI") {
                    OrbitNavBrandMark(
                        size = 22.dp,
                        color = content.iconPrimary,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                verticalAlignment = Alignment.Bottom,
            ) {
                BrandSample(label = "Launcher light") {
                    OrbitLauncherIcon(size = 64.dp, darkPlate = false)
                }
            }
        }
    }

    GallerySection("Splash screen · replay") {
        var generation by remember { mutableIntStateOf(0) }
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Full opening beat in a clipped preview. Tap Replay after it finishes.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(OrbitTheme.shapeTokens.card),
            ) {
                key(generation) {
                    OrbitSplashScreen(
                        onFinished = {},
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            OrbitButton(
                label = "Replay splash",
                onClick = { generation += 1 },
                variant = OrbitButtonVariant.Secondary,
            )
        }
    }
}

@Composable
private fun BrandSample(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        content()
        Text(
            text = label,
            style = OrbitTheme.typography.labelSmall,
            color = OrbitTheme.contentColors.textSecondary,
        )
    }
}
