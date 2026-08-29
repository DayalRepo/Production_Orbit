package com.orbitai.erp.navigation

import com.orbitai.erp.core.model.Permission
import kotlinx.serialization.Serializable

/**
 * Top-level destinations. Each carries the [Permission] that grants access, so the role-aware
 * navigation shell can derive a user's tabs from their permissions rather than a per-role list.
 */
@Serializable
sealed interface TopLevelDestination {
    @Serializable
    data object Dashboard : TopLevelDestination

    @Serializable
    data object SiteUpdates : TopLevelDestination

    @Serializable
    data object Tasks : TopLevelDestination

    @Serializable
    data object Issues : TopLevelDestination

    @Serializable
    data object Team : TopLevelDestination

    @Serializable
    data object Materials : TopLevelDestination

    @Serializable
    data object Procurement : TopLevelDestination

    @Serializable
    data object Invoices : TopLevelDestination

    @Serializable
    data object Inbox : TopLevelDestination

    @Serializable
    data object AuditLog : TopLevelDestination
}

/** Metadata the navigation shell needs to render an entry for a destination. */
data class TopLevelEntry(
    val destination: TopLevelDestination,
    val title: String,
    val requiredPermission: Permission,
)

val topLevelEntries: List<TopLevelEntry> = listOf(
    TopLevelEntry(TopLevelDestination.Dashboard, "Dashboard", Permission.ViewProjectDashboard),
    TopLevelEntry(TopLevelDestination.SiteUpdates, "Site Updates", Permission.ViewSiteUpdates),
    TopLevelEntry(TopLevelDestination.Tasks, "Tasks", Permission.ViewTasks),
    TopLevelEntry(TopLevelDestination.Issues, "Issues", Permission.ViewIssues),
    TopLevelEntry(TopLevelDestination.Team, "Team", Permission.ViewTeam),
    TopLevelEntry(TopLevelDestination.Materials, "Materials", Permission.ViewMaterials),
    TopLevelEntry(TopLevelDestination.Procurement, "Procurement", Permission.ViewProcurement),
    TopLevelEntry(TopLevelDestination.Invoices, "Invoices", Permission.ViewInvoices),
    TopLevelEntry(TopLevelDestination.Inbox, "Inbox", Permission.ViewInbox),
    TopLevelEntry(TopLevelDestination.AuditLog, "Audit Log", Permission.ViewAuditLog),
)
