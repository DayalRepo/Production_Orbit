package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.icon.OrbitIcons

// ── Project Manager ──────────────────────────────────────────────────────────

object OrbitProjectManagerNavIds {
    const val Dashboard = "pm.dashboard"
    const val Work = "pm.work"
    const val Assistant = "pm.assistant"
    const val Issues = "pm.issues"
    const val Messages = "pm.messages"
}

@Immutable
object OrbitProjectManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    /** Create tasks, raise issues, schedule, order materials — PM work hub. */
    val Work = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Work,
        icon = OrbitIcons.NoteAdd,
        label = "Work",
    )
    val Orbit = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    /** Open issue queue / escalations (separate from creating work). */
    val Issues = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Issues,
        icon = OrbitIcons.BadgeAlert,
        label = "Issues",
    )
    val Inbox = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
    )
    val items = listOf(Dashboard, Work, Orbit, Issues, Inbox)
}

@Composable
fun OrbitProjectManagerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitProjectManagerNavItems.items
            .withNotificationBadge(OrbitProjectManagerNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitProjectManagerNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(items, selectedId, onSelect, modifier, applyNavigationBarInset)
}

// ── Site Engineer ────────────────────────────────────────────────────────────

object OrbitSiteEngineerNavIds {
    const val Dashboard = "se.dashboard"
    const val Work = "se.work"
    const val Assistant = "se.assistant"
    const val Issues = "se.issues"
    const val Messages = "se.messages"
}

@Immutable
object OrbitSiteEngineerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    /** Assigned tasks and site work to execute. */
    val Work = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Work,
        icon = OrbitIcons.NotepadText,
        label = "Work",
    )
    val Orbit = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    /** Issues raised on site / assigned to resolve. */
    val Issues = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Issues,
        icon = OrbitIcons.BadgeAlert,
        label = "Issues",
    )
    val Inbox = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
    )
    val items = listOf(Dashboard, Work, Orbit, Issues, Inbox)
}

@Composable
fun OrbitSiteEngineerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitSiteEngineerNavItems.items
            .withNotificationBadge(OrbitSiteEngineerNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitSiteEngineerNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(items, selectedId, onSelect, modifier, applyNavigationBarInset)
}

// ── Contractor ───────────────────────────────────────────────────────────────

object OrbitContractorNavIds {
    const val Dashboard = "contractor.dashboard"
    const val Work = "contractor.work"
    const val Assistant = "contractor.assistant"
    const val Invoices = "contractor.invoices"
    const val Messages = "contractor.messages"
}

@Immutable
object OrbitContractorNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitContractorNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    /** Assigned tasks and issues to execute. */
    val Work = OrbitNavItem(
        id = OrbitContractorNavIds.Work,
        icon = OrbitIcons.NotepadText,
        label = "Work",
    )
    val Orbit = OrbitNavItem(
        id = OrbitContractorNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    val Invoices = OrbitNavItem(
        id = OrbitContractorNavIds.Invoices,
        icon = OrbitIcons.ReceiptIndianRupee,
        label = "Invoices",
    )
    val Inbox = OrbitNavItem(
        id = OrbitContractorNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
    )
    val items = listOf(Dashboard, Work, Orbit, Invoices, Inbox)
}

@Composable
fun OrbitContractorNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitContractorNavItems.items
            .withNotificationBadge(OrbitContractorNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitContractorNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(items, selectedId, onSelect, modifier, applyNavigationBarInset)
}

// ── Warehouse Manager ────────────────────────────────────────────────────────

object OrbitWarehouseManagerNavIds {
    const val Dashboard = "warehouse.dashboard"
    const val Inventory = "warehouse.inventory"
    const val Assistant = "warehouse.assistant"
    const val Requests = "warehouse.requests"
    const val Messages = "warehouse.messages"
}

@Immutable
object OrbitWarehouseManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    val Store = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Inventory,
        icon = OrbitIcons.Warehouse,
        label = "Store",
    )
    val Orbit = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    val Requests = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Requests,
        icon = OrbitIcons.ListBullet,
        label = "Requests",
    )
    val Inbox = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
    )
    val items = listOf(Dashboard, Store, Orbit, Requests, Inbox)
}

@Composable
fun OrbitWarehouseManagerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitWarehouseManagerNavItems.items
            .withNotificationBadge(OrbitWarehouseManagerNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitWarehouseManagerNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(items, selectedId, onSelect, modifier, applyNavigationBarInset)
}

// ── Procurement Manager ──────────────────────────────────────────────────────

object OrbitProcurementManagerNavIds {
    const val Dashboard = "procurement.dashboard"
    const val Orders = "procurement.orders"
    const val Assistant = "procurement.assistant"
    const val Approvals = "procurement.approvals"
    const val Messages = "procurement.messages"
}

@Immutable
object OrbitProcurementManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    val Orders = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Orders,
        icon = OrbitIcons.ShoppingCart,
        label = "Orders",
    )
    val Orbit = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    val Approvals = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Approvals,
        icon = OrbitIcons.ReceiptIndianRupee,
        label = "Approvals",
    )
    val Inbox = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
    )
    val items = listOf(Dashboard, Orders, Orbit, Approvals, Inbox)
}

@Composable
fun OrbitProcurementManagerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitProcurementManagerNavItems.items
            .withNotificationBadge(OrbitProcurementManagerNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitProcurementManagerNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(items, selectedId, onSelect, modifier, applyNavigationBarInset)
}

// ── QA / QC ──────────────────────────────────────────────────────────────────

object OrbitQaQcNavIds {
    const val Dashboard = "qaqc.dashboard"
    const val Inspections = "qaqc.inspections"
    const val Assistant = "qaqc.assistant"
    const val Issues = "qaqc.issues"
    const val Messages = "qaqc.messages"
}

@Immutable
object OrbitQaQcNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitQaQcNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        label = "Dashboard",
    )
    val Checks = OrbitNavItem(
        id = OrbitQaQcNavIds.Inspections,
        icon = OrbitIcons.BadgeCheck,
        label = "Checks",
    )
    val Orbit = OrbitNavItem(
        id = OrbitQaQcNavIds.Assistant,
        icon = OrbitIcons.BubbleChat,
        label = "Orbit",
        contentDescription = "Orbit AI",
        emphasized = true,
    )
    val Issues = OrbitNavItem(
        id = OrbitQaQcNavIds.Issues,
        icon = OrbitIcons.BadgeAlert,
        label = "Issues",
    )
    val Inbox = OrbitNavItem(
        id = OrbitQaQcNavIds.Messages,
        icon = OrbitIcons.BubbleChat,
        label = "Inbox",
    )
    val items = listOf(Dashboard, Checks, Orbit, Issues, Inbox)
}

@Composable
fun OrbitQaQcNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
    insightCount: Int = 0,
) {
    val items = remember(notificationCount, insightCount) {
        OrbitQaQcNavItems.items
            .withNotificationBadge(OrbitQaQcNavIds.Messages, notificationCount)
            .withInsightBadge(OrbitQaQcNavIds.Assistant, insightCount)
    }
    OrbitBottomNavBar(items, selectedId, onSelect, modifier, applyNavigationBarInset)
}
