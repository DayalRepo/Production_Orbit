package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading
import com.orbitai.erp.core.designsystem.component.input.OrbitFileUpload
import com.orbitai.erp.core.designsystem.component.input.OrbitUploadItem
import com.orbitai.erp.core.designsystem.component.input.OrbitUploadState
import com.orbitai.erp.core.designsystem.component.media.OrbitImageCarousel
import com.orbitai.erp.core.designsystem.component.media.OrbitImageCarouselPage
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.platform.PickedFile
import com.orbitai.erp.platform.rememberCameraPicker
import com.orbitai.erp.platform.rememberImagePicker
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.file_docs
import com.orbitai.erp.resources.file_pdf
import com.orbitai.erp.resources.file_sheet
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.attachment.formatBytes
import com.orbitai.erp.ui.component.attachment.formatByteProgress
import com.orbitai.erp.ui.component.attachment.imageUploadLeading
import com.orbitai.erp.ui.component.attachment.runUploadSimulation
import com.orbitai.erp.ui.card.WorkItemPhoto
import com.orbitai.erp.ui.card.workItemPhotoPainter
import com.orbitai.erp.ui.component.input.ManagedDescriptionField
import com.orbitai.erp.ui.form.DraftPhoto
import com.orbitai.erp.ui.form.FormFieldLabel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * Issue description plus photo upload: drop zone, in-flight rows, then completed thumbnails.
 */
@Composable
fun IssuePhotosPage(
    modifier: Modifier = Modifier,
    description: String = "",
    initialPhotos: List<DraftPhoto> = emptyList(),
    onDescriptionChange: ((String) -> Unit)? = null,
    onPhotoCompleted: ((id: String, name: String, size: String) -> Unit)? = null,
    onPhotoRemoved: ((id: String) -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val scope = rememberCoroutineScope()
    val seeded = initialPhotos.map { photo ->
        PhotoEntry(
            picked = PickedFile(
                id = photo.id,
                name = photo.fileName,
                sizeBytes = parseFileSizeLabel(photo.fileSize),
            ),
            uploadedBytes = parseFileSizeLabel(photo.fileSize),
            state = OrbitUploadState.Completed,
            progress = 1f,
            preview = workItemPhotoPainter(WorkItemPhoto(photo.id, photo.fileName, photo.fileSize)),
        )
    }
    val entries = remember(initialPhotos.map { it.id }) {
        mutableStateListOf<PhotoEntry>().also { it.addAll(seeded) }
    }
    var deleteTarget by remember { mutableStateOf<PhotoEntry?>(null) }
    var viewingId by remember { mutableStateOf<String?>(null) }
    val jobs = remember { mutableMapOf<String, Job>() }
    val cancelledIds = remember { mutableSetOf<String>() }

    fun startUpload(picked: PickedFile) {
        val named = picked.copy(
            name = nextIssueImageName(entries.map { it.picked.name }, picked.name),
        )
        cancelledIds.remove(named.id)
        val entry = PhotoEntry(
            picked = named,
            uploadedBytes = 0L,
            state = OrbitUploadState.Uploading,
            progress = 0f,
        )
        entries += entry
        val job = scope.launch {
            val finished = runUploadSimulation(
                totalBytes = named.sizeBytes.coerceAtLeast(1L),
                isCancelled = { named.id in cancelledIds },
            ) { uploaded, fraction ->
                val index = entries.indexOfFirst { it.id == entry.id }
                if (index < 0) return@runUploadSimulation
                entries[index] = entries[index].copy(
                    uploadedBytes = uploaded,
                    progress = fraction,
                    state = if (fraction >= 1f) {
                        OrbitUploadState.Completed
                    } else {
                        OrbitUploadState.Uploading
                    },
                )
                if (fraction >= 1f) {
                    onPhotoCompleted?.invoke(
                        named.id,
                        named.name,
                        formatBytes(named.sizeBytes.coerceAtLeast(0L)),
                    )
                }
            }
            jobs.remove(entry.id)
            if (!finished) {
                entries.removeAll { it.id == entry.id }
            }
        }
        jobs[entry.id] = job
    }

    val launchGallery = rememberImagePicker { picked ->
        picked?.let(::startUpload)
    }
    val launchCamera = rememberCameraPicker { picked ->
        picked?.let(::startUpload)
    }

    val uploading = entries.filter { it.state == OrbitUploadState.Uploading }
    val completed = entries.filter { it.state == OrbitUploadState.Completed }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
    ) {
        if (onDescriptionChange != null) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                FormFieldLabel("Issue description")
                ManagedDescriptionField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = "Issue description",
                    placeholder = "What is wrong on site",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Photos")
            OrbitFileUpload(
                items = uploading.map { it.toOrbitItem() },
                onBrowseClick = launchGallery,
                leadingFor = { item ->
                    val entry = entries.firstOrNull { it.id == item.id }
                    leadingForEntry(entry)
                },
                onCancelUpload = { item ->
                    cancelledIds += item.id
                    jobs.remove(item.id)?.cancel()
                    entries.removeAll { it.id == item.id }
                },
                dropZoneTitle = "Add a photo of the issue.",
                dropZoneHint = "JPEG, PNG, and HEIC, up to 50 MB.",
                browseLabel = "Upload photo",
                cameraLabel = "Click photo",
                onCameraClick = launchCamera,
            )
        }

        if (completed.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                FormFieldLabel("Attachments")
                completed.forEach { entry ->
                    val preview = (leadingForEntry(entry) as? OrbitAttachmentLeading.Preview)
                        ?.painter
                    FileAttachmentRow(
                        fileName = entry.picked.name,
                        fileSize = formatBytes(entry.picked.sizeBytes.coerceAtLeast(0L)),
                        preview = preview,
                        onRemove = { deleteTarget = entry },
                        onClick = { viewingId = entry.id },
                    )
                }
            }
        }
    }

    val viewingIndex = completed.indexOfFirst { it.id == viewingId }
    if (viewingIndex >= 0) {
        OrbitImageCarousel(
            pages = completed.map { OrbitImageCarouselPage(it.id, it.picked.name) },
            painterFor = { page ->
                when (val leading = leadingForEntry(completed.firstOrNull { it.id == page.id })) {
                    is OrbitAttachmentLeading.Preview -> leading.painter
                    is OrbitAttachmentLeading.Artwork -> leading.painter
                    else -> null
                }
            },
            onDismiss = { viewingId = null },
            startIndex = viewingIndex,
        )
    }

    deleteTarget?.let { target ->
        OrbitConfirmDialog(
            title = "Remove photo",
            message = "Remove \"${target.picked.name}\"? This cannot be undone.",
            destructive = true,
            onConfirm = {
                cancelledIds += target.id
                jobs.remove(target.id)?.cancel()
                entries.removeAll { it.id == target.id }
                onPhotoRemoved?.invoke(target.id)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
        )
    }
}

