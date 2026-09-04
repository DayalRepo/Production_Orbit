package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
 * [bubble-chat](https://hugeicons.com/icon/bubble-chat?style=stroke-rounded).
 * Circle action: [brain-03](https://hugeicons.com/icon/brain-03?style=stroke-rounded).
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
        icon = OrbitIcons.BubbleChat,
        contentDescription = "Messages",
    )
    val Assistant = OrbitNavItem(
        id = OrbitCeoNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
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
) {
    OrbitBottomNavBar(
        primaryItems = OrbitCeoNavItems.primary,
        actionItem = OrbitCeoNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}
