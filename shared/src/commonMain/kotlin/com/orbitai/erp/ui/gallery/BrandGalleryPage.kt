package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.orbitai.erp.core.designsystem.component.brand.OrbitMarkDefaults
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
    val openingColor = OrbitMarkDefaults.color()


    GallerySection("Brand") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                verticalAlignment = Alignment.Bottom,
            ) {
                BrandSample {
                    OrbitLauncherIcon(size = 64.dp)
                }
                BrandSample {
                    OrbitOpeningMark(color = openingColor)
                }
                BrandSample {
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
                BrandSample {
                    OrbitLauncherIcon(size = 64.dp, darkPlate = false)
                }
            }
        }
    }

    GallerySection("Splash") {
        var generation by remember { mutableIntStateOf(0) }
        var playing by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(OrbitTheme.shapeTokens.card),
                contentAlignment = Alignment.Center,
            ) {
                if (playing) {
                    key(generation) {
                        OrbitSplashScreen(
                            onFinished = { playing = false },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else {
                    OrbitOpeningMark(color = openingColor, spread = 1f)
                }
            }
            OrbitButton(
                label = if (playing) "Playing…" else "Replay splash",
                onClick = {
                    generation += 1
                    playing = true
                },
                variant = OrbitButtonVariant.Secondary,
            )
        }
    }
}

@Composable
private fun BrandSample(
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        content()
    }
}
