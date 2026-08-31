package com.orbitai.erp.core.designsystem.component.media

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlin.math.abs
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

/** The two colours a waveform is drawn from. */
@Immutable
data class OrbitWaveColors(
    /** Bars that have been played, or — while recording — every bar there is. */
    val active: Color,
    /** Bars still ahead of the playhead. */
    val inactive: Color,
)

object OrbitWaveDefaults {
    val colors: OrbitWaveColors
        @Composable get() = OrbitWaveColors(
            active = if (OrbitTheme.isDark) OrbitPalette.Blue80 else OrbitPalette.Blue50,
            // Neutral ink at partial strength, *not* the near-invisible `controlContainer` the
            // progress track uses. The two look like the same job and are not: a progress track's
            // unlit run is empty space — there is nothing there to see — whereas a voice note's
            // unplayed run is the clip's own shape, which is the main thing the row is showing and
            // the only way to tell one recording from another before playing it. At the track's 8%
            // the silhouette was so faint the row read as a disabled control.
            inactive = OrbitTheme.contentColors.iconPrimary.copy(alpha = InactiveAlpha),
        )

    /**
     * Shortest a bar is ever drawn, as a fraction of the band.
     *
     * Silence still gets a mark. A waveform with true zeros has gaps in it, and a gap reads as *the
     * clip ends here* rather than as a quiet moment — which is actively wrong on a voice note, where
     * the pauses between words are most of the recording.
     *
     * The value is set so the stub is meaningfully **taller than it is wide**. At the first attempt
     * it was not, and with the caps rounded that made every silent bar a perfect circle: a silent
     * clip rendered as an evenly spaced row of dots, which does not read as a quiet waveform at all
     * — it reads as a dotted rule, the thing used elsewhere to separate sections. A short vertical
     * tick is unmistakably a very small bar.
     */
    const val MinBar = 0.22f

    /** See [colors]. Strong enough to read as a shape, weak enough to lose to the played run. */
    private const val InactiveAlpha = 0.32f
}

/**
 * An audio waveform: amplitude over time, as a row of mirrored vertical bars.
 *
 * One component for two jobs that look like they should be separate — the live meter while
 * recording, and the scrubber on a finished clip — because they are the same drawing with a
 * different [progress]. Recording is simply the case where everything is played: the newest sample
 * is at the right, the whole row is active, and the caller pushes [amplitudes] as they arrive.
 * Playback is the case where the playhead is partway along. Building these as two components means
 * the clip you were just watching redraws itself slightly differently the moment you stop, which
 * reads as a glitch even when every individual pixel is defensible.
 *
 * ### Why bars and not a filled envelope
 *
 * A continuous filled waveform is prettier and worse. At the width available in a composer the
 * envelope is a few dozen pixels tall, and a filled shape at that scale turns into a blob whose
 * interior carries no information. Discrete bars keep the per-sample reading legible, and they
 * degrade honestly: at narrow widths you get fewer bars rather than a smoother lie.
 *
 * ### Amplitude is not owned here
 *
 * [amplitudes] comes in from the caller and this component does no audio work whatsoever. That
 * boundary is deliberate and it is the reason this is usable as a library piece: the design system
 * has no business holding a microphone permission, a recorder, or a platform audio session. It
 * draws numbers between 0 and 1. Where they came from — a real `AudioRecord`, an AVAudioEngine tap,
 * or a gallery stub — is the app's problem.
 *
 * Values are clamped rather than trusted, because a recorder that returns a spike above 1.0 on the
 * first frame is common enough, and the failure without a clamp is a bar drawn outside the band.
 *
 * @param amplitudes normalised 0..1, oldest first. Longer than the bars that fit is fine and is the
 *   normal case while recording: see [resample].
 * @param progress how far the playhead has travelled, 0..1. Pass 1f while recording.
 * @param live the clip is still being captured. Changes what happens when there are fewer samples
 *   than bars — grow in from the right, rather than stretch to fill. See [resample]; the difference
 *   is visible and the wrong choice looks like a bug in both directions.
 * @param playing audio is coming out of the speaker right now. Adds a travelling ripple through the
 *   bars behind the playhead.
 *
 *   This is real feedback rather than decoration, and it is worth the frames. A paused clip and a
 *   playing clip whose playhead is creeping a pixel a second are visually identical for the first
 *   few seconds — the only other cue is the play glyph having swapped to a pause glyph, which is a
 *   24dp change at the far left of the row that nobody looks at after pressing it. On a short clip
 *   with the volume down, or on a device whose media session silently failed to start, the user's
 *   only question is "is this playing?" and the ripple answers it immediately.
 *
 *   Deliberately shallow, and confined to the played run. The bars are data, and an animation that
 *   swung them far enough to change the read of the waveform would be trading the component's
 *   purpose for its liveliness.
 *
 * @param contentDescription what this waveform is of. A waveform is a graphic conveying information
 *   (WCAG 1.1.1) and is meaningless to a screen reader without it — but the *useful* description is
 *   the clip's duration and state, not "waveform", so this is required rather than defaulted.
 */
