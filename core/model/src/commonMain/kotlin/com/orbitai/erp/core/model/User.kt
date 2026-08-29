package com.orbitai.erp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val jobTitle: String? = null,
    val phone: String? = null,
    /** Projects this user is assigned to. Empty for roles with organisation-wide scope. */
    val projectIds: List<String> = emptyList(),
    val isActive: Boolean = true,
) {
    /** Fallback avatar text, e.g. "Deena Dayal" becomes "DD". */
    val initials: String
        get() = fullName.trim()
            .split(' ')
            .filter { it.isNotBlank() }
            .let { parts ->
                when {
                    parts.isEmpty() -> "?"
                    parts.size == 1 -> parts[0].take(2).uppercase()
                    else -> "${parts.first().first()}${parts.last().first()}".uppercase()
                }
            }
}

/**
 * The signed-in user plus their resolved capabilities. Screens gate on [permissions] rather than
 * reading [User.role], so permission changes stay in one place.
 */
@Serializable
data class Session(
    val user: User,
    val permissions: Set<Permission>,
    val activeProjectId: String? = null,
) {
    fun can(permission: Permission): Boolean = permission in permissions

    fun canAny(vararg permission: Permission): Boolean = permission.any { it in permissions }

    fun canAll(vararg permission: Permission): Boolean = permission.all { it in permissions }

    companion object {
        /** Builds a session with the default permission set for the user's role. */
        fun forUser(user: User, activeProjectId: String? = null): Session = Session(
            user = user,
            permissions = Permission.forRole(user.role),
            activeProjectId = activeProjectId ?: user.projectIds.firstOrNull(),
        )
    }
}
