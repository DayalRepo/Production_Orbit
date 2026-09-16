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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.platform.rememberInvoicePdfExporter
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.form.page.invoiceWorkCatalogue

/** Who is looking at the invoice — generator can edit; recipient is view-only. */
enum class InvoiceViewerRole {
    /** Sending / generating user — Download, Share, and Edit. */
    Sender,

    /** Received / viewing user — Download and Share only. */
    Receiver,
}

@Composable
fun CreatedInvoiceScreen(
    record: InvoiceRecord,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    role: InvoiceViewerRole = InvoiceViewerRole.Sender,
    onOpenAttachedWork: () -> Unit = {},
    onEdit: () -> Unit = {},
) {
    OrbitBackHandler(onBack = onBack)
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val exportPdf = rememberInvoicePdfExporter()
    val catalogue = remember(record.projectType) { invoiceWorkCatalogue(record.projectType) }
    val attached = remember(record, catalogue) {
        catalogue.filter {
            it.id in record.attachedTaskIds || it.id in record.attachedIssueIds
        }
    }
    val pdfModel = remember(record, attached) { record.toPdfModel(attached) }
    val files = pdfModel.files
    val title = when (role) {
        InvoiceViewerRole.Sender -> "Sent invoice"
        InvoiceViewerRole.Receiver -> "Received invoice"
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
            Text(
                text = title,
                style = OrbitTheme.typography.titleLarge,
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (role == InvoiceViewerRole.Sender) {
                OrbitIconButton(
                    contentDescription = "Edit invoice",
                    onClick = onEdit,
                    icon = OrbitIcons.SquarePen,
                    style = OrbitIconButtonStyle.Neutral,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal)
                .padding(bottom = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            MobileInvoiceDocument(model = pdfModel)

            if (attached.isNotEmpty()) {
                OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Text(
                        text = "Attached work".uppercase(),
                        style = OrbitTheme.extendedTypography.sectionLabel,
                        color = content.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    if (attached.size > 1) {
                        ActionButton(
                            action = ActionKind.View,
                            onClick = onOpenAttachedWork,
                            label = "View all",
                            size = OrbitButtonSize.Small,
                            showIcon = false,
                        )
                    }
                }
                InvoiceWorkItemCard(
                    record = attached.first(),
                    onView = onOpenAttachedWork,
                )
            }

            if (files.isNotEmpty()) {
                OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
                Text(
                    text = "Files".uppercase(),
                    style = OrbitTheme.extendedTypography.sectionLabel,
                    color = content.textSecondary,
                )
                files.forEach { file ->
                    FileAttachmentRow(
                        fileName = file.fileName,
                        fileSize = file.fileSize,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                OrbitButton(
                    label = "Download",
                    onClick = { exportPdf(pdfModel, InvoicePdfAction.Download) },
                    variant = OrbitButtonVariant.Primary,
                    size = OrbitButtonSize.Large,
                    icon = OrbitIcons.Download,
                    modifier = Modifier.weight(1f),
                    shape = OrbitTheme.shapeTokens.card,
                )
                OrbitButton(
                    label = "Share",
                    onClick = { exportPdf(pdfModel, InvoicePdfAction.Share) },
                    variant = OrbitButtonVariant.Primary,
                    size = OrbitButtonSize.Large,
                    icon = OrbitIcons.Share,
                    modifier = Modifier.weight(1f),
                    shape = OrbitTheme.shapeTokens.card,
                )
            }
            if (role == InvoiceViewerRole.Sender) {
                ActionButton(
                    action = ActionKind.Edit,
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
