package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.ui.component.badge.SeverityBadge
import com.orbitai.erp.ui.form.FormFieldLabel

/**
 * Tappable severity badges on one row. Selected is glass; the rest stay outlined.
 *
 * Small size so Low / Medium / High / Critical stay on a single line for both villa and
 * apartment forms, matching the issue card.
 */
@Composable
fun SeverityPicker(
    selected: Severity?,
    onSelect: (Severity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FormFieldLabel("Severity")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Severity.entries.forEach { severity ->
                val isSelected = severity == selected
                SeverityBadge(
                    severity = severity,
                    size = OrbitBadgeSize.Small,
                    emphasis = if (isSelected) {
                        OrbitBadgeEmphasis.Glass
                    } else {
                        OrbitBadgeEmphasis.Outline
                    },
                    modifier = Modifier
                        .semantics {
                            this.selected = isSelected
                        }
                        .clickable(
                            role = Role.RadioButton,
                            onClick = { onSelect(severity) },
                        ),
                )
            }
        }
    }
}