@Composable
fun OrbitAudioWave(
    amplitudes: List<Float>,
    contentDescription: String,
    modifier: Modifier = Modifier,
    progress: Float = 1f,
    live: Boolean = false,
    playing: Boolean = false,
    colors: OrbitWaveColors = OrbitWaveDefaults.colors,
) {
    val sizing = OrbitTheme.sizing
    val density = LocalDensity.current

    // The transition is only created while it is being used, so a list of twenty finished voice
    // notes is not running twenty animation clocks in the background. A stopped waveform costs
    // nothing beyond its single draw.
    val phase = if (playing) {
        val transition = rememberInfiniteTransition(label = "orbit-wave")
        val animated by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            // Linear and restarting, because the phase is an angle: it wraps from 1 back to 0 and
            // the wave has to keep travelling at a constant rate through the seam. Any easing, or a
            // Reverse repeat, makes the ripple visibly slow down and turn around at the loop point.
            animationSpec = infiniteRepeatable(
                animation = tween(RippleMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "orbit-wave-phase",
        )
        animated
    } else {
        0f
    }

    val barPx = with(density) { sizing.waveBarWidth.toPx() }
    val gapPx = with(density) { sizing.waveBarGap.toPx() }
    val radius = with(density) { CornerRadius(sizing.waveBarWidth.toPx() / 2f) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(sizing.waveHeight)
            .semantics { this.contentDescription = contentDescription },
    ) {
        val width = size.width
        if (width <= 0f) return@Canvas

        val slot = barPx + gapPx
        val count = ((width + gapPx) / slot).toInt()
        if (count <= 0) return@Canvas

        val bars = resample(amplitudes, count, live)
        val mid = size.height / 2f
        // Rounded caps eat radius from both ends of a bar, so a bar shorter than its own width
        // renders as a squashed lozenge rather than a stub. Floor the height at the width.
        val minHeight = max(size.height * OrbitWaveDefaults.MinBar, barPx)
        val playedTo = (progress.coerceIn(0f, 1f) * count).roundToInt()

        bars.forEachIndexed { index, amplitude ->
            // The ripple is applied to the amplitude before the floor is taken, so silent stubs
            // ripple along with everything else. Exempting them would leave the quiet passages of a
            // clip conspicuously frozen while the loud ones moved, which reads as the animation
            // being broken rather than as the audio being quiet.
            val swell = if (playing && index < playedTo) {
                1f + RippleDepth * sin(TwoPi * (index.toFloat() / count * RippleWaves - phase))
            } else {
                1f
            }
            val h = max(minHeight, amplitude.coerceIn(0f, 1f) * size.height) * swell
            drawRoundRect(
                color = if (index < playedTo) colors.active else colors.inactive,
                topLeft = Offset(x = index * slot, y = mid - h / 2f),
                size = Size(barPx, h),
                cornerRadius = radius,
            )
        }
    }
}

/** One full pass of the ripple along the waveform. Slow enough to read as travel, not as flicker. */
private const val RippleMs = 1_100

/**
 * How many crests are on screen at once.
 *
 * Under about two the whole played run rises and falls together, which reads as the row breathing
 * rather than as anything moving through it. Far above three it turns into a shimmer at bar
 * frequency and starts to alias against the bars themselves as the width changes.
 */
private const val RippleWaves = 2.5f

/**
 * Peak height change, as a fraction. Eight percent: visible as motion in the row as a whole, small
 * enough that no single bar's height can be misread because of it.
 */
private const val RippleDepth = 0.08f

private const val TwoPi = (2.0 * PI).toFloat()

/**
 * Fits [source] to exactly [count] bars.
 *
 * ### Too many samples: take the peak, never the mean
 *
 * A voice note is mostly quiet with short loud consonants, and averaging buckets flattens exactly
 * those consonants into the silence around them. The result is the dead, uniform strip that makes
 * every recording look like it captured nothing. Peak keeps the envelope, which is the entire thing
 * a waveform is for.
 *
 * ### Too few samples: it depends on whether the clip is still growing, and this is not cosmetic
 *
 * The two cases want opposite treatment, and using one rule for both is wrong on device in a way
 * that is obvious the moment you see it.
 *
 * A **live** meter pads at the front, so the bars grow in from the right like a chart recorder. The
 * clip genuinely is shorter than the window; the empty space is the future, and it belongs ahead of
 * the samples. Getting this backwards makes the meter appear to shrink while you speak.
 *
 * A **finished** clip stretches to fill instead. It is not partially recorded — that is the whole
 * clip — so padding it leaves a run of dead bars at the left that reads as silence the recording
 * does not contain. It also breaks the playhead: [progress] is a fraction of the *bars*, so with a
 * quarter of them padding, playback appears to sit still for the first quarter of the clip. This
 * was the first version's behaviour and the padding was plainly visible as a dotted lead-in on
 * every sample clip in the gallery.
 *
 * Stretching is nearest-neighbour rather than interpolated. Interpolation would invent intermediate
 * amplitudes that were never recorded, and on a signal whose peaks are the information, inventing
 * values *between* peaks is precisely how you smooth the peaks away — the same mistake as averaging,
 * arrived at from the other direction.
 */
internal fun resample(source: List<Float>, count: Int, live: Boolean = false): List<Float> = when {
    count <= 0 -> emptyList()
    source.isEmpty() -> List(count) { 0f }
    source.size == count -> source
    source.size < count && live -> List(count - source.size) { 0f } + source
    source.size < count -> List(count) { index ->
        abs(source[index * source.size / count])
    }
    else -> {
        val bucket = source.size.toFloat() / count
        List(count) { index ->
            val from = (index * bucket).toInt()
            val to = (((index + 1) * bucket).toInt()).coerceAtLeast(from + 1).coerceAtMost(source.size)
            var peak = 0f
            for (i in from until to) peak = max(peak, abs(source[i]))
            peak
        }
    }
}
