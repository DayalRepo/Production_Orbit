package com.orbitai.erp.ui.component.attachment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentRow
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.file_docs
import com.orbitai.erp.resources.file_pdf
import com.orbitai.erp.resources.file_sheet
import org.jetbrains.compose.resources.painterResource

/**
 * An attachment row that works out its own leading mark from the filename.
 *
 * This lives in `:shared` rather than in the design system for one reason: it reaches for image
 * resources. `:core:designsystem` cannot own artwork for Google Docs and Excel without becoming a
 * library that only this product can use, so it takes an [OrbitAttachmentLeading] and stays ignorant
 * of what a ".xlsx" is. The mapping is here, in one place, so that no screen has to repeat it.
 *
 * The recognised set is deliberately short — PDF, Docs, Sheets. Those three cover most of what
 * actually moves through a construction project's inbox, and everything else falls back to the pin
 * glyph. Adding artwork per format is a slope with no bottom: there are hundreds of extensions, most
 * appear once, and a half-populated icon set looks more broken than a consistent fallback does.
 *
 * @param preview a thumbnail for image attachments. When present it wins over the extension, since a
 *   photo should show the photo rather than a generic image mark.
 * @param onRemove detach from a draft. Reversible by reattaching, so it is a neutral cross.
 * @param onRename open a rename affordance. Housekeeping, so it is neutral ink too.
 * @param onDelete destroy the file. Irreversible, so it is the one control on the row that is red.
 *   Pass this or [onRemove], not both — they look nearly identical and mean very different things,
 *   and a row offering both asks the user to distinguish "take this off the draft" from "delete
 *   this permanently" using two small glyphs.
 * @param onDownload save the file locally. Typical on read-only rows where no edit controls appear.
 */
@Composable
fun FileAttachmentRow(
    fileName: String,
    fileSize: String,
    modifier: Modifier = Modifier,
    preview: Painter? = null,
    onRemove: (() -> Unit)? = null,
    onRename: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onDownload: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val leading = when {
        preview != null -> OrbitAttachmentLeading.Preview(preview)
        else -> when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> OrbitAttachmentLeading.Artwork(painterResource(Res.drawable.file_pdf))
            "doc", "docx", "gdoc" ->
                OrbitAttachmentLeading.Artwork(painterResource(Res.drawable.file_docs))
            "xls", "xlsx", "csv", "gsheet" ->
                OrbitAttachmentLeading.Artwork(painterResource(Res.drawable.file_sheet))
            else -> OrbitAttachmentLeading.Glyph
        }
    }

    OrbitAttachmentRow(
        fileName = fileName,
        fileSize = fileSize,
        leading = leading,
        modifier = modifier,
        onRemove = onRemove,
        onRename = onRename,
        onDelete = onDelete,
        onDownload = onDownload,
        onClick = onClick,
    )
}
