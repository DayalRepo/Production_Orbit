package com.orbitai.erp.ui.form

import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.amountInWordsInr
import com.orbitai.erp.ui.card.formatInr
import com.orbitai.erp.ui.card.invoiceTextSnapshot
import com.orbitai.erp.ui.card.isValidIfsc
import com.orbitai.erp.ui.card.sampleInvoiceVilla
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InvoiceDraftTest {

    @Test
    fun `gst totals and indian formatting`() {
        val draft = InvoiceDraft()
        draft.replaceLine("line-0") {
            it.copy(description = "Slab work", quantity = 1.0, rate = 100_000.0)
        }
        draft.applyGst = true
        draft.cgstPercent = 9.0
        draft.sgstPercent = 9.0
        assertEquals(100_000.0, draft.subtotal)
        assertEquals(9_000.0, draft.cgstAmount)
        assertEquals(9_000.0, draft.sgstAmount)
        assertEquals(118_000.0, draft.grandTotal)
        assertEquals("₹ 1,18,000.00", formatInr(draft.grandTotal))
        assertEquals("Rupees One Lakh Eighteen Thousand Only", amountInWordsInr(draft.grandTotal))
    }

    @Test
    fun `ifsc soft validation`() {
        assertTrue(isValidIfsc(""))
        assertTrue(isValidIfsc("HDFC0001234"))
        assertFalse(isValidIfsc("HDFC001234"))
        assertTrue(isValidIfsc("hdfc0001234"))
        assertTrue(isValidIfsc("HDFC0001234"))
    }

    @Test
    fun `create needs parties lines and bank`() {
        val draft = InvoiceDraft()
        assertFalse(draft.isReadyToCreate())
        draft.billTo = draft.billTo.copy(name = "Client")
        assertFalse(draft.isReadyToCreate())
        draft.replaceLine("line-0") {
            it.copy(description = "Work", quantity = 1.0, rate = 500.0)
        }
        // Prefill bank is already filled by defaultSeller/bank
        assertTrue(draft.bank.accountHolder.isNotBlank())
        assertTrue(draft.isReadyToCreate())
        val record = draft.toRecord(ProjectType.Villas)
        assertEquals(1, record.lines.size)
        assertTrue(record.invoiceTextSnapshot().contains("INVOICE"))
    }

    @Test
    fun `sample invoice snapshot is non empty`() {
        val sample = sampleInvoiceVilla()
        assertTrue(sample.attachedTaskIds.isNotEmpty())
        assertTrue(sample.invoiceTextSnapshot().contains(sample.number))
    }
}
