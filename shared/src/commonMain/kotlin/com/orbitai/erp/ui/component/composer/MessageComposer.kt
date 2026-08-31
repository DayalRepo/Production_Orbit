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
import com.orbitai.erp.core.designsystem.component.input.OrbitAttachMenu
import com.orbitai.erp.core.designsystem.component.input.OrbitAttachOption
import com.orbitai.erp.core.designsystem.component.input.OrbitComposerMode
import com.orbitai.erp.core.designsystem.component.input.OrbitMessageField
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * A recorded clip, as the composer hands it back.
 *
 * @param amplitudes the envelope, normalised 0..1.
 * @param seconds length. Kept as a number rather than a formatted string so that the caller can
 *   render it in its own locale; [formatDuration] is here for the common case.
 */
@Stable
data class VoiceClip(
    val id: Long,
    val amplitudes: List<Float>,
    val seconds: Int,
)

/**
 * The two placeholders, so the wording is decided once rather than retyped per screen.
 *
 * They are deliberately different sentences and not one shared string. A chat box and an assistant
 * prompt box are the same component doing genuinely different work, and the placeholder is the only
 * thing on screen that says which: "Write a message" sets the expectation that a person reads it,
 * "Ask Orbit AI" that a model answers it. Users write differently for the two — shorter and more
 * elliptical for a colleague, more complete for a model — and they only know which mode they are in
 * from this line.
 *
 * Both are short by construction. See the note on the `placeholder` parameter for why the space is
 * tighter than it looks.
 */
object OrbitComposerPlaceholder {
    const val Message = "Write a message"
    const val Ai = "Ask Orbit AI"
}

/** "0:07". Minutes and seconds is enough — a voice note long enough to need hours is a file. */
fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

/**
 * The composer with its recording and menu state wired up.
 *
 * ### Why the state machine lives here and not in the design system
 *
 * `OrbitMessageField` is deliberately inert: it renders a mode and reports taps. Everything that
 * makes the microphone *work* — starting capture, ticking the elapsed clock, accumulating
 * amplitudes, deciding what a stop produces — is sequencing over time, and sequencing over time is
 * where platform reality intrudes. A real implementation of this has to request a runtime
 * permission that the user can refuse, survive the activity being backgrounded mid-recording, and
 * release an audio session it does not own. None of that can live in a component that also has to
 * render in a gallery and a unit test.
 *
 * So the split is: the design system draws, this layer decides. Swapping the stub below for a real
 * `AudioRecord` or `AVAudioEngine` is a change to this file and to nothing else, which is the whole
 * point of putting the seam here.
 *
 * ### The amplitudes are synthetic, and that is flagged rather than hidden
 *
 * [fakeAmplitude] generates a plausible speech envelope so the visualiser can be reviewed before
 * any audio plumbing exists. It is not a placeholder that will quietly ship — it is the only thing
 * in this file a real recorder replaces, and it is named so that it cannot be mistaken for signal.
 *
 * @param onSend fires with the text and any clip attached. The composer clears itself; a composer
 *   that waited for the caller to clear it leaves the sent message sitting in the box on a slow
 *   network, and people send it twice.
 */
@Composable
fun MessageComposer(
    modifier: Modifier = Modifier,
    // Short enough to survive the space it actually has. The composer gives its text area roughly
    // half the screen once the plus, mic and send buttons have taken their touch targets, and a
    // placeholder that overruns is ellipsised — a hint ending in "..." reads as a rendering fault
    // rather than as a prompt, and the half of the sentence that gets cut is always the end.
    //
    // The default names the plainer of the two jobs. A composer wired to the assistant should pass
    // `OrbitComposerPlaceholder.Ai` or its own string: one box doing both jobs cannot say so in the
    // space available, and trying produced "Ask Orbit AI, or write a mes...". Where the box really
    // is both, the surrounding screen says which — a placeholder is not the place to explain a
    // feature.
    placeholder: String = OrbitComposerPlaceholder.Message,
    label: String = "Message",
    onSend: (text: String, clip: VoiceClip?) -> Unit = { _, _ -> },
    onAttachImage: () -> Unit = {},
    onAttachFile: () -> Unit = {},
    onClipRecorded: (VoiceClip) -> Unit = {},
) {
    var text by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var elapsed by remember { mutableIntStateOf(0) }
    val amplitudes = remember { mutableStateListOf<Float>() }

    // One tick drives both the clock and the meter. Separate timers for the two is how the number
    // and the waveform end up disagreeing about how long the recording is.
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
        // A clip shorter than a syllable is a mis-tap on the mic, not a recording. Returning it
        // would attach a silent sliver to the message, which the user then has to notice and
        // delete — worse than the tap simply not having taken.
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

    OrbitMessageField(
        value = text,
        onValueChange = { text = it },
        label = label,
        placeholder = placeholder,
        mode = mode,
        attachExpanded = menuOpen,
        modifier = modifier,
        onSend = {
            val clip = finishRecording()
            if (text.isNotBlank() || clip != null) {
                onSend(text, clip)
                clip?.let(onClipRecorded)
                text = ""
            }
        },
        onMicClick = {
            // Opening the mic closes the attach menu. Both occupy the same middle of the composer
            // and leaving the panel up over a live waveform is just clutter.
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
                    OrbitAttachOption("Upload image", OrbitIcons.ImageUpload, onAttachImage),
                    OrbitAttachOption("Attach file", OrbitIcons.FileUpload, onAttachFile),
                ),
            )
        },
    )
}

/**
 * A speech-shaped amplitude for frame [n]. **Stub — a real recorder replaces this.**
 *
 * Two sines beating against each other give the slow swell of phrases and the faster bumps of
 * syllables inside them; the noise keeps consecutive bars from being identical, which is the tell
 * that gives a fake waveform away instantly. A single sine looks like a test tone, and pure random
 * looks like static — neither reads as a voice, and the point of the stub is to exercise the
 * visualiser against something shaped like its real input.
 */
private fun fakeAmplitude(n: Int): Float {
    val phrase = abs(sin(n * 0.06f))
    val syllable = abs(sin(n * 0.31f))
    val noise = Random.nextFloat() * 0.25f
    return (0.25f + 0.55f * phrase * syllable + noise).coerceIn(0f, 1f)
}

private const val TickMs = 80L
private const val FramesPerSecond = 1000 / TickMs.toInt()
private const val MinClipSeconds = 1
