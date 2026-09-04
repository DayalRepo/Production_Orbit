package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarBounds
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimeField
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimePicker
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.datetime.orbitRemainingCountdown
import com.orbitai.erp.ui.datetime.orbitToday
import kotlinx.coroutines.delay

/**
 * The date-range picker, shown with the field that owns it and the allocated / remaining counters.
 *
 * Bounds come from the real clock here, which is the whole point of the design system taking today as
 * data — this page is where the conversion from `kotlinx-datetime` lives, and `:core:designsystem`
 * stays free of it.
 */
@Composable
internal fun DateTimeGalleryPage() {
    val spacing = OrbitTheme.spacing
    val bounds = remember { OrbitCalendarBounds(today = orbitToday()) }

    var range by remember { mutableStateOf<OrbitDateRange?>(null) }
    var rangeOpen by remember { mutableStateOf(false) }
    var remaining by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(range) {
        val current = range
        val end = current?.end
        if (current == null || end == null) {
            remaining = null
            return@LaunchedEffect
        }
        while (true) {
            remaining = orbitRemainingCountdown(allocatedDays = current.days, endDate = end)
            delay(1_000)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {

        GallerySection("Date range") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                OrbitDateTimeField(
                    value = range?.format(),
                    placeholder = "Start – end dates",
                    label = "Programme window",
                    onClick = { rangeOpen = !rangeOpen },
                )

                if (rangeOpen) {
                    OrbitDateTimePicker(
                        bounds = bounds,
                        selection = range,
                        confirmLabel = "Set dates",
                        onCancel = { rangeOpen = false },
                        onConfirm = {
                            range = it
                            rangeOpen = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                OrbitDateTimeField(
                    value = remaining,
                    placeholder = "Days · 00d 00h:00m:00s",
                    label = "Remaining",
                    leadingIcon = OrbitIcons.Clock,
                )
            }
        }
    }
}
