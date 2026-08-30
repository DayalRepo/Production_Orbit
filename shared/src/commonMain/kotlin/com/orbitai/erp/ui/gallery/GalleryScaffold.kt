package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The furniture the gallery pages share.
 *
 * Kept in its own file so that adding a component group means adding one file rather than editing a
 * growing one — the gallery is a component library browser, and a browser whose chrome is tangled
 * with its content stops being editable somewhere around the fourth group.
 */

/** A titled group of samples. The title is uppercased here so no caller has to remember to. */
@Composable
internal fun GallerySection(title: String, content: @Composable () -> Unit) {
    val spacing = OrbitTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text(
            text = title.uppercase(),
            style = OrbitTheme.extendedTypography.sectionLabel,
            color = OrbitTheme.contentColors.textTertiary,
            textAlign = TextAlign.Start,
            modifier = Modifier.widthIn(max = OrbitTheme.sizing.maxContentWidth),
        )
        content()
    }
}

/** Wrapping row at badge density. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GalleryFlow(content: @Composable () -> Unit) {
    val spacing = OrbitTheme.spacing
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        content()
    }
}

/**
 * Same as [GalleryFlow] with more room between rows.
 *
 * Interactive controls carry a touch target beyond their visible pill, so at badge spacing their hit
 * areas overlap vertically and the wrong one answers a tap near an edge.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GalleryControlFlow(content: @Composable () -> Unit) {
    val spacing = OrbitTheme.spacing
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        content()
    }
}
