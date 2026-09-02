package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitStep
import com.orbitai.erp.core.designsystem.component.progress.OrbitStepIndicator
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.progress.ProgressCard

@Composable
internal fun ProgressGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        ProgressCard(
            label = "Progress",
            progress = 0.71f,
            delta = 14f,
        )
        ProgressCard(
            label = "Remaining",
            progress = 0.29f,
            delta = -14f,
            higherIsBetter = false,
        )
        ProgressCard(
            label = null,
            progress = 0.52f,
            delta = 3f,
            contentDescription = "Progress, 52 percent, up 3 percent vs last week",
        )
    }

    GallerySection("Rounding · the four readings that matter") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            listOf(
                0f to "0% · nothing lit",
                0.01f to "1% · one slat, never none",
                0.99f to "99% · one slat dark, never full",
                1f to "100% · every slat lit",
            ).forEach { (value, caption) ->
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = caption,
                        style = OrbitTheme.typography.bodySmall,
                        color = content.textSecondary,
                    )
                    OrbitSegmentedProgress(
                        progress = value,
                        contentDescription = caption,
                    )
                }
            }
        }
    }

    GallerySection("Delta chips") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrbitDelta(value = 14f, contentDescription = "up 14 percent")
            OrbitDelta(value = -4.2f, contentDescription = "down 4.2 percent")
            OrbitDelta(
                value = 6.5f,
                higherIsBetter = false,
                contentDescription = "up 6.5 percent, worse",
            )
            OrbitDelta(
                value = -3f,
                higherIsBetter = false,
                contentDescription = "down 3 percent, better",
            )
        }
    }

    GallerySection("Step indicator · STAGES header, tap to expand") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            Text(
                text = "Status sub-lines follow stage vocabulary: Not started, In progress, Inspecting, Pending, Complete.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            OrbitStepIndicator(
                steps = WorkflowNotStarted,
                currentIndex = 0,
                progressSummary = "1/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStepIndicator(
                steps = WorkflowInProgress,
                currentIndex = 1,
                progressSummary = "2/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStepIndicator(
                steps = WorkflowInspectionReviewing,
                currentIndex = 2,
                progressSummary = "3/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStepIndicator(
                steps = WorkflowInspectionRework,
                currentIndex = 2,
                progressSummary = "3/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStepIndicator(
                steps = WorkflowApprovalPending,
                currentIndex = 3,
                progressSummary = "4/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStepIndicator(
                steps = WorkflowApprovalRejected,
                currentIndex = 3,
                progressSummary = "4/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStepIndicator(
                steps = WorkflowComplete,
                currentIndex = 4,
                progressSummary = "5/5 stages approved",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private val WorkflowNotStarted = listOf(
    OrbitStep(label = "Scheduled", statusLabel = "Not started"),
    OrbitStep(label = "In progress", statusLabel = "In progress"),
    OrbitStep(label = "Inspection", statusLabel = "Inspecting"),
    OrbitStep(label = "Approval", statusLabel = "Pending"),
    OrbitStep(label = "Completed", statusLabel = "Complete"),
)

private val WorkflowInProgress = listOf(
    OrbitStep(
        label = "Scheduled",
        statusLabel = "Started",
        startedOn = "01/09/2026",
        endedOn = "02/09/2026",
    ),
    OrbitStep(
        label = "In progress",
        statusLabel = "In progress",
        startedOn = "02/09/2026",
    ),
    OrbitStep(label = "Inspection", statusLabel = "Inspecting"),
    OrbitStep(label = "Approval", statusLabel = "Pending"),
    OrbitStep(label = "Completed", statusLabel = "Complete"),
)

private val WorkflowInspectionReviewing = listOf(
    OrbitStep(
        label = "Scheduled",
        statusLabel = "Started",
        startedOn = "28/08/2026",
        endedOn = "29/08/2026",
    ),
    OrbitStep(
        label = "In progress",
        statusLabel = "Submitted",
        startedOn = "29/08/2026",
        endedOn = "31/08/2026",
    ),
    OrbitStep(
        label = "Inspection",
        statusLabel = "Reviewing",
        startedOn = "31/08/2026",
    ),
    OrbitStep(label = "Approval", statusLabel = "Pending"),
    OrbitStep(label = "Completed", statusLabel = "Complete"),
)

private val WorkflowInspectionRework = listOf(
    OrbitStep(
        label = "Scheduled",
        statusLabel = "Started",
        startedOn = "28/08/2026",
        endedOn = "29/08/2026",
    ),
    OrbitStep(
        label = "In progress",
        statusLabel = "Submitted",
        startedOn = "29/08/2026",
        endedOn = "31/08/2026",
    ),
    OrbitStep(
        label = "Inspection",
        statusLabel = "Rework",
        startedOn = "31/08/2026",
    ),
    OrbitStep(label = "Approval", statusLabel = "Pending"),
    OrbitStep(label = "Completed", statusLabel = "Complete"),
)

private val WorkflowApprovalPending = listOf(
    OrbitStep(
        label = "Scheduled",
        statusLabel = "Started",
        startedOn = "20/08/2026",
        endedOn = "21/08/2026",
    ),
    OrbitStep(
        label = "In progress",
        statusLabel = "Submitted",
        startedOn = "21/08/2026",
        endedOn = "25/08/2026",
    ),
    OrbitStep(
        label = "Inspection",
        statusLabel = "Done",
        startedOn = "25/08/2026",
        endedOn = "27/08/2026",
    ),
    OrbitStep(
        label = "Approval",
        statusLabel = "Pending",
        startedOn = "27/08/2026",
    ),
    OrbitStep(label = "Completed", statusLabel = "Complete"),
)

private val WorkflowApprovalRejected = listOf(
    OrbitStep(
        label = "Scheduled",
        statusLabel = "Started",
        startedOn = "20/08/2026",
        endedOn = "21/08/2026",
    ),
    OrbitStep(
        label = "In progress",
        statusLabel = "Submitted",
        startedOn = "21/08/2026",
        endedOn = "25/08/2026",
    ),
    OrbitStep(
        label = "Inspection",
        statusLabel = "Done",
        startedOn = "25/08/2026",
        endedOn = "27/08/2026",
    ),
    OrbitStep(
        label = "Approval",
        statusLabel = "Rejected",
        startedOn = "27/08/2026",
    ),
    OrbitStep(label = "Completed", statusLabel = "Complete"),
)

private val WorkflowComplete = listOf(
    OrbitStep(
        label = "Scheduled",
        statusLabel = "Started",
        startedOn = "15/08/2026",
        endedOn = "16/08/2026",
    ),
    OrbitStep(
        label = "In progress",
        statusLabel = "Submitted",
        startedOn = "16/08/2026",
        endedOn = "20/08/2026",
    ),
    OrbitStep(
        label = "Inspection",
        statusLabel = "Done",
        startedOn = "20/08/2026",
        endedOn = "22/08/2026",
    ),
    OrbitStep(
        label = "Approval",
        statusLabel = "Approved",
        startedOn = "22/08/2026",
        endedOn = "23/08/2026",
    ),
    OrbitStep(
        label = "Completed",
        statusLabel = "Complete",
        startedOn = "23/08/2026",
        endedOn = "23/08/2026",
    ),
)
