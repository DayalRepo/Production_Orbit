package com.orbitai.erp.core.data.session

import com.orbitai.erp.core.model.Session
import com.orbitai.erp.core.model.User
import com.orbitai.erp.core.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface SessionRepository {
    val session: Flow<Session?>

    suspend fun setActiveProject(projectId: String?)

    suspend fun signOut()
}

/**
 * Stand-in session source for the UI phase. Lets us preview every role's shell before auth exists;
 * replaced by a Supabase-backed implementation in the backend phase.
 */
class FakeSessionRepository(
    initialRole: UserRole = UserRole.ProjectManager,
) : SessionRepository {

    private val state = MutableStateFlow<Session?>(Session.forUser(previewUser(initialRole)))

    override val session: Flow<Session?> = state.asStateFlow()

    override suspend fun setActiveProject(projectId: String?) {
        state.update { it?.copy(activeProjectId = projectId) }
    }

    override suspend fun signOut() {
        state.value = null
    }

    /** Switches the previewed role. Development affordance only. */
    fun switchRole(role: UserRole) {
        state.value = Session.forUser(previewUser(role))
    }

    companion object {
        fun previewUser(role: UserRole): User = when (role) {
            UserRole.Ceo -> User(
                id = "u-ceo",
                fullName = "Ananya Rao",
                email = "ananya.rao@orbitai.example",
                role = role,
                jobTitle = "Chief Executive Officer",
            )

            UserRole.ProjectManager -> User(
                id = "u-pm",
                fullName = "Vikram Shah",
                email = "vikram.shah@orbitai.example",
                role = role,
                jobTitle = "Senior Project Manager",
                projectIds = listOf("p-metro-phase-2", "p-riverside-towers"),
            )

            UserRole.SiteEngineer -> User(
                id = "u-eng",
                fullName = "Rahul Menon",
                email = "rahul.menon@orbitai.example",
                role = role,
                jobTitle = "Site Engineer",
                projectIds = listOf("p-metro-phase-2"),
            )

            UserRole.Contractor -> User(
                id = "u-con",
                fullName = "Imran Qureshi",
                email = "imran@buildwell.example",
                role = role,
                jobTitle = "Structural Subcontractor",
                projectIds = listOf("p-metro-phase-2"),
            )

            UserRole.QaQc -> User(
                id = "u-qa",
                fullName = "Sneha Kulkarni",
                email = "sneha.kulkarni@orbitai.example",
                role = role,
                jobTitle = "QA/QC Inspector",
                projectIds = listOf("p-metro-phase-2", "p-riverside-towers"),
            )

            UserRole.WarehouseManager -> User(
                id = "u-wh",
                fullName = "Deepak Iyer",
                email = "deepak.iyer@orbitai.example",
                role = role,
                jobTitle = "Warehouse Manager",
                projectIds = listOf("p-metro-phase-2"),
            )

            UserRole.ProcurementManager -> User(
                id = "u-proc",
                fullName = "Fatima Sheikh",
                email = "fatima.sheikh@orbitai.example",
                role = role,
                jobTitle = "Procurement Manager",
                projectIds = listOf("p-metro-phase-2", "p-riverside-towers"),
            )
        }
    }
}
