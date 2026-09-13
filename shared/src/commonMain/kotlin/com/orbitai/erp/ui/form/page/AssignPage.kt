package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignField
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignMember
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.form.FormFieldLabel
import com.orbitai.erp.ui.gallery.rememberGalleryContractors
import com.orbitai.erp.ui.gallery.rememberGalleryProcurementManagers
import com.orbitai.erp.ui.gallery.rememberGallerySiteEngineers
import com.orbitai.erp.ui.gallery.rememberGalleryWarehouseManagers

/**
 * One labelled assign field. Shared so task, issue, and materials-order forms reuse the same control.
 */
@Composable
fun AssignRoleField(
    heading: String,
    selectedIds: Set<String>,
    members: List<OrbitAssignMember>,
    onToggle: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        FormFieldLabel(heading)
        OrbitAssignField(
            selectedIds = selectedIds,
            members = members,
            onToggle = onToggle,
            label = heading,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

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
        AssignRoleField(
            heading = "Site engineer",
            selectedIds = siteEngineerIds,
            members = siteEngineers,
            onToggle = onSiteEngineerToggle,
            placeholder = "Assign site engineers",
        )
        AssignRoleField(
            heading = "Contractor",
            selectedIds = contractorIds,
            members = contractors,
            onToggle = onContractorToggle,
            placeholder = "Assign contractors",
        )
    }
}

/** Procurement and warehouse assign fields for a materials order. */
@Composable
fun AssignSupplyPage(
    procurementIds: Set<String>,
    onProcurementToggle: (String) -> Unit,
    warehouseIds: Set<String>,
    onWarehouseToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val procurement = rememberGalleryProcurementManagers()
    val warehouse = rememberGalleryWarehouseManagers()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
    ) {
        AssignRoleField(
            heading = "Procurement manager",
            selectedIds = procurementIds,
            members = procurement,
            onToggle = onProcurementToggle,
            placeholder = "Assign procurement managers",
        )
        AssignRoleField(
            heading = "Warehouse manager",
            selectedIds = warehouseIds,
            members = warehouse,
            onToggle = onWarehouseToggle,
            placeholder = "Assign warehouse managers",
        )
    }
}
