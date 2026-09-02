package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Softens whichever edge a vertically scrolled box has text hidden behind.
 *
 * The vertical counterpart to [OrbitFieldOverflowFade], and it exists for the same reason: a
 * scrolled container that shows no sign of being scrolled is lying about how much it contains. In a
 * composer that lie is expensive — a user reads the five lines they can see, believes that is the
 * whole prompt, and sends it with a paragraph above the fold they had stopped thinking about.
 *
 * ### Both edges, independently
 *
 * Top fades once you have scrolled down, bottom fades while there is more below, and in the middle
 * of a long prompt both are faded at once. Fading only the bottom — the common shortcut — tells you
 * there is more *ahead* but leaves you unable to tell whether you are at the beginning, which is the
 * half of the question that matters when you are checking what you are about to send.
 *
 * ### Why not a scrollbar
 *
 * A scrollbar is a better indicator in the abstract: it shows position *and* proportion, where a
 * fade only says "there is more". But it needs a gutter, and a gutter inside a 48dp composer takes
 * width from the text and puts a piece of chrome in a component that is otherwise entirely content.
 * The fade costs no space at all, which is the whole reason it is the convention for scrolled text
 * everywhere else. The trade is real and this is the cheaper side of it.
 *
 * ### The alpha is animated
 *
 * Both edges cross-fade rather than snapping on. The top fade appears the instant you scroll one
 * pixel, and an instant hard edge at that moment reads as a rendering glitch — something flickering
 * at the top of the box — rather than as a hint. Over a few frames it reads as material.
 *
 * ### The offscreen layer is not optional
 *
 * `DstIn` needs its own layer to punch through, and without [CompositingStrategy.Offscreen] the
 * blend applies against whatever is behind the field and erases the *fill* rather than the text —
 * the box appears to have notches cut out of it. This is the failure mode worth remembering,
 * because it looks like a shape bug rather than a blending one.
 */
@Composable
internal fun OrbitScrollFade(
    scrollState: ScrollState,
    content: @Composable () -> Unit,
) {
    val fade = with(LocalDensity.current) { FadeDp.dp.toPx() }

    val topAlpha by animateFloatAsState(
        targetValue = if (scrollState.value > 0) 1f else 0f,
        animationSpec = tween(FadeMs),
        label = "orbit-scroll-fade-top",
    )
    val bottomAlpha by animateFloatAsState(
        targetValue = if (scrollState.value < scrollState.maxValue) 1f else 0f,
        animationSpec = tween(FadeMs),
        label = "orbit-scroll-fade-bottom",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                if (topAlpha > 0f) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            // Lerped rather than switched, so a partly-faded edge is partly
                            // transparent instead of absent — that is what makes the cross-fade
                            // read as the edge softening rather than as it appearing.
                            colors = listOf(Color.Black.copy(alpha = 1f - topAlpha), Color.Black),
                            startY = 0f,
                            endY = fade,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                }
                if (bottomAlpha > 0f) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black, Color.Black.copy(alpha = 1f - bottomAlpha)),
                            startY = size.height - fade,
                            endY = size.height,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                }
            },
    ) {
        content()
    }
}

/**
 * How deep the fade reaches.
 *
 * Under half a line. Deep enough to read as a soft edge, shallow enough that the partly-faded line
 * is still readable — the point is to say "there is more", not to censor the line that says it.
 */
private const val FadeDp = 14
private const val FadeMs = 120
