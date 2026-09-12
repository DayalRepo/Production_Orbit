package com.orbitai.erp.ui.card

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.PlaceholderScreen

@Composable
fun CreatedItemsScreen(
    items: List<WorkItemRecord>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Created items",
) {
    var updateKind by remember { mutableStateOf<WorkItemKind?>(null) }
    val updating = updateKind

    if (updating != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(OrbitTheme.colorScheme.background),
        ) {
            UpdatePlaceholderBar(onBack = { updateKind = null })
            PlaceholderScreen(
                title = if (updating == WorkItemKind.Issue) {
                    "Update issue"
                } else {
                    "Update task"
                },
                subtitle = "This screen is next.",
                modifier = Modifier.weight(1f),
            )
        }
        return
    }

    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .padding(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.screenVertical,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitIconButton(
                contentDescription = "Back",
                onClick = onBack,
                icon = OrbitIcons.ArrowLeft,
                style = OrbitIconButtonStyle.Neutral,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = OrbitTheme.typography.headlineSmall.copy(
                        fontWeight = OrbitTheme.fontWeights.heading,
                    ),
                    color = OrbitTheme.contentColors.textPrimary,
                )
                Text(
                    text = if (items.size == 1) "1 CARD" else "${items.size} CARDS",
                    style = OrbitTheme.extendedTypography.sectionLabel,
                    color = OrbitTheme.contentColors.textTertiary,
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.xl))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.cardGap),
        ) {
            items.forEach { record ->
                WorkItemCard(
                    record = record,
                    onUpdate = { updateKind = record.kind },
                )
            }
        }
    }
}

@Composable
private fun UpdatePlaceholderBar(onBack: () -> Unit) {
    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(safe)
            .padding(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrbitIconButton(
            contentDescription = "Back",
            onClick = onBack,
            icon = OrbitIcons.ArrowLeft,
            style = OrbitIconButtonStyle.Neutral,
        )
    }
}
