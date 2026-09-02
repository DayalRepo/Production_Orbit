package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading
import com.orbitai.erp.core.designsystem.component.feedback.OrbitLoadingIcon
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressDefaults
import com.orbitai.erp.core.designsystem.foundation.orbitCircularPressIndication
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors
/**
 * Whether an upload row is still in flight or already on the server.
 */
@Immutable
enum class OrbitUploadState {
    Uploading,
    Completed,
}

/**
 * One file in an [OrbitFileUpload] list.
 *
 * @param progress 0..1 while [state] is [OrbitUploadState.Uploading]. Ignored once completed.
 * @param progressLabel already formatted, e.g. "60 KB of 120 KB".
 */
@Immutable
data class OrbitUploadItem(
    val id: String,
    val fileName: String,
    val progressLabel: String,
    val state: OrbitUploadState,
    val progress: Float = 1f,
)

/**
 * A dashed drop zone plus a vertical list of upload rows beneath it.
 *
 * The design system owns the chrome — dashed rim, white/black fill, progress bar, status copy —
 * and the caller owns the file list and what happens when Browse is tapped. Artwork for PDF, Docs,
 * Sheets and image previews is passed per item through [leadingFor], because that mapping lives in
 * `:shared` next to the drawable resources.
 */
@Composable
fun OrbitFileUpload(
    items: List<OrbitUploadItem>,
    onBrowseClick: () -> Unit,
    leadingFor: @Composable (OrbitUploadItem) -> OrbitAttachmentLeading,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dropZoneTitle: String = "Choose a file or drag & drop it here.",
    dropZoneHint: String = "JPEG, PNG, PDF, and MP4 formats, up to 50 MB.",
    browseLabel: String = "Browse File",
    onCancelUpload: ((OrbitUploadItem) -> Unit)? = null,
    onRemove: ((OrbitUploadItem) -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        OrbitFileUploadDropZone(
            title = dropZoneTitle,
            hint = dropZoneHint,
            browseLabel = browseLabel,
            enabled = enabled,
            onBrowseClick = onBrowseClick,
        )

        items.forEach { item ->
            OrbitFileUploadItemRow(
                item = item,
                leading = leadingFor(item),
                onCancel = if (item.state == OrbitUploadState.Uploading) {
                    onCancelUpload?.let { { it(item) } }
                } else {
                    null
                },
                onRemove = if (item.state == OrbitUploadState.Completed) {
                    onRemove?.let { { it(item) } }
                } else {
                    null
                },
            )
        }
    }
}

/**
 * The dashed input area only. Exposed so a screen can place other content between the zone and the
 * list without reimplementing the border drawing.
 */
@Composable
fun OrbitFileUploadDropZone(
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String = "Choose a file or drag & drop it here.",
    hint: String = "JPEG, PNG, PDF, and MP4 formats, up to 50 MB.",
    browseLabel: String = "Browse File",
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.cardCompact
    val dashColor = content.textTertiary
    val cornerRadius = 8.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(control.cardContainer, shape)
            .drawBehind {
                val stroke = Stroke(
                    width = DashStroke.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(DashLength.toPx(), DashGap.toPx()),
                        0f,
                    ),
                )
                drawRoundRect(
                    color = dashColor,
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    style = stroke,
                )
            }
            .padding(horizontal = spacing.lg, vertical = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitGlyph(
            icon = OrbitIcons.Upload,
            size = sizing.iconMd,
            tint = content.iconInactive,
            contentDescription = null,
        )
        Text(
            text = title,
            style = OrbitTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = content.textPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = hint,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(spacing.xs))
        UploadBrowseButton(
            label = browseLabel,
            enabled = enabled,
            onClick = onBrowseClick,
        )
    }
}

@Composable
private fun UploadBrowseButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.inputChip
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .heightIn(min = BrowseButtonHeight)
            .clip(shape)
            .border(OrbitTheme.sizing.hairline, control.controlBorder, shape)
            .background(control.cardContainer, shape)
            .then(
                if (enabled) {
                    Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .indication(interactionSource, orbitPressIndication())
                } else {
                    Modifier
                },
            )
            .padding(horizontal = OrbitTheme.spacing.md, vertical = OrbitTheme.spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = OrbitTheme.typography.labelLarge,
            color = if (enabled) content.textPrimary else content.textDisabled,
        )
    }
}

