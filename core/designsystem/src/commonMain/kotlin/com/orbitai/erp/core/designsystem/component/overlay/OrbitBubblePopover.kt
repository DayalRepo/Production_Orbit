package com.orbitai.erp.core.designsystem.component.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.foundation.orbitElevatedFill
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlin.math.roundToInt

/**
 * The glass bubble that points at the thing it belongs to: shape, placement, animation, and the
 * heading row with its close control. Everything below the divider is the caller's.
 *
 * ### Why this is a shared shell rather than one component per panel
 *
 * There are two of these panels — an identity card on a face in the avatar stack, an account menu on
 * your own avatar — and there will be more. What differs between them is a list of rows; what they
 * share is every hard part: a pointer built into the outline, a position provider that flips and
 * clamps, a pointer offset that has to survive that clamp, and an exit animation that only works if
 * the content outlives the state change that dismissed it. Duplicating that per panel means four
 * copies of the flip logic, and the third one is where the pointer starts pointing at nothing.
 *
 * It stays internal. A shell parameterised by arbitrary content is a tempting way to build a generic
 * popover, and a generic popover is how panels stop looking alike — the constraint that these are
 * always a heading, a rule and a few rows is the reason they read as one family.
 *
 * ### Why it points, and why the pointer is part of the panel
 *
 * A panel that appears near a tapped item and a panel that appears *attached* to it are different
 * components as far as the reader is concerned. In a wrapped grid of twenty avatars the faces are a
 * few millimetres apart, and a floating card centred over them is genuinely ambiguous about which
 * one it belongs to — the user has to reconstruct the answer from where their own thumb was, which
 * they have already forgotten. The pointer removes the question.
 *
 * It is drawn as part of the panel's own outline rather than as a triangle stacked against it, via
 * [OrbitBubbleShape]. That is not a rendering detail: a stacked pointer leaves the panel's border
 * drawn straight across the mouth, casts its own shadow onto the panel behind it, and misses the
 * fill's gradient — three separate tells that read, correctly, as two objects. The whole argument
 * for pointing is that the bubble and the anchor are one thing; a pointer that is visibly bolted on
 * undercuts it.
 *
 * It flips. Above the anchor by default, because a finger is on the anchor and a panel below it is a
 * panel under the user's own hand, but there genuinely is not room above for an avatar near the top
 * of the screen and the alternative to flipping is drawing off-window.
 *
 * ### The pointer tracks the anchor, not the panel
 *
 * The bubble is centred on the anchor until that would push it off the screen edge, at which point
 * it is clamped — and a clamped bubble with a centred pointer indicates empty space beside the
 * avatar, which is worse than no pointer at all because it confidently points at the wrong face. So
 * the position provider records how far it had to slide and the pointer is offset back by the same
 * amount, staying over the anchor while the panel moves. The shape clamps the offset again to keep
 * the pointer clear of its own rounded corners.
 *
 * ### Opening and closing
 *
 * Both directions animate, which takes more machinery than it looks like it should: a `Popup` that
 * is simply not composed when closed has no frame in which to run its exit, so the content is kept
 * mounted until the transition has finished and only then removed. Without that the panel fades in
 * politely and then vanishes, which reads as a glitch and loses the one cue confirming that the tap
 * outside was received.
 *
 * @param title the heading. Short — a label for the panel, not a summary of it.
 * @param minWidth floor on the bubble's width. Exposed with [maxWidth] so a caller whose values run
 *   long — a full legal entity name, an equipment serial — can widen it rather than have it wrap.
 */
