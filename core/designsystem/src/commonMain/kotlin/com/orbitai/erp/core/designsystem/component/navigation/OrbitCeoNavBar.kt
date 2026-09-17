package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.icon.OrbitIcons

object OrbitCeoNavIds {
    const val Dashboard = "ceo.dashboard"
    const val Projects = "ceo.projects"
    const val Assistant = "ceo.assistant"
    const val Approvals = "ceo.approvals"
    const val Messages = "ceo.messages"
}

/**
 * CEO: Dashboard · Projects · Orbit · Approvals · Inbox.
 *
 * Approvals is the fifth slot — pending CEO decisions (budgets, change orders, hires).
 * Alternatives: Reports, Finance, Team.
 */
@Immutable
object OrbitCeoNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitCeoNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    val Projects = OrbitNavItem(
        id = OrbitCeoNavIds.Projects,
        icon = OrbitIcons.Archive04,
        label = "Projects",
    )
    val Orbit = OrbitNavItem(
        id = OrbitCeoNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    val Approvals = OrbitNavItem(
        id = OrbitCeoNavIds.Approvals,
        icon = OrbitIcons.ReceiptIndianRupee,
        label = "Approvals",
    )
    val Inbox = OrbitNavItem(
        id = OrbitCeoNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
        contentDescription = "Inbox",
    )

    val items: List<OrbitNavItem> = listOf(Dashboard, Projects, Orbit, Approvals, Inbox)
}

@Composable
fun OrbitCeoNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitCeoNavItems.items
            .withNotificationBadge(OrbitCeoNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitCeoNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(
        items = items,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}
