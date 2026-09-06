package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.OrbitTitleAlignment
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Screen chrome above a list or detail: back (optional), title, trailing actions.
 *
 * Title alignment follows [OrbitTheme.topBarTitleAlignment] — start on Android, centre on iOS —
 * so the same call site matches each platform's navigation habit without branching in features.
 *
 * @param scrolled raises the bar one shadow step when content has moved under it.
 */
@Composable
fun OrbitTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrolled: Boolean = false,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.card
    val centered = OrbitTheme.topBarTitleAlignment == OrbitTitleAlignment.Center
    val shadowLevel = if (scrolled) OrbitShadow.Level2 else OrbitShadow.Level1

    Row(
        modifier = modifier
            .fillMaxWidth()
            .orbitDropShadow(shape = shape, level = shadowLevel)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
            )
            .heightIn(min = sizing.appBarHeight)
            .padding(horizontal = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Box(modifier = Modifier.padding(end = if (navigationIcon != null) spacing.xs else spacing.none)) {
            navigationIcon?.invoke()
        }

        Text(
            text = title,
            style = OrbitTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = content.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.weight(1f),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}
