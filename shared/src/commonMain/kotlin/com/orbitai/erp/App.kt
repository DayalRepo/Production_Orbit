package com.orbitai.erp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbitai.erp.core.data.session.SessionRepository
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.rbac.LocalSession
import com.orbitai.erp.ui.PlaceholderScreen
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject

@Composable
fun App() {
    OrbitTheme {
        val sessionRepository: SessionRepository = koinInject()
        val sessionFlow: Flow<com.orbitai.erp.core.model.Session?> =
            remember(sessionRepository) { sessionRepository.session }
        val session by sessionFlow.collectAsStateWithLifecycle(initialValue = null)

        val currentSession = session
        if (currentSession == null) {
            PlaceholderScreen(
                title = "OrbitAI",
                subtitle = "Sign-in is not implemented yet.",
            )
        } else {
            CompositionLocalProvider(LocalSession provides currentSession) {
                PlaceholderScreen(
                    title = "Welcome, ${currentSession.user.fullName}",
                    subtitle = "${currentSession.user.role.displayName} · " +
                        "${currentSession.permissions.size} permissions granted",
                )
            }
        }
    }
}
