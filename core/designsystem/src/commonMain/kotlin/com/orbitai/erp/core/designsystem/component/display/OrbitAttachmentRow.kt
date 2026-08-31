package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * What goes in an attachment row's leading slot.
 *
 * Three cases rather than one nullable painter, because the three are drawn differently and a single
 * `Painter?` would hide that. A photo is cropped to a square and given a corner; file-type artwork is
 * drawn whole, uncropped and untinted, because it is already a picture of a document and cropping it
 * would cut the corner fold off; the glyph fallback is tinted with the theme's ink like every other
 * icon. Collapsing these into one parameter means the caller has to remember which rules apply, and
 * the failure mode — a stretched PDF badge — is quiet.
 */
@Immutable
sealed interface OrbitAttachmentLeading {
    /** A photo or drawing. Cropped square and rounded. */
    data class Preview(val painter: Painter) : OrbitAttachmentLeading

    /** File-type artwork: the PDF, Docs and Sheets marks. Drawn whole, at its own colours. */
    data class Artwork(val painter: Painter) : OrbitAttachmentLeading

    /** Everything with no artwork of its own — DWG, ZIP, anything unrecognised. */
    data object Glyph : OrbitAttachmentLeading
}

/**
 * A full-width attachment row: what it is, what it is called, how big, and a way to remove it.
 *
 * ### Why a row and not a tile
 *
 * The square tile this replaces could show the file's type and nothing else — there is no room at
 * 56dp for a name, so the name lived only in the spoken description and a sighted user staring at
 * four grey squares could not tell which was the drawing and which was the invoice. Going wide costs
 * one item per screen and buys the two facts people actually need to identify a file. It also gives
 * the remove control somewhere to live: on a square tile the only place for it is a corner, where it
 * would be about 20dp, under half the minimum touch target.
 *
 * ### No container around the leading mark
 *
 * The artwork and the glyph sit directly on the row's own surface, with no disc or tile behind them.
 * A container behind a mark that is *already* a bounded shape — and the PDF and Sheets artwork both
 * are — reads as a box inside a box, and the row already has one border doing that job.
 *
 * ### White and black, with the one border
 *
 * The fill is `cardContainer` (near-white on light, near-black on dark) rather than the grey tonal
 * fill the tile used, so nothing on the row competes with the file-type artwork for colour. Edge
 * definition comes from the shared 1dp `controlBorder` plus a contact shadow instead.
 *
 * @param fileName shown in full where it fits and ellipsised in the middle when it does not — the
 *   end of a filename carries the extension, so truncating the tail is the one thing that must not
 *   happen.
 * @param fileSize already formatted: "2 MB", "940 KB". Formatting needs a locale and a unit
 *   convention, neither of which belongs in the design system.
 * @param onRemove renders the trailing close control when set. Leave `null` on a read-only view of
 *   an existing record, where the row is evidence rather than a draft.
 */
