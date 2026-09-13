package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/** One photo in an [OrbitAttachmentPeekGroup]. */
data class OrbitAttachmentPeekItem(
    val name: String,
    val painter: Painter? = null,
    val id: String? = null,
)

/**
 * Overlapping attachment thumbnails that expand into a pickable peek row.
 *
 * Collapsed, the stack is one target. Expanded, each square is its own target so a photo can be
 * opened without guessing which face was on top.
 */
@Composable
fun OrbitAttachmentPeekGroup(
    items: List<OrbitAttachmentPeekItem>,
    modifier: Modifier = Modifier,
    max: Int = 4,
    thumbnailSize: Dp = OrbitTheme.sizing.attachmentMark,
    background: androidx.compose.ui.graphics.Color = OrbitTheme.colorScheme.background,
    expanded: Boolean = false,
    onToggle: (() -> Unit)? = null,
    onItemClick: ((index: Int, item: OrbitAttachmentPeekItem) -> Unit)? = null,
) {
    if (items.isEmpty()) return
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.cardCompact
    val ring = sizing.avatarStackRing
    val tile = thumbnailSize + ring * 2
    val step = -(tile * sizing.avatarStackOverlap)
    val shown = items.take(max.coerceAtLeast(1))
    val overflow = items.size - shown.size
    val spoken = buildString {
        append(shown.joinToString(", ") { it.name })
        if (overflow > 0) {
            if (shown.isNotEmpty()) append(" and ")
            append(overflow)
            append(if (overflow == 1) " other" else " others")
        }
    }
    val silent = remember { MutableInteractionSource() }

    if (expanded) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            items.forEachIndexed { index, item ->
                key(item.id ?: "${item.name}-$index") {
                    AttachmentPeekTile(
                        item = item,
                        size = thumbnailSize,
                        tile = tile,
                        ring = ring,
                        background = background,
                        onClick = onItemClick?.let { { it(index, item) } },
                    )
                }
            }
            if (onToggle != null) {
                Box(
                    modifier = Modifier
                        .size(tile)
                        .clip(shape)
                        .background(background, shape)
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = silent,
                            indication = null,
                            role = Role.Button,
                            onClick = onToggle,
                        )
                        .padding(ring)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Collapse photos"
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(thumbnailSize)
                            .clip(shape)
                            .background(control.interactiveContainer, shape),
                        contentAlignment = Alignment.Center,
                    ) {
                        OrbitGlyph(
                            icon = OrbitIcons.Cancel,
                            size = sizing.iconSm,
                            tint = content.iconInactive,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
        return
    }

    Row(
        modifier = modifier
            .then(
                if (onToggle != null) {
                    Modifier
                        .clip(RoundedCornerShape(percent = 20))
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = silent,
                            indication = null,
                            role = Role.Button,
                            onClick = onToggle,
                        )
                } else {
                    Modifier
                },
            )
            .semantics(mergeDescendants = true) {
                contentDescription = spoken
                if (onToggle != null) {
                    onClick(label = "Show all photos") { onToggle(); true }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(step),
    ) {
        shown.forEachIndexed { index, item ->
            key(item.id ?: "${item.name}-$index") {
                Box(
                    modifier = Modifier
                        .size(tile)
                        .background(background, shape)
                        .padding(ring)
                        .clearAndSetSemantics {},
                    contentAlignment = Alignment.Center,
                ) {
                    AttachmentPeekImage(item = item, size = thumbnailSize)
                }
            }
        }
        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .size(tile)
                    .background(background, shape)
                    .padding(ring),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(thumbnailSize)
                        .clip(shape)
                        .background(control.interactiveContainer, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+$overflow",
                        style = OrbitTheme.extendedTypography.cardLabel,
                        color = content.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentPeekTile(
    item: OrbitAttachmentPeekItem,
    size: Dp,
    tile: Dp,
    ring: Dp,
    background: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)?,
) {
    val shape = OrbitTheme.shapeTokens.cardCompact
    val silent = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(tile)
            .background(background, shape)
            .then(
                if (onClick != null) {
                    Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = silent,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                } else {
                    Modifier
                },
            )
            .padding(ring)
            .semantics(mergeDescendants = true) {
                contentDescription = item.name
            },
        contentAlignment = Alignment.Center,
    ) {
        AttachmentPeekImage(item = item, size = size)
    }
}

@Composable
private fun AttachmentPeekImage(
    item: OrbitAttachmentPeekItem,
    size: Dp,
) {
    val shape = OrbitTheme.shapeTokens.cardCompact
    val painter = item.painter
    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(shape),
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(OrbitTheme.controlColors.interactiveContainer, shape),
            contentAlignment = Alignment.Center,
        ) {
            OrbitGlyph(
                icon = OrbitIcons.AttachmentFile,
                size = OrbitTheme.sizing.iconSm,
                tint = OrbitTheme.contentColors.iconPrimary,
                contentDescription = null,
            )
        }
    }
}
