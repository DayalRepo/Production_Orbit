package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.icon.OrbitNavSolidIcons

// ── Project Manager ──────────────────────────────────────────────────────────

object OrbitProjectManagerNavIds {
    const val Dashboard = "pm.dashboard"
    /** Ongoing tasks, issues, and material-order cards. */
    const val Ongoing = "pm.ongoing"
    const val Assistant = "pm.assistant"
    /** Scheduling, planning, assigning tasks, and raise-issue creation. */
    const val Plan = "pm.plan"
    const val Messages = "pm.messages"
}

@Immutable
object OrbitProjectManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Dashboard,
        icon = OrbitNavSolidIcons.DashboardCircle,
        label = "Home",
        contentDescription = "Home",
    )
    val Ongoing = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Ongoing,
        icon = OrbitNavSolidIcons.NotebookText,
        label = "Ongoing",
        contentDescription = "Ongoing work",
    )
    val Orbit = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Assistant,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "AI",
        contentDescription = "Orbit.ai",
        emphasized = true,
    )
    val Plan = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Plan,
        icon = OrbitNavSolidIcons.Calendar01,
        label = "Plan",
        contentDescription = "Plan and assign",
    )
    val Inbox = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Messages,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "Chat",
        contentDescription = "Chat",
    )
    val items = listOf(Dashboard, Ongoing, Orbit, Plan, Inbox)
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
    /** Assigned tasks and raise-issue cards. */
    const val Work = "se.work"
    const val Assistant = "se.assistant"
    /** Completed task cards and assigned issues. */
    const val Done = "se.done"
    const val Messages = "se.messages"
}

@Immutable
object OrbitSiteEngineerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Dashboard,
        icon = OrbitNavSolidIcons.DashboardCircle,
        label = "Home",
        contentDescription = "Home",
    )
    val Work = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Work,
        icon = OrbitNavSolidIcons.NotepadText,
        label = "Work",
        contentDescription = "Assigned work",
    )
    val Orbit = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Assistant,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "AI",
        contentDescription = "Orbit.ai",
        emphasized = true,
    )
    val Done = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Done,
        icon = OrbitNavSolidIcons.ClipboardCheck,
        label = "Done",
        contentDescription = "Completed work",
    )
    val Inbox = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Messages,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "Chat",
        contentDescription = "Chat",
    )
    val items = listOf(Dashboard, Work, Orbit, Done, Inbox)
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
    /** Assigned tasks and issues. */
    const val Work = "contractor.work"
    const val Assistant = "contractor.assistant"
    /** Invoices for done work and completed cards. */
    const val Invoices = "contractor.invoices"
    const val Messages = "contractor.messages"
}

@Immutable
object OrbitContractorNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitContractorNavIds.Dashboard,
        icon = OrbitNavSolidIcons.DashboardCircle,
        label = "Home",
        contentDescription = "Home",
    )
    val Work = OrbitNavItem(
        id = OrbitContractorNavIds.Work,
        icon = OrbitNavSolidIcons.NotepadText,
        label = "Work",
        contentDescription = "Assigned work",
    )
    val Orbit = OrbitNavItem(
        id = OrbitContractorNavIds.Assistant,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "AI",
        contentDescription = "Orbit.ai",
        emphasized = true,
    )
    val Invoices = OrbitNavItem(
        id = OrbitContractorNavIds.Invoices,
        icon = OrbitNavSolidIcons.ReceiptIndianRupee,
        label = "Invoice",
        contentDescription = "Invoice",
    )
    val Inbox = OrbitNavItem(
        id = OrbitContractorNavIds.Messages,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "Chat",
        contentDescription = "Chat",
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
        icon = OrbitNavSolidIcons.DashboardCircle,
        label = "Home",
        contentDescription = "Home",
    )
    val Store = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Inventory,
        icon = OrbitNavSolidIcons.Warehouse,
        label = "Store",
    )
    val Orbit = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Assistant,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "AI",
        contentDescription = "Orbit.ai",
        emphasized = true,
    )
    val Requests = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Requests,
        icon = OrbitNavSolidIcons.ListBullet,
        label = "Audit",
        contentDescription = "Audit",
    )
    val Inbox = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Messages,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "Chat",
        contentDescription = "Chat",
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
        icon = OrbitNavSolidIcons.DashboardCircle,
        label = "Home",
        contentDescription = "Home",
    )
    val Orders = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Orders,
        icon = OrbitNavSolidIcons.ShoppingCart,
        label = "Orders",
    )
    val Orbit = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Assistant,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "AI",
        contentDescription = "Orbit.ai",
        emphasized = true,
    )
    val Approvals = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Approvals,
        icon = OrbitNavSolidIcons.ReceiptIndianRupee,
        label = "Invoice",
        contentDescription = "Invoice",
    )
    val Inbox = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Messages,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "Chat",
        contentDescription = "Chat",
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
    /** Task and issue inspections in progress. */
    const val Inspect = "qaqc.inspect"
    const val Assistant = "qaqc.assistant"
    /** Completed inspected task and issue cards. */
    const val Done = "qaqc.done"
    const val Messages = "qaqc.messages"
}

@Immutable
object OrbitQaQcNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitQaQcNavIds.Dashboard,
        icon = OrbitNavSolidIcons.DashboardCircle,
        label = "Home",
        contentDescription = "Home",
    )
    val Inspect = OrbitNavItem(
        id = OrbitQaQcNavIds.Inspect,
        icon = OrbitNavSolidIcons.ClipboardCheck,
        label = "Inspect",
        contentDescription = "Inspections",
    )
    val Orbit = OrbitNavItem(
        id = OrbitQaQcNavIds.Assistant,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "AI",
        contentDescription = "Orbit.ai",
        emphasized = true,
    )
    val Done = OrbitNavItem(
        id = OrbitQaQcNavIds.Done,
        icon = OrbitNavSolidIcons.NotebookText,
        label = "Done",
        contentDescription = "Completed inspections",
    )
    val Inbox = OrbitNavItem(
        id = OrbitQaQcNavIds.Messages,
        icon = OrbitNavSolidIcons.BubbleChat,
        label = "Chat",
        contentDescription = "Chat",
    )
    val items = listOf(Dashboard, Inspect, Orbit, Done, Inbox)
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