@Composable
private fun OrbitFileUploadItemRow(
    item: OrbitUploadItem,
    leading: OrbitAttachmentLeading,
    onCancel: (() -> Unit)?,
    onRemove: (() -> Unit)?,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.cardCompact
    val progressColors = OrbitProgressDefaults.colors
    val mark = UploadPreviewMark

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
            )
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(mark),
                contentAlignment = Alignment.Center,
            ) {
                UploadLeading(leading = leading, mark = mark)
            }

            Spacer(Modifier.size(spacing.md))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${item.fileName}, ${item.progressLabel}"
                    },
                verticalArrangement = Arrangement.spacedBy(spacing.xxs),
            ) {
                Text(
                    text = item.fileName,
                    style = OrbitTheme.typography.bodyLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                )
                UploadStatusLine(item = item)
            }

            when {
                onCancel != null -> UploadRowAction(
                    icon = OrbitIcons.Cancel,
                    description = "Cancel upload of ${item.fileName}",
                    onClick = onCancel,
                )
                onRemove != null -> UploadRowAction(
                    icon = OrbitIcons.Delete,
                    description = "Remove ${item.fileName}",
                    tint = OrbitBadgeTone.Red.colors.label,
                    onClick = onRemove,
                )
            }
        }

        if (item.state == OrbitUploadState.Uploading) {
            UploadProgressBar(
                progress = item.progress.coerceIn(0f, 1f),
                colors = progressColors,
                height = UploadProgressHeight,
            )
        }
    }
}

@Composable
private fun UploadLeading(
    leading: OrbitAttachmentLeading,
    mark: Dp,
) {
    val content = OrbitTheme.contentColors
    val sizing = OrbitTheme.sizing

    when (leading) {
        is OrbitAttachmentLeading.Preview -> androidx.compose.foundation.Image(
            painter = leading.painter,
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .size(mark)
                .clip(OrbitTheme.shapeTokens.tooltip),
        )
        is OrbitAttachmentLeading.Artwork -> androidx.compose.foundation.Image(
            painter = leading.painter,
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            modifier = Modifier.size(mark),
        )
        OrbitAttachmentLeading.Glyph -> OrbitGlyph(
            icon = OrbitIcons.AttachmentFile,
            size = mark,
            tint = content.iconPrimary,
            contentDescription = null,
            minimumStroke = sizing.iconStrokeHairline,
        )
    }
}

@Composable
private fun UploadStatusLine(item: OrbitUploadItem) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val green = OrbitBadgeTone.Green.colors.label

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.progressLabel,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textSecondary,
            maxLines = 1,
        )
        when (item.state) {
            OrbitUploadState.Uploading -> {
                Text(
                    text = "·",
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textSecondary,
                )
                OrbitLoadingIcon(
                    size = OrbitTheme.sizing.iconXs,
                    tint = content.textSecondary,
                )
                Text(
                    text = "Uploading…",
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textSecondary,
                )
            }
            OrbitUploadState.Completed -> {
                Text(
                    text = "·",
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textSecondary,
                )
                Box(
                    modifier = Modifier
                        .size(OrbitTheme.sizing.iconXs)
                        .clip(CircleShape)
                        .background(green),
                    contentAlignment = Alignment.Center,
                ) {
                    OrbitGlyph(
                        icon = OrbitIcons.Tick,
                        size = (OrbitTheme.sizing.iconXs.value * 0.65f).dp,
                        tint = OrbitTheme.controlColors.onActionContainer,
                        contentDescription = null,
                    )
                }
                Text(
                    text = "Completed",
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun UploadRowAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    tint: Color? = null,
) {
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }
    val iconTint = tint ?: content.iconInactive

    Box(
        modifier = Modifier
            .size(sizing.minTouchTarget)
            .clip(CircleShape)
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .indication(interactionSource, orbitCircularPressIndication())
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        OrbitGlyph(
            icon = icon,
            size = sizing.iconSm,
            tint = iconTint,
            contentDescription = null,
        )
    }
}

@Composable
private fun UploadProgressBar(
    progress: Float,
    colors: com.orbitai.erp.core.designsystem.component.progress.OrbitProgressColors,
    height: Dp,
) {
    val shape = RoundedCornerShape(percent = 50)
    val fraction = progress.coerceIn(0f, 1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = fraction,
                    range = 0f..1f,
                )
            },
    ) {
        val radius = CornerRadius(size.height / 2f)
        drawRoundRect(color = colors.track, cornerRadius = radius)
        if (fraction > 0f) {
            drawRoundRect(
                color = colors.filled,
                size = Size(size.width * fraction, size.height),
                cornerRadius = radius,
            )
        }
    }
}

private val DashLength = 7.dp
private val DashGap = 5.dp
private val DashStroke = 1.5.dp
private val UploadProgressHeight = 4.dp
private val BrowseButtonHeight = 32.dp
private val UploadPreviewMark = 40.dp
