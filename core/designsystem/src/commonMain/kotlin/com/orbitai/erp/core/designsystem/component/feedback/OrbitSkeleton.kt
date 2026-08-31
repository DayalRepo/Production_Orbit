package com.orbitai.erp.core.designsystem.component.feedback

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A placeholder block that pulses while real content loads.
 *
 * ### A pulse, not a shimmer
 *
 * The fashionable treatment is a specular band sweeping left to right. This one just breathes
 * opacity, for two reasons. A sweep is a horizontal motion across the screen, which is the same
 * gesture vocabulary as a swipe, and on a list of eight placeholders it reads as content already
 * moving. And a sweeping gradient is a per-frame shader over every placeholder on screen — on the
 * mid-range Android hardware this product targets, spending that on the loading state is spending it
 * at exactly the moment the device is already busy doing the thing being waited for.
 *
 * The pulse is also deliberately slow and shallow. Anything faster starts to read as an error
 * condition, and `prefers-reduced-motion` users are served by an animation that never travels.
 *
 * ### It says "loading", once
 *
 * A skeleton screen is typically six or eight of these. Each one announcing itself would produce a
 * screen that says "loading" eight times, so only [OrbitSkeletonList] and the other assemblies carry
 * a description; the individual blocks are silent. A bare [OrbitSkeleton] used on its own should be
 * given a description by whatever contains it.
 */
@Composable
fun OrbitSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
) {
    val control = OrbitTheme.controlColors

    // Static in previews and screenshot tests, where an infinite animation never settles and the
    // test either flakes on the frame it caught or hangs waiting for idle.
    val alpha = if (LocalInspectionMode.current) {
        PulseHigh
    } else {
        val transition = rememberInfiniteTransition(label = "skeleton")
        transition.animateFloat(
            initialValue = PulseLow,
            targetValue = PulseHigh,
            animationSpec = infiniteRepeatable(
                animation = tween(PulseDurationMillis),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "skeleton-alpha",
        ).value
    }

    Box(
        modifier = modifier
            .alpha(alpha)
            .background(control.controlContainer, shape),
    )
}

/** A single line of placeholder text. [widthFraction] varies the ragged right edge of a paragraph. */
@Composable
fun OrbitSkeletonLine(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
) {
    OrbitSkeleton(
        modifier = modifier
            .fillMaxWidth(widthFraction.coerceIn(0.05f, 1f))
            .height(OrbitTheme.sizing.skeletonLineHeight),
    )
}

/**
 * The placeholder for a list of rows, each an avatar beside two lines of text.
 *
 * The second line of every row is short and the first is long, which is what a list of names and
 * subtitles actually looks like. Uniform-width placeholders are the tell that gives a skeleton away
 * as a loading state rather than as content arriving — the eye reads a rectangle grid as a table and
 * then has to re-read the screen when the real, ragged content replaces it.
 *
 * @param rows how many placeholders to draw. Match roughly what fits on screen; drawing twenty for a
 *   list that will show six makes the arriving content look like it shrank.
 */
@Composable
fun OrbitSkeletonList(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    avatarSize: Dp = OrbitTheme.sizing.avatarSm,
) {
    val spacing = OrbitTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading" },
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        // A fixed pattern rather than a random one: a skeleton that recomposes with different widths
        // flickers, and seeding a generator to stop that is more machinery than the effect is worth.
        val widths = listOf(0.62f, 0.78f, 0.55f, 0.7f, 0.6f)

        repeat(rows.coerceAtLeast(1)) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrbitSkeleton(
                    modifier = Modifier.size(avatarSize),
                    shape = CircleShape,
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    OrbitSkeletonLine(widthFraction = widths[index % widths.size])
                    OrbitSkeletonLine(widthFraction = widths[(index + 2) % widths.size] * 0.6f)
                }
            }
        }
    }
}

/** Half a second each way. Slower than it feels like it should be; faster reads as an alarm. */
private const val PulseDurationMillis = 900

private const val PulseLow = 0.45f
private const val PulseHigh = 1f
