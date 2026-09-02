package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A continuous pill progress track (rounded capsule fill), for checklist and similar summaries.
 *
 * Prefer this over [OrbitSegmentedProgress] when the reading is a single fraction rather than a
 * stepped count of slats.
 */
@Composable
fun OrbitPillProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colors: OrbitProgressColors = OrbitProgressDefaults.colors,
    height: Dp = OrbitTheme.sizing.progressTrackHeight,
) {
    val fraction = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
                contentDescription?.let { this.contentDescription = it }
            },
    ) {
        val radius = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(color = colors.track, cornerRadius = radius)
        val filledWidth = size.width * fraction
        if (filledWidth > 0f) {
            drawRoundRect(
                color = colors.filled,
                size = Size(filledWidth.coerceAtLeast(size.height), size.height),
                cornerRadius = radius,
            )
        }
    }
}
