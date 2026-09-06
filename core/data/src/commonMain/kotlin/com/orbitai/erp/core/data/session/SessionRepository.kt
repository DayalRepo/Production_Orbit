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
 * Stand-in session source for the UI phase. Backed by [MockDirectory] (real-named roster, villas +
 * apartment/community projects, fixed mock OTPs).
 *
 * Auth plan (with role screens): mobile + OTP only, no sign-up. CEO is organisation-wide; other
 * roles are project-scoped.
 */
class FakeSessionRepository(
    initialRole: UserRole = UserRole.ProjectManager,
) : SessionRepository {

    private val state = MutableStateFlow<Session?>(
        Session.forUser(MockDirectory.previewUser(initialRole)),
    )

    override val session: Flow<Session?> = state.asStateFlow()

    override suspend fun setActiveProject(projectId: String?) {
        state.update { it?.copy(activeProjectId = projectId) }
    }

    override suspend fun signOut() {
        state.value = null
    }

    /** Switches the previewed role. Development affordance only. */
    fun switchRole(role: UserRole) {
        state.value = Session.forUser(MockDirectory.previewUser(role))
    }

    /**
     * Mock OTP login. Returns false if the phone is not onboarded or the OTP does not match.
     */
    fun signInWithOtp(phone: String, otp: String): Boolean {
        val user = MockDirectory.userByPhone(phone) ?: return false
        if (user.otp != otp.trim()) return false
        state.value = Session.forUser(user.toUser())
        return true
    }

    companion object {
        fun previewUser(role: UserRole): User = MockDirectory.previewUser(role)
    }
}
