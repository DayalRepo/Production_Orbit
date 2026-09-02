package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * The two colours a segmented track is drawn from.
 *
 * Taken as a parameter rather than read from a domain enum, for the same reason every other
 * component here does it: `:core:designsystem` must not know what a project or an inspection is.
 */
@Immutable
data class OrbitProgressColors(
    val filled: Color,
    val track: Color,
)

object OrbitProgressDefaults {

    /**
     * Blue in both themes, pale on dark and mid on light — and that asymmetry is forced rather than
     * chosen.
     *
     * A bar is only informative if the lit run is distinguishable from the unlit one, and that
     * distinction is a lightness gap against the track. The track is a translucent neutral, so it
     * takes the card's own lightness: near-white on light, near-black on dark. To open a gap against
     * a near-white track the fill has to come *down*; against a near-black one it has to go *up*.
     * There is no single blue that does both — one colour for both themes lands in the middle, where
     * it is a poor separation twice over instead of a good one once.
     *
     * The consequence worth stating plainly, because it is the opposite of what most people expect
     * when they ask for a light blue bar: a genuinely *pale* blue is only available on the dark
     * theme. On a white card, `Blue60` at the top of a slat measures 2.55:1 against the track, which
     * is a bar you cannot read without also reading the number beside it. `Blue50` is as light as
     * the light theme goes while holding the floor, and it only gets there because the slat
     * highlight is kept low — see [SlatHighlightLight].
     *
     * Both are ramp steps rather than hand-picked values, and `SegmentedProgressContrastTest` pins
     * the gap at 3:1 or better at *both* ends of every slat — the WCAG 1.4.11 floor for a graphical
     * object that carries meaning.
     */
    val colors: OrbitProgressColors
        @Composable @ReadOnlyComposable get() = OrbitProgressColors(
            filled = if (OrbitTheme.isDark) OrbitPalette.Blue80 else OrbitPalette.Blue50,
            // Translucent, so the card tints through it exactly the way a badge's fill does. The
            // unlit run is a recess in the card rather than a grey object sitting on top of it.
            track = OrbitTheme.controlColors.controlContainer,
        )

    /**
     * How many slats a full-width bar is cut into.
     *
     * A count rather than a slat width, so the reading is stable across devices: 70% lights the same
     * number of slats on a small phone and on a tablet, and only the slats get wider. Fixing the
     * width instead would change the count with the screen, and with it the rounding, so the same
     * value could light a visibly different fraction on two devices.
     *
     * ### Fifty, because fifty makes one slat worth exactly 2%
     *
     * This is the reason for the specific number, and it is about arithmetic rather than looks. With
     * a count that does not divide 100 cleanly, the mapping from a percentage to a slat count is a
     * rounding nobody can do in their head, so the bar and the figure printed beside it drift in
     * ways that look like bugs -- at 48 slats, 25% lit 12 slats and so did 24%. At fifty:
     *
     * ```
     *   1 slat  =  2%        10 slats =  20%        25 slats =  50%
     *   5 slats =  10%       20 slats =  40%        50 slats = 100%
     * ```
     *
     * Every whole even percentage lands exactly on a slat boundary, and every odd one sits exactly
     * halfway between two, which is the most predictable rounding available. See [litSegments] for
     * the two places that rounding is deliberately overridden.
     *
     * ### Why the count did not have to change to widen the slats
     *
     * Worth spelling out, because "wider bars" sounds like it must mean "fewer bars" and here it did
     * not. The track always spans its container, so the width is a fixed budget split between slats
     * and gaps: `slat = (width - gaps) / count`. There are therefore two ways to widen a slat, and
     * removing slats is only one of them -- the other is taking the width out of the gaps instead.
     *
     * The last pass did the second. `progressSegmentGap` went from 2.5dp to 1.5dp, which on a 330dp
     * phone card returned 47dp of empty space to the slats and took each one from 4.4dp to 5.1dp,
     * while the count went *up* from 48 to 50 for the 2% arithmetic above. Both asks were met at
     * once: the gaps are visibly tighter and the bars are slightly fatter.
     *
     * ### The ceiling this is still under
     *
     * The whole argument for slats is that people count objects better than they judge a proportion,
     * and a bar cut fine enough that the slats read as a hatched texture has given that up while
     * still paying the quantisation error -- the worst of both. That is the failure mode to watch,
     * and the number that predicts it is the gap-to-slat ratio rather than the count: at 5.1dp
     * against a 1.5dp gap a slat is about 3.4 times the space beside it, so the two resolve
     * separately and the run does not fuse. Check that ratio, not this constant, before changing
     * either value; `SegmentedProgressLayoutTest` pins it from both directions.
     *
     * The floor is the opposite failure and is further away than it looks: too few slats and the
     * quantising distorts the reading, since each slat is worth more.
     */
    const val SegmentCount = 50

