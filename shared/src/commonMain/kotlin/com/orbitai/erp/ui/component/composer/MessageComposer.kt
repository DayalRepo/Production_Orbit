package com.orbitai.erp.ui.component.composer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading
import com.orbitai.erp.core.designsystem.component.input.OrbitAttachMenu
import com.orbitai.erp.core.designsystem.component.input.OrbitAttachOption
import com.orbitai.erp.core.designsystem.component.input.OrbitComposerAttachment
import com.orbitai.erp.core.designsystem.component.input.OrbitComposerMode
import com.orbitai.erp.core.designsystem.component.input.OrbitMessageComposer
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.platform.PickedFile
import com.orbitai.erp.platform.rememberDocumentPicker
import com.orbitai.erp.platform.rememberImagePicker
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.file_docs
import com.orbitai.erp.resources.file_pdf
import com.orbitai.erp.resources.file_sheet
import com.orbitai.erp.ui.component.attachment.imageUploadLeading
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Stable
data class VoiceClip(
    val id: Long,
    val amplitudes: List<Float>,
    val seconds: Int,
)

@Stable
data class ComposerAttachment(
    val picked: PickedFile,
)

object OrbitComposerPlaceholder {
    const val Message = "Write a message"
    const val Ai = "Ask Orbit AI"
}

fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

@Composable
fun MessageComposer(
    modifier: Modifier = Modifier,
    placeholder: String = OrbitComposerPlaceholder.Message,
    label: String = "Message",
    onSend: (text: String, clip: VoiceClip?, attachments: List<ComposerAttachment>) -> Unit =
        { _, _, _ -> },
    onClipRecorded: (VoiceClip) -> Unit = {},
) {
    var text by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var elapsed by remember { mutableIntStateOf(0) }
    val amplitudes = remember { mutableStateListOf<Float>() }
    val attachments = remember { mutableStateListOf<ComposerAttachment>() }
    var removeTarget by remember { mutableStateOf<ComposerAttachment?>(null) }

    val launchImagePicker = rememberImagePicker { picked ->
        if (picked != null) {
            attachments += ComposerAttachment(picked)
        }
    }
    val launchDocumentPicker = rememberDocumentPicker { picked ->
        if (picked != null) {
            attachments += ComposerAttachment(picked)
        }
    }

    LaunchedEffect(recording, paused) {
        if (!recording || paused) return@LaunchedEffect
        var frame = amplitudes.size
        while (true) {
            delay(TickMs)
            amplitudes += fakeAmplitude(frame)
            frame++
            if (frame % FramesPerSecond == 0) elapsed++
        }
    }

    val mode = if (recording) {
        OrbitComposerMode.Recording(
            elapsed = formatDuration(elapsed),
            amplitudes = amplitudes.toList(),
            paused = paused,
        )
    } else {
        OrbitComposerMode.Text
    }

    fun finishRecording(): VoiceClip? {
        if (!recording) return null
        val clip = if (elapsed >= MinClipSeconds || amplitudes.size >= FramesPerSecond) {
            VoiceClip(
                id = Random.nextLong(),
                amplitudes = amplitudes.toList(),
                seconds = elapsed.coerceAtLeast(1),
            )
        } else {
            null
        }
        recording = false
        paused = false
        elapsed = 0
        amplitudes.clear()
        return clip
    }

    val stripItems = attachments.map { entry ->
        OrbitComposerAttachment(
            id = entry.picked.id,
            fileName = entry.picked.name,
            fileSize = formatComposerBytes(entry.picked.sizeBytes),
            leading = composerLeading(entry.picked),
        )
    }

    OrbitMessageComposer(
        value = text,
        onValueChange = { text = it },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        mode = mode,
        attachExpanded = menuOpen,
        attachments = stripItems,
        onRemoveAttachment = { id ->
            removeTarget = attachments.firstOrNull { it.picked.id == id }
        },
        onSend = {
            val clip = finishRecording()
            val queued = attachments.toList()
            if (text.isNotBlank() || clip != null || queued.isNotEmpty()) {
                onSend(text, clip, queued)
                clip?.let(onClipRecorded)
                text = ""
                attachments.clear()
            }
        },
        onMicClick = {
            menuOpen = false
            recording = true
        },
        onCancelRecording = {
            recording = false
            paused = false
            elapsed = 0
            amplitudes.clear()
        },
        onPauseRecording = { paused = !paused },
        onAttachClick = { menuOpen = !menuOpen },
        attachMenu = {
            OrbitAttachMenu(
                expanded = menuOpen,
                onDismiss = { menuOpen = false },
                items = listOf(
                    OrbitAttachOption("Upload image", OrbitIcons.ImageUpload, launchImagePicker),
                    OrbitAttachOption("Attach file", OrbitIcons.FileUpload, launchDocumentPicker),
                ),
            )
        },
    )

    removeTarget?.let { target ->
        OrbitConfirmDialog(
            title = "Remove file",
            message = "Remove \"${target.picked.name}\" from this message? You can attach it again.",
            onConfirm = {
                attachments.removeAll { it.picked.id == target.picked.id }
                removeTarget = null
            },
            onDismiss = { removeTarget = null },
        )
    }
}

@Composable
private fun composerLeading(picked: PickedFile): OrbitAttachmentLeading {
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

private fun formatComposerBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    else -> {
        val tenths = (bytes * 10f / (1024f * 1024f)).toInt()
        "${tenths / 10}.${tenths % 10} MB"
    }
}

private fun fakeAmplitude(n: Int): Float {
    val t = n * 0.11f
    val phrase = abs(sin(t * 0.85f))
    val syllable = abs(sin(t * 3.1f))
    val burst = abs(sin(t * 6.4f + sin(t * 0.55f)))
    val noise = Random.nextFloat() * 0.55f
    val spike = if (Random.nextFloat() < 0.12f) Random.nextFloat() * 0.35f else 0f
    return (0.08f + 0.55f * phrase + 0.42f * syllable * burst + noise + spike).coerceIn(0.04f, 1f)
}

private const val TickMs = 50L
private const val FramesPerSecond = 1000 / TickMs.toInt()
private const val MinClipSeconds = 1
