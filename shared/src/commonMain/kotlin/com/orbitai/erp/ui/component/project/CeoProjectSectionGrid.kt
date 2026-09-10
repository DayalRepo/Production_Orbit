package com.orbitai.erp.ui.component.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

data class CeoProjectSectionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
)

fun ceoProjectSections(model: CeoProjectCardModel): List<CeoProjectSectionItem> {
    val divisionsSubtitle = when (model.type) {
        CeoProjectType.Villa -> "${model.units} villas"
        CeoProjectType.Apartment -> "${model.units} units"
    }
    return listOf(
        CeoProjectSectionItem(
            id = "divisions",
            title = "Divisions",
            subtitle = divisionsSubtitle,
            icon = OrbitIcons.Layers01,
        ),
        CeoProjectSectionItem(
            id = "materials",
            title = "Materials",
            subtitle = "No at risk",
            icon = OrbitIcons.Warehouse,
        ),
        CeoProjectSectionItem(
            id = "bills",
            title = "Bills decision",
            subtitle = "No bills to decide",
            icon = OrbitIcons.NotepadText,
        ),
        CeoProjectSectionItem(
            id = "team",
            title = "Team",
            subtitle = "7 people",
            icon = OrbitIcons.User,
        ),
    )
}

/**
 * 2×2 overview section cards — Divisions, Materials, Bills decision, Team.
 */
@Composable
fun CeoProjectSectionGrid(
    sections: List<CeoProjectSectionItem>,
    modifier: Modifier = Modifier,
    onSectionClick: ((CeoProjectSectionItem) -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val rows = sections.take(4).chunked(2)
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                row.forEach { section ->
                    CeoProjectSectionCard(
                        item = section,
                        onClick = onSectionClick?.let { handler -> { handler(section) } },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CeoProjectSectionCard(
    item: CeoProjectSectionItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    OrbitCard(
        modifier = modifier,
        padding = spacing.md,
        container = OrbitTheme.controlColors.cardContainer,
        onClick = onClick,
        contentDescription = "${item.title}, ${item.subtitle}",
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                OrbitGlyph(
                    icon = item.icon,
                    size = 22.dp,
                    tint = content.iconPrimary,
                    contentDescription = null,
                )
                OrbitGlyph(
                    icon = OrbitIcons.ArrowRight,
                    size = 16.dp,
                    tint = content.textTertiary,
                    contentDescription = null,
                )
            }

            Column {
                Text(
                    text = item.title,
                    style = OrbitTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(spacing.xxs))
                Text(
                    text = item.subtitle,
                    style = OrbitTheme.typography.labelMedium,
                    color = content.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
