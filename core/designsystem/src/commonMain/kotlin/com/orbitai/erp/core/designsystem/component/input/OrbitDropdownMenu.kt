package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.orbitai.erp.core.designsystem.component.container.OrbitVerticalScrollbar
import com.orbitai.erp.core.designsystem.foundation.orbitDropShadow
import com.orbitai.erp.core.designsystem.foundation.orbitElevatedFill
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * How long the list takes to open, and the interval the chevron's turn is matched to.
 *
 * A shade slower than the popovers, on purpose. A bubble appears at a point and is read at a glance;
 * a dropdown unrolls downward over content the user was just looking at, and covering that content
 * too fast is what makes a list feel like it ambushed you. It is still short enough not to be waited
 * on.
 */
internal const val OrbitDropdownOpenMs = 180
private const val OrbitDropdownCloseMs = 130

/** Whether a dropdown's list is showing. Its own helper so both dropdowns seed it identically. */
@Composable
internal fun rememberDropdownExpanded(): MutableState<Boolean> = remember { mutableStateOf(false) }

/**
 * The panel of options that drops out of a field.
 *
 * ### The header does not scroll, and the list does
 *
 * The search box and the "add" action live in [header], outside the scroll container; only the
 * options move. This is the difference between a search box that is a tool and one that is an
 * artefact — a filter that scrolls out of view the moment you use it cannot be corrected without
 * scrolling back to find it, and on a hundred-item list the user has usually forgotten it was there
 * at all. Pinning it also means the field can never be in the confusing state of showing three
 * options with no visible explanation that a filter is applied.
 *
 * The cost is that the header eats into the panel's height budget, which is why [header] is a slot
 * rather than a fixed structure: a dropdown of six options passes nothing and gets the whole height
 * for its list.
 *
 * ### Matched to the field's width, not to its own content
 *
 * The panel takes the width the anchor measured rather than sizing to its longest row. A list wider
 * or narrower than the control it came from reads as a separate floating object that happens to be
 * nearby — and in a form, a panel wider than its field overhangs the fields either side of it, so it
 * is briefly unclear which one is being answered. Matching the width makes the panel look like the
 * field has grown downward, which is what it is.
 *
 * ### Why it unrolls rather than fades
 *
 * The open animation expands from the top edge, so the panel's growth starts exactly at the bottom
 * of the field and moves away from it. That is the same claim the width match is making, in motion:
 * this came out of that control. A plain fade would put a finished rectangle on screen with no
 * indication of where it came from, which on a dense form is a real question.
 *
 * ### Glass and shadow, and why the shadow is doing the work
 *
 * The panel floats over a form that is itself full of bordered fields, so the rim alone cannot
 * separate it — there are rims everywhere underneath it. The shadow is what puts it on a different
 * plane, and it is why the panel stays legible over a busy layout in either theme.
 *
 * @param width the anchor's measured width. Zero until the anchor has been laid out, in which case
 *   the panel sizes itself for the one frame before the real width arrives.
 * @param header pinned above the scrolling options. Draw its own trailing divider if it needs one;
 *   the panel does not assume the header is present.
 */
@Composable
internal fun OrbitDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    width: Dp,
    modifier: Modifier = Modifier,
    embedded: Boolean = false,
    /** Panel outline. AI assist menus pass [RectangleShape] for square corners. */
    shape: Shape = OrbitTheme.shapeTokens.field,
    header: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transition = remember { MutableTransitionState(false) }
    transition.targetState = expanded
    if (!expanded && transition.isIdle && !transition.currentState) return

    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors

    val panel: @Composable () -> Unit = {
        AnimatedVisibility(
            visibleState = transition,
            enter = fadeIn(tween(OrbitDropdownOpenMs)) +
                expandVertically(tween(OrbitDropdownOpenMs), expandFrom = Alignment.Top),
            exit = fadeOut(tween(OrbitDropdownCloseMs)) +
                shrinkVertically(tween(OrbitDropdownCloseMs), shrinkTowards = Alignment.Top),
        ) {
            Column(
                modifier = modifier
                    .then(
                        when {
                            embedded -> Modifier.fillMaxWidth()
                            width > Dp.Hairline -> Modifier.width(width)
                            else -> Modifier
                        },
                    )
                    .then(
                        if (embedded) {
                            Modifier
                        } else {
                            Modifier
                                .orbitDropShadow(shape = shape, level = OrbitShadow.Level2)
                                .orbitGlass(
                                    fill = orbitElevatedFill(OrbitShadow.Level2),
                                    shape = shape,
                                    highlightAlpha = if (OrbitTheme.isDark) {
                                        OrbitGlass.SurfaceHighlightDark
                                    } else {
                                        OrbitGlass.SurfaceHighlightLight
                                    },
                                    edge = control.controlBorder,
                                    edgeWidth = sizing.hairline,
                                )
                        },
                    )
                    .padding(vertical = spacing.xs),
            ) {
                header?.invoke(this)

                val listScroll = rememberScrollState()

                Row(
                    modifier = Modifier.heightIn(
                        max = if (embedded) sizing.dropdownMaxHeight / 2 else sizing.dropdownMaxHeight,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(listScroll),
                        content = content,
                    )
                    if (listScroll.maxValue > 0) {
                        OrbitVerticalScrollbar(
                            scrollState = listScroll,
                            modifier = Modifier.padding(end = spacing.xs),
                        )
                    }
                }
            }
        }
    }

    if (embedded) {
        panel()
        return
    }

    val density = LocalDensity.current
    val gapPx = with(density) { OrbitDropdownGap.roundToPx() }
    val edgeMarginPx = with(density) { OrbitDropdownEdgeMargin.roundToPx() }
    val provider = remember(gapPx, edgeMarginPx) {
        BelowAnchorPositionProvider(gapPx, edgeMarginPx)
    }

    Popup(
        popupPositionProvider = provider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        panel()
    }
}