@Composable
fun OrbitAttachmentRow(
    fileName: String,
    fileSize: String,
    leading: OrbitAttachmentLeading,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
    onRename: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.cardCompact

    val interactionSource = remember { MutableInteractionSource() }
    val mark: Dp = sizing.attachmentMark

    Row(
        modifier = modifier
            .fillMaxWidth()
            // `heightIn`, never `height`: the filename is text, and at 200% font scale the row has
            // to grow rather than clip the thing it exists to show (WCAG 1.4.4).
            // Which minimum depends on whether the row has controls: they carry their own touch
            // target and push a row past its stated height, so a row without them needs a taller
            // floor to end up the same height as its neighbours. See the token.
            .heightIn(
                min = if (onRemove == null && onRename == null && onDelete == null) {
                    sizing.attachmentRowHeightReadOnly
                } else {
                    sizing.attachmentRowHeight
                },
            )
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
            .then(
                if (onClick == null) {
                    Modifier
                } else {
                    Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .indication(interactionSource, orbitPressIndication())
                },
            )
            // Vertical padding is minimal because the remove control's 48dp touch target is already
            // the tallest thing in the row and sets the height on its own. Padding on top of that
            // just made the row 64dp for no visual gain — the target is invisible, so the space it
            // claimed read as an over-tall box rather than as breathing room.
            .padding(horizontal = spacing.md, vertical = spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (leading) {
            is OrbitAttachmentLeading.Preview -> Image(
                painter = leading.painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(mark)
                    .clip(OrbitTheme.shapeTokens.tooltip),
            )

            is OrbitAttachmentLeading.Artwork -> Image(
                painter = leading.painter,
                contentDescription = null,
                // Fit, not crop. These marks have a folded corner and their own margins; cropping
                // to a square eats exactly the corner that identifies them as documents.
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(mark),
            )

            // Drawn at the full mark size, the same box the PDF and Sheets artwork fills. Sized
            // down it sat in a pool of empty space while the artwork rows filled theirs, and the
            // filenames stopped lining up with each other — an outline glyph and a solid image are
            // already different enough without also being different sizes.
            //
            // The compensation is stroke rather than scale: the thinnest weight the system has, so
            // that growing the glyph does not also make the one unrecognised file the heaviest mark
            // in the list, which would be backwards for the row carrying the least information.
            OrbitAttachmentLeading.Glyph -> OrbitGlyph(
                icon = OrbitIcons.AttachmentFile,
                size = mark,
                tint = content.iconPrimary,
                contentDescription = null,
                minimumStroke = sizing.iconStrokeHairline,
            )
        }

        Spacer(Modifier.size(spacing.md))

        Column(
            modifier = Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) {
                    contentDescription = "$fileName, $fileSize"
                },
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = fileName,
                // Size carries the hierarchy here; weight does not.
                //
                // The filename is the row's content and everything else on it is chrome, so it has
                // to be the thing you see first — but the two ways to buy that are not equal. A step
                // up in size separates it from the file size beneath while leaving both as ordinary
                // running text. Adding weight on top of that was a step too far: at Medium every row
                // read as emphasised, and a list where every line is emphasised has the same flat
                // texture as a list where none is, only heavier. Size alone is the quieter of the
                // two levers and it was already enough.
                style = OrbitTheme.typography.bodyLarge,
                color = content.textPrimary,
                maxLines = 1,
                // Middle, not end. "structural-drawing-level-4-rev-c.dwg" truncated at the tail
                // becomes "structural-drawing-le…", which has thrown away the extension — the one
                // part of a filename that says what the thing is.
                overflow = TextOverflow.MiddleEllipsis,
            )
            Text(
                text = fileSize,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 1,
            )
        }

        // Rename before delete, and delete last. The destructive control goes at the far edge so
        // that a thumb reaching in from the side of the screen meets the recoverable action first —
        // the same reasoning that puts Delete at the bottom of a menu rather than the top.
        if (onRename != null) {
            OrbitAttachmentAction(
                icon = OrbitIcons.TextEdit,
                // Neutral ink, the same shade as the file size beneath the name. Renaming is a
                // housekeeping action and colouring it would make it compete with the file itself.
                tint = content.iconInactive,
                description = "Rename $fileName",
                onClick = onRename,
            )
        }
        if (onDelete != null) {
            OrbitAttachmentAction(
                icon = OrbitIcons.Delete,
                // Red, and this is the one place on the row that gets colour. Unlike Remove, which
                // just detaches a file from a draft, Delete destroys it — and the cost of the two
                // being confusable is not symmetric, so the irreversible one is marked.
                tint = OrbitBadgeTone.Red.colors.label,
                description = "Delete $fileName",
                onClick = onDelete,
            )
        }
        if (onRemove != null) {
            OrbitAttachmentAction(
                icon = OrbitIcons.Cancel,
                // Secondary ink, not red. Removing an attachment is destructive but
                // reversible-by-reattaching, and a red cross on every row in a composer makes a
                // list of files look like a list of errors.
                tint = content.iconInactive,
                // Names the file it removes. "Close" or "Remove" alone is ambiguous the moment
                // there is more than one attachment, which is the normal case.
                description = "Remove $fileName",
                onClick = onRemove,
            )
        }
    }
}

/**
 * One trailing control on an attachment row.
 *
 * A bare glyph rather than an `OrbitIconButton`, because the row already carries a rim and a fill
 * and a second ring inside it would be the third nested rounded rectangle in 48dp. It keeps the
 * full touch target regardless: the glyph is drawn at icon size inside a box sized to
 * `sizing.minTouchTarget`, which is the same split the icon button uses and the reason a small
 * visible control is still comfortably tappable.
 *
 * That sizing is also the ceiling on how many of these a row can carry. Two is comfortable; three
 * takes 144dp of a 360dp screen and starts eating the filename, which is the row's actual content.
 * A row needing more than two actions wants a menu, not more glyphs.
 *
 * @param description the accessible name, and it must name the file — "Delete" alone is ambiguous
 *   the moment there is more than one attachment, which is the normal case.
 */
@Composable
private fun OrbitAttachmentAction(
    icon: ImageVector,
    tint: Color,
    description: String,
    onClick: () -> Unit,
) {
    val sizing = OrbitTheme.sizing
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(sizing.minTouchTarget)
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .indication(interactionSource, orbitPressIndication())
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        OrbitGlyph(
            icon = icon,
            size = sizing.iconSm,
            tint = tint,
            contentDescription = null,
            minimumStroke = sizing.iconStrokeHairline,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}
