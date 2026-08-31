package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarBounds
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimeField
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimePicker
import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateTimeSelection
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.datetime.orbitToday

/**
 * The date-and-time picker, shown with the field that owns it.
 *
 * The field and the panel are presented together because that is the pairing a form uses: the field
 * is what sits in the layout and the panel is what it opens. Showing the panel alone would hide the
 * component's actual empty state, which is the field reading "Target date" with nothing in it.
 *
 * Bounds come from the real clock here, which is the whole point of the design system taking today as
 * data — this page is where the conversion from `kotlinx-datetime` lives, and `:core:designsystem`
 * stays free of it.
 */
@Composable
internal fun DateTimeGalleryPage() {
    val spacing = OrbitTheme.spacing
    val bounds = remember { OrbitCalendarBounds(today = orbitToday()) }

    var moment by remember { mutableStateOf<OrbitDateTimeSelection?>(null) }
    // Closed, so the page opens on the state a form actually starts in: an empty field. Opening on the
    // panel hides the fact that the field is the component's entry point and the panel is what it
    // produces — which is the whole interaction being demonstrated.
    var momentOpen by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {

        GallerySection("Date & time") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                OrbitDateTimeField(
                    value = moment?.format(),
                    placeholder = "Target date & time",
                    label = "Inspection due",
                    onClick = { momentOpen = !momentOpen },
                )

                if (momentOpen) {
                    OrbitDateTimePicker(
                        bounds = bounds,
                        selection = moment,
                        confirmLabel = "Schedule",
                        onCancel = { momentOpen = false },
                        onConfirm = {
                            moment = it
                            momentOpen = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
