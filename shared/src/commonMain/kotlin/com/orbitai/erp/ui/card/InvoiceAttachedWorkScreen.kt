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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.form.page.invoiceWorkCatalogue

@Composable
fun InvoiceAttachedWorkScreen(
    invoice: InvoiceRecord,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OrbitBackHandler(onBack = onBack)
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val catalogue = remember(invoice.projectType) { invoiceWorkCatalogue(invoice.projectType) }
    val tasks = remember(invoice, catalogue) {
        catalogue.filter { it.id in invoice.attachedTaskIds }
    }
    val issues = remember(invoice, catalogue) {
        catalogue.filter { it.id in invoice.attachedIssueIds }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
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
                    text = "Attached work",
                    style = OrbitTheme.typography.titleLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = invoice.number,
                    style = OrbitTheme.extendedTypography.reference,
                    color = content.textSecondary,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.screenHorizontal,
                    vertical = spacing.screenVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(
                text = "Tasks".uppercase(),
                style = OrbitTheme.extendedTypography.sectionLabel,
                color = content.textSecondary,
            )
            if (tasks.isEmpty()) {
                EmptyReviewNote("No tasks attached")
            } else {
                tasks.forEach { item ->
                    InvoiceWorkItemCard(record = item)
                }
            }

            OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
            Text(
                text = "Issues".uppercase(),
                style = OrbitTheme.extendedTypography.sectionLabel,
                color = content.textSecondary,
            )
            if (issues.isEmpty()) {
                EmptyReviewNote("No issues attached")
            } else {
                issues.forEach { item ->
                    InvoiceWorkItemCard(record = item)
                }
            }
        }
    }
}
