package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.card.InvoiceAttachment
import com.orbitai.erp.ui.component.attachment.ManagedFileUpload
import com.orbitai.erp.ui.form.FormSection

@Composable
fun InvoiceUploadsPage(
    uploads: List<InvoiceAttachment>,
    onAdd: (InvoiceAttachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.md),
    ) {
        FormSection(title = "Files", showDivider = false) {
            Text(
                text = "Attach challans, measurements, or site photos (optional).",
                style = OrbitTheme.typography.bodyMedium,
                color = OrbitTheme.contentColors.textSecondary,
            )
            ManagedFileUpload(
                modifier = Modifier.fillMaxWidth(),
                browseLabel = "Browse file",
                cameraLabel = "Click photo",
                onCompleted = { id, name, size ->
                    if (uploads.none { it.id == id }) {
                        onAdd(InvoiceAttachment(id, name, size))
                    }
                },
            )
        }
    }
}
