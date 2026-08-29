package com.orbitai.erp.core.model

/**
 * Capabilities a role may hold. The UI gates on these rather than on [UserRole] directly, so a
 * permission change never requires hunting down role checks scattered across screens.
 */
enum class Permission {
    ViewExecutiveDashboard,
    ViewProjectDashboard,

    ViewSiteUpdates,
    CreateSiteUpdate,
    ApproveSiteUpdate,

    ViewTasks,
    CreateTask,
    AssignTask,
    UpdateTaskProgress,
    CloseTask,

    ViewIssues,
    ReportIssue,
    AssignIssue,
    ResolveIssue,
    VerifyIssue,

    ViewTeam,
    ManageTeam,
    ManageRoles,

    ViewMaterials,
    RecordMaterialUsage,
    ManageInventory,
    ApproveMaterialRequest,

    ViewProcurement,
    CreatePurchaseOrder,
    ApprovePurchaseOrder,

    ViewInvoices,
    CreateInvoice,
    ApproveInvoice,

    ViewAuditLog,
    ExportReports,

    ViewInbox,
    BroadcastMessage,

    ViewAiInsights,
    ApplyAiRecommendation,
    ;

    companion object {
        /**
         * Static role-to-permission matrix. Server-side authorisation remains the source of
         * truth; this exists so the UI can hide affordances a user cannot act on.
         */
        fun forRole(role: UserRole): Set<Permission> = when (role) {
            UserRole.Ceo -> setOf(
                ViewExecutiveDashboard, ViewProjectDashboard,
                ViewSiteUpdates,
                ViewTasks, ViewIssues,
                ViewTeam, ManageTeam, ManageRoles,
                ViewMaterials, ViewProcurement, ApprovePurchaseOrder,
                ViewInvoices, ApproveInvoice,
                ViewAuditLog, ExportReports,
                ViewInbox, BroadcastMessage,
                ViewAiInsights, ApplyAiRecommendation,
            )

            UserRole.ProjectManager -> setOf(
                ViewProjectDashboard,
                ViewSiteUpdates, CreateSiteUpdate, ApproveSiteUpdate,
                ViewTasks, CreateTask, AssignTask, UpdateTaskProgress, CloseTask,
                ViewIssues, ReportIssue, AssignIssue, ResolveIssue,
                ViewTeam, ManageTeam,
                ViewMaterials, ApproveMaterialRequest,
                ViewProcurement, CreatePurchaseOrder,
                ViewInvoices, CreateInvoice,
                ViewAuditLog, ExportReports,
                ViewInbox, BroadcastMessage,
                ViewAiInsights, ApplyAiRecommendation,
            )

            UserRole.SiteEngineer -> setOf(
                ViewProjectDashboard,
                ViewSiteUpdates, CreateSiteUpdate,
                ViewTasks, UpdateTaskProgress,
                ViewIssues, ReportIssue, ResolveIssue,
                ViewTeam,
                ViewMaterials, RecordMaterialUsage,
                ViewInbox,
                ViewAiInsights,
            )

            UserRole.Contractor -> setOf(
                ViewSiteUpdates, CreateSiteUpdate,
                ViewTasks, UpdateTaskProgress,
                ViewIssues, ReportIssue,
                ViewMaterials, RecordMaterialUsage,
                ViewInvoices, CreateInvoice,
                ViewInbox,
            )

            UserRole.QaQc -> setOf(
                ViewProjectDashboard,
                ViewSiteUpdates,
                ViewTasks,
                ViewIssues, ReportIssue, VerifyIssue,
                ViewTeam,
                ViewMaterials,
                ViewAuditLog, ExportReports,
                ViewInbox,
                ViewAiInsights,
            )

            UserRole.WarehouseManager -> setOf(
                ViewProjectDashboard,
                ViewTasks,
                ViewIssues, ReportIssue,
                ViewMaterials, RecordMaterialUsage, ManageInventory, ApproveMaterialRequest,
                ViewProcurement,
                ViewAuditLog, ExportReports,
                ViewInbox,
                ViewAiInsights, ApplyAiRecommendation,
            )

            UserRole.ProcurementManager -> setOf(
                ViewProjectDashboard,
                ViewTasks,
                ViewIssues,
                ViewMaterials, ApproveMaterialRequest,
                ViewProcurement, CreatePurchaseOrder, ApprovePurchaseOrder,
                ViewInvoices, CreateInvoice,
                ViewAuditLog, ExportReports,
                ViewInbox,
                ViewAiInsights, ApplyAiRecommendation,
            )
        }
    }
}