    /**
     * Fixed slat count for checklist progress — independent of how many items the list has.
     *
     * Twenty-four bars at full width reads as a standard bunch on phone cards; [OrbitSegmentedProgress]
     * scales slat width down on narrow tracks but keeps the same count cap.
     */
    const val ChecklistBarCount = 24

    /**
     * Peak alpha of the specular highlight on a lit slat, per theme.
     *
     * Deliberately weaker than every other glass surface in the system, and on light it is weak
     * enough to be worth justifying. The highlight only ever moves the fill toward white, so on a
     * light theme — where the fill is the darker of the two colours — every point of highlight is
     * spent directly out of the contrast budget against the track. At the ring's 0.16 the light
     * theme's blue drops to 2.55:1 and the bar stops being readable on its own. At this value it
     * holds close to 4:1 and still catches the top edge, which on a 20dp slat is all the glass cue
     * there is room for.
     *
     * Dark is lower again for the opposite reason: the fill is already pale, so white on top of it
     * lightens the fill rather than reading as light glancing off it — the same milky bloom the icon
     * button's ring ran into.
     */
    const val SlatHighlightLight = 0.08f
    const val SlatHighlightDark = 0.05f
}

/**
 * A progress track drawn as discrete vertical slats rather than one continuous fill.
 *
 * ### Why slats
 *
 * A solid bar is read by comparing the length of its filled part to the length of the whole, which
 * is a proportion judgement, and people are poor at those — the classic result is that a solid bar
 * at 70% is routinely read as anything from 60 to 80. Slats convert that into counting, which is
 * both more accurate and, more usefully, *comparable*: two stacked bars with a three-slat difference
 * are obviously different, where the same difference in solid bars looks like the same bar twice.
 *
 * The cost is quantisation, and it is a real cost rather than a rendering artefact. This component
 * is therefore only ever half of a reading — it is meant to sit beneath the exact figure, never to
 * replace it.
 *
 * ### Rounding
 *
 * Two cases are pulled out of the arithmetic because rounding gets them wrong in a way that is
 * actively misleading rather than merely imprecise. Any progress above zero lights at least one
 * slat, since a wholly dark bar is how this component says *nothing has started* and 2% is not
 * nothing. Any progress below 100% leaves at least one slat dark, since a wholly lit bar says
 * *finished* and 99.4% is not finished — that one matters most on the handover checklists where
 * somebody is looking for a reason to sign off.
 *
 * ### Accessibility
 *
 * The track publishes `ProgressBarRangeInfo`, so assistive technology reads the true fraction rather
 * than the quantised slat count. [contentDescription] should name what is progressing; without it
 * the bar announces a bare percentage with no subject, which in a column of four bars is useless.
 *
 * Meaning is never carried by the blue alone: the lit and unlit runs differ in lightness by at least
 * 3:1, so the boundary survives greyscale and colour deficiency, and the caller is expected to show
 * the figure alongside.
 *
 * @param progress 0f..1f. Values outside are clamped rather than throwing — a progress bar is not
 *   worth crashing a site report over, and a division that briefly yields 1.02 is a very ordinary
 *   bug in a caller.
 * @param segmentCount slats at full width. Reduced automatically if they would render narrower than
 *   `sizing.progressSegmentMinWidth`.
 */
