package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarBounds
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimeField
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimePicker
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.InvoiceParty
import com.orbitai.erp.ui.datetime.orbitToday
import com.orbitai.erp.ui.form.FormFieldLabel
import com.orbitai.erp.ui.form.FormSection
import com.orbitai.erp.ui.form.SiteLocations

@Composable
fun InvoiceMetaPage(
    number: String,
    onNumberChange: (String) -> Unit,
    dateRange: OrbitDateRange,
    onDateRangeChange: (OrbitDateRange) -> Unit,
    projects: List<String>,
    projectName: String?,
    onProjectSelect: (String) -> Unit,
    projectType: ProjectType,
    villa: String?,
    onVillaSelect: (String) -> Unit,
    tower: String?,
    onTowerSelect: (String) -> Unit,
    apartmentUnit: String?,
    onApartmentUnitSelect: (String) -> Unit,
    from: InvoiceParty,
    onFromChange: (InvoiceParty) -> Unit,
    billTo: InvoiceParty,
    onBillToChange: (InvoiceParty) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val bounds = remember { OrbitCalendarBounds(today = orbitToday()) }
    var rangeOpen by remember { mutableStateOf(false) }
    val units = remember(tower) { tower?.let { SiteLocations.unitsFor(it) }.orEmpty() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FormSection(title = "Details", showDivider = false) {
            LabeledField("Invoice number") {
                OrbitTextField(
                    value = number,
                    onValueChange = onNumberChange,
                    label = "Invoice number",
                    placeholder = "INV-2026-001",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                FormFieldLabel("Issued – due")
                OrbitDateTimeField(
                    value = dateRange.format(),
                    placeholder = "Issued – due dates",
                    onClick = { rangeOpen = !rangeOpen },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (rangeOpen) {
                    OrbitDateTimePicker(
                        bounds = bounds,
                        selection = dateRange,
                        confirmLabel = "Set dates",
                        onCancel = { rangeOpen = false },
                        onConfirm = {
                            onDateRangeChange(it)
                            rangeOpen = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        FormSection(title = "Location") {
            InvoiceDropdown(
                heading = "Project",
                selected = projectName,
                options = projects,
                onSelect = onProjectSelect,
                placeholder = projects.firstOrNull() ?: "Project",
            )
            when (projectType) {
                ProjectType.Villas -> InvoiceDropdown(
                    heading = "Villa",
                    selected = villa,
                    options = SiteLocations.villas,
                    onSelect = onVillaSelect,
                    placeholder = "Villa 12",
                )
                ProjectType.ApartmentCommunity -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    InvoiceDropdown(
                        heading = "Tower",
                        selected = tower,
                        options = SiteLocations.towers,
                        onSelect = onTowerSelect,
                        placeholder = "A",
                        size = OrbitFieldSize.Small,
                        modifier = Modifier.weight(1f),
                    )
                    InvoiceDropdown(
                        heading = "Unit",
                        selected = apartmentUnit,
                        options = units,
                        onSelect = onApartmentUnitSelect,
                        placeholder = if (tower == null) "—" else "101",
                        size = OrbitFieldSize.Small,
                        enabled = tower != null,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        FormSection(title = "From") {
            LabeledField("Organisation") {
                OrbitTextField(
                    value = from.name,
                    onValueChange = { onFromChange(from.copy(name = it)) },
                    label = "Organisation",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            PartyAddressFields(party = from, onChange = onFromChange)
            LabeledField("GSTIN") {
                OrbitTextField(
                    value = from.gstin,
                    onValueChange = { onFromChange(from.copy(gstin = it.uppercase())) },
                    label = "GSTIN",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        FormSection(title = "Bill to") {
            LabeledField("Name") {
                OrbitTextField(
                    value = billTo.name,
                    onValueChange = { onBillToChange(billTo.copy(name = it)) },
                    label = "Name",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            PartyAddressFields(party = billTo, onChange = onBillToChange)
            LabeledField("GSTIN") {
                OrbitTextField(
                    value = billTo.gstin,
                    onValueChange = { onBillToChange(billTo.copy(gstin = it.uppercase())) },
                    label = "GSTIN",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            LabeledField("Phone") {
                OrbitTextField(
                    value = billTo.phone,
                    onValueChange = { onBillToChange(billTo.copy(phone = it)) },
                    label = "Phone",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PartyAddressFields(
    party: InvoiceParty,
    onChange: (InvoiceParty) -> Unit,
) {
    val spacing = OrbitTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        LabeledField("Door no", modifier = Modifier.weight(1f)) {
            OrbitTextField(
                value = party.doorNo,
                onValueChange = { onChange(party.copy(doorNo = it)) },
                label = "Door no",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LabeledField("Apartment", modifier = Modifier.weight(1f)) {
            OrbitTextField(
                value = party.apartmentName,
                onValueChange = { onChange(party.copy(apartmentName = it)) },
                label = "Apartment name",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    LabeledField("Street") {
        OrbitTextField(
            value = party.street,
            onValueChange = { onChange(party.copy(street = it)) },
            label = "Street",
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        LabeledField("City", modifier = Modifier.weight(1f)) {
            OrbitTextField(
                value = party.city,
                onValueChange = { onChange(party.copy(city = it)) },
                label = "City",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LabeledField("State", modifier = Modifier.weight(1f)) {
            OrbitTextField(
                value = party.state,
                onValueChange = { onChange(party.copy(state = it)) },
                label = "State",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    LabeledField("Pincode") {
        OrbitTextField(
            value = party.pincode,
            onValueChange = { onChange(party.copy(pincode = it.filter { ch -> ch.isDigit() }.take(6))) },
            label = "Pincode",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LabeledField(
    heading: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        FormFieldLabel(heading)
        content()
    }
}

@Composable
private fun InvoiceDropdown(
    heading: String,
    selected: String?,
    options: List<String>,
    onSelect: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        FormFieldLabel(heading)
        OrbitDropdownField(
            selected = selected,
            options = options,
            onSelect = onSelect,
            label = heading,
            placeholder = placeholder,
            enabled = enabled,
            searchable = false,
            size = size,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
