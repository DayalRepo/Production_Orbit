package com.orbitai.erp.ui.component.badge

import androidx.compose.ui.graphics.vector.ImageVector
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.model.ApprovalStatus
import com.orbitai.erp.core.model.ProjectHealth
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.StockLevel
import com.orbitai.erp.core.model.WorkStatus

/**
 * Every badge the product uses, as one closed list.
 *
 * This is the app-level catalogue, deliberately separate from `:core:designsystem`. The design
 * system owns the pill — geometry, glass, contrast — and knows nothing about site work; this file
 * owns the vocabulary. Splitting them is what stops `OrbitBadge` from growing a parameter every
 * time the domain gains a state, and what lets the same pill be reused by a screen that has no
 * concept of a work status at all.
 *
 * Two rules kept this list honest:
 *
 * A tone is never chosen for variety. States that mean the same thing to a reader share a tone —
 * Successful, Done, Approved and Completed are all green, because a Site Engineer scanning a list
 * should not have to learn that green and teal both mean "finished". Where two states are
 * genuinely different but adjacent in the workflow, the icon carries the difference.
 *
 * Nothing here relies on hue alone. Every entry pairs a tone with a distinct glyph, so the badge
 * survives greyscale, a colour-blind reader, and the dim outdoor screen these are actually read on
 * (WCAG 1.4.1).
 */
enum class BadgeKind(val label: String, val tone: OrbitBadgeTone) {
    // Active work.
    InProgress("In progress", OrbitBadgeTone.Blue),
    Started("Started", OrbitBadgeTone.Blue),
    Paused("Paused", OrbitBadgeTone.Amber),
    Pending("Pending", OrbitBadgeTone.Amber),

    // Review and inspection.
    Review("In review", OrbitBadgeTone.Violet),
    Inspection("Inspection", OrbitBadgeTone.Violet),

    // Finished well. All one tone on purpose — see the class note.
    Successful("Successful", OrbitBadgeTone.Green),
    Done("Done", OrbitBadgeTone.Green),
    Approved("Approved", OrbitBadgeTone.Green),
    Completed("Completed", OrbitBadgeTone.Green),

    // Finished badly.
    Failed("Failed", OrbitBadgeTone.Red),
    Cancelled("Cancelled", OrbitBadgeTone.Rose),
    Issue("Issue", OrbitBadgeTone.Red),
    Deleted("Deleted", OrbitBadgeTone.Red),

    // Documents and submissions.
    Submitted("Submitted", OrbitBadgeTone.Indigo),
    Upload("Uploaded", OrbitBadgeTone.Indigo),

    // Time.
    Scheduled("Scheduled", OrbitBadgeTone.Cyan),
    Delayed("Delayed", OrbitBadgeTone.Orange),
    Overdue("Overdue", OrbitBadgeTone.Rose),

    // Record lifecycle.
    Created("Created", OrbitBadgeTone.Slate),
    Added("Added", OrbitBadgeTone.Teal),
    Restored("Restored", OrbitBadgeTone.Teal),
    Missing("Missing", OrbitBadgeTone.Orange),
    Status("Status", OrbitBadgeTone.Slate),
    ;

    /**
     * Resolved on access rather than passed to the constructor, so opening this enum does not
     * force all 27 [ImageVector]s to be built. Each one is lazy inside [OrbitIcons], and a screen
     * showing six badges should pay for six glyphs.
     */
    val icon: ImageVector
        get() = when (this) {
            InProgress -> OrbitIcons.Progress
            Started -> OrbitIcons.Play
            Paused -> OrbitIcons.Pause
            Pending -> OrbitIcons.Clock
            Review -> OrbitIcons.ListBullet
            Inspection -> OrbitIcons.NotepadDashed
            Successful, Done, Approved -> OrbitIcons.CheckmarkBadge
            Completed -> OrbitIcons.Tick
            Failed, Cancelled -> OrbitIcons.CancelCircle
            Issue -> OrbitIcons.BadgeAlert
            Deleted -> OrbitIcons.Delete
            Submitted -> OrbitIcons.Mail
            Upload -> OrbitIcons.Upload
            Scheduled -> OrbitIcons.Calendar
            Delayed -> OrbitIcons.TimeQuarter
            Overdue -> OrbitIcons.StopWatch
            Created -> OrbitIcons.Bookmark
            Added -> OrbitIcons.Add
            Restored -> OrbitIcons.Repeat
            Missing -> OrbitIcons.Puzzle
            Status -> OrbitIcons.Status
        }
}

/**
 * Severity gets its own scale rather than a [BadgeKind] entry per level.
 *
 * The battery glyphs are a filling gauge, so the four levels read as one ascending measure even
 * before the colour registers — which is the point of a severity indicator and something four
 * unrelated icons could not do.
 */
val Severity.badgeTone: OrbitBadgeTone
    get() = when (this) {
        Severity.Low -> OrbitBadgeTone.Teal
        Severity.Medium -> OrbitBadgeTone.Amber
        Severity.High -> OrbitBadgeTone.Orange
        Severity.Critical -> OrbitBadgeTone.Red
    }

val Severity.badgeIcon: ImageVector
    get() = when (this) {
        Severity.Low -> OrbitIcons.BatteryLow
        Severity.Medium -> OrbitIcons.BatteryMedium
        Severity.High -> OrbitIcons.BatteryHigh
        Severity.Critical -> OrbitIcons.BatteryFull
    }

// ---------------------------------------------------------------- domain mappings

val WorkStatus.badgeKind: BadgeKind
    get() = when (this) {
        WorkStatus.Open -> BadgeKind.Created
        WorkStatus.InProgress -> BadgeKind.InProgress
        // Blocked has no dedicated badge: to a reader it is an issue, and giving it a separate
        // glyph would imply a distinction the screens do not actually make.
        WorkStatus.Blocked -> BadgeKind.Issue
        WorkStatus.InReview -> BadgeKind.Review
        WorkStatus.Completed -> BadgeKind.Completed
        WorkStatus.Cancelled -> BadgeKind.Cancelled
    }

val ApprovalStatus.badgeKind: BadgeKind
    get() = when (this) {
        ApprovalStatus.Draft -> BadgeKind.Created
        ApprovalStatus.Pending -> BadgeKind.Pending
        ApprovalStatus.Approved -> BadgeKind.Approved
        ApprovalStatus.Rejected -> BadgeKind.Failed
    }

val StockLevel.badgeKind: BadgeKind
    get() = when (this) {
        StockLevel.Healthy -> BadgeKind.Done
        StockLevel.Low -> BadgeKind.Delayed
        StockLevel.OutOfStock -> BadgeKind.Missing
    }

val ProjectHealth.badgeKind: BadgeKind
    get() = when (this) {
        ProjectHealth.OnTrack -> BadgeKind.InProgress
        ProjectHealth.AtRisk -> BadgeKind.Issue
        ProjectHealth.Delayed -> BadgeKind.Delayed
    }
