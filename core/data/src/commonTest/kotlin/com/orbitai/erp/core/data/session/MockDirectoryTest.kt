package com.orbitai.erp.core.data.session

import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MockDirectoryTest {

    @Test
    fun `organisation has villas and apartment community projects`() {
        assertEquals(2, MockDirectory.OrganisationProjects.size)
        assertEquals(
            ProjectType.Villas,
            MockDirectory.project(MockDirectory.VillasProjectId).type,
        )
        assertEquals(
            ProjectType.ApartmentCommunity,
            MockDirectory.project(MockDirectory.ApartmentProjectId).type,
        )
    }

    @Test
    fun `ceo is organisation wide with no project ids`() {
        val ceo = MockDirectory.primaryForRole(UserRole.Ceo)
        assertTrue(ceo.role.hasOrganisationWideScope)
        assertTrue(ceo.projectIds.isEmpty())
        assertEquals(MockDirectory.OrganisationId, ceo.organisationId)
    }

    @Test
    fun `non ceo users are project scoped`() {
        MockDirectory.Users.filter { it.role != UserRole.Ceo }.forEach { user ->
            assertTrue(user.projectIds.isNotEmpty(), "${user.fullName} must have projects")
            assertTrue(user.projectIds.all { id -> MockDirectory.OrganisationProjects.any { it.id == id } })
        }
    }

    @Test
    fun `otp login accepts onboarded phone and code`() {
        val pm = MockDirectory.userById("u-pm-villas")
        assertNotNull(MockDirectory.userByPhone(pm.phone))
        assertEquals(pm.otp, "110002")
        assertNull(MockDirectory.userByPhone("+91 90000 00000"))
    }

    @Test
    fun `fake session signs in with mock otp`() {
        val repo = FakeSessionRepository()
        val se = MockDirectory.userById("u-se-apt")
        assertTrue(repo.signInWithOtp(se.phone, se.otp))
    }
}
