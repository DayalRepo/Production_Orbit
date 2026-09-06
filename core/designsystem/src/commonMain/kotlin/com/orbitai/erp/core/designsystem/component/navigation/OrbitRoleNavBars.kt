package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.icon.OrbitIcons

// ── Project Manager ──────────────────────────────────────────────────────────

object OrbitProjectManagerNavIds {
    const val Dashboard = "pm.dashboard"
    const val Tasks = "pm.tasks"
    const val Messages = "pm.messages"
    const val Assistant = "pm.assistant"
}

/**
 * Project Manager icon set.
 *
 * Primary: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [note-add](https://hugeicons.com/icon/note-add?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitProjectManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Tasks = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Tasks,
        icon = OrbitIcons.NoteAdd,
        contentDescription = "Tasks",
    )
    val Messages = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitProjectManagerNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Tasks, Messages)
    val action: OrbitNavItem = Assistant
}

@Composable
fun OrbitProjectManagerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitProjectManagerNavItems.primary.withNotificationBadge(
            messageId = OrbitProjectManagerNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitProjectManagerNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}

// ── Site Engineer ────────────────────────────────────────────────────────────

object OrbitSiteEngineerNavIds {
    const val Dashboard = "se.dashboard"
    const val Notes = "se.notes"
    const val Messages = "se.messages"
    const val Assistant = "se.assistant"
}

/**
 * Site Engineer icon set.
 *
 * Primary: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [notepad-text](https://hugeicons.com/icon/notepad-text?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitSiteEngineerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Notes = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Notes,
        icon = OrbitIcons.NotepadText,
        contentDescription = "Notes",
    )
    val Messages = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitSiteEngineerNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Notes, Messages)
    val action: OrbitNavItem = Assistant
}

@Composable
fun OrbitSiteEngineerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitSiteEngineerNavItems.primary.withNotificationBadge(
            messageId = OrbitSiteEngineerNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitSiteEngineerNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}

// ── Contractor ───────────────────────────────────────────────────────────────

object OrbitContractorNavIds {
    const val Dashboard = "contractor.dashboard"
    const val Notes = "contractor.notes"
    const val Messages = "contractor.messages"
    const val Assistant = "contractor.assistant"
}

/**
 * Contractor icon set — same layout glyphs as Site Engineer, separate ids for routing.
 *
 * Primary: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [notepad-text](https://hugeicons.com/icon/notepad-text?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitContractorNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitContractorNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Notes = OrbitNavItem(
        id = OrbitContractorNavIds.Notes,
        icon = OrbitIcons.NotepadText,
        contentDescription = "Notes",
    )
    val Messages = OrbitNavItem(
        id = OrbitContractorNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitContractorNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Notes, Messages)
    val action: OrbitNavItem = Assistant
}

@Composable
fun OrbitContractorNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitContractorNavItems.primary.withNotificationBadge(
            messageId = OrbitContractorNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitContractorNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}

// ── Warehouse Manager ────────────────────────────────────────────────────────

object OrbitWarehouseManagerNavIds {
    const val Dashboard = "warehouse.dashboard"
    const val Inventory = "warehouse.inventory"
    const val Messages = "warehouse.messages"
    const val Assistant = "warehouse.assistant"
}

/**
 * Warehouse Manager icon set.
 *
 * Primary: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [warehouse](https://hugeicons.com/icon/warehouse?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitWarehouseManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Inventory = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Inventory,
        icon = OrbitIcons.Warehouse,
        contentDescription = "Warehouse",
    )
    val Messages = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitWarehouseManagerNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Inventory, Messages)
    val action: OrbitNavItem = Assistant
}

@Composable
fun OrbitWarehouseManagerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitWarehouseManagerNavItems.primary.withNotificationBadge(
            messageId = OrbitWarehouseManagerNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitWarehouseManagerNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}

// ── Procurement Manager ──────────────────────────────────────────────────────

object OrbitProcurementManagerNavIds {
    const val Dashboard = "procurement.dashboard"
    const val Orders = "procurement.orders"
    const val Messages = "procurement.messages"
    const val Assistant = "procurement.assistant"
}

/**
 * Procurement Manager icon set.
 *
 * Primary: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [shopping-cart-add-01](https://hugeicons.com/icon/shopping-cart-add-01?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitProcurementManagerNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Orders = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Orders,
        icon = OrbitIcons.ShoppingCartAdd01,
        contentDescription = "Orders",
    )
    val Messages = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitProcurementManagerNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Orders, Messages)
    val action: OrbitNavItem = Assistant
}

@Composable
fun OrbitProcurementManagerNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitProcurementManagerNavItems.primary.withNotificationBadge(
            messageId = OrbitProcurementManagerNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitProcurementManagerNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}

// ── QA / QC ──────────────────────────────────────────────────────────────────

object OrbitQaQcNavIds {
    const val Dashboard = "qaqc.dashboard"
    const val Inspections = "qaqc.inspections"
    const val Messages = "qaqc.messages"
    const val Assistant = "qaqc.assistant"
}

/**
 * QA/QC icon set.
 *
 * Primary: [dashboard-circle](https://hugeicons.com/icon/dashboard-circle?style=stroke-rounded),
 * [badge-check](https://hugeicons.com/icon/badge-check?style=stroke-rounded),
 * [bell-dot](https://hugeicons.com/icon/bell-dot?style=stroke-rounded).
 * Circle: Orbit pixel brand mark (AI assistant).
 */
@Immutable
object OrbitQaQcNavItems {
    val Dashboard = OrbitNavItem(
        id = OrbitQaQcNavIds.Dashboard,
        icon = OrbitIcons.DashboardCircle,
        contentDescription = "Dashboard",
    )
    val Inspections = OrbitNavItem(
        id = OrbitQaQcNavIds.Inspections,
        icon = OrbitIcons.BadgeCheck,
        contentDescription = "Quality",
    )
    val Messages = OrbitNavItem(
        id = OrbitQaQcNavIds.Messages,
        icon = OrbitIcons.BellDot,
        contentDescription = "Notifications",
    )
    val Assistant = OrbitNavItem(
        id = OrbitQaQcNavIds.Assistant,
        icon = OrbitIcons.Brain03,
        contentDescription = "Assistant",
        brandMark = true,
    )

    val primary: List<OrbitNavItem> = listOf(Dashboard, Inspections, Messages)
    val action: OrbitNavItem = Assistant
}

@Composable
fun OrbitQaQcNavBar(
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    applyNavigationBarInset: Boolean = true,
    notificationCount: Int = 0,
) {
    val primary = remember(notificationCount) {
        OrbitQaQcNavItems.primary.withNotificationBadge(
            messageId = OrbitQaQcNavIds.Messages,
            count = notificationCount,
        )
    }
    OrbitBottomNavBar(
        primaryItems = primary,
        actionItem = OrbitQaQcNavItems.action,
        selectedId = selectedId,
        onSelect = onSelect,
        modifier = modifier,
        applyNavigationBarInset = applyNavigationBarInset,
    )
}
