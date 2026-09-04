package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitMessageField
import com.orbitai.erp.core.designsystem.component.media.OrbitAudioWave
import com.orbitai.erp.core.designsystem.component.media.OrbitVoiceNoteRow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.composer.ManagedVoiceNoteRow
import com.orbitai.erp.ui.component.composer.MessageComposer
import com.orbitai.erp.ui.component.composer.OrbitComposerPlaceholder
import com.orbitai.erp.ui.component.composer.VoiceClip
import com.orbitai.erp.ui.component.composer.formatDuration
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

/**
 * The message composer, the live meter and recorded voice notes.
 *
 * The single most device-dependent page in the gallery, and the one where a screenshot is least
 * useful: the pill-to-rectangle morph, the overflow fade appearing as a prompt passes five lines,
 * the attach menu dismissing on an outside tap and the waveform filling in while you speak are all
 * motion. Reviewing this on a phone is not a nicety here — half of what it does is invisible in a
 * still.
 */
@Composable
internal fun ComposerGalleryPage() {
    val spacing = OrbitTheme.spacing

    val clips = remember { mutableStateListOf<VoiceClip>() }
    var playingId by remember { mutableStateOf<Long?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Playback is simulated for the same reason the recording is: there is no audio layer yet, and
    // the thing under review is whether a moving playhead reads correctly, which does not need real
    // sound. One clip plays at a time — hence a single `playingId` rather than a flag per row.
    LaunchedEffect(playingId) {
        val id = playingId ?: return@LaunchedEffect
        val clip = clips.firstOrNull { it.id == id } ?: return@LaunchedEffect
        val steps = clip.seconds * 20
        while (progress < 1f) {
            delay(50)
            progress += 1f / steps
        }
        playingId = null
        progress = 0f
    }

    // Attach via the plus menu — thumbnails queue above the composer in a horizontal strip.
    GallerySection("Message composer · chat and AI prompt") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            MessageComposer(
                modifier = Modifier.fillMaxWidth(),
                label = "Message",
                placeholder = OrbitComposerPlaceholder.Message,
                onClipRecorded = { clips += it },
            )
            MessageComposer(
                modifier = Modifier.fillMaxWidth(),
                label = "Prompt",
                placeholder = OrbitComposerPlaceholder.Ai,
                onClipRecorded = { clips += it },
            )
        }
    }

    GallerySection("Composer · grown, scrolled, disabled") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            // Starts past one line, so the squared-off shape is visible without anyone having to
            // type. Deleting back down to a single line is the fastest way to see the morph.
            var grown by remember {
                mutableStateOf("Concrete pour on level 4 slipped to Thursday — the pump is still on Tower A.")
            }
            OrbitMessageField(
                value = grown,
                onValueChange = { grown = it },
                label = "Site update",
                placeholder = "Describe the delay and what is blocking it",
                onSend = { grown = "" },
                onMicClick = {},
                onAttachClick = {},
                modifier = Modifier.fillMaxWidth(),
            )

            // Long enough to exceed the five-line cap, so it opens already scrollable and the
            // bottom fade is showing on arrival. Scrolling up lights the top fade too.
            var overflowing by remember { mutableStateOf(LongPrompt) }
            OrbitMessageField(
                value = overflowing,
                onValueChange = { overflowing = it },
                label = "Prompt",
                onSend = {},
                onMicClick = {},
                onAttachClick = {},
                modifier = Modifier.fillMaxWidth(),
            )

            OrbitMessageField(
                value = "",
                onValueChange = {},
                label = "Message",
                placeholder = "Generating your report",
                onSend = {},
                onMicClick = {},
                onAttachClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Waveform · silence to full") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            OrbitAudioWave(
                amplitudes = List(60) { 0f },
                contentDescription = "Silent clip",
            )
            OrbitAudioWave(
                amplitudes = sampleEnvelope(60),
                progress = 0.35f,
                contentDescription = "Clip, 35 percent played",
            )
            OrbitAudioWave(
                amplitudes = sampleEnvelope(60),
                contentDescription = "Clip, fully played",
            )
        }
    }

    GallerySection("Voice notes · play, scrub, delete") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            SampleVoiceNote(
                seconds = 14,
                playing = playingId == SampleAId,
                progress = if (playingId == SampleAId) progress else 0f,
                onPlayPause = {
                    playingId = if (playingId == SampleAId) null else SampleAId
                    progress = 0f
                },
                onRemove = {},
            )
            SampleVoiceNote(
                seconds = 47,
                playing = playingId == SampleBId,
                progress = if (playingId == SampleBId) progress else 0f,
                onPlayPause = {
                    playingId = if (playingId == SampleBId) null else SampleBId
                    progress = 0f
                },
                onDelete = {},
            )
            SampleListenOnlyVoiceNote(
                seconds = 9,
                playing = playingId == SampleListenId,
                progress = if (playingId == SampleListenId) progress else 0f,
                onPlayPause = {
                    playingId = if (playingId == SampleListenId) null else SampleListenId
                    progress = 0f
                },
            )

            clips.forEach { clip ->
                ManagedVoiceNoteRow(
                    amplitudes = clip.amplitudes,
                    progress = if (playingId == clip.id) progress else 0f,
                    duration = formatDuration(clip.seconds),
                    playing = playingId == clip.id,
                    onPlayPause = {
                        playingId = if (playingId == clip.id) null else clip.id
                        progress = 0f
                    },
                    onDeleted = { clips.remove(clip) },
                )
            }
        }
    }
}

@Composable
private fun SampleListenOnlyVoiceNote(
    seconds: Int,
    playing: Boolean,
    progress: Float,
    onPlayPause: () -> Unit,
) {
    OrbitVoiceNoteRow(
        amplitudes = remember(seconds) { sampleEnvelope(seconds * 4) },
        progress = progress,
        duration = formatDuration(seconds),
        playing = playing,
        onPlayPause = onPlayPause,
        listenOnly = true,
        onDownload = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SampleVoiceNote(
    seconds: Int,
    playing: Boolean,
    progress: Float,
    onPlayPause: () -> Unit,
    onRemove: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    // Managed rather than raw, even for a canned sample. The dialogs are the behaviour being shown
    // here -- a bin that silently discards a recording is the bug this section exists to demonstrate
    // the absence of -- so a gallery row wired straight to the design system component would be
    // demonstrating the wrong thing.
    ManagedVoiceNoteRow(
        amplitudes = remember(seconds) { sampleEnvelope(seconds * 4) },
        progress = progress,
        duration = formatDuration(seconds),
        playing = playing,
        onPlayPause = onPlayPause,
        onRemoved = onRemove,
        onDeleted = onDelete,
    )
}

/** A speech-shaped envelope, deterministic so the gallery does not reshuffle on recomposition. */
private fun sampleEnvelope(n: Int): List<Float> = List(n) { i ->
    (0.2f + 0.6f * abs(sin(i * 0.07f)) * abs(sin(i * 0.29f)) + 0.15f * abs(sin(i * 1.1f)))
        .coerceIn(0f, 1f)
}

/** Six sentences: comfortably past the five-line cap at every font scale. */
private val LongPrompt = """
    Summarise every open RFI on Tower B, grouped by trade, and flag the ones that have been
    outstanding more than ten working days. For each of those, pull the last comment and say who it
    is waiting on. Then check the procurement log for any material tied to those RFIs and tell me
    whether the lead time still clears the current programme dates. If anything is at risk, propose
    a resequencing that keeps the handover date intact.
""".trimIndent()

private const val SampleAId = -1L
private const val SampleBId = -2L
private const val SampleListenId = -3L
