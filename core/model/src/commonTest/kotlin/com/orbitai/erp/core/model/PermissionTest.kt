package com.orbitai.erp.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PermissionTest {

    @Test
    fun everyRoleHasAtLeastOnePermission() {
        UserRole.entries.forEach { role ->
            assertTrue(
                Permission.forRole(role).isNotEmpty(),
                "$role has no permissions, so it would see an empty app shell",
            )
        }
    }

    @Test
    fun everyRoleCanReadTheInbox() {
        UserRole.entries.forEach { role ->
            assertTrue(
                Permission.ViewInbox in Permission.forRole(role),
                "$role cannot open the inbox",
            )
        }
    }

    @Test
    fun onlyCeoHasOrganisationWideScope() {
        val organisationWide = UserRole.entries.filter { it.hasOrganisationWideScope }
        assertEquals(listOf(UserRole.Ceo), organisationWide)
    }

    @Test
    fun executiveDashboardIsCeoOnly() {
        UserRole.entries.forEach { role ->
            val canView = Permission.ViewExecutiveDashboard in Permission.forRole(role)
            assertEquals(role == UserRole.Ceo, canView, "$role executive dashboard access")
        }
    }

    @Test
    fun contractorCannotSeeInternalRecords() {
        val contractor = Permission.forRole(UserRole.Contractor)
        assertFalse(Permission.ViewAuditLog in contractor)
        assertFalse(Permission.ViewProcurement in contractor)
        assertFalse(Permission.ManageTeam in contractor)
        assertFalse(Permission.ViewAiInsights in contractor)
    }

    @Test
    fun approvalPermissionsImplyReadAccess() {
        val approvalToRead = mapOf(
            Permission.ApproveInvoice to Permission.ViewInvoices,
            Permission.ApprovePurchaseOrder to Permission.ViewProcurement,
            Permission.ApproveMaterialRequest to Permission.ViewMaterials,
            Permission.ApproveSiteUpdate to Permission.ViewSiteUpdates,
            Permission.AssignTask to Permission.ViewTasks,
            Permission.AssignIssue to Permission.ViewIssues,
        )

        UserRole.entries.forEach { role ->
            val granted = Permission.forRole(role)
            approvalToRead.forEach { (approve, read) ->
                if (approve in granted) {
                    assertTrue(
                        read in granted,
                        "$role can $approve but cannot $read",
                    )
                }
            }
        }
    }

    @Test
    fun sessionResolvesPermissionsFromRole() {
        val session = Session.forUser(
            User(id = "u1", fullName = "Test User", email = "t@example.com", role = UserRole.QaQc),
        )

        assertTrue(session.can(Permission.VerifyIssue))
        assertFalse(session.can(Permission.CreatePurchaseOrder))
        assertTrue(session.canAny(Permission.CreatePurchaseOrder, Permission.VerifyIssue))
        assertFalse(session.canAll(Permission.CreatePurchaseOrder, Permission.VerifyIssue))
    }
}
