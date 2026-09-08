package com.orbitai.erp.ui.ceo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.LocalWindowSize
import com.orbitai.erp.core.designsystem.foundation.WindowSize
import com.orbitai.erp.core.designsystem.foundation.WindowWidthClass
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import com.orbitai.erp.core.designsystem.theme.OrbitSpacing
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Resolved screen insets and grid metrics for CEO (and later role) shells.
 *
 * Horizontal padding widens on tablet so content does not hug the bezel; the floating bottom nav
 * clearance is computed from [OrbitSizing] so Android (48dp touch / taller bar) and iOS (44pt)
 * stay consistent without hard-coded magic numbers in each screen.
 */
data class CeoLayoutMetrics(
    val horizontal: Dp,
    val top: Dp,
    val bottomContent: Dp,
    val sectionGap: Dp,
    val cardGap: Dp,
    val columns: Int,
    val maxContentWidth: Dp,
)

@Composable
fun rememberCeoLayoutMetrics(
    includeNavClearance: Boolean = true,
): CeoLayoutMetrics {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val window = LocalWindowSize.current
    return remember(spacing, sizing, window, includeNavClearance) {
        ceoLayoutMetrics(spacing, sizing, window, includeNavClearance)
    }
}

/**
 * Pure layout math — unit-testable without Compose.
 *
 * [includeNavClearance] adds space so scrollable body clears the floating [OrbitCeoNavBar].
 */
fun ceoLayoutMetrics(
    spacing: OrbitSpacing,
    sizing: OrbitSizing,
    window: WindowSize,
    includeNavClearance: Boolean = true,
): CeoLayoutMetrics {
    val horizontal = when (window.widthClass) {
        WindowWidthClass.Compact -> spacing.screenHorizontal
        WindowWidthClass.Medium -> spacing.xxl
        WindowWidthClass.Expanded -> spacing.xxxl
    }
    val navClearance = if (includeNavClearance) {
        // Bar height + system gap + air gap. Call sites must also pad navigationBars — the floating
        // nav sits above the system gesture/3-button inset, which this math does not include.
        sizing.bottomNavHeight + sizing.bottomNavSystemGap + spacing.md
    } else {
        spacing.screenVertical
    }
    return CeoLayoutMetrics(
        horizontal = horizontal,
        // Tight under the status bar so page titles sit consistently near the top.
        top = spacing.sm,
        bottomContent = navClearance,
        sectionGap = spacing.xxl,
        cardGap = spacing.cardGap,
        columns = window.dashboardColumns,
        maxContentWidth = sizing.maxContentWidth,
    )
}

/**
 * Outer column for a CEO page: status-bar safe, width-capped, tokenised padding.
 */
@Composable
fun CeoScreenScaffold(
    modifier: Modifier = Modifier,
    metrics: CeoLayoutMetrics = rememberCeoLayoutMetrics(),
    header: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = metrics.maxContentWidth)
                .fillMaxSize()
                .padding(horizontal = metrics.horizontal)
                .padding(top = metrics.top, bottom = metrics.bottomContent),
            verticalArrangement = Arrangement.spacedBy(metrics.sectionGap),
        ) {
            header?.invoke(this)
            content()
        }
    }
}

/**
 * Top header row: leading title block, trailing actions (avatar) end-aligned.
 */
@Composable
fun CeoScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = title.uppercase(),
                // Page titles: h3 + SemiBold (design-system heading weight); uppercase for screen chrome.
                style = OrbitTheme.typography.headlineSmall,
                color = content.textPrimary,
                maxLines = 1,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                    maxLines = 2,
                )
            }
        }
        if (trailing != null) {
            Box(contentAlignment = Alignment.TopEnd) {
                trailing()
            }
        }
    }
}

/**
 * Responsive dashboard grid using [CeoLayoutMetrics.columns] (1 / 2 / 4 by width class).
 */
@Composable
fun CeoDashboardGrid(
    modifier: Modifier = Modifier,
    metrics: CeoLayoutMetrics = rememberCeoLayoutMetrics(),
    contentPadding: PaddingValues = PaddingValues(),
    content: LazyGridScope.() -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(metrics.columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(metrics.cardGap),
        verticalArrangement = Arrangement.spacedBy(metrics.cardGap),
        content = content,
    )
}

/**
 * Measures available width and exposes a local column count for nested CEO layouts.
 */
@Composable
fun CeoMeasureWidth(
    content: @Composable (availableWidth: Dp, columns: Int) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = WindowSize.of(maxWidth, maxHeight).dashboardColumns
        content(maxWidth, columns)
    }
}
