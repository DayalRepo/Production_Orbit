package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.CreatedItemsScreen
import com.orbitai.erp.ui.card.UnitCardsScreen
import com.orbitai.erp.ui.card.UnitRecord
import com.orbitai.erp.ui.card.WorkItemKind
import com.orbitai.erp.ui.card.WorkItemRecord
import com.orbitai.erp.ui.card.sampleIssueApartment
import com.orbitai.erp.ui.card.sampleIssueVilla
import com.orbitai.erp.ui.card.sampleOrderApartment
import com.orbitai.erp.ui.card.sampleOrderVilla
import com.orbitai.erp.ui.card.sampleTaskApartment
import com.orbitai.erp.ui.card.sampleTaskVilla
import com.orbitai.erp.ui.card.sampleUnitApartment
import com.orbitai.erp.ui.card.sampleUnitVilla
import com.orbitai.erp.ui.card.toRecord
import com.orbitai.erp.ui.form.CreateTaskForm
import com.orbitai.erp.ui.form.MaterialsOrderForm
import com.orbitai.erp.ui.form.RaiseIssueForm

/**
 * A scrolling gallery of everything in the design system, for reviewing it on a real device.
 *
 * This is a review surface, not a product screen — a phone in the hand is the only place the glass
 * tint, the icon stroke weight and the dark-theme contrast can actually be judged, and a canvas or a
 * screenshot flatters all three. It should be deleted, or moved behind a debug flag, once real
 * screens exist.
 *
 * The groups live in sibling files — [ButtonGalleryPage], [BadgeGalleryPage] — one per component
 * folder in the library. This file is only the frame: the scroll, the insets, the padding and the
 * theme toggle. Adding a component group means adding a file and one line here.
 */
@Composable
fun ComponentGalleryScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var openForm by remember { mutableStateOf<FormPreview?>(null) }
    val createdItems = remember { mutableStateListOf<WorkItemRecord>() }
    var viewing by remember { mutableStateOf<List<WorkItemRecord>?>(null) }
    var viewingTitle by remember { mutableStateOf("Created items") }
    var viewingUnits by remember { mutableStateOf<List<UnitRecord>?>(null) }
    var viewingUnitsTitle by remember { mutableStateOf("Units") }

    fun openCards(items: List<WorkItemRecord>, title: String) {
        viewingTitle = title
        viewing = items
        viewingUnits = null
        openForm = null
    }

    fun openUnits(units: List<UnitRecord>, title: String) {
        viewingUnitsTitle = title
        viewingUnits = units
        viewing = null
        openForm = null
    }

    val units = viewingUnits
    val cards = viewing
    when {
        units != null -> UnitCardsScreen(
            units = units,
            title = viewingUnitsTitle,
            onBack = { viewingUnits = null },
            modifier = modifier,
        )
        cards != null -> CreatedItemsScreen(
            items = cards,
            title = viewingTitle,
            onBack = { viewing = null },
            modifier = modifier,
        )
        openForm == FormPreview.CreateTaskVilla -> CreateTaskForm(
            projectType = ProjectType.Villas,
            onDismiss = { openForm = null },
            onCreate = { draft ->
                createdItems.add(
                    0,
                    draft.toRecord(kind = WorkItemKind.Task, projectType = ProjectType.Villas),
                )
                openCards(createdItems.toList(), "Created items")
            },
        )
        openForm == FormPreview.CreateTaskApartment -> CreateTaskForm(
            projectType = ProjectType.ApartmentCommunity,
            onDismiss = { openForm = null },
            onCreate = { draft ->
                createdItems.add(
                    0,
                    draft.toRecord(
                        kind = WorkItemKind.Task,
                        projectType = ProjectType.ApartmentCommunity,
                    ),
                )
                openCards(createdItems.toList(), "Created items")
            },
        )
        openForm == FormPreview.RaiseIssueVilla -> RaiseIssueForm(
            projectType = ProjectType.Villas,
            onDismiss = { openForm = null },
            onRaise = { draft ->
                createdItems.add(0, draft.toRecord(ProjectType.Villas))
                openCards(createdItems.toList(), "Created items")
            },
        )
        openForm == FormPreview.RaiseIssueApartment -> RaiseIssueForm(
            projectType = ProjectType.ApartmentCommunity,
            onDismiss = { openForm = null },
            onRaise = { draft ->
                createdItems.add(0, draft.toRecord(ProjectType.ApartmentCommunity))
                openCards(createdItems.toList(), "Created items")
            },
        )
        openForm == FormPreview.MaterialsOrderVilla -> MaterialsOrderForm(
            projectType = ProjectType.Villas,
            onDismiss = { openForm = null },
            onCreate = { draft ->
                createdItems.add(0, draft.toRecord(ProjectType.Villas))
                openCards(createdItems.toList(), "Created items")
            },
        )
        openForm == FormPreview.MaterialsOrderApartment -> MaterialsOrderForm(
            projectType = ProjectType.ApartmentCommunity,
            onDismiss = { openForm = null },
            onCreate = { draft ->
                createdItems.add(0, draft.toRecord(ProjectType.ApartmentCommunity))
                openCards(createdItems.toList(), "Created items")
            },
        )
        else -> GalleryIndex(
            isDark = isDark,
            onToggleTheme = onToggleTheme,
            onOpenForm = { preview ->
                when (preview) {
                    FormPreview.SampleTaskVilla ->
                        openCards(listOf(sampleTaskVilla()), "Created task")
                    FormPreview.SampleTaskApartment ->
                        openCards(listOf(sampleTaskApartment()), "Created task")
                    FormPreview.SampleIssueVilla ->
                        openCards(listOf(sampleIssueVilla()), "Raised issue")
                    FormPreview.SampleIssueApartment ->
                        openCards(listOf(sampleIssueApartment()), "Raised issue")
                    FormPreview.SampleOrderVilla ->
                        openCards(listOf(sampleOrderVilla()), "Materials order")
                    FormPreview.SampleOrderApartment ->
                        openCards(listOf(sampleOrderApartment()), "Materials order")
                    FormPreview.SampleUnitVilla ->
                        openUnits(listOf(sampleUnitVilla()), "Unit card — Villas")
                    FormPreview.SampleUnitApartment ->
                        openUnits(
                            listOf(sampleUnitApartment()),
                            "Unit card — Apartment / Community",
                        )
                    else -> openForm = preview
                }
            },
            modifier = modifier,
        )
    }
}