@Composable
internal fun OrbitBubblePopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    title: String,
    minWidth: Dp,
    maxWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Seeded closed even when `expanded` is already true on first composition, so the bubble plays
    // its entrance rather than snapping into place.
    val transition = remember { MutableTransitionState(false) }
    transition.targetState = expanded

    // `isIdle` is false for exactly as long as the transition is still running, which is the precise
    // window in which the content must stay composed for the closing animation to have anything to
    // animate.
    if (!expanded && transition.isIdle && !transition.currentState) return

    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val contentColors = OrbitTheme.contentColors
    val density = LocalDensity.current

    // Written by the position provider during layout, read by the shape when it builds its outline.
    // A shared holder rather than a return value because a `PopupPositionProvider` has no way to
    // hand anything back to its content, and the pointer cannot be placed without knowing where the
    // panel actually ended up.
    var pointerOffset by remember { mutableFloatStateOf(0f) }
    var below by remember { mutableStateOf(false) }

    val arrowH = with(density) { ArrowHeight.toPx() }
    val edgeColor = if (OrbitTheme.isDark) {
        control.controlBorder
    } else {
        contentColors.textPrimary.copy(alpha = LightEdgeAlpha)
    }

    val edgeMarginPx = with(density) { PopoverEdgeMargin.toPx() }.roundToInt()

    val provider = remember(arrowH, edgeMarginPx) {
        PointerPositionProvider(
            gapPx = arrowH.roundToInt(),
            edgeMarginPx = edgeMarginPx,
            onPlaced = { offset, isBelow ->
                pointerOffset = offset
                below = isBelow
            },
        )
    }

    val shape = remember(below, pointerOffset) {
        OrbitBubbleShape(
            // The panel points *up* when it has been placed below its anchor.
            pointingUp = below,
            pointerOffsetPx = pointerOffset,
            arrowWidth = ArrowWidth,
            arrowHeight = ArrowHeight,
            cornerRadius = PopoverCorner,
        )
    }

    Popup(
        popupPositionProvider = provider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(
            visibleState = transition,
            // Grows from the pointer, which is the end nearest the anchor. A panel that scales from
            // its own centre reads as having arrived; one that grows from the thing it points at
            // reads as having come out of it — the same causal claim the pointer is making. The
            // origin follows the flip, or the bubble would grow out of its own far edge.
            enter = fadeIn(tween(OpenMs)) + scaleIn(
                tween(OpenMs),
                initialScale = 0.9f,
                transformOrigin = TransformOrigin(0.5f, if (below) 0f else 1f),
            ),
            // Faster out than in. An entrance is information and the eye has to be led to it; a
            // dismissal is an acknowledgement, and a leisurely one sits between the user and
            // whatever they tapped next.
            exit = fadeOut(tween(CloseMs)) + scaleOut(
                tween(CloseMs),
                targetScale = 0.94f,
                transformOrigin = TransformOrigin(0.5f, if (below) 0f else 1f),
            ),
        ) {
            Column(
                modifier = modifier
                    .widthIn(min = minWidth, max = maxWidth)
                    // Shadow, fill and rim all take the same outline, so the pointer is lit,
                    // shaded and bordered as part of the panel rather than beside it.
                    // Level 4. A popover is drawn over arbitrary content and has to read as being
                    // somewhere else entirely, not one rung up from the card underneath it.
                    .orbitDropShadow(shape = shape, level = OrbitShadow.Level4)
                    .orbitGlass(
                        fill = orbitElevatedFill(OrbitShadow.Level4),
                        shape = shape,
                        highlightAlpha = if (OrbitTheme.isDark) {
                            OrbitGlass.SurfaceHighlightDark
                        } else {
                            OrbitGlass.SurfaceHighlightLight
                        },
                        edge = edgeColor,
                        edgeWidth = sizing.hairline,
                    )
                    // Reserves the pointer's depth on whichever side it protrudes, so no content
                    // can land underneath it. The shape insets its body by exactly this much.
                    .padding(
                        top = if (below) ArrowHeight else 0.dp,
                        bottom = if (below) 0.dp else ArrowHeight,
                    )
                    .padding(vertical = spacing.xs),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = spacing.md, end = spacing.xxs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        // The largest and heaviest thing in the panel. It started a step smaller
                        // and lighter than this and read as a stray caption rather than as the
                        // bubble's title -- which matters more here than it would on a card,
                        // because a panel that appears over other content has to say what it is
                        // before it says anything else.
                        style = OrbitTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    OrbitIconButton(
                        contentDescription = "Close $title",
                        onClick = onDismiss,
                        icon = OrbitIcons.Cancel,
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Small,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xs))

                // Inset to match the rules the content draws between its own sections. Full-bleed, it
                // was the one rule in the panel that touched both rims, so the header looked like a
                // separately attached strip rather than the first section of one card.
                OrbitDivider(
                    inset = spacing.md + spacing.xs,
                    endInset = spacing.md + spacing.xs,
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                content()
            }
        }
    }
}

