package com.orbitai.erp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.data.session.FakeSessionRepository
import com.orbitai.erp.core.designsystem.component.brand.OrbitSplashScreen
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.avatar_01
import com.orbitai.erp.ui.PlaceholderScreen
import com.orbitai.erp.ui.auth.AuthFlowScreen
import com.orbitai.erp.ui.auth.AuthThemeToggle
import com.orbitai.erp.ui.ceo.CeoShellScreen
import com.orbitai.erp.platform.LockNativeSystemBars
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext

@Composable
fun App() {
    // Light by default. Override is in-memory so the account popover / auth toggle can preview
    // both themes without leaving the app; it resets on relaunch.
    var themeOverride by remember { mutableStateOf<Boolean?>(null) }
    val darkTheme = themeOverride ?: false
    var showSplash by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val ceoAvatar = painterResource(Res.drawable.avatar_01)

    val sessionRepository = remember {
        GlobalContext.get().get<FakeSessionRepository>()
    }
    val session by sessionRepository.session.collectAsState(initial = null)

    OrbitTheme(darkTheme = darkTheme) {
        LockNativeSystemBars()
        when {
            showSplash -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Themed fill so splash tracks the in-app toggle (window XML stays system-native).
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = OrbitTheme.colorScheme.background,
                    ) {
                        OrbitSplashScreen(
                            onFinished = {
                                // Yield so the splash coroutine finishes disposing before the next tree mounts.
                                scope.launch {
                                    yield()
                                    showSplash = false
                                }
                            },
                        )
                    }
                    AuthThemeToggle(
                        isDark = darkTheme,
                        onThemeChange = { themeOverride = it },
                    )
                }
            }

            session == null -> {
                AuthFlowScreen(
                    sessionRepository = sessionRepository,
                    onAuthenticated = {
                        // Session flow already updated; recomposition picks up the role shell.
                    },
                    isDark = darkTheme,
                    onThemeChange = { themeOverride = it },
                )
            }

            session!!.user.role == UserRole.Ceo -> {
                CeoShellScreen(
                    isDark = darkTheme,
                    onThemeChange = { themeOverride = it },
                    avatar = ceoAvatar,
                    onSignOut = {
                        scope.launch { sessionRepository.signOut() }
                    },
                )
            }

            else -> {
                PlaceholderScreen(
                    title = session!!.user.role.displayName,
                    subtitle = "Role shell coming next · ${session!!.user.fullName}",
                )
            }
        }
    }
}