@Composable
fun OrbitSegmentedProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colors: OrbitProgressColors = OrbitProgressDefaults.colors,
    height: Dp = OrbitTheme.sizing.progressTrackHeight,
    segmentCount: Int = OrbitProgressDefaults.SegmentCount,
) {
    val sizing = OrbitTheme.sizing
    val fraction = progress.coerceIn(0f, 1f)

    val gapPx: Float
    val minWidthPx: Float
    val radiusPx: Float
    with(androidx.compose.ui.platform.LocalDensity.current) {
        gapPx = sizing.progressSegmentGap.toPx()
        minWidthPx = sizing.progressSegmentMinWidth.toPx()
        radiusPx = sizing.progressSegmentRadius.toPx()
    }

    // Read outside the draw lambda, which is not a composable scope.
    val highlightAlpha = if (OrbitTheme.isDark) {
        OrbitProgressDefaults.SlatHighlightDark
    } else {
        OrbitProgressDefaults.SlatHighlightLight
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(current = fraction, range = 0f..1f)
                if (contentDescription != null) this.contentDescription = contentDescription
            },
    ) {
        val width = size.width
        if (width <= 0f) return@Canvas

        val count = affordableSegments(width, minWidthPx, gapPx, segmentCount)
        val slatWidth = (width - gapPx * (count - 1)) / count
        if (slatWidth <= 0f) return@Canvas

        val lit = litSegments(fraction, count)

        val corner = CornerRadius(radiusPx, radiusPx)

        repeat(count) { index ->
            val x = index * (slatWidth + gapPx)
            val slat = Size(slatWidth, size.height)
            val at = Offset(x, 0f)

            drawRoundRect(
                color = if (index < lit) colors.filled else colors.track,
                topLeft = at,
                size = slat,
                cornerRadius = corner,
            )

            // The glass read, and the reason each slat gets its own rather than one wash across the
            // lit run: a highlight per slat makes each one a separate pane catching the light, which
            // is what stops a run of them collapsing into a single solid bar with texture on it.
            // Only on lit slats — an unlit slat is a recess, and a recess does not catch light.
            if (index < lit) {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = highlightAlpha),
                            Color.White.copy(alpha = 0f),
                        ),
                        startY = 0f,
                        endY = size.height * HighlightFalloff,
                    ),
                    topLeft = at,
                    size = slat,
                    cornerRadius = corner,
                )
            }
        }
    }
}

/**
 * How many slats are lit at [fraction], given [count] of them.
 *
 * Extracted from the draw pass so the two rules that matter are reachable by a test: above zero
 * always lights at least one, and below one always leaves at least one dark. Both are corrections to
 * plain rounding, and both fix a case where the bar would otherwise state something false rather
 * than merely imprecise — an empty bar means *not started* and a full one means *done*.
 *
 * @param fraction expected already clamped to 0f..1f.
 */
internal fun litSegments(fraction: Float, count: Int): Int = when {
    count <= 0 -> 0
    fraction <= 0f -> 0
    fraction >= 1f -> count
    // A single slat has nowhere to put "started but not finished", and the clamp below would ask
    // for a value in 1..0, which throws. Lit, since something has started.
    count == 1 -> 1
    else -> (fraction * count).roundToInt().coerceIn(1, count - 1)
}

/**
 * The largest slat count that fits, never more than [requested].
 *
 * Solves `count * min + (count - 1) * gap <= width` directly rather than looping down from the
 * requested count. Clamped above by [requested] so this can only ever reduce the count — a wide
 * screen gets wider slats, not more of them, which is what keeps the same value looking the same on
 * every device.
 */
internal fun affordableSegments(
    width: Float,
    minWidth: Float,
    gap: Float,
    requested: Int,
): Int {
    if (width <= 0f || requested <= 0) return 0
    val affordable = floor((width + gap) / (minWidth + gap)).toInt()
    return requested.coerceAtMost(affordable).coerceAtLeast(1)
}

/**
 * How far down a slat the specular highlight has faded out.
 *
 * Just under half. Carried further the slat reads as a vertical gradient — a paint choice — rather
 * than as a lit edge, and the material cue is lost.
 */
private const val HighlightFalloff = 0.45f
