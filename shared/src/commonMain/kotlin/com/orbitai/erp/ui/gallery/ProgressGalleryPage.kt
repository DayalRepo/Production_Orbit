package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitDonutProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitFormPageBar
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProof
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofKind
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofStep
import com.orbitai.erp.core.designsystem.component.progress.OrbitStep
import com.orbitai.erp.core.designsystem.component.progress.OrbitStepIndicator
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.progress.ProgressCard

@Composable
internal fun ProgressGalleryPage() {
    val spacing = OrbitTheme.spacing

    GallerySection("Donut progress") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrbitDonutProgress(
                    progress = 0.78f,
                    colors = OrbitDonutProgressDefaults.greenColors,
                    caption = "Health",
                    contentDescription = "Health, 78 percent",
                )
                OrbitDonutProgress(
                    progress = 0.62f,
                    colors = OrbitDonutProgressDefaults.blueColors,
                    caption = "Progress",
                    contentDescription = "Progress, 62 percent",
                )
            }
        }
    }

    GallerySection("Form page bar") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            FormPageSample(pageCount = 2, currentPage = 0)
            FormPageSample(pageCount = 3, currentPage = 1)
            FormPageSample(pageCount = 4, currentPage = 2)
            FormPageSample(pageCount = 5, currentPage = 4)
            FormPageSample(pageCount = 7, currentPage = 3)
        }
    }

    GallerySection("Progress") {
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
    }

    GallerySection("Segmented progress") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            listOf(0f, 0.01f, 0.99f, 1f).forEach { value ->
                OrbitSegmentedProgress(
                    progress = value,
                    contentDescription = "Progress ${(value * 100).toInt()} percent",
                )
            }
        }
    }

    GallerySection("Delta") {
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

    GallerySection("Stage proof") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            OrbitStageProof(
                kind = OrbitStageProofKind.Villa,
                completedCount = 3,
                stages = listOf(
                    OrbitStageProofStep("SR", "Structure", "01/08/2026", "20/08/2026"),
                    OrbitStageProofStep("UI", "Unit internal", "21/08/2026", "05/09/2026"),
                    OrbitStageProofStep("UE", "Unit external", "06/09/2026", "18/09/2026"),
                    OrbitStageProofStep("ED", "Ext. development", "19/09/2026", null),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitStageProof(
                kind = OrbitStageProofKind.Building,
                completedCount = 4,
                stages = listOf(
                    OrbitStageProofStep("SR", "Structure", "01/07/2026", "15/07/2026"),
                    OrbitStageProofStep("CA", "Common area", "16/07/2026", "30/07/2026"),
                    OrbitStageProofStep("UI", "Unit internal", "01/08/2026", "20/08/2026"),
                    OrbitStageProofStep("UE", "Unit external", "21/08/2026", "10/09/2026"),
                    OrbitStageProofStep("ED", "Ext. development", "11/09/2026", null),
                    OrbitStageProofStep("BS", "Basement"),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Step indicator") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
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

@Composable
private fun FormPageSample(
    pageCount: Int,
    currentPage: Int,
) {
    OrbitFormPageBar(
        pageCount = pageCount,
        currentPage = currentPage,
        modifier = Modifier.fillMaxWidth(),
    )
}
