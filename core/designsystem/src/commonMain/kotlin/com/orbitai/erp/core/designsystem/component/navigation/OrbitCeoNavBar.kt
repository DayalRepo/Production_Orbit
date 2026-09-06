package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.icon.OrbitIcons

/**
 * Stable destination ids for the CEO floating bottom nav.
 *
 * Kept as constants so screens, deep links and API token mappings can refer to the same keys without
 * depending on display order.
 */
object OrbitCeoNavIds {
    const val Dashboard = "ceo.dashboard"
    const val Projects = "ceo.projects"
    const val Messages = "ceo.messages"
    const val Assistant = "ceo.assistant"
}

/**
 * CEO icon set for [OrbitCeoNavBar].
 *
 * Primary pill: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [layers-01](https://hugeicons.com/icon/layers-01?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle action: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitCeoNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitCeoNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Projects = OrbitNavItem(
        id = OrbitCeoNavIds.Projects,
        icon = OrbitIcons.Layers01,
        contentDescription = "Projects",
    )
    val Messages = OrbitNavItem(
        id = OrbitCeoNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitCeoNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Projects, Messages)
    val action: OrbitNavItem = Assistant
}

/**
 * CEO role floating bottom nav — icons only, no labels.
 *
 * Separate from other role bars (PM, Site Engineer, …) so each role can ship its own icon map
 * without branching inside the shared [OrbitBottomNavBar] layout.
 */
@Composable
fun OrbitCeoNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitCeoNavItems.primary.withNotificationBadge(
            messageId = OrbitCeoNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitCeoNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}
