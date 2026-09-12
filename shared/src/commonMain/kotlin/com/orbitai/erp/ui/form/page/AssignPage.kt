package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.form.FormFieldLabel
import com.orbitai.erp.ui.gallery.rememberGalleryContractors
import com.orbitai.erp.ui.gallery.rememberGallerySiteEngineers

/**
 * Separate site-engineer and contractor assign fields. Rosters come from the gallery people set.
 */
@Composable
fun AssignPage(
    siteEngineerIds: Set<String>,
    onSiteEngineerToggle: (String) -> Unit,
    contractorIds: Set<String>,
    onContractorToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val siteEngineers = rememberGallerySiteEngineers()
    val contractors = rememberGalleryContractors()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Site engineer")
            OrbitAssignField(
                selectedIds = siteEngineerIds,
                members = siteEngineers,
                onToggle = onSiteEngineerToggle,
                label = "Site engineer",
                placeholder = "Assign site engineers",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Contractor")
            OrbitAssignField(
                selectedIds = contractorIds,
                members = contractors,
                onToggle = onContractorToggle,
                label = "Contractor",
                placeholder = "Assign contractors",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
