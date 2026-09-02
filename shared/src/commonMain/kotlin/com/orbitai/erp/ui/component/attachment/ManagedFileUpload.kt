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
import com.orbitai.erp.platform.PickedFile
import com.orbitai.erp.platform.rememberFilePicker
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
) {
    val scope = rememberCoroutineScope()
    val entries = remember { mutableStateListOf<UploadEntry>() }
    var deleteTarget by remember { mutableStateOf<UploadEntry?>(null) }
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
                entries[index] = entries[index].copy(
                    uploadedBytes = uploaded,
                    progress = fraction,
                    state = if (fraction >= 1f) OrbitUploadState.Completed else OrbitUploadState.Uploading,
                )
            }
            jobs.remove(entry.id)
            if (!finished) {
                entries.removeAll { it.id == entry.id }
            }
        }
        jobs[entry.id] = job
    }

    val launchPicker = rememberFilePicker { picked ->
        picked?.let(::startUpload)
    }

    OrbitFileUpload(
        items = entries.map { it.toOrbitItem() },
        onBrowseClick = launchPicker,
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
        modifier = modifier,
    )

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
