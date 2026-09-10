package com.orbitai.erp.ui.ceo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.project.CeoProjectCardModel
import com.orbitai.erp.ui.component.project.CeoProjectOverviewCard
import com.orbitai.erp.ui.component.project.CeoProjectSectionGrid
import com.orbitai.erp.ui.component.project.ceoProjectSections

/**
 * Project overview — large progress summary card + Divisions / Materials / Bills / Team grid.
 * Fitted to the viewport without scrolling.
 */
@Composable
fun CeoProjectDetailScreen(
    project: CeoProjectCardModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val metrics = rememberCeoLayoutMetrics()
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val sections = remember(project.id) { ceoProjectSections(project) }

    CeoScreenScaffold(
        modifier = modifier.fillMaxSize(),
        metrics = metrics,
        header = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OrbitIconButton(
                        icon = OrbitIcons.ArrowLeft,
                        onClick = onBack,
                        contentDescription = "Back to projects",
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Small,
                    )
                }
                Spacer(Modifier.height(spacing.sm))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = OrbitTheme.sizing.hairline,
                    color = control.controlBorder,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            CeoProjectOverviewCard(model = project)

            CeoProjectSectionGrid(
                sections = sections,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}
