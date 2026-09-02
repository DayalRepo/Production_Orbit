package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignField
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignMember
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.avatar_01
import com.orbitai.erp.resources.avatar_02
import com.orbitai.erp.resources.avatar_03
import com.orbitai.erp.resources.avatar_04
import com.orbitai.erp.resources.avatar_05
import org.jetbrains.compose.resources.painterResource

/**
 * Assign-to-user fields for task screens: separate site engineer and contractor pickers with search,
 * multi-select, avatar stacks and info cards.
 */
@Composable
internal fun AssignGalleryPage() {
    val spacing = OrbitTheme.spacing

    val siteEngineers = listOf(
        OrbitAssignMember(
            id = "se-1",
            name = "Priya Sharma",
            role = "Site Engineer",
            mobile = "+91 98200 41122",
            username = "priya.sharma",
            avatar = painterResource(Res.drawable.avatar_02),
        ),
        OrbitAssignMember(
            id = "se-2",
            name = "Ravi Menon",
            role = "Site Engineer",
            mobile = "+91 99400 77310",
            username = "ravi.menon",
            avatar = painterResource(Res.drawable.avatar_03),
        ),
        OrbitAssignMember(
            id = "se-3",
            name = "Kavita Joshi",
            role = "Site Engineer",
            mobile = "+91 88790 13345",
            username = "kavita.joshi",
        ),
        OrbitAssignMember(
            id = "se-4",
            name = "Anita Desai",
            role = "Site Engineer",
            mobile = "+91 98330 20984",
            avatar = painterResource(Res.drawable.avatar_04),
        ),
    )

    val contractors = listOf(
        OrbitAssignMember(
            id = "c-1",
            name = "Sanjay Iyer",
            role = "Contractor",
            mobile = "+91 90030 55817",
            username = "sanjay.iyer",
            avatar = painterResource(Res.drawable.avatar_05),
        ),
        OrbitAssignMember(
            id = "c-2",
            name = "Meera Nair",
            role = "Contractor",
            mobile = "+91 94470 66203",
            username = "meera.nair",
            avatar = painterResource(Res.drawable.avatar_01),
        ),
        OrbitAssignMember(
            id = "c-3",
            name = "Vikram Rao",
            role = "Contractor",
            mobile = "+971 50 442 8890",
            username = "vikram.rao",
        ),
        OrbitAssignMember(
            id = "c-4",
            name = "Arjun Pillai",
            role = "Contractor",
            mobile = "+91 98450 22109",
        ),
        OrbitAssignMember(
            id = "c-5",
            name = "Neha Gupta",
            role = "Contractor",
            mobile = "+91 99887 66554",
        ),
        OrbitAssignMember(
            id = "c-6",
            name = "Rahul Verma",
            role = "Contractor",
            mobile = "+91 91234 56789",
        ),
        OrbitAssignMember(
            id = "c-7",
            name = "Deepak Singh",
            role = "Contractor",
            mobile = "+91 90123 45678",
        ),
    )

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
            modifier = Modifier.fillMaxWidth(),
        )
    }

    GallerySection("Assign · contractor") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
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
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
