package com.orbitai.erp.core.designsystem.component.container

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** How [OrbitCardViewer] lays its pages out. */
enum class OrbitCardViewerMode { Carousel, Stack }

/**
 * One card's placement inside the viewer, in pixels from the top-start of the viewport.
 *
 * [zIndex] is higher for cards that should draw — and receive taps — in front.
 */
data class OrbitCardSlot(
    val x: Float,
    val y: Float,
    val zIndex: Float,
)

/**
 * Horizontal carousel: the selected card is centred, neighbours sit one card-width (+ gap) away
 * so they peek in from the sides. Closer pages sit in front.
 */
fun orbitCardCarouselSlot(
    index: Int,
    pagerOffset: Float,
    count: Int,
    viewportWidth: Float,
    cardWidth: Float,
    gap: Float,
): OrbitCardSlot {
    val dx = (index - pagerOffset) * (cardWidth + gap)
    val x = (viewportWidth - cardWidth) / 2f + dx
    val distance = abs(index - pagerOffset)
    val zIndex = count - distance
    return OrbitCardSlot(x = x, y = 0f, zIndex = zIndex)
}

/**
 * Wallet stack: index 0 sits at the bottom and in front. Later cards sit above it and behind,
 * each revealing a [peek] strip at the top.
 */
fun orbitCardStackSlot(
    index: Int,
    count: Int,
    viewportWidth: Float,
    cardWidth: Float,
    peek: Float,
): OrbitCardSlot {
    val x = (viewportWidth - cardWidth) / 2f
    val y = (count - 1 - index) * peek
    val zIndex = (count - index).toFloat()
    return OrbitCardSlot(x = x, y = y, zIndex = zIndex)
}

fun orbitCardStackHeight(cardHeight: Float, count: Int, peek: Float): Float {
    if (count <= 0) return 0f
    return cardHeight + (count - 1) * peek
}

fun orbitCardViewerClampIndex(index: Int, count: Int): Int {
    if (count <= 0) return 0
    return index.coerceIn(0, count - 1)
}

fun orbitCardIndexLabel(index: Int): String =
    (index + 1).coerceAtLeast(0).toString().padStart(2, '0')

fun lerpOrbitCardSlot(from: OrbitCardSlot, to: OrbitCardSlot, fraction: Float): OrbitCardSlot {
    val t = fraction.coerceIn(0f, 1f)
    return OrbitCardSlot(
        x = from.x + (to.x - from.x) * t,
        y = from.y + (to.y - from.y) * t,
        zIndex = from.zIndex + (to.zIndex - from.zIndex) * t,
    )
}

private val ModeSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

private val PagerSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMedium,
)

/**
 * A page of cards that can sit in a horizontal carousel or collapse into a wallet stack.
 *
 * This is a layout, not a screen. It fills the width it is given and sizes its height to the
 * cards; put it in a `weight(1f)` slot when the parent is a column.
 *
 * Tap the centred card in the carousel to fold the set into a stack. Tap any card in the stack
 * to unfold, with that card becoming the new centre. Drag sideways in the carousel to page.
 *
 * The two layouts share one set of nodes. Positions are interpolated, so cards glide and restack
 * instead of cross-fading.
 */
