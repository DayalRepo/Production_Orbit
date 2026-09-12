package com.orbitai.erp.ui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A labelled block on a create / raise form, with an optional rule above it.
 */
@Composable
internal fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        if (showDivider) {
            OrbitDivider(
                modifier = Modifier.padding(vertical = spacing.xs),
                color = OrbitTheme.controlColors.dividerElevated,
            )
        }
        Text(
            text = title.uppercase(),
            style = OrbitTheme.extendedTypography.sectionLabel,
            color = OrbitTheme.contentColors.textSecondary,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
            content = content,
        )
    }
}
