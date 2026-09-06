package com.orbitai.erp.core.data.session

import com.orbitai.erp.core.model.Project
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.User
import com.orbitai.erp.core.model.UserRole

/**
 * Demo organisation, projects, and OTP login roster for the UI phase.
 *
 * **Access rules**
 * - [UserRole.Ceo] — organisation-wide (`projectIds` empty); sees villas + apartment/community.
 * - Every other role — assigned to specific projects only.
 *
 * Auth is mobile + fixed mock OTP (no sign-up). Replace with Supabase OTP in the backend phase.
 */
object MockDirectory {

    const val OrganisationId = "org-prestige-estates"
    const val OrganisationName = "Prestige Estates Projects Limited"

    const val VillasProjectId = "p-prestige-golfshire-villas"
    const val VillasProjectName = "Prestige Golfshire Villas"
    const val VillasLocation = "Dommasandra, Bengaluru"

    const val ApartmentProjectId = "p-prestige-lakeside-habitat"
    const val ApartmentProjectName = "Prestige Lakeside Habitat"
    const val ApartmentLocation = "Varthur Road, Bengaluru"

    val OrganisationProjects: List<Project> = listOf(
        Project(
            id = VillasProjectId,
            name = VillasProjectName,
            type = ProjectType.Villas,
            organisationId = OrganisationId,
            location = VillasLocation,
        ),
        Project(
            id = ApartmentProjectId,
            name = ApartmentProjectName,
            type = ProjectType.ApartmentCommunity,
            organisationId = OrganisationId,
            location = ApartmentLocation,
        ),
    )

    fun project(id: String): Project =
        OrganisationProjects.first { it.id == id }

    /**
     * Onboarded users. Phone is the login id; [MockAuthUser.otp] is accepted until real SMS OTP.
     */
    val Users: List<MockAuthUser> = listOf(
        MockAuthUser(
            id = "u-ceo",
            fullName = "Ananya Krishnamurthy",
            email = "ananya.krishnamurthy@prestigeconstructions.com",
            role = UserRole.Ceo,
            phone = "+91 98450 11001",
            otp = "110001",
            organisationId = OrganisationId,
            projectIds = emptyList(), // org-wide
        ),
        MockAuthUser(
            id = "u-pm-villas",
            fullName = "Vikram Mehta",
            email = "vikram.mehta@prestigeconstructions.com",
            role = UserRole.ProjectManager,
            phone = "+91 98450 11002",
            otp = "110002",
            organisationId = OrganisationId,
            projectIds = listOf(VillasProjectId),
        ),
        MockAuthUser(
            id = "u-pm-apt",
            fullName = "Sanjana Iyer",
            email = "sanjana.iyer@prestigeconstructions.com",
            role = UserRole.ProjectManager,
            phone = "+91 98450 11003",
            otp = "110003",
            organisationId = OrganisationId,
            projectIds = listOf(ApartmentProjectId),
        ),
        MockAuthUser(
            id = "u-se-villas",
            fullName = "Arjun Reddy",
            email = "arjun.reddy@prestigeconstructions.com",
            role = UserRole.SiteEngineer,
            phone = "+91 98450 11004",
            otp = "110004",
            organisationId = OrganisationId,
            projectIds = listOf(VillasProjectId),
        ),
        MockAuthUser(
            id = "u-se-apt",
            fullName = "Ravi Menon",
            email = "ravi.menon@prestigeconstructions.com",
            role = UserRole.SiteEngineer,
            phone = "+91 98450 11005",
            otp = "110005",
            organisationId = OrganisationId,
            projectIds = listOf(ApartmentProjectId),
        ),
        MockAuthUser(
            id = "u-con-villas",
            fullName = "Imran Qureshi",
            email = "imran@buildwellinfra.com",
            role = UserRole.Contractor,
            phone = "+91 98450 11006",
            otp = "110006",
            organisationId = OrganisationId,
            projectIds = listOf(VillasProjectId),
        ),
        MockAuthUser(
            id = "u-con-apt",
            fullName = "Suresh Pillai",
            email = "suresh@buildwellinfra.com",
            role = UserRole.Contractor,
            phone = "+91 98450 11007",
            otp = "110007",
            organisationId = OrganisationId,
            projectIds = listOf(ApartmentProjectId),
        ),
        MockAuthUser(
            id = "u-qa",
            fullName = "Sneha Kulkarni",
            email = "sneha.kulkarni@prestigeconstructions.com",
            role = UserRole.QaQc,
            phone = "+91 98450 11008",
            otp = "110008",
            organisationId = OrganisationId,
            // Cross-project QA for both types
            projectIds = listOf(VillasProjectId, ApartmentProjectId),
        ),
        MockAuthUser(
            id = "u-wh-villas",
            fullName = "Deepak Iyer",
            email = "deepak.iyer@prestigeconstructions.com",
            role = UserRole.WarehouseManager,
            phone = "+91 98450 11009",
            otp = "110009",
            organisationId = OrganisationId,
            projectIds = listOf(VillasProjectId),
        ),
        MockAuthUser(
            id = "u-wh-apt",
            fullName = "Meera Nair",
            email = "meera.nair@prestigeconstructions.com",
            role = UserRole.WarehouseManager,
            phone = "+91 98450 11010",
            otp = "110010",
            organisationId = OrganisationId,
            projectIds = listOf(ApartmentProjectId),
        ),
        MockAuthUser(
            id = "u-proc",
            fullName = "Fatima Sheikh",
            email = "fatima.sheikh@prestigeconstructions.com",
            role = UserRole.ProcurementManager,
            phone = "+91 98450 11011",
            otp = "110011",
            organisationId = OrganisationId,
            // Procurement spans both project types
            projectIds = listOf(VillasProjectId, ApartmentProjectId),
        ),
    )

