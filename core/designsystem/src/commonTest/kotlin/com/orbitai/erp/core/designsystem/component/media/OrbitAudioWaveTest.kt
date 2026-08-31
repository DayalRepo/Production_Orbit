package com.orbitai.erp.core.designsystem.component.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * [resample] is the whole of the waveform's logic — the rest is drawing — and the two decisions
 * inside it are both the kind that look like details and are actually the difference between a
 * waveform that reads as a voice and one that reads as a bug.
 */
class OrbitAudioWaveTest {

    @Test
    fun `always returns exactly the requested count`() {
        listOf(0, 1, 5, 17, 60).forEach { source ->
            listOf(1, 8, 33).forEach { count ->
                assertEquals(
                    count,
                    resample(List(source) { 0.5f }, count).size,
                    "resample($source -> $count) must fill the row exactly",
                )
            }
        }
    }

    @Test
    fun `no bars at all is a valid answer rather than a crash`() {
        // A zero-width canvas happens for a frame during layout, and the arithmetic that finds the
        // bar count divides by it.
        assertEquals(emptyList(), resample(listOf(1f), 0))
        assertEquals(emptyList(), resample(emptyList(), 0))
    }

    @Test
    fun `a live recording pads at the front so bars grow in from the right`() {
        val out = resample(listOf(0.8f, 0.9f), 5, live = true)

        assertEquals(listOf(0f, 0f, 0f, 0.8f, 0.9f), out)
        // The specific failure this guards: padding at the end instead puts the newest sample in
        // the middle and the meter appears to shrink while you speak.
        assertEquals(0.9f, out.last(), "newest sample belongs at the right edge")
    }

    @Test
    fun `a finished clip stretches to fill instead of padding`() {
        val out = resample(listOf(0.8f, 0.9f), 6, live = false)

        // No blanks anywhere. Padding a finished clip puts dead bars at the left that read as
        // silence the recording does not contain, and — worse — throws the playhead off, since
        // progress is a fraction of the bars and a quarter of them would not belong to the audio.
        assertTrue(out.none { it == 0f }, "a finished clip has no padding: $out")
        assertEquals(6, out.size)
        assertEquals(0.8f, out.first())
        assertEquals(0.9f, out.last())
    }

    @Test
    fun `stretching repeats samples rather than inventing values between them`() {
        val out = resample(listOf(0f, 1f), 4, live = false)

        // Nearest-neighbour, not interpolation. Interpolating would emit 0.33 and 0.67 here —
        // amplitudes that were never recorded — and on a signal whose peaks are the information,
        // inventing values between peaks is how the peaks get smoothed away.
        assertEquals(listOf(0f, 0f, 1f, 1f), out)
    }

    @Test
    fun `downsampling keeps peaks rather than averaging them away`() {
        // One loud consonant in an otherwise quiet bucket. Averaging buries it; a voice note made
        // of averaged buckets renders as a flat strip and looks like it recorded nothing.
        val quietWithOneSpike = listOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f)

        val out = resample(quietWithOneSpike, 2)

        assertEquals(1f, out[0], "the spike must survive into its bucket")
        assertTrue(out[0] > out[1], "the bucket holding the spike must outrank the silent one")
    }

    @Test
    fun `an exact fit is returned untouched`() {
        val source = listOf(0.1f, 0.2f, 0.3f)
        assertEquals(source, resample(source, 3))
    }

    @Test
    fun `negative samples count toward the peak by magnitude`() {
        // A raw PCM tap is signed and swings both ways. Taking the peak without abs() would report
        // a loud negative trough as silence, so half the waveform would go missing.
        assertEquals(listOf(0.9f), resample(listOf(-0.9f, 0.1f), 1))
    }

    @Test
    fun `an empty source still fills the row`() {
        assertEquals(List(4) { 0f }, resample(emptyList(), 4))
    }
}
