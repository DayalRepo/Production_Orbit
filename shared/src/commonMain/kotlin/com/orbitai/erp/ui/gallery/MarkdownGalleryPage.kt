package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.markdown.OrbitMarkdown

/**
 * Review surface for [OrbitMarkdown]: headings, inline marks, bullets, and an overflowing table
 * so the shared list scrollbars can be judged on a device.
 */
@Composable
internal fun MarkdownGalleryPage() {
    GallerySection("Markdown") {
        OrbitMarkdown(
            source = BriefMarkdown,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    GallerySection("Markdown table") {
        OrbitMarkdown(
            source = TableMarkdown,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private val BriefMarkdown = """
    # Tower A pour
    ## Sequence
    The **pump** still holds Thursday if it clears Tower A by 11:00. Move the level 4 slab to Friday
    and *confirm* the crane with Priya.

    ### Hold points
    - Bring the formwork crew forward one half-day
    - __Notify__ the batching plant before noon
    - ~~Monday inspection~~ moved to Tuesday so the approval stage stays clean
""".trimIndent()

private val TableMarkdown = """
    | Stage | Crew | Window | Gate | Notes |
    | --- | --- | --- | --- | --- |
    | Formwork | Crew A | 06:00 | East | Nets on |
    | Steel | Crew B | 08:30 | East | Fe500D |
    | Pour | Crew C | 11:00 | North | Pump hold |
    | Strike | Crew A | 16:00 | East | Cure 12h |
    | Cure check | QA | 06:00+1 | North | Moisture |
    | Sign-off | PM | 09:00+1 | — | Photos |
""".trimIndent()
