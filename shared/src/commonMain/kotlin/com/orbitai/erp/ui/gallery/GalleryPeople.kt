package com.orbitai.erp.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.designsystem.component.input.OrbitAssignMember
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.avatar_01
import com.orbitai.erp.resources.avatar_02
import com.orbitai.erp.resources.avatar_03
import com.orbitai.erp.resources.avatar_04
import com.orbitai.erp.resources.avatar_05
import com.orbitai.erp.ui.component.team.TeamMember
import org.jetbrains.compose.resources.painterResource

/**
 * Gallery people drawn from [MockDirectory] — same ids, phones and short roles as OTP login.
 * mock login and the eventual API.
 */
@Composable
internal fun rememberGalleryPainters(): GalleryPainters {
    val a01 = painterResource(Res.drawable.avatar_01)
    val a02 = painterResource(Res.drawable.avatar_02)
    val a03 = painterResource(Res.drawable.avatar_03)
    val a04 = painterResource(Res.drawable.avatar_04)
    val a05 = painterResource(Res.drawable.avatar_05)
    return remember(a01, a02, a03, a04, a05) {
        GalleryPainters(a01, a02, a03, a04, a05)
    }
}

internal data class GalleryPainters(
    val avatar01: Painter,
    val avatar02: Painter,
    val avatar03: Painter,
    val avatar04: Painter,
    val avatar05: Painter,
)

@Composable
internal fun rememberGalleryCrew(painters: GalleryPainters = rememberGalleryPainters()): List<TeamMember> =
    remember(painters) {
        val byId = MockDirectory.Users.associateBy { it.id }
        listOf(
            byId.getValue("u-pm-apt").toTeamMember(painters.avatar02),
            byId.getValue("u-se-apt").toTeamMember(painters.avatar03),
            byId.getValue("u-qa").toTeamMember(painters.avatar04),
            byId.getValue("u-con-apt").toTeamMember(painters.avatar05),
            byId.getValue("u-wh-apt").toTeamMember(painters.avatar01),
            byId.getValue("u-proc").toTeamMember(null),
            byId.getValue("u-se-villas").toTeamMember(null),
        )
    }

@Composable
fun rememberGallerySiteEngineers(): List<OrbitAssignMember> {
    val painters = rememberGalleryPainters()
    return remember(painters) {
        listOf(
            MockDirectory.userById("u-se-villas").toAssignMember("arjun.reddy", painters.avatar02),
            MockDirectory.userById("u-se-apt").toAssignMember("ravi.menon", painters.avatar03),
        )
    }
}

@Composable
fun rememberGalleryContractors(): List<OrbitAssignMember> {
    val painters = rememberGalleryPainters()
    return remember(painters) {
        listOf(
            MockDirectory.userById("u-con-villas").toAssignMember("imran.qureshi", painters.avatar05),
            MockDirectory.userById("u-con-apt").toAssignMember("suresh.pillai", painters.avatar01),
        )
    }
}

@Composable
fun rememberGalleryProcurementManagers(): List<OrbitAssignMember> {
    val painters = rememberGalleryPainters()
    return remember(painters) {
        listOf(
            MockDirectory.userById("u-proc").toAssignMember("fatima.sheikh", painters.avatar04),
        )
    }
}

@Composable
fun rememberGalleryWarehouseManagers(): List<OrbitAssignMember> {
    val painters = rememberGalleryPainters()
    return remember(painters) {
        listOf(
            MockDirectory.userById("u-wh-villas").toAssignMember("deepak.iyer", painters.avatar01),
            MockDirectory.userById("u-wh-apt").toAssignMember("meera.nair", painters.avatar05),
        )
    }
}

/** Account-menu preview: CEO (org) and site engineer on the apartment project. */
internal object GalleryAccountSamples {
    private val ceo = MockDirectory.userById("u-ceo")
    private val site = MockDirectory.userById("u-se-apt")

    val CeoName = ceo.fullName
    val CeoPhone = ceo.phone
    val CeoRole = ceo.role.shortLabel
    val OrgName = MockDirectory.OrganisationName
    val SiteName = site.fullName
    val SitePhone = site.phone
    val SiteRole = site.role.shortLabel
    val ProjectName = MockDirectory.ApartmentProjectName
}

private fun com.orbitai.erp.core.data.session.MockAuthUser.toTeamMember(
    avatar: Painter?,
): TeamMember = TeamMember(
    id = id,
    name = fullName,
    phone = phone,
    role = role.shortLabel,
    avatar = avatar,
)

private fun com.orbitai.erp.core.data.session.MockAuthUser.toAssignMember(
    username: String,
    avatar: Painter?,
): OrbitAssignMember = OrbitAssignMember(
    id = id,
    name = fullName,
    role = role.shortLabel,
    mobile = phone,
    username = username,
    avatar = avatar,
)
