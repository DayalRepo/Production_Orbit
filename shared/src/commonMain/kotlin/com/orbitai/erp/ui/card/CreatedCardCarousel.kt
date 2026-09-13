package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import kotlin.math.abs
import kotlinx.coroutines.launch

internal data class CreatedCardSlide(
    val record: WorkItemRecord,
    val role: UserRole,
)

internal fun createdCardSlides(items: List<WorkItemRecord>): List<CreatedCardSlide> =
    items.flatMap { record ->
        createdCardRoles(record.kind).map { role -> CreatedCardSlide(record, role) }
    }

internal fun createdCardPageLabel(index: Int, count: Int): String {
    if (count <= 0) return "0/0"
    val page = index.coerceIn(0, count - 1) + 1
    return "$page/$count"
}

/**
 * After a finger drag, pick the page to settle on. A short swipe still pages so the carousel
 * does not feel like it needs a full-width flick.
 */
internal fun createdCardSwipeTargetPage(
    current: Int,
    lastIndex: Int,
    dragPx: Float,
    pageWidthPx: Float,
    thresholdFraction: Float = 0.12f,
): Int {
    if (lastIndex < 0) return 0
    val page = current.coerceIn(0, lastIndex)
    if (pageWidthPx <= 0f) return page
    val travelled = -dragPx
    val passed = abs(travelled) >= pageWidthPx * thresholdFraction
    val delta = when {
        !passed -> 0
        travelled > 0f -> 1
        else -> -1
    }
    return (page + delta).coerceIn(0, lastIndex)
}

@Composable
internal fun CreatedCardCarousel(
    slides: List<CreatedCardSlide>,
    onUpdate: (CreatedCardSlide) -> Unit,
    onEdit: (CreatedCardSlide) -> Unit,
    onDelete: (CreatedCardSlide) -> Unit,
    onView: (CreatedCardSlide) -> Unit,
    onStart: (CreatedCardSlide) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (slides.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    val spacing = OrbitTheme.spacing
    LaunchedEffect(slides.size, pagerState.currentPage) {
        if (pagerState.currentPage > slides.lastIndex) {
            pagerState.scrollToPage(slides.lastIndex)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(slides.size) {
                    if (slides.size <= 1) return@pointerInput
                    val slop = viewConfiguration.touchSlop
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var dragPx = 0f
                        var totalX = 0f
                        var totalY = 0f
                        var axis: Boolean? = null
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            val delta = change.positionChangeIgnoreConsumed()
                            totalX += delta.x
                            totalY += delta.y
                            if (axis == null && (abs(totalX) > slop || abs(totalY) > slop)) {
                                axis = abs(totalX) >= abs(totalY)
                            }
                            if (axis == true) {
                                change.consume()
                                dragPx += delta.x
                                pagerState.dispatchRawDelta(-delta.x)
                            }
                            if (!change.pressed) break
                        }
                        if (axis == true) {
                            val pageWidth = pagerState.layoutInfo.pageSize
                                .toFloat()
                                .coerceAtLeast(1f)
                            val target = createdCardSwipeTargetPage(
                                current = pagerState.currentPage,
                                lastIndex = slides.lastIndex,
                                dragPx = dragPx,
                                pageWidthPx = pageWidth,
                            )
                            scope.launch { pagerState.animateScrollToPage(target) }
                        }
                    }
                },
            contentPadding = PaddingValues(horizontal = spacing.sm),
            pageSpacing = spacing.xs,
            beyondViewportPageCount = 1,
            userScrollEnabled = false,
        ) { page ->
            val slide = slides[page]
            key(slide.record.id, slide.role) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    WorkItemCard(
                        record = slide.record,
                        viewerRole = slide.role,
                        onUpdate = { onUpdate(slide) },
                        onEdit = { onEdit(slide) },
                        onDelete = { onDelete(slide) },
                        onView = { onView(slide) },
                        onStart = { onStart(slide) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        CreatedCardPagerChrome(
            pagerState = pagerState,
            count = slides.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OrbitTheme.spacing.md),
        )
    }
}

@Composable
private fun CreatedCardPagerChrome(
    pagerState: PagerState,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val current = pagerState.currentPage
    val label = createdCardPageLabel(current, count)
    Column(
        modifier = modifier.semantics {
            contentDescription = "Card $label"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            createdCardDotWindow(current, count).forEach { index ->
                CreatedCardDot(selected = index == current)
            }
        }
        Text(
            text = label,
            style = OrbitTheme.extendedTypography.cardLabel,
            color = OrbitTheme.contentColors.textSecondary,
        )
    }
}

internal fun createdCardDotWindow(current: Int, count: Int, maxDots: Int = 7): IntRange {
    if (count <= 0) return IntRange.EMPTY
    if (count <= maxDots) return 0 until count
    val half = maxDots / 2
    val start = (current - half).coerceIn(0, count - maxDots)
    return start until (start + maxDots)
}

@Composable
private fun CreatedCardDot(selected: Boolean) {
    val content = OrbitTheme.contentColors
    Box(
        modifier = Modifier
            .size(if (selected) 8.dp else 6.dp)
            .clip(CircleShape)
            .background(if (selected) content.iconPrimary else content.iconInactive),
    )
}
