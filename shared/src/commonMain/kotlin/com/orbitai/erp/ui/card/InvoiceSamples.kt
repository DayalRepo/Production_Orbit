package com.orbitai.erp.ui.card

import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.model.ApprovalStatus
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.datetime.orbitToday
import com.orbitai.erp.ui.datetime.toLocalDate
import com.orbitai.erp.ui.datetime.toOrbitCalendarDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

fun sampleInvoiceVilla(): InvoiceRecord {
    val issued = orbitToday()
    val due = issued.toLocalDate().plus(DatePeriod(days = 15)).toOrbitCalendarDate()
    val task = sampleTaskVilla()
    val issue = sampleIssueVilla12()
    return InvoiceRecord(
        id = "sample-inv-villa",
        number = "INV-2026-052",
        status = ApprovalStatus.Pending,
        projectType = ProjectType.Villas,
        issued = issued,
        due = due,
        projectName = MockDirectory.OrganisationProjects.first {
            it.type == ProjectType.Villas
        }.name,
        villa = "Villa 12",
        from = defaultSellerParty(),
        billTo = InvoiceParty(
            name = "Mr. Ramesh Krishnan",
            address = "Villa 12, Prestige Golfshire, Bengaluru 560064",
            gstin = "29AABCR1234Q1Z5",
            phone = "+91 98450 11223",
            email = "ramesh.krishnan@example.com",
        ),
        lines = listOf(
            InvoiceLineItem("l1", "Slab concreting — Villa 12", 1.0, 85_000.0, "9954"),
            InvoiceLineItem("l2", "Formwork labour", 1.0, 18_000.0, "9954"),
        ),
        applyGst = true,
        cgstPercent = 9.0,
        sgstPercent = 9.0,
        bank = defaultBankDetails(),
        notes = "Payment due within 15 days of issue.",
        placeOfSupply = "Karnataka",
        attachedTaskIds = listOf(task.id),
        attachedIssueIds = listOf(issue.id),
        uploads = listOf(
            InvoiceAttachment("inv-u1", "measurement.pdf", "420 KB"),
        ),
    )
}

fun sampleInvoiceApartment(): InvoiceRecord {
    val issued = orbitToday()
    val due = issued.toLocalDate().plus(DatePeriod(days = 21)).toOrbitCalendarDate()
    val task = sampleTaskApartment()
    val issue = sampleIssueApartment101()
    return InvoiceRecord(
        id = "sample-inv-apt",
        number = "INV-2026-061",
        status = ApprovalStatus.Pending,
        projectType = ProjectType.ApartmentCommunity,
        issued = issued,
        due = due,
        projectName = MockDirectory.OrganisationProjects.first {
            it.type == ProjectType.ApartmentCommunity
        }.name,
        tower = "Tower A",
        apartmentUnit = "A-101",
        from = defaultSellerParty(),
        billTo = InvoiceParty(
            name = "Sneha Reddy",
            address = "A-101, Tower A, Prestige Lakeside Habitat, Bengaluru 560037",
            gstin = "29AADCS9988P1Z2",
            phone = "+91 99001 44556",
            email = "sneha.reddy@example.com",
        ),
        lines = listOf(
            InvoiceLineItem("l1", "Blockwork — A-101", 1.0, 42_500.0, "9954"),
            InvoiceLineItem("l2", "Internal plaster", 1.0, 28_000.0, "9954"),
        ),
        applyGst = true,
        cgstPercent = 9.0,
        sgstPercent = 9.0,
        bank = defaultBankDetails(),
        notes = "Payable via NEFT / UPI.",
        placeOfSupply = "Karnataka",
        attachedTaskIds = listOf(task.id),
        attachedIssueIds = listOf(issue.id),
        uploads = listOf(
            InvoiceAttachment("inv-u2", "site-photo.jpg", "1.1 MB"),
        ),
    )
}

fun defaultSellerParty(): InvoiceParty = InvoiceParty(
    name = MockDirectory.OrganisationName,
    address = "Prestige Falcon Towers, Brunton Road, Bengaluru 560025",
    gstin = "29AABCP1234F1Z8",
    phone = "+91 80 2555 0101",
    email = "accounts@prestigeestates.example",
)

fun defaultBankDetails(): InvoiceBankDetails = InvoiceBankDetails(
    accountHolder = MockDirectory.OrganisationName,
    bankName = "HDFC Bank",
    accountNumber = "50200011223344",
    ifsc = "HDFC0001234",
    upiId = "prestige@hdfcbank",
    qrAttachment = InvoiceAttachment("qr-sample", "upi-qr.png", "86 KB"),
)
