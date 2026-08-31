package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.progress.ProgressCard

/**
 * The progress card, and the two primitives it is assembled from.
 *
 * The edge cases get as much room as the happy path, because they are where a segmented bar goes
 * wrong: 0% and 100% are the two readings a person acts on, and 1% and 99% are the two that a naive
 * rounding turns into 0% and 100% and quietly lies about.
 */
@Composable
internal fun ProgressGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    // No gallery heading above these two: the point of stacking them bare is to see the cards the
    // way a dashboard column will, where nothing sits between them and the next card is the only
    // thing giving scale to the one above it.
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        ProgressCard(
            label = "Progress",
            progress = 0.71f,
            delta = 14f,
        )
        // The mirror of the card above, and the reason `higherIsBetter` exists. Remaining work
        // falling is good news, so this delta is negative and green while the one above it is
        // positive and green — a chip that coloured by sign alone would get one of them wrong.
        ProgressCard(
            label = "Remaining",
            progress = 0.29f,
            delta = -14f,
            higherIsBetter = false,
        )
        // No heading at all, for a card under a section header that already names it. The spoken
        // description has to be supplied by hand here, since there is no label to build it from.
        ProgressCard(
            label = null,
            progress = 0.52f,
            delta = 3f,
            contentDescription = "Progress, 52 percent, up 3 percent vs last week",
        )
    }

    GallerySection("Rounding · the four readings that matter") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            listOf(
                0f to "0% · nothing lit",
                0.01f to "1% · one slat, never none",
                0.99f to "99% · one slat dark, never full",
                1f to "100% · every slat lit",
            ).forEach { (value, caption) ->
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = caption,
                        style = OrbitTheme.typography.bodySmall,
                        color = content.textSecondary,
                    )
                    OrbitSegmentedProgress(
                        progress = value,
                        contentDescription = caption,
                    )
                }
            }
        }
    }

    GallerySection("Delta chips") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrbitDelta(value = 14f, contentDescription = "up 14 percent")
            OrbitDelta(value = -4.2f, contentDescription = "down 4.2 percent")
            OrbitDelta(
                value = 6.5f,
                higherIsBetter = false,
                contentDescription = "up 6.5 percent, worse",
            )
            OrbitDelta(
                value = -3f,
                higherIsBetter = false,
                contentDescription = "down 3 percent, better",
            )
        }
    }
}