@Composable
fun OrbitCardViewer(
    itemCount: Int,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onSelectedIndexChange: (Int) -> Unit = {},
    carouselPeek: Dp = 36.dp,
    stackPeek: Dp = 56.dp,
    gap: Dp = OrbitTheme.spacing.cardGap,
    itemKey: (index: Int) -> Any = { it },
    itemContent: @Composable (index: Int) -> Unit,
) {
    if (itemCount <= 0) return

    val density = LocalDensity.current
    val content = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(OrbitCardViewerMode.Carousel) }
    val modeAnim = remember { Animatable(0f) }
    val pagerAnim = remember { Animatable(selectedIndex.toFloat()) }
    var dragging by remember { mutableStateOf(false) }
    var dragPager by remember { mutableFloatStateOf(selectedIndex.toFloat()) }

    LaunchedEffect(itemCount) {
        val clamped = orbitCardViewerClampIndex(pagerAnim.value.roundToInt(), itemCount)
        if (clamped.toFloat() != pagerAnim.value) {
            pagerAnim.snapTo(clamped.toFloat())
            onSelectedIndexChange(clamped)
        }
    }

    LaunchedEffect(mode) {
        modeAnim.animateTo(
            targetValue = if (mode == OrbitCardViewerMode.Stack) 1f else 0f,
            animationSpec = ModeSpring,
        )
    }

    val displayPager = if (dragging) dragPager else pagerAnim.value
    val activeIndex = orbitCardViewerClampIndex(displayPager.roundToInt(), itemCount)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append("Card ${activeIndex + 1} of $itemCount, ")
                    append(if (mode == OrbitCardViewerMode.Stack) "stacked" else "carousel")
                }
            },
    ) {
        val viewportW = constraints.maxWidth.toFloat()
        val viewportH = if (constraints.hasBoundedHeight) {
            constraints.maxHeight.toFloat()
        } else {
            with(density) { 480.dp.toPx() }
        }
        val peekCarousel = with(density) { carouselPeek.toPx() }
        val peekStack = with(density) { stackPeek.toPx() }
        val gapPx = with(density) { gap.toPx() }
        val cardW = (viewportW - peekCarousel * 2f).coerceAtLeast(viewportW * 0.7f)
        val peekBudget = (itemCount - 1) * peekStack
        val cardMaxH = (viewportH - peekBudget).coerceAtLeast(with(density) { 220.dp.toPx() })

        val stackH = orbitCardStackHeight(cardMaxH, itemCount, peekStack)
        val carouselH = cardMaxH
        val hostH = carouselH + (stackH - carouselH) * modeAnim.value

        fun snapPagerTo(index: Int) {
            val clamped = orbitCardViewerClampIndex(index, itemCount)
            scope.launch {
                pagerAnim.animateTo(clamped.toFloat(), PagerSpring)
            }
            onSelectedIndexChange(clamped)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { hostH.toDp() })
                .pointerInput(mode, itemCount, cardW, gapPx) {
                    if (mode != OrbitCardViewerMode.Carousel || itemCount <= 1) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragStart = {
                            dragging = true
                            dragPager = pagerAnim.value
                        },
                        onDragEnd = {
                            val target = orbitCardViewerClampIndex(dragPager.roundToInt(), itemCount)
                            dragging = false
                            snapPagerTo(target)
                        },
                        onDragCancel = {
                            dragging = false
                            snapPagerTo(dragPager.roundToInt())
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val span = (cardW + gapPx).coerceAtLeast(1f)
                            dragPager = (dragPager - dragAmount / span)
                                .coerceIn(0f, (itemCount - 1).toFloat())
                        },
                    )
                },
        ) {
            repeat(itemCount) { index ->
                key(itemKey(index)) {
                val carousel = orbitCardCarouselSlot(
                    index = index,
                    pagerOffset = displayPager,
                    count = itemCount,
                    viewportWidth = viewportW,
                    cardWidth = cardW,
                    gap = gapPx,
                )
                val stacked = orbitCardStackSlot(
                    index = index,
                    count = itemCount,
                    viewportWidth = viewportW,
                    cardWidth = cardW,
                    peek = peekStack,
                )
                val slot = lerpOrbitCardSlot(carousel, stacked, modeAnim.value)
                val scroll = rememberScrollState()
                val canScroll = mode == OrbitCardViewerMode.Carousel &&
                    index == activeIndex &&
                    modeAnim.value < 0.2f

                Box(
                    modifier = Modifier
                        .zIndex(slot.zIndex)
                        .offset { IntOffset(slot.x.roundToInt(), slot.y.roundToInt()) }
                        .width(with(density) { cardW.toDp() })
                        .heightIn(max = with(density) { cardMaxH.toDp() })
                        .clickable {
                            when (mode) {
                                OrbitCardViewerMode.Stack -> {
                                    scope.launch { pagerAnim.snapTo(index.toFloat()) }
                                    onSelectedIndexChange(index)
                                    mode = OrbitCardViewerMode.Carousel
                                }
                                OrbitCardViewerMode.Carousel -> {
                                    if (index == activeIndex) {
                                        mode = OrbitCardViewerMode.Stack
                                    } else {
                                        snapPagerTo(index)
                                    }
                                }
                            }
                        },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = with(density) { cardMaxH.toDp() })
                            .verticalScroll(scroll, enabled = canScroll),
                    ) {
                        itemContent(index)
                    }
                    val labelAlpha = modeAnim.value
                    if (labelAlpha > 0.05f) {
                        Text(
                            text = orbitCardIndexLabel(index),
                            style = OrbitTheme.extendedTypography.cardLabel,
                            color = content.textSecondary.copy(alpha = labelAlpha),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(spacing.sm),
                        )
                    }
                }
                }
            }
        }
    }
}
