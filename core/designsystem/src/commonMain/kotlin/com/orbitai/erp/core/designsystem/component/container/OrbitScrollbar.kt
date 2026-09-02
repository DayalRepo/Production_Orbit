package com.orbitai.erp.core.designsystem.component.container

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlinx.coroutines.launch

/**
 * A vertical scrollbar with end arrows, a recessed track and a pill thumb — per the component spec.
 *
 * The arrows step by roughly one viewport. The thumb reports position only; flicking the list remains
 * the primary way to move a long distance on touch devices.
 */
@Composable
fun OrbitVerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    thickness: Dp = OrbitScrollbarDefaults.Thickness,
    minThumbLength: Dp = OrbitScrollbarDefaults.MinThumbLength,
) {
    val max = scrollState.maxValue
    if (max <= 0) return

    val scope = rememberCoroutineScope()
    val fraction = scrollState.value.toFloat() / max.toFloat()
    val step = (scrollState.viewportSize * 0.85f).toInt().coerceAtLeast(1)

    OrbitScrollbarChrome(
        vertical = true,
        thumbFraction = viewportFraction(scrollState.viewportSize, max),
        travelFraction = fraction,
        thickness = thickness,
        minThumbLength = minThumbLength,
        modifier = modifier,
        onDecrease = {
            scope.launch {
                scrollState.scrollTo((scrollState.value - step).coerceAtLeast(0))
            }
        },
        onIncrease = {
            scope.launch {
                scrollState.scrollTo((scrollState.value + step).coerceAtMost(max))
            }
        },
        decreaseDescription = "Scroll up",
        increaseDescription = "Scroll down",
    )
}

/**
 * The same chrome for a lazy row or column, with arrows that step one item at a time.
 */
@Composable
fun OrbitLazyScrollbar(
    listState: LazyListState,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
    thickness: Dp = OrbitScrollbarDefaults.Thickness,
    minThumbLength: Dp = OrbitScrollbarDefaults.MinThumbLength,
) {
    val info = listState.layoutInfo
    val visible = info.visibleItemsInfo
    if (visible.isEmpty()) return

    val averageItem = visible.sumOf { it.size } / visible.size
    if (averageItem <= 0) return

    val viewport = info.viewportEndOffset - info.viewportStartOffset
    val contentLength = averageItem.toLong() * info.totalItemsCount
    if (contentLength <= viewport) return

    val scrolled = listState.firstVisibleItemIndex.toLong() * averageItem +
        listState.firstVisibleItemScrollOffset
    val travel = (contentLength - viewport).coerceAtLeast(1L)

    val scope = rememberCoroutineScope()
    val lastIndex = (info.totalItemsCount - 1).coerceAtLeast(0)

    OrbitScrollbarChrome(
        vertical = !horizontal,
        thumbFraction = viewport.toFloat() / contentLength.toFloat(),
        travelFraction = (scrolled.toFloat() / travel.toFloat()).coerceIn(0f, 1f),
        thickness = thickness,
        minThumbLength = minThumbLength,
        modifier = modifier,
        onDecrease = {
            scope.launch {
                val target = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                listState.animateScrollToItem(target)
            }
        },
        onIncrease = {
            scope.launch {
                val target = (listState.firstVisibleItemIndex + 1).coerceAtMost(lastIndex)
                listState.animateScrollToItem(target)
            }
        },
        decreaseDescription = if (horizontal) "Scroll left" else "Scroll up",
        increaseDescription = if (horizontal) "Scroll right" else "Scroll down",
    )
}

