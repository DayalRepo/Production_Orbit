package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import com.orbitai.erp.core.designsystem.component.container.OrbitLazyScrollbar
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitHorizontalScrollbar
import com.orbitai.erp.core.designsystem.component.container.OrbitVerticalScrollbar
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Arrowed track scrollbars — the chrome used beside long menus and attachment strips.
 *
 * Worth scrolling on a device: thumb travel, arrow steps, and light/dark track contrast.
 */
@Composable
internal fun ScrollbarGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    GallerySection("Vertical scrollbar · menu chrome") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Arrows step ~one viewport. Thumb is positional only — flick the list to travel.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            OrbitCard(padding = spacing.sm) {
                val scroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(scroll)
                            .padding(end = spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        ScrollbarSampleLines.forEach { line ->
                            Text(
                                text = line,
                                style = OrbitTheme.typography.bodyMedium,
                                color = content.textPrimary,
                            )
                        }
                    }
                    if (scroll.maxValue > 0) {
                        OrbitVerticalScrollbar(
                            scrollState = scroll,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = spacing.xs),
                        )
                    }
                }
            }
        }
    }

    GallerySection("Horizontal scrollbar · strip chrome") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Same chrome on its side — used under composer attachment strips.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            OrbitCard(padding = spacing.sm) {
                val scroll = rememberScrollState()
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scroll)
                            .padding(bottom = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HorizontalChipLabels.forEach { label ->
                            Box(
                                modifier = Modifier
                                    .width(112.dp)
                                    .height(40.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    text = label,
                                    style = OrbitTheme.typography.labelLarge,
                                    color = content.textPrimary,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                    if (scroll.maxValue > 0) {
                        OrbitHorizontalScrollbar(
                            scrollState = scroll,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    GallerySection("Lazy scrollbar · item-step arrows") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Same chrome for LazyColumn / LazyRow — arrows move one item. Used in time pickers.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            OrbitCard(padding = spacing.sm) {
                val listState = rememberLazyListState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp),
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        items(ScrollbarSampleLines.size) { index ->
                            Text(
                                text = ScrollbarSampleLines[index],
                                style = OrbitTheme.typography.bodyMedium,
                                color = content.textPrimary,
                            )
                        }
                    }
                    OrbitLazyScrollbar(
                        listState = listState,
                        horizontal = false,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = spacing.xs),
                    )
                }
            }
        }
    }
}

private val ScrollbarSampleLines = listOf(
    "01 · Foundation pour — Tower A",
    "02 · Column cages — level 2",
    "03 · Slab formwork — level 3",
    "04 · MEP rough-in — wet areas",
    "05 · External plaster — east elevation",
    "06 · Waterproofing — terrace",
    "07 · Tile setting — bathrooms",
    "08 · Paint — first coat corridors",
    "09 · Joinery — kitchen carcasses",
    "10 · Snag list — client walkthrough",
    "11 · Landscape — driveway kerbs",
    "12 · Handover pack — as-builts",
)

private val HorizontalChipLabels = listOf(
    "boq-rev-3.xlsx",
    "east-elev.jpg",
    "rfi-042.pdf",
    "site-notes.docx",
    "slab-cycle.mp4",
    "handover.pdf",
)
