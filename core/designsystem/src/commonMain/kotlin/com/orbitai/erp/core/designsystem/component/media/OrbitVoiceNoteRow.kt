package com.orbitai.erp.core.designsystem.component.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A recorded voice note: play it, see it, and get rid of it.
 *
 * The audio counterpart to `OrbitAttachmentRow`, and it is a sibling rather than a variant of it
 * because the two disagree about what the row's middle is *for*. A file row's middle is a name and
 * a size — text, read once, to identify the thing. A voice note has no name worth showing; what
 * identifies it is its shape and its length, and the middle has to be a waveform that also acts as
 * the playback position. Bending the file row into that shape would have meant a leading slot that
 * is sometimes artwork and sometimes a button, and a name slot that is sometimes text and sometimes
 * a canvas, which is two components wearing one name.
 *
 * The chrome is deliberately identical — same fill, same rim, same shadow, same height — because
 * these do appear in the same list, and a voice note that sat a few dp taller or a shade lighter
 * than the PDFs beside it would look like a rendering bug rather than a different kind of thing.
 *
 * ### Play is the leading control
 *
 * On the left, where a file row puts its type artwork, because playing is what you do with a voice
 * note — it is the equivalent of the artwork's job of saying "this is a PDF", except you have to
 * press it. Putting play on the right with the destructive controls would sit the one action you
 * want most next to the one you want least.
 *
 * @param amplitudes the clip's envelope. See [OrbitAudioWave] on why this is passed in rather than
 *   decoded here.
 * @param progress playback position, 0..1.
 * @param duration already formatted — "0:14". Formatting a duration needs a locale, which does not
 *   belong in the design system.
 * @param listenOnly hides destructive controls. Download still appears when [onDownload] is set,
 *   matching a read-only attachment thumbnail.
 * @param onDownload save the clip locally. Typical on listen-only rows.
 */
@Composable
fun OrbitVoiceNoteRow(
    amplitudes: List<Float>,
    progress: Float,
    duration: String,
    playing: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Voice note",
    listenOnly: Boolean = false,
    onRemove: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onDownload: (() -> Unit)? = null,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.cardCompact

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.attachmentRowHeight)
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
            .padding(horizontal = spacing.sm, vertical = spacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrbitIconButton(
            // Names the clip and its state, so a screen reader user is told what pressing this does
            // now rather than what the control is called in general.
            contentDescription = if (playing) "Pause $label" else "Play $label",
            onClick = onPlayPause,
            icon = if (playing) OrbitIcons.Pause else OrbitIcons.Play,
            style = OrbitIconButtonStyle.Neutral,
            size = OrbitIconButtonSize.Small,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            OrbitAudioWave(
                amplitudes = amplitudes,
                progress = progress,
                playing = playing,
                // The whole row is one thing to a screen reader, and this is the sentence that
                // describes it. The waveform's shape is not conveyable and not worth trying to
                // convey; the length and position are.
                contentDescription = "$label, $duration",
            )
            Text(
                text = duration,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                maxLines = 1,
                // Already spoken as part of the waveform's description above.
                modifier = Modifier.clearAndSetSemantics {},
            )
        }

        if (onDownload != null) {
            OrbitIconButton(
                contentDescription = "Download $label",
                onClick = onDownload,
                icon = OrbitIcons.Download,
                style = OrbitIconButtonStyle.Neutral,
                size = OrbitIconButtonSize.Small,
            )
        }
        if (!listenOnly && onDelete != null) {
            OrbitIconButton(
                contentDescription = "Delete $label",
                onClick = onDelete,
                icon = OrbitIcons.Delete,
                style = OrbitIconButtonStyle.Destructive,
                size = OrbitIconButtonSize.Small,
            )
        }
        if (!listenOnly && onRemove != null) {
            OrbitIconButton(
                contentDescription = "Remove $label",
                onClick = onRemove,
                icon = OrbitIcons.Cancel,
                // Neutral, not destructive. Detaching a clip from a draft is reversible by
                // re-recording; the red is reserved for the one that destroys it.
                style = OrbitIconButtonStyle.Neutral,
                size = OrbitIconButtonSize.Small,
            )
        }
    }
}