@Composable
private fun GalleryIndex(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onOpenForm: (FormPreview) -> Unit,
    modifier: Modifier,
) {
    val spacing = OrbitTheme.spacing
    // The system bars, then the screen padding. Both are needed and the order matters: the app draws
    // edge to edge, so without the inset the first row sits under the status bar clock and the last
    // under the gesture bar, and applying it after the scroll modifier is what keeps the inset out of
    // the scrolling content — otherwise the top gap scrolls away with everything else.
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.screenVertical,
            ),
        verticalArrangement = Arrangement.spacedBy(spacing.xxl),
    ) {
        // No title, just the toggle. The theme is legible from the screen itself, and a heading
        // naming the design system is the one thing on a review surface that never gets reviewed.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            // Shows the mode you are switching *to*, which is the convention every OS settings
            // toggle uses. Showing the current mode reads as a status light and nobody taps it.
            OrbitIconButton(
                contentDescription = if (isDark) {
                    "Switch to light theme"
                } else {
                    "Switch to dark theme"
                },
                onClick = onToggleTheme,
                icon = if (isDark) OrbitIcons.Sun else OrbitIcons.Moon,
                style = OrbitIconButtonStyle.Neutral,
            )
        }

        FormGalleryPage(onOpen = onOpenForm)
        DateTimeGalleryPage()
        BrandGalleryPage()
        ProgressGalleryPage()
        MessageBubbleGalleryPage()
        MarkdownGalleryPage()
        ChecklistGalleryPage()
        AvatarGalleryPage(isDark = isDark, onToggleTheme = onToggleTheme)
        OverlayGalleryPage()
        DisplayGalleryPage()
        ScrollbarGalleryPage()
        FeedbackGalleryPage()
        StateGalleryPage()
        InputGalleryPage()
        NavigationGalleryPage()
        AssignGalleryPage()
        ComposerGalleryPage()
        ButtonGalleryPage()
        BadgeGalleryPage()

        // On Android the gesture bar sits directly under the last row of badges.
        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}
