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
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.datetime.orbitToday
import com.orbitai.erp.ui.form.FormFieldLabel
import com.orbitai.erp.ui.form.SiteLocations

/**
 * Villa + floor, or tower + floor + unit on one row, then a date-range field.
 */
@Composable
fun LocationSchedulePage(
    projectType: ProjectType,
    villa: String?,
    onVillaSelect: (String) -> Unit,
    floor: String?,
    onFloorSelect: (String) -> Unit,
    tower: String?,
    onTowerSelect: (String) -> Unit,
    apartmentUnit: String?,
    onApartmentUnitSelect: (String) -> Unit,
    dateRange: OrbitDateRange?,
    onDateRangeChange: (OrbitDateRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val bounds = remember { OrbitCalendarBounds(today = orbitToday()) }
    var rangeOpen by remember { mutableStateOf(false) }
    val units = remember(tower) { tower?.let { SiteLocations.unitsFor(it) }.orEmpty() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
    ) {
        when (projectType) {
            ProjectType.Villas -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    LocationField(
                        heading = "Villa",
                        selected = villa,
                        options = SiteLocations.villas,
                        onSelect = onVillaSelect,
                        placeholder = "Select a villa",
                        searchPlaceholder = "Search villas",
                        modifier = Modifier.weight(1f),
                    )
                    LocationField(
                        heading = "Floor",
                        selected = floor,
                        options = SiteLocations.floors,
                        onSelect = onFloorSelect,
                        placeholder = "Select a floor",
                        searchPlaceholder = "Search floors",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            ProjectType.ApartmentCommunity -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    LocationField(
                        heading = "Tower",
                        selected = tower,
                        options = SiteLocations.towers,
                        onSelect = onTowerSelect,
                        placeholder = "Select a tower",
                        searchPlaceholder = "Search towers",
                        modifier = Modifier.weight(1f),
                    )
                    LocationField(
                        heading = "Floor",
                        selected = floor,
                        options = SiteLocations.floors,
                        onSelect = onFloorSelect,
                        placeholder = "Select a floor",
                        searchPlaceholder = "Search floors",
                        modifier = Modifier.weight(1f),
                    )
                    LocationField(
                        heading = "Unit",
                        selected = apartmentUnit,
                        options = units,
                        onSelect = onApartmentUnitSelect,
                        placeholder = if (tower == null) {
                            "Select a tower first"
                        } else {
                            "Select a unit"
                        },
                        searchPlaceholder = "Search units",
                        enabled = tower != null,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Date range")
            OrbitDateTimeField(
                value = dateRange?.format(),
                placeholder = "Start – end dates",
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
}

@Composable
private fun LocationField(
    heading: String,
    selected: String?,
    options: List<String>,
    onSelect: (String) -> Unit,
    placeholder: String,
    searchPlaceholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FormFieldLabel(heading)
        OrbitDropdownField(
            selected = selected,
            options = options,
            onSelect = onSelect,
            label = heading,
            placeholder = placeholder,
            searchPlaceholder = searchPlaceholder,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
