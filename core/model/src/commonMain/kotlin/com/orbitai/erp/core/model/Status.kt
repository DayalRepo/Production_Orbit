package com.orbitai.erp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Lifecycle shared by tasks and issues so one status component can render both. */
@Serializable
enum class WorkStatus {
    @SerialName("open")
    Open,

    @SerialName("in_progress")
    InProgress,

    @SerialName("blocked")
    Blocked,

    @SerialName("in_review")
    InReview,

    @SerialName("completed")
    Completed,

    @SerialName("cancelled")
    Cancelled,
    ;

    val displayName: String
        get() = when (this) {
            Open -> "Open"
            InProgress -> "In Progress"
            Blocked -> "Blocked"
            InReview -> "In Review"
            Completed -> "Completed"
            Cancelled -> "Cancelled"
        }

    val isTerminal: Boolean get() = this == Completed || this == Cancelled
}

@Serializable
enum class Priority {
    @SerialName("low")
    Low,

    @SerialName("medium")
    Medium,

    @SerialName("high")
    High,

    @SerialName("urgent")
    Urgent,
    ;

    val displayName: String
        get() = when (this) {
            Low -> "Low"
            Medium -> "Medium"
            High -> "High"
            Urgent -> "Urgent"
        }
}

/** Defect severity used by QA/QC inspections. */
@Serializable
enum class Severity {
    @SerialName("low")
    Low,

    @SerialName("medium")
    Medium,

    @SerialName("high")
    High,

    @SerialName("critical")
    Critical,
    ;

    val displayName: String
        get() = when (this) {
            Low -> "Low"
            Medium -> "Medium"
            High -> "High"
            Critical -> "Critical"
        }
}

/** Red/amber/green project health, shown on executive and project dashboards. */
@Serializable
enum class ProjectHealth {
    @SerialName("on_track")
    OnTrack,

    @SerialName("at_risk")
    AtRisk,

    @SerialName("delayed")
    Delayed,
    ;

    val displayName: String
        get() = when (this) {
            OnTrack -> "On Track"
            AtRisk -> "At Risk"
            Delayed -> "Delayed"
        }
}

/** Inventory position for a material line item. */
@Serializable
enum class StockLevel {
    @SerialName("healthy")
    Healthy,

    @SerialName("low")
    Low,

    @SerialName("out_of_stock")
    OutOfStock,
    ;

    val displayName: String
        get() = when (this) {
            Healthy -> "In Stock"
            Low -> "Low Stock"
            OutOfStock -> "Out of Stock"
        }
}

/** Approval lifecycle shared by purchase orders, invoices and material requests. */
@Serializable
enum class ApprovalStatus {
    @SerialName("draft")
    Draft,

    @SerialName("pending")
    Pending,

    @SerialName("approved")
    Approved,

    @SerialName("rejected")
    Rejected,
    ;

    val displayName: String
        get() = when (this) {
            Draft -> "Draft"
            Pending -> "Pending Approval"
            Approved -> "Approved"
            Rejected -> "Rejected"
        }
}