/**
 * Centres the panel over its anchor, flips it below when there is no room above, and reports how far
 * it had to slide so the pointer can compensate.
 */
private class PointerPositionProvider(
    private val gapPx: Int,
    private val edgeMarginPx: Int,
    private val onPlaced: (offset: Float, below: Boolean) -> Unit,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        // Below the anchor when there is room, above it when there is not.
        //
        // A bubble hangs off a face, and faces sit anywhere on a screen — including the last row
        // above the keyboard, where a panel placed below would be half off the bottom. Flipping is
        // what keeps the whole panel reachable; the pointer moves to the opposite edge with it, so
        // the bubble still visibly belongs to the avatar it came from either way.
        val above = anchorBounds.top - popupContentSize.height
        val below = anchorBounds.bottom + popupContentSize.height <= windowSize.height || above < 0
        val y = if (below) anchorBounds.bottom else above

        val anchorCentre = anchorBounds.left + anchorBounds.width / 2
        val wanted = anchorCentre - popupContentSize.width / 2

        // Clamped to a margin inside the window, not to the window itself. Clamping to zero is what
        // put the panel flush against the screen edge whenever its anchor sat near a corner — the
        // rounded corner and the rim on that side ran straight off the display, so a panel that is
        // meant to read as floating above the screen looked instead like it was welded to the frame.
        //
        // `coerceAtLeast` on the upper bound so that a panel wider than the window minus its margins
        // degrades to being merely flush rather than throwing on an inverted range.
        val minX = edgeMarginPx
        val maxX = (windowSize.width - popupContentSize.width - edgeMarginPx).coerceAtLeast(minX)
        val x = wanted.coerceIn(minX, maxX)

        // How far the clamp moved the panel, expressed as the offset the pointer needs in order to
        // stay over the anchor. The shape clamps this again against its own corners; this bound is
        // the coarse one, and exists so a wildly off-screen anchor cannot ask for a nonsense value.
        val drift = (wanted - x).toFloat()
        val limit = (popupContentSize.width / 2f - gapPx * 2f).coerceAtLeast(0f)
        onPlaced(drift.coerceIn(-limit, limit), below)

        return IntOffset(x, y.coerceAtLeast(edgeMarginPx))
    }
}

/**
 * The pointer's span and depth.
 *
 * The ratio matters more than either number. A curved pointer spends part of its width easing out of
 * the panel's edge, so below about 2:1 the curves consume the whole span and there is nothing left
 * to form a tip; much past 3:1 it flattens into a bump that no longer reads as pointing anywhere.
 */
private val ArrowWidth: Dp = 20.dp
private val ArrowHeight: Dp = 8.dp

/**
 * Tighter than the card radius the panel used to borrow.
 *
 * A 16dp radius on a bubble this small leaves almost no straight edge for the pointer to grow out
 * of, and the pointer has to stay clear of the corner arcs — at that radius on a 200dp panel the
 * legal span for the pointer is barely half the width, so a clamped bubble runs out of room to track
 * its anchor.
 */
private val PopoverCorner: Dp = 12.dp

/**
 * The rim alpha a light-theme panel draws over its own primary text colour.
 *
 * Low enough to read as an edge rather than an outline, high enough to separate a white panel from a
 * white page. Shared with the dialog, which has the same problem.
 */
private const val LightEdgeAlpha = 0.14f

/**
 * How close to the window edge the panel may be pushed.
 *
 * A popover anchored to an avatar in the last column wants to be centred on a face that is itself
 * near the edge, so the clamp is doing real work on every such tap — and clamped to the window it put
 * the panel's rounded corner and rim flush against the display, which reads as clipped rather than as
 * floating.
 *
 * Deliberately larger than [PopoverCorner]: the margin has to clear the corner radius for the curve to
 * be seen as a curve, and at 12dp against a 12dp radius the arc still ends exactly at the edge.
 */
private val PopoverEdgeMargin: Dp = 16.dp

private const val OpenMs = 150
private const val CloseMs = 110
