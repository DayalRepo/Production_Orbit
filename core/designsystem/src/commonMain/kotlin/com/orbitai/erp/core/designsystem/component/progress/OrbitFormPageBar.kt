package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A **form page bar** (also called a wizard step bar / multi-step form indicator).
 *
 * Shows one separate corner-rounded bar per filling page. Bars through the current page use the
 * filled progress colour with glass + shadow; upcoming pages stay on the track colour with the same
 * treatment. Not a pill capsule — corner radius only.
 *
 * @param pageCount how many filling pages the form has (must be ≥ 1).
 * @param currentPage 0-based index of the page the user is on.
 */
@Composable
fun OrbitFormPageBar(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    colors: OrbitProgressColors = OrbitProgressDefaults.colors,
    height: Dp = FormPageBarHeight,
    gap: Dp = OrbitTheme.sizing.progressSegmentGap,
    contentDescription: String? = null,
) {
    val pages = pageCount.coerceAtLeast(1)
    val current = currentPage.coerceIn(0, pages - 1)
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val shape = RoundedCornerShape(sizing.progressSegmentRadius)
    val label = contentDescription
        ?: "Form page ${current + 1} of $pages"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics { this.contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        repeat(pages) { index ->
            val filled = index <= current
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
                    .clip(shape)
                    .orbitGlass(
                        fill = if (filled) colors.filled else colors.track,
                        shape = shape,
                        highlightAlpha = if (dark) {
                            OrbitGlass.SurfaceHighlightDark
                        } else {
                            OrbitGlass.SurfaceHighlightLight
                        },
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                        sheen = if (dark) 1f else OrbitGlass.Sheen,
                    ),
            )
        }
    }
}

/** Shorter than a KPI track — a quiet status strip above a multi-page form. */
private val FormPageBarHeight = 4.dp
