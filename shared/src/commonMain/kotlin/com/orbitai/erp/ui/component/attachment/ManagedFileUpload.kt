package com.orbitai.erp.ui.component.attachment

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
import com.orbitai.erp.core.designsystem.component.media.orbitIsImageFileName
import com.orbitai.erp.platform.PickedFile
import com.orbitai.erp.platform.rememberCameraPicker
import com.orbitai.erp.platform.rememberFilePicker
import com.orbitai.erp.platform.rememberImagePicker
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.file_docs
import com.orbitai.erp.resources.file_pdf
import com.orbitai.erp.resources.file_sheet
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * [OrbitFileUpload] with a real platform picker, upload progress, cancel and delete confirmation.
 *
 * The design-system component stays stateless; this layer owns picker, simulation and dialogs —
 * the same split as [ManagedAttachmentRow].
 */
@Composable
fun ManagedFileUpload(
    modifier: Modifier = Modifier,
    browseLabel: String = "Browse File",
    cameraLabel: String? = "Click photo",
    dropZoneTitle: String = "Choose a file or drag & drop it here.",
    dropZoneHint: String = "JPEG, PNG, PDF, and MP4 formats, up to 50 MB.",
    photosOnly: Boolean = false,
    onCompleted: ((id: String, name: String, sizeLabel: String) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val entries = remember { mutableStateListOf<UploadEntry>() }
    var deleteTarget by remember { mutableStateOf<UploadEntry?>(null) }
    var viewingId by remember { mutableStateOf<String?>(null) }
    val jobs = remember { mutableMapOf<String, Job>() }
    val cancelledIds = remember { mutableSetOf<String>() }

    fun startUpload(picked: PickedFile) {
        cancelledIds.remove(picked.id)
        val entry = UploadEntry(
            picked = picked,
            uploadedBytes = 0L,
            state = OrbitUploadState.Uploading,
            progress = 0f,
        )
        entries += entry
        val job = scope.launch {
            val finished = runUploadSimulation(
                totalBytes = picked.sizeBytes.coerceAtLeast(1L),
                isCancelled = { picked.id in cancelledIds },
            ) { uploaded, fraction ->
                val index = entries.indexOfFirst { it.id == entry.id }
                if (index < 0) return@runUploadSimulation
                val done = fraction >= 1f
                entries[index] = entries[index].copy(
                    uploadedBytes = uploaded,
                    progress = fraction,
                    state = if (done) OrbitUploadState.Completed else OrbitUploadState.Uploading,
                )
                if (done) {
                    onCompleted?.invoke(
                        picked.id,
                        picked.name,
                        formatBytes(picked.sizeBytes.coerceAtLeast(1L)),
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

    val launchFiles = rememberFilePicker { picked ->
        picked?.let(::startUpload)
    }
    val launchImages = rememberImagePicker { picked ->
        picked?.let(::startUpload)
    }
    val launchCamera = rememberCameraPicker { picked ->
        picked?.let(::startUpload)
    }

    OrbitFileUpload(
        items = entries.map { it.toOrbitItem() },
        onBrowseClick = if (photosOnly) launchImages else launchFiles,
        dropZoneTitle = dropZoneTitle,
        dropZoneHint = dropZoneHint,
        browseLabel = browseLabel,
        cameraLabel = cameraLabel,
        onCameraClick = launchCamera,
        leadingFor = { item ->
            val entry = entries.firstOrNull { it.id == item.id }
            leadingForEntry(entry)
        },
        onCancelUpload = { item ->
            cancelledIds += item.id
            jobs.remove(item.id)?.cancel()
            entries.removeAll { it.id == item.id }
        },
        onRemove = { item ->
            entries.firstOrNull { it.id == item.id }?.let { deleteTarget = it }
        },
        onItemClick = { item ->
            if (orbitIsImageFileName(item.fileName)) viewingId = item.id
        },
        modifier = modifier,
    )

    val completedImages = entries.filter {
        it.state == OrbitUploadState.Completed && orbitIsImageFileName(it.picked.name)
    }
    val viewingIndex = completedImages.indexOfFirst { it.id == viewingId }
    if (viewingIndex >= 0) {
        OrbitImageCarousel(
            pages = completedImages.map { OrbitImageCarouselPage(it.id, it.picked.name) },
            painterFor = { page ->
                when (val leading = leadingForEntry(completedImages.firstOrNull { it.id == page.id })) {
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
            title = "Remove file",
            message = "Remove \"${target.picked.name}\"? This cannot be undone.",
            destructive = true,
            onConfirm = {
                cancelledIds += target.id
                jobs.remove(target.id)?.cancel()
                entries.removeAll { it.id == target.id }
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
        )
    }
}

@Composable
private fun leadingForEntry(entry: UploadEntry?): OrbitAttachmentLeading {
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

private data class UploadEntry(
    val picked: PickedFile,
    val uploadedBytes: Long,
    val state: OrbitUploadState,
    val progress: Float,
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
