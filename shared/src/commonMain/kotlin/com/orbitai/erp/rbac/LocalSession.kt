package com.orbitai.erp.rbac

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.orbitai.erp.core.model.Permission
import com.orbitai.erp.core.model.Session

/**
 * The signed-in session, provided once at the app root. Throws when read outside a session scope
 * so an unauthenticated screen can never silently render authenticated UI.
 */
val LocalSession = compositionLocalOf<Session> {
    error("No Session provided. Wrap this content in ProvideSession.")
}

/** True when the current user holds [permission]. */
@Composable
fun hasPermission(permission: Permission): Boolean {
    val session = LocalSession.current
    return remember(session, permission) { session.can(permission) }
}

/**
 * Renders [content] only if the user holds [permission], otherwise [fallback] (nothing by default).
 *
 * This hides affordances a user cannot act on. It is not a security boundary — the backend must
 * enforce the same rules.
 */
@Composable
fun PermissionGate(
    permission: Permission,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (hasPermission(permission)) content() else fallback()
}

/** [PermissionGate] variant that passes when the user holds any of [permissions]. */
@Composable
fun AnyPermissionGate(
    vararg permissions: Permission,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val session = LocalSession.current
    if (permissions.any { session.can(it) }) content() else fallback()
}
