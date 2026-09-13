package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.ui.component.dropdown.WorkStage
import com.orbitai.erp.ui.component.badge.BadgeKind
import com.orbitai.erp.ui.component.badge.purchaseOrderBadgeKind
import com.orbitai.erp.ui.component.badge.purchaseOrderDisplayName
import com.orbitai.erp.ui.form.DraftPhoto
import com.orbitai.erp.ui.form.MaterialsOrderDraft
import com.orbitai.erp.ui.form.RaiseIssueDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkItemRecordTest {

    @Test
    fun `villa location is villa and floor`() {
        val record = sampleTaskVilla().copy(
            villa = "Villa 12",
            floor = "1st floor",
        )
        assertEquals("Villa 12 · 1st floor", record.locationLine)
    }

    @Test
    fun `apartment location is tower floor and unit`() {
        val record = sampleTaskApartment().copy(
            tower = "Tower A",
            floor = "1st floor",
            apartmentUnit = "A-101",
        )
        assertEquals("Tower A · 1st floor · A-101", record.locationLine)
    }

    @Test
    fun `stage heading and task title are separate`() {
        val record = sampleTaskVilla().copy(
            stage = WorkStage.Structure,
            task = "Slab Concreting",
            projectType = ProjectType.Villas,
            status = WorkStatus.Open,
        )
        assertEquals("Structure", record.stageHeading)
        assertEquals("Slab Concreting", record.taskTitle)
        assertEquals("T-1042", record.number)
        assertEquals("T-1042", record.numberLabel)
        assertEquals("I-2031", sampleIssueVilla().numberLabel)
    }

    @Test
    fun `issue card line is an untruncated rewrite of the description`() {
        assertEquals("—", rewriteIssueCardLine("  "))
        assertEquals(
            "Hairline crack at the reveal marked and",
            rewriteIssueCardLine("Hairline crack at the reveal marked and surface opened for rework."),
        )
        val long = "A very long site note that would wrap past the created issue card width on a phone"
        val line = rewriteIssueCardLine(long)
        assertTrue(line.length <= IssueCardLineMaxChars)
        assertTrue(!line.endsWith("…") && !line.endsWith("..."))
        assertEquals(
            "Hairline crack at the reveal marked and",
            sampleIssueVilla().issueCardLine,
        )
        assertEquals("0 PHOTOS", workItemPhotoCountLabel(0))
        assertEquals("2 PHOTOS", workItemPhotoCountLabel(sampleIssueVilla().photos.size))
        assertEquals("5 PHOTOS", workItemPhotoCountLabel(5))
    }

    @Test
    fun `editing a record keeps identity and writes the draft back`() {
        val issue = sampleIssueVilla()
        val draft = issue.toRaiseIssueDraft()
        draft.description = "Opened crack at the parapet"
        draft.work.task = "Parapet plaster"
        val next = issue.applyIssueDraft(draft)
        assertEquals(issue.id, next.id)
        assertEquals(issue.number, next.number)
        assertEquals(issue.status, next.status)
        assertEquals("Opened crack at the parapet", next.description)
        assertEquals("Parapet plaster", next.task)
        assertEquals(issue.requestedMaterials, next.requestedMaterials)
    }

    @Test
    fun `created cards use the gallery workflow track`() {
        val labels = listOf("Scheduled", "In progress", "Inspection", "Approval", "Completed")
        val villa = sampleTaskVilla()
        assertEquals(labels, villa.workflowSteps.map { it.label })
        assertEquals(0, villa.workflowCurrentIndex)
        assertEquals("Not started", villa.workflowSteps[0].statusLabel)

        val apartment = sampleTaskApartment()
        assertEquals(labels, apartment.workflowSteps.map { it.label })
        assertEquals(1, apartment.workflowCurrentIndex)
        assertEquals("Started", apartment.workflowSteps[0].statusLabel)
        assertEquals("In progress", apartment.workflowSteps[1].statusLabel)
    }

    @Test
    fun `checklist toggle updates progress`() {
        val record = sampleTaskVilla().withChecklistChecked("c-v3", checked = true)
        assertEquals(true, record.checklistItems.single { it.id == "c-v3" }.checked)
        assertEquals(1f, record.progress)
    }

    @Test
    fun `usage log splits given materials from extra`() {
        val review = sampleTaskVilla().materialReview()
        assertEquals(listOf("OPC 53 Grade Cement", "20mm Aggregate"), review.given.map { it.material })
        assertEquals(listOf("Binding Wire"), review.extra.map { it.material })
        val table = materialLinesMarkdown(review.extra)
        assertTrue(table.contains("Binding Wire"))
        assertTrue(table.contains("| Material | Qty | Unit |"))
        assertEquals("", materialLinesMarkdown(emptyList()))
    }

    @Test
    fun `a comment is appended`() {
        val record = sampleTaskVilla().withComment(
            WorkItemComment("n1", "You", "Noted.", "Now", mine = true),
        )
        assertEquals("Noted.", record.comments.last().body)
        assertEquals(true, record.comments.last().mine)
    }

    @Test
    fun `workflow steps carry start and end dates from the card range`() {
        val range = OrbitDateRange(
            start = OrbitCalendarDate(2026, 9, 1),
            end = OrbitCalendarDate(2026, 9, 13),
        )
        val open = workItemWorkflowSteps(WorkStatus.Open, range)
        assertEquals("01/09/2026", open[0].startedOn)
        assertNull(open[0].endedOn)
        open.drop(1).forEach { step ->
            assertNull(step.startedOn)
            assertNull(step.endedOn)
        }

        val inProgress = workItemWorkflowSteps(WorkStatus.InProgress, range)
        assertEquals("01/09/2026", inProgress[0].startedOn)
        assertEquals("04/09/2026", inProgress[0].endedOn)
        assertEquals("04/09/2026", inProgress[1].startedOn)
        assertNull(inProgress[1].endedOn)
        inProgress.drop(2).forEach { step ->
            assertNull(step.startedOn)
            assertNull(step.endedOn)
        }

        val done = workItemWorkflowSteps(WorkStatus.Completed, range)
        assertEquals("01/09/2026", done[0].startedOn)
        assertEquals("04/09/2026", done[0].endedOn)
        assertEquals("04/09/2026", done[1].startedOn)
        assertEquals("07/09/2026", done[1].endedOn)
        assertEquals("07/09/2026", done[2].startedOn)
        assertEquals("10/09/2026", done[2].endedOn)
        assertEquals("10/09/2026", done[3].startedOn)
        assertEquals("13/09/2026", done[3].endedOn)
        assertEquals("13/09/2026", done[4].startedOn)
        assertEquals("13/09/2026", done[4].endedOn)
    }

    @Test
    fun `sample cards expose dates on reached workflow steps`() {
        val villa = sampleTaskVilla()
        val villaRange = villa.dateRange
        assertNotNull(villaRange)
        assertEquals(villaRange.start.formatSlashed(), villa.workflowSteps[0].startedOn)
        assertNull(villa.workflowSteps[0].endedOn)
        villa.workflowSteps.drop(1).forEach { step ->
            assertNull(step.startedOn)
            assertNull(step.endedOn)
        }

        val apartment = sampleTaskApartment()
        assertNotNull(apartment.workflowSteps[0].startedOn)
        assertNotNull(apartment.workflowSteps[0].endedOn)
        assertNotNull(apartment.workflowSteps[1].startedOn)
        assertNull(apartment.workflowSteps[1].endedOn)

        val completed = villa.copy(status = WorkStatus.Completed).workflowSteps
        completed.forEach { step ->
            assertNotNull(step.startedOn)
            assertNotNull(step.endedOn)
        }
        assertEquals(villaRange.end.formatSlashed(), completed.last().endedOn)
    }

    @Test
    fun `completed workflow marks every prior step done`() {
        val record = sampleTaskVilla().copy(status = WorkStatus.Completed)
        assertEquals(4, record.workflowCurrentIndex)
        assertEquals("Started", record.workflowSteps[0].statusLabel)
        assertEquals("Submitted", record.workflowSteps[1].statusLabel)
        assertEquals("Done", record.workflowSteps[2].statusLabel)
        assertEquals("Approved", record.workflowSteps[3].statusLabel)
        assertEquals("Complete", record.workflowSteps[4].statusLabel)
    }

    @Test
    fun `raise issue draft copies description and photos`() {
        val draft = RaiseIssueDraft()
        draft.description = "  Crack at the reveal  "
        draft.photos += DraftPhoto("p1", "issueimage1.jpg", "1.2 MB")
        val record = draft.toRecord(ProjectType.Villas)
        assertEquals(WorkItemKind.Issue, record.kind)
        assertEquals("Crack at the reveal", record.description)
        assertEquals(listOf("issueimage1.jpg"), record.photos.map { it.fileName })
    }

    @Test
    fun `rework note is stored with the rework status`() {
        val next = sampleTaskVilla().withRework("Redo the joint")
        assertEquals(WorkStatus.Rework, next.status)
        assertEquals("Redo the joint", next.reworkNote)
    }

    @Test
    fun `receiving a requested material moves it into the given table`() {
        val open = sampleIssueVilla()
        val waiting = open.requestedMaterials.single()
        assertEquals(MaterialRequestStatus.LowStock, waiting.status)
        val received = open.receiveRequestedMaterial(waiting.id)
        assertTrue(received.requestedMaterials.isEmpty())
        assertTrue(received.materials.any { it.material == waiting.material })
    }

    @Test
    fun `a completed need line is upserted as ordered`() {
        val line = OrbitMaterialUsageLine("need-1", "Binding Wire", 4, "Kg")
        val next = sampleTaskVilla().upsertRequestedMaterial(line)
        val added = next.requestedMaterials.single { it.id == "need-1" }
        assertEquals("Binding Wire", added.material)
        assertEquals(MaterialRequestStatus.Ordered, added.status)
    }

    @Test
    fun `assignees split into site engineers and contractors`() {
        val (site, contractors) = splitAssigneeIds(setOf("u-se-villas", "u-con-villas"))
        assertEquals(setOf("u-se-villas"), site)
        assertEquals(setOf("u-con-villas"), contractors)
    }

    @Test
    fun `supply assignees split into procurement and warehouse`() {
        val (procurement, warehouse) = splitSupplyAssigneeIds(setOf("u-proc", "u-wh-villas"))
        assertEquals(setOf("u-proc"), procurement)
        assertEquals(setOf("u-wh-villas"), warehouse)
        assertEquals("order", workItemNoun(WorkItemKind.PurchaseOrder))
    }

    @Test
    fun `purchase order badges follow ordered in progress done received`() {
        assertEquals(BadgeKind.Ordered, WorkStatus.Open.purchaseOrderBadgeKind)
        assertEquals("Ordered", WorkStatus.Open.purchaseOrderDisplayName)
        assertEquals(BadgeKind.InProgress, WorkStatus.InProgress.purchaseOrderBadgeKind)
        assertEquals("In progress", WorkStatus.InProgress.purchaseOrderDisplayName)
        assertEquals(BadgeKind.Done, WorkStatus.InReview.purchaseOrderBadgeKind)
        assertEquals("Done", WorkStatus.InReview.purchaseOrderDisplayName)
        assertEquals(BadgeKind.Received, WorkStatus.Completed.purchaseOrderBadgeKind)
        assertEquals("Received", WorkStatus.Completed.purchaseOrderDisplayName)
        assertEquals("PO-1234", sampleOrderVilla().number)
        assertEquals("Prestige Golfshire Villas", sampleOrderVilla().locationLine)
        assertEquals(4f, sampleOrderVilla().progressDelta)
    }

    @Test
    fun `materials order draft becomes a purchase order record`() {
        val draft = MaterialsOrderDraft()
        draft.materialLines[0] = OrbitMaterialUsageLine("m0", "OPC 53 Grade Cement", 40, "Bags")
        draft.projectName = "Prestige Golfshire Villas"
        draft.procurementIds = setOf("u-proc")
        draft.warehouseIds = setOf("u-wh-villas")
        val record = draft.toRecord(ProjectType.Villas)
        assertEquals(WorkItemKind.PurchaseOrder, record.kind)
        assertTrue(record.number.startsWith("PO-"))
        assertEquals(WorkStatus.Open, record.status)
        assertEquals(setOf("u-proc", "u-wh-villas"), record.assigneeIds)
        assertEquals("OPC 53 Grade Cement", record.materials.single().material)
        assertEquals("Prestige Golfshire Villas", record.locationLine)
        assertEquals(0f, record.progressDelta)
        val edited = record.toMaterialsOrderDraft()
        edited.materialLines[0] = OrbitMaterialUsageLine("m0", "Binding Wire", 8, "Kg")
        val next = record.applyMaterialsOrderDraft(edited)
        assertEquals(record.id, next.id)
        assertEquals("Binding Wire", next.materials.single().material)
        val afterPhoto = record.withOrderPhoto(WorkItemPhoto("p-new", "order3.jpg", "900 KB"))
        assertEquals(WorkStatus.InProgress, afterPhoto.status)
        assertEquals("order3.jpg", afterPhoto.photos.last().fileName)
    }
}
