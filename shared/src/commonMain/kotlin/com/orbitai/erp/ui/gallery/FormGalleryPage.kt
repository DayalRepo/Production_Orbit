package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind

internal enum class FormPreview {
    CreateTaskVilla,
    CreateTaskApartment,
    RaiseIssueVilla,
    RaiseIssueApartment,
    SampleTaskVilla,
    SampleTaskApartment,
    SampleIssueVilla,
    SampleIssueApartment,
    SampleOrderVilla,
    SampleOrderApartment,
    SampleUnitVilla,
    SampleUnitApartment,
    MaterialsOrderVilla,
    MaterialsOrderApartment,
}

@Composable
internal fun FormGalleryPage(
    onOpen: (FormPreview) -> Unit,
) {
    val spacing = OrbitTheme.spacing

    GallerySection("Create task") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ActionButton(
                action = ActionKind.Open,
                label = "Create task — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.CreateTaskVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Create task — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.CreateTaskApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Raise issue") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ActionButton(
                action = ActionKind.Open,
                label = "Raise issue — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.RaiseIssueVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Raise issue — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.RaiseIssueApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Materials order") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ActionButton(
                action = ActionKind.Open,
                label = "Materials order — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.MaterialsOrderVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Materials order — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.MaterialsOrderApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Created cards") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ActionButton(
                action = ActionKind.Open,
                label = "Created task — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.SampleTaskVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Created task — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.SampleTaskApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Raised issue — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.SampleIssueVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Raised issue — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.SampleIssueApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Materials order — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.SampleOrderVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Materials order — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.SampleOrderApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Unit cards") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ActionButton(
                action = ActionKind.Open,
                label = "Unit card — ${ProjectType.Villas.displayName}",
                onClick = { onOpen(FormPreview.SampleUnitVilla) },
                modifier = Modifier.fillMaxWidth(),
            )
            ActionButton(
                action = ActionKind.Open,
                label = "Unit card — ${ProjectType.ApartmentCommunity.displayName}",
                onClick = { onOpen(FormPreview.SampleUnitApartment) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
