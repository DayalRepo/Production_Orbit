package com.orbitai.erp.ui.form

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Visible caption above a form field.
 *
 * Orbit field `label`s are spoken, not drawn. Forms that need a caption use this so every page
 * matches the work-item card headings: [cardLabel] in capitals, secondary ink.
 */
@Composable
internal fun FormFieldLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = OrbitTheme.extendedTypography.cardLabel,
        color = OrbitTheme.contentColors.textSecondary,
    )
}
