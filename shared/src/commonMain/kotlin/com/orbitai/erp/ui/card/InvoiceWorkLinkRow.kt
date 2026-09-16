package com.orbitai.erp.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/** Compact tappable work link showing only the task/issue id (e.g. T-1042). */
@Composable
fun InvoiceWorkLinkRow(
    number: String,
    kindLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    val label = "Open $kindLabel $number"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = label
                role = Role.Button
            }
            .clickable(onClick = onClick)
            .padding(vertical = OrbitTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        OrbitGlyph(
            icon = OrbitIcons.ArrowRight,
            size = OrbitTheme.sizing.iconSm,
            tint = content.iconAccent,
            contentDescription = null,
        )
        Text(
            text = number,
            style = OrbitTheme.extendedTypography.reference,
            color = content.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}
