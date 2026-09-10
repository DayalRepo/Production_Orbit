package com.orbitai.erp.ui.ceo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.project.CeoProjectCard
import com.orbitai.erp.ui.component.project.CeoProjectCardModel
import com.orbitai.erp.ui.component.project.CeoProjectsDemoData

/** CEO projects list — villa and apartment/community cards; tap opens overview detail. */
@Composable
fun CeoProjectsScreen(modifier: Modifier = Modifier) {
    var selectedProject by remember { mutableStateOf<CeoProjectCardModel?>(null) }
    val project = selectedProject

    if (project != null) {
        CeoProjectDetailScreen(
            project = project,
            onBack = { selectedProject = null },
            modifier = modifier,
        )
    } else {
        CeoProjectsListScreen(
            onProjectClick = { selectedProject = it },
            modifier = modifier,
        )
    }
}

@Composable
private fun CeoProjectsListScreen(
    onProjectClick: (CeoProjectCardModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val metrics = rememberCeoLayoutMetrics()
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val projects = remember { CeoProjectsDemoData.Projects }

    CeoScreenScaffold(
        modifier = modifier.fillMaxSize(),
        metrics = metrics,
        header = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CeoScreenHeader(title = "Projects")
                Spacer(Modifier.height(spacing.md))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = OrbitTheme.sizing.hairline,
                    color = control.controlBorder,
                )
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(metrics.cardGap),
        ) {
            items(
                items = projects,
                key = { it.id },
            ) { item ->
                CeoProjectCard(
                    model = item,
                    onClick = { onProjectClick(item) },
                )
            }
        }
    }
}