/**
 * The air between the field and the panel that drops out of it.
 *
 * Flush was the first attempt and it reads as one tall object with a seam across it, because the
 * field and the panel share a fill, a rim and a corner radius — the only thing distinguishing them
 * at the join is a hairline, and a hairline is also what separates two rows *inside* the panel. A
 * few dp of background between them is what lets the panel read as floating above the form rather
 * than as the field having sprouted.
 *
 * Small, though. Enough gap and the panel stops looking attached to anything and starts looking like
 * it belongs to whatever is underneath it.
 */
private val OrbitDropdownGap = 4.dp

/**
 * The least space the panel keeps between itself and the edge of the screen.
 *
 * Matches the popover cards' own edge margin, so a dropdown and an avatar bubble pushed against the
 * same rim stop at the same place — two overlays inset by different amounts is the kind of difference
 * nobody can name but everybody sees.
 */
private val OrbitDropdownEdgeMargin = 12.dp

/**
 * Hangs the panel off the bottom of its anchor, and flips it above when there is no room below.
 *
 * The flip is not a nicety. A dropdown near the bottom of a scrolling form is the common case rather
 * than the edge case, and a panel that renders past the window is one whose last options cannot be
 * reached at all. Anchoring left rather than centring keeps the panel's leading edge flush with the
 * field's, so the option text starts on the same vertical line as the value it will replace.
 */
private class BelowAnchorPositionProvider(
    private val gapPx: Int,
    private val edgeMarginPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        // Below the field when the panel fits there, above it only when it fits there and not below.
        //
        // ### Why "fits" is checked on both sides and not just below
        //
        // The first version flipped above whenever the panel did not fit below, then clamped the result
        // into the window. For a field in the middle of a form that clamp is what produced the bug it
        // was supposed to prevent: neither side had room for the panel, so it flipped above, computed a
        // negative y, got pushed back down to the top margin — and landed straight over the field that
        // opened it. The value you were choosing was hidden behind the list of choices.
        //
        // So a flip now requires that the panel genuinely fit above. When neither side can hold it the
        // panel stays below, where its top is anchored to the field and it is the panel's own tail that
        // runs off the screen rather than the field that disappears. That is the better failure: the
        // list scrolls internally, so its lower rows are still reachable, and the field stays visible
        // the whole time. In practice this case is rare, because the list is capped at
        // `dropdownMaxHeight` precisely so a panel is never tall enough to need it.
        val below = anchorBounds.bottom + gapPx
        val above = anchorBounds.top - popupContentSize.height - gapPx

        val fitsBelow = below + popupContentSize.height <= windowSize.height - edgeMarginPx
        val fitsAbove = above >= edgeMarginPx

        val y = if (!fitsBelow && fitsAbove) above else below

        // Held off the screen's edges rather than flush against them. A panel wider than the control
        // that opened it gets pushed sideways until it fits, and clamping at zero put it hard into the
        // corner — where it has no shadow to read against, its rounded corner is cut by the display's
        // own, and it stops looking like a panel belonging to a field and starts looking like part of
        // the chrome. The year grid is where this showed up: four year pills are much wider than the
        // year trigger, so it is always the panel that gets pushed.
        val maxX = (windowSize.width - popupContentSize.width - edgeMarginPx).coerceAtLeast(edgeMarginPx)
        val x = anchorBounds.left.coerceIn(edgeMarginPx.coerceAtMost(maxX), maxX)

        // y is deliberately not clamped. Both branches above already respect the margin on the side
        // they were chosen for, and a clamp here is exactly what used to drag an above-placed panel
        // back down over its own field.
        return IntOffset(x, y)
    }
}
