package com.orbitai.erp.ui.ceo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.designsystem.component.navigation.OrbitCeoNavBar
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * CEO role shell: swipeable pages synced with [OrbitCeoNavBar].
 *
 * Splash finishes in [com.orbitai.erp.App] before this mounts. Theme override is owned by the app
 * root so the account popover on Dashboard can flip light/dark for the whole tree.
 */
@Composable
fun CeoShellScreen(
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
    notificationCount: Int = 0,
    onSignOut: () -> Unit = {},
) {
    val destinations = remember { CeoDestination.entries }
    val pagerState = rememberPagerState(pageCount = { destinations.size })
    val scope = rememberCoroutineScope()
    var selectedPage by remember { mutableIntStateOf(0) }
    val ceo = remember { MockDirectory.userById("u-ceo") }

    // Keep the floating nav selection in lockstep with a finger-swipe settle.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> selectedPage = page }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = OrbitTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1,
                key = { page -> destinations[page].id },
            ) { page ->
                when (destinations[page]) {
                    CeoDestination.Dashboard -> CeoDashboardScreen(
                        userName = ceo.fullName,
                        userRole = ceo.role.shortLabel,
                        userPhone = ceo.phone,
                        avatar = avatar,
                        isDark = isDark,
                        onThemeChange = onThemeChange,
                        onSignOut = onSignOut,
                    )
                    CeoDestination.Projects -> CeoProjectsScreen()
                    CeoDestination.Messages -> CeoMessagesScreen()
                    CeoDestination.Assistant -> CeoAssistantScreen()
                }
            }

            OrbitCeoNavBar(
                selectedId = destinations[selectedPage].id,
                onSelect = { id ->
                    val index = destinations.indexOf(CeoDestination.fromId(id))
                    if (index >= 0 && index != selectedPage) {
                        selectedPage = index
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
                notificationCount = notificationCount,
            )
        }
    }
}