@Composable
private fun OrbitScrollbarChrome(
    vertical: Boolean,
    thumbFraction: Float,
    travelFraction: Float,
    thickness: Dp,
    minThumbLength: Dp,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    decreaseDescription: String,
    increaseDescription: String,
    modifier: Modifier = Modifier,
) {
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val trackShape = RoundedCornerShape(OrbitScrollbarDefaults.TrackCorner)
    val thumbShape = RoundedCornerShape(percent = 50)

    val position by animateFloatAsState(
        targetValue = travelFraction,
        animationSpec = tween(ThumbFollowMs),
        label = "orbit-scrollbar-thumb",
    )

    val decreaseArrow = @Composable {
        ScrollbarArrow(
            icon = OrbitIcons.ChevronDown,
            rotation = if (vertical) 180f else 90f,
            description = decreaseDescription,
            onClick = onDecrease,
            size = thickness,
        )
    }
    val increaseArrow = @Composable {
        ScrollbarArrow(
            icon = OrbitIcons.ChevronDown,
            rotation = if (vertical) 0f else 270f,
            description = increaseDescription,
            onClick = onIncrease,
            size = thickness,
        )
    }

    val track = @Composable { scopeModifier: Modifier ->
        BoxWithConstraints(
            modifier = scopeModifier
                .clip(trackShape)
                .background(control.dividerElevated)
                .padding(OrbitScrollbarDefaults.TrackInset)
                .clearAndSetSemantics {},
            contentAlignment = Alignment.TopStart,
        ) {
            val trackLength = if (vertical) maxHeight else maxWidth
            val thumbLength = maxOf(trackLength * thumbFraction.coerceIn(0f, 1f), minThumbLength)
            val slack = (trackLength - thumbLength).coerceAtLeast(0.dp)

            Box(
                modifier = Modifier
                    .then(
                        if (vertical) {
                            Modifier
                                .offset(y = slack * position)
                                .width(thickness - OrbitScrollbarDefaults.TrackInset * 2)
                                .height(thumbLength)
                        } else {
                            Modifier
                                .offset(x = slack * position)
                                .height(thickness - OrbitScrollbarDefaults.TrackInset * 2)
                                .width(thumbLength)
                        },
                    )
                    .background(content.iconPrimary.copy(alpha = ThumbAlpha), thumbShape),
            )
        }
    }

    if (vertical) {
        Column(
            modifier = modifier.width(thickness),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(OrbitScrollbarDefaults.SegmentGap),
        ) {
            decreaseArrow()
            track(
                Modifier
                    .weight(1f)
                    .width(thickness)
                    .fillMaxHeight(),
            )
            increaseArrow()
        }
    } else {
        Row(
            modifier = modifier.height(thickness),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OrbitScrollbarDefaults.SegmentGap),
        ) {
            decreaseArrow()
            track(
                Modifier
                    .weight(1f)
                    .height(thickness)
                    .fillMaxWidth(),
            )
            increaseArrow()
        }
    }
}

@Composable
private fun ScrollbarArrow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    rotation: Float,
    description: String,
    onClick: () -> Unit,
    size: Dp,
) {
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(OrbitScrollbarDefaults.TrackCorner)

    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(control.controlBorder.copy(alpha = ButtonFillAlpha))
            .indication(interactionSource, orbitPressIndication())
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        OrbitGlyph(
            icon = icon,
            size = OrbitScrollbarDefaults.ArrowIcon,
            tint = content.iconPrimary,
            contentDescription = null,
            minimumStroke = OrbitTheme.sizing.iconStrokeHairline,
            modifier = Modifier.rotate(rotation),
        )
    }
}

/** Whether a plain scroll container has anything hidden, for callers deciding to show a bar at all. */
internal fun viewportFraction(viewportSize: Int, maxValue: Int): Float {
    val content = viewportSize.toLong() + maxValue
    if (content <= 0) return 1f
    return viewportSize.toFloat() / content.toFloat()
}

object OrbitScrollbarDefaults {

    /** Full bar width including arrow buttons. */
    val Thickness: Dp = 10.dp

    val ArrowIcon: Dp = 8.dp

    val TrackCorner: Dp = 2.dp

    val TrackInset: Dp = 1.dp

    val SegmentGap: Dp = 2.dp

    val MinThumbLength: Dp = 22.dp
}

private const val ThumbFollowMs = 120

private const val ThumbAlpha = 0.78f

private const val ButtonFillAlpha = 0.55f
