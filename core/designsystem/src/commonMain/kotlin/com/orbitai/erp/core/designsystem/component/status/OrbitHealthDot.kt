package com.orbitai.erp.core.designsystem.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.ColorPair
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A red/amber/green dot for project health, and the smallest status affordance in the library.
 *
 * [label] is mandatory even when [showLabel] is false: roughly 8% of men have some form of red-green
 * colour deficiency, and a bare RAG dot is meaningless to them and to a screen reader. When the
 * label is hidden it becomes the content description instead of being dropped.
 *
 * @param withHalo draws the container colour as a ring around the dot. Gives the dot presence on a
 *   dashboard card without enlarging it in a dense list.
 */
@Composable
fun OrbitHealthDot(
    label: String,
    colors: ColorPair,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    withHalo: Boolean = false,
    dotSize: Dp = OrbitTheme.sizing.indicatorDotMd,
) {
    val spacing = OrbitTheme.spacing

    Row(
        modifier = if (showLabel) modifier else modifier.semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (withHalo) {
                        Modifier
                            .background(colors.container, CircleShape)
                            .padding(spacing.xs)
                    } else {
                        Modifier
                    },
                ),
        ) {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(colors.content, CircleShape),
            )
        }
        if (showLabel) {
            Text(
                text = label,
                style = OrbitTheme.typography.labelMedium,
                color = OrbitTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
