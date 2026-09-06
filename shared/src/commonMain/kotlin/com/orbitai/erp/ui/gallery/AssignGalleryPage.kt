package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Assign-to-user fields for task screens: separate site engineer and contractor pickers with search,
 * multi-select, avatar stacks and info cards.
 *
 * Rosters come from [rememberGallerySiteEngineers] / [rememberGalleryContractors] — id, short role,
 * mobile, username — matching assign payloads screens will receive from the API.
 */
@Composable
internal fun AssignGalleryPage() {
    val spacing = OrbitTheme.spacing
    val siteEngineers = rememberGallerySiteEngineers()
    val contractors = rememberGalleryContractors()

    var siteSelected by remember { mutableStateOf(setOf("se-1", "se-2", "se-3", "se-4")) }
    var contractorSelected by remember { mutableStateOf(setOf("c-1", "c-2", "c-3", "c-4", "c-5")) }

    GallerySection("Assign · site engineer") {
        OrbitAssignField(
            selectedIds = siteSelected,
            members = siteEngineers,
            onToggle = { id ->
                siteSelected = if (id in siteSelected) siteSelected - id else siteSelected + id
            },
            label = "Site engineer",
            placeholder = "Assign site engineers",
        )
    }

    GallerySection("Assign · contractor") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            OrbitAssignField(
                selectedIds = contractorSelected,
                members = contractors,
                onToggle = { id ->
                    contractorSelected = if (id in contractorSelected) {
                        contractorSelected - id
                    } else {
                        contractorSelected + id
                    }
                },
                label = "Contractor",
                placeholder = "Assign contractors",
            )
        }
    }
}
