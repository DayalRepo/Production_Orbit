package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.platform.OrbitBackHandler

/**
 * Preview / review surface for [UnitCard] samples — same chrome pattern as created work-item
 * screens, without edit / delete / detail flows (those come later).
 */
@Composable
fun UnitCardsScreen(
    units: List<UnitRecord>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Units",
    onViewUnit: (UnitRecord) -> Unit = {},
) {
    OrbitBackHandler(onBack = onBack)
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = spacing.screenHorizontal,
                    vertical = spacing.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitIconButton(
                contentDescription = "Back",
                onClick = onBack,
                icon = OrbitIcons.ArrowLeft,
                style = OrbitIconButtonStyle.Neutral,
            )
            Text(
                text = title,
                style = OrbitTheme.typography.titleLarge,
                color = content.textPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.screenHorizontal,
                    vertical = spacing.screenVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            units.forEach { unit ->
                UnitCard(
                    record = unit,
                    onView = { onViewUnit(unit) },
                )
            }
        }
    }
}