private fun parseFileSizeLabel(label: String): Long {
    val number = label.substringBefore(' ').toDoubleOrNull() ?: return 0L
    return when {
        "GB" in label.uppercase() -> (number * 1024 * 1024 * 1024).toLong()
        "MB" in label.uppercase() -> (number * 1024 * 1024).toLong()
        "KB" in label.uppercase() -> (number * 1024).toLong()
        else -> number.toLong()
    }
}

@Composable
private fun leadingForEntry(entry: PhotoEntry?): OrbitAttachmentLeading {
    entry?.preview?.let { return OrbitAttachmentLeading.Preview(it) }
    val picked = entry?.picked ?: return OrbitAttachmentLeading.Glyph
    imageUploadLeading(picked.previewUri, picked.name)?.let { return it }
    return when (picked.name.substringAfterLast('.', "").lowercase()) {
        "pdf" -> OrbitAttachmentLeading.Artwork(painterResource(Res.drawable.file_pdf))
        "doc", "docx", "gdoc" ->
            OrbitAttachmentLeading.Artwork(painterResource(Res.drawable.file_docs))
        "xls", "xlsx", "csv", "gsheet" ->
            OrbitAttachmentLeading.Artwork(painterResource(Res.drawable.file_sheet))
        else -> OrbitAttachmentLeading.Glyph
    }
}

private data class PhotoEntry(
    val picked: PickedFile,
    val uploadedBytes: Long,
    val state: OrbitUploadState,
    val progress: Float,
    val preview: androidx.compose.ui.graphics.painter.Painter? = null,
) {
    val id: String get() = picked.id

    fun toOrbitItem(): OrbitUploadItem = OrbitUploadItem(
        id = id,
        fileName = picked.name,
        progressLabel = formatByteProgress(
            uploadedBytes.coerceAtMost(picked.sizeBytes),
            picked.sizeBytes.coerceAtLeast(1L),
        ),
        state = state,
        progress = progress,
    )
}