    fun userById(id: String): MockAuthUser =
        Users.first { it.id == id }

    fun userByPhone(phone: String): MockAuthUser? {
        val digits = phone.filter { it.isDigit() }
        return Users.firstOrNull { it.phone.filter { c -> c.isDigit() } == digits }
    }

    fun previewUser(role: UserRole): User =
        Users.first { it.role == role }.toUser()

    /** Default gallery / fake-session user for a role (first match). */
    fun primaryForRole(role: UserRole): MockAuthUser =
        Users.first { it.role == role }
}

/**
 * One onboarded mobile login. [otp] is the fixed mock code until SMS OTP is wired.
 */
data class MockAuthUser(
    val id: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val phone: String,
    val otp: String,
    val organisationId: String,
    val projectIds: List<String>,
) {
    fun toUser(): User = User(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        jobTitle = role.displayName,
        phone = phone,
        organisationId = organisationId,
        projectIds = projectIds,
    )

    val projectNames: List<String>
        get() = projectIds.map { MockDirectory.project(it).name }

    val accessSummary: String
        get() = if (role.hasOrganisationWideScope) {
            "Organisation-wide · ${MockDirectory.OrganisationName}"
        } else {
            projectNames.joinToString(" · ")
        }
}

/** @deprecated Use [MockDirectory] phone constants via [MockDirectory.primaryForRole]. */
@Deprecated("Use MockDirectory", ReplaceWith("MockDirectory.primaryForRole(role).phone"))
object MockAuthPhones {
    val Ceo get() = MockDirectory.primaryForRole(UserRole.Ceo).phone
    val ProjectManager get() = MockDirectory.primaryForRole(UserRole.ProjectManager).phone
    val SiteEngineer get() = MockDirectory.primaryForRole(UserRole.SiteEngineer).phone
    val Contractor get() = MockDirectory.primaryForRole(UserRole.Contractor).phone
    val QaQc get() = MockDirectory.primaryForRole(UserRole.QaQc).phone
    val WarehouseManager get() = MockDirectory.primaryForRole(UserRole.WarehouseManager).phone
    val ProcurementManager get() = MockDirectory.primaryForRole(UserRole.ProcurementManager).phone

    fun forRole(role: UserRole): String = MockDirectory.primaryForRole(role).phone
}

/** @deprecated Use [MockDirectory]. */
@Deprecated("Use MockDirectory", ReplaceWith("MockDirectory"))
object MockTenancy {
    const val OrganisationId = MockDirectory.OrganisationId
    const val OrganisationName = MockDirectory.OrganisationName
    const val ProjectMetroId = MockDirectory.ApartmentProjectId
    const val ProjectMetroName = MockDirectory.ApartmentProjectName
    const val ProjectRiversideId = MockDirectory.VillasProjectId
    const val ProjectRiversideName = MockDirectory.VillasProjectName
}
