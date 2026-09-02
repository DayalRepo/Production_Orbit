package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.media.OrbitAudioWave
import androidx.compose.ui.draw.clip
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * What the composer is doing right now.
 *
 * A sealed state rather than a pair of booleans, because "recording" and "typing" are mutually
 * exclusive and a `isRecording: Boolean` alongside a `value: String` lets a caller express the
 * impossible combination — and then something has to decide what that renders as, silently.
 */
@androidx.compose.runtime.Immutable
sealed interface OrbitComposerMode {
    /** Typing. The text area and the mic are showing. */
    data object Text : OrbitComposerMode

    /**
     * Capturing audio. The text area is replaced by a live meter.
     *
     * @param elapsed already formatted — "0:07".
     * @param paused capture is held. The waveform freezes rather than clearing, so what has been
     *   recorded so far stays on screen; a meter that emptied on pause would read as discarded.
     */
    data class Recording(
        val elapsed: String,
        val amplitudes: List<Float>,
        val paused: Boolean = false,
    ) : OrbitComposerMode
}

/**
 * The message composer: attach, write, dictate, send.
 *
 * Built for the AI prompt box as much as for chat, which is what drives most of the decisions below
 * — a prompt is frequently a paragraph, often pasted, and is edited before it is sent, where a chat
 * message is usually a line typed once. A composer tuned only for chat is a single-line pill that
 * becomes unusable the moment someone writes three sentences into it.
 *
 * ### It grows, then it scrolls
 *
 * Height follows the text up to [maxLines] and then stops and scrolls. Both halves matter. Growing
 * is what lets you see a whole prompt while writing it — the alternative, a fixed one-line box,
 * hides everything but the tail of your own sentence. Stopping is what keeps the composer from
 * eating the conversation it belongs to; past about five lines the thing you are replying *to* has
 * scrolled off, and people write worse prompts when they cannot see the context.
 *
 * ### Pill, then rectangle
 *
 * Fully round while it is one line, squaring off to the card radius as it grows. This is not
 * decoration. A pill's corners consume horizontal space proportional to the height, so a tall pill
 * has enormous scooped corners that push the first and last lines of text inward and waste the
 * width where the text is longest. The rounded rectangle is the shape that works at height; the
 * pill is the shape that works at one line. Animating between them costs nothing and keeps the two
 * from reading as different components.
 *
 * ### Overflow affordance
 *
 * When the text has scrolled, the field fades at the edge the hidden text is behind — at the top
 * once you have scrolled down, at the bottom while there is more below, and at both when you are in
 * the middle. Without it a full composer looks exactly like a composer whose text happens to end at
 * the bottom line, and the failure is the quiet kind: the user believes they can see their whole
 * The field grows with content up to [maxLines], then scrolls inside a capped viewport with no edge fade.
 *
 * ### Send is disabled, not hidden
 *
 * An empty composer keeps its send button, greyed. Hiding it would reflow the row every time the
 * field goes from empty to non-empty, which moves the mic button under the user's thumb as they
 * start typing — and a control that relocates while you reach for it is worse than one that is
 * visible but inert.
 *
 * ### What this component does not do
 *
 * It records nothing, plays nothing, and opens no pickers. [mode], [amplitudes] and every callback
 * are the caller's. That is what makes it a library component rather than a screen: the microphone
 * permission, the recorder and the file picker are all platform concerns with their own lifecycles,
 * and a design-system component that reached for them could not be rendered in a gallery, a test,
 * or a preview.
 *
 * @param label the accessible name of the text area. Required — [placeholder] vanishes on the first
 *   keystroke and cannot serve as the name.
 * @param onMicClick begin capture. The caller flips [mode] to [OrbitComposerMode.Recording]; this
 *   component does not assume the request succeeded, because it can be refused by the permission
 *   dialog.
 * @param onCancelRecording throw the recording away. There are deliberately only two exits from
 *   recording — discard this, or send — rather than a third "stop and keep it in the box". A row
 *   holding pause, stop, discard and send is four controls where the two people reach for are the
 *   first and last, and the middle pair are then routinely mistaken for each other. Committing the
 *   clip is what the send button does.
 * @param attachMenu drawn anchored to the plus button. Pass `OrbitAttachMenu` here; it is a slot
 *   rather than a list of options so a screen can supply its own panel without this signature
 *   growing one parameter per menu item.
 * @param hasQueuedContent true when something other than typed text should keep Send active — e.g.
 *   attachments queued in [OrbitMessageComposer] above this field.
 */
@Composable
fun OrbitMessageField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onAttachClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    mode: OrbitComposerMode = OrbitComposerMode.Text,
    attachExpanded: Boolean = false,
    enabled: Boolean = true,
    maxLines: Int = 5,
    onCancelRecording: () -> Unit = {},
    onPauseRecording: () -> Unit = {},
    attachMenu: (@Composable () -> Unit)? = null,
    hasQueuedContent: Boolean = false,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors

    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()

    val base: TextStyle = OrbitTheme.typography.bodyLarge

    // The ceiling the field grows to, in the user's own units. Derived from the line height rather
    // than hardcoded in dp so that raising the system font scale gives a taller box holding the same
    // five lines, instead of the same box holding two and a half of them (WCAG 1.4.4).
    val maxHeight = with(LocalDensity.current) { (base.lineHeight * maxLines).toDp() }
    val ink = if (enabled) content.textPrimary else content.textPrimary.copy(OrbitAlpha.Disabled)
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    // Line count drives the shape, so it has to come from the layout rather than from counting '\n'
    // in the value — a single long line that soft-wraps is visually three lines and has exactly the
    // same corner problem as three typed ones.
    var lineCount by remember { mutableIntStateOf(1) }
    val recording = mode is OrbitComposerMode.Recording
    val multiline = lineCount > 1 && !recording

    val radius by animateDpAsState(
        // Half the min height is a true pill at one line. The card radius is where it lands once it
        // has grown; anything larger keeps scooping the corners into the text.
        targetValue = if (multiline) CardRadius else sizing.fieldHeightLg / 2,
        animationSpec = tween(ShapeMs),
        label = "orbit-composer-radius",
    )
    val shape = RoundedCornerShape(radius)

    val canSend = enabled && (value.isNotBlank() || recording || hasQueuedContent)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.fieldHeightLg)
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) 0f else OrbitGlass.SurfaceHighlightLight,
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
                sheen = if (OrbitTheme.isDark) 1f else OrbitGlass.Sheen,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            // Bottom, not centre. Once the field is several lines tall the buttons have to stay level
            // with the last line — where the caret is and where the next word will land. Centred, they
            // drift into the middle of the paragraph and stop looking attached to the writing.
            verticalAlignment = if (multiline) {
                Alignment.Bottom
            } else {
                Alignment.CenterVertically
            },
        ) {
            Box {
                OrbitIconButton(
                    contentDescription = if (attachExpanded) "Close attach menu" else "Attach",
                    onClick = onAttachClick,
                    icon = OrbitIcons.Add,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Medium,
                    // The plus reads as "on" while its menu is open, which is what tells you the panel
                    // above belongs to this button rather than to the send button beside it.
                    selected = attachExpanded,
                    state = if (enabled) OrbitButtonState.Active else OrbitButtonState.Disabled,
                    modifier = Modifier.padding(start = sizing.composerEdgeInset),
                )
                attachMenu?.invoke()
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    // Horizontal padding as well as vertical, which the first pass was missing. Without
                    // it the text began flush against the plus button's ripple bounds, so the first
                    // character of every message sat closer to the icon than the placeholder's own
                    // descender did to the rim — the composer read as three things crammed together
                    // rather than as one field with controls on either side.
                    .padding(horizontal = spacing.xs, vertical = spacing.xs)
                    // Match the control row height so the placeholder centres against the plus and
                    // mic, not against an undersized text measure that sat high in the pill.
                    .heightIn(min = if (multiline) 0.dp else sizing.fieldHeightLg - spacing.xs * 2),
                contentAlignment = Alignment.CenterStart,
            ) {
                when (mode) {
                    is OrbitComposerMode.Recording -> RecordingMeter(
                        mode = mode,
                        onPauseRecording = onPauseRecording,
                    )

                    OrbitComposerMode.Text -> {
                        CompositionLocalProvider(
                            LocalTextSelectionColors provides TextSelectionColors(
                                handleColor = control.actionContainer,
                                backgroundColor = control.actionContainer.copy(alpha = SelectionAlpha),
                            ),
                        ) {
                            // Cap the viewport, then scroll the field inside it. Putting heightIn and
                            // verticalScroll on the same node left maxValue at 0 on some hosts, so the
                            // overflow fade never appeared even when the prompt was past five lines.
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = maxHeight),
                            ) {
                                BasicTextField(
                                    value = value,
                                    onValueChange = onValueChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(scrollState)
                                        .orbitReleaseFocusWithKeyboard()
                                        .semantics { contentDescription = label },
                                    enabled = enabled,
                                    textStyle = base.copy(color = ink, fontWeight = FontWeight.Medium),
                                    // Default, not Send. A prompt is a paragraph and the return key has
                                    // to insert a newline; putting Send there means every attempt at a
                                    // second sentence fires the message off half-written.
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                                    interactionSource = interactionSource,
                                    cursorBrush = SolidColor(control.actionContainer),
                                    onTextLayout = { lineCount = it.lineCount },
                                )
                            }
                        }

                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                // Same weight as the text that replaces it. It was left at the base
                                // style's regular while the field itself renders Medium, so the hint
                                // sat visibly lighter *and* on a slightly different baseline, and the
                                // first keystroke made the line appear to jump and thicken. A
                                // placeholder is a preview of what you are about to type; any metric it
                                // does not share with the real text shows up as a shift.
                                style = base.copy(fontWeight = FontWeight.Medium),
                                color = hint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterStart)
                                    .clearAndSetSemantics {},
                            )
                        }
                    }
                }
            }

            Row(
                // Explicit air between the mic and send, and between that pair and the rim. These were
                // butted together at zero, which put two 48dp targets edge to edge: legal by the touch
                // guidance and still wrong, because adjacent targets with no gap read as one wide
                // control and are mis-hit at the seam. The gap is a platform token, since Android's
                // larger targets need less of it than iOS's to look equally spaced.
                horizontalArrangement = Arrangement.spacedBy(sizing.composerControlGap),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = sizing.composerEdgeInset),
            ) {
                if (recording) {
                    OrbitIconButton(
                        // Discard, not stop. A red cross means *throw this away* and it has to do
                        // that; the first pass had it keeping the clip, which is the worst kind of
                        // mismatch — the control that looks destructive being the safe one teaches
                        // people to distrust every red glyph in the product.
                        contentDescription = "Discard recording",
                        onClick = onCancelRecording,
                        icon = OrbitIcons.Delete,
                        style = OrbitIconButtonStyle.Destructive,
                        size = OrbitIconButtonSize.Medium,
                    )
                } else {
                    OrbitIconButton(
                        contentDescription = "Record a voice message",
                        onClick = onMicClick,
                        icon = OrbitIcons.MicRecord,
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Medium,
                        state = if (enabled) OrbitButtonState.Active else OrbitButtonState.Disabled,
                    )
                }

                OrbitIconButton(
                    contentDescription = "Send",
                    onClick = onSend,
                    // An upward arrow rather than a paper plane. The plane is mail's idiom and carries
                    // "dispatched, gone"; this box is as often a prompt as a message, and a prompt is
                    // submitted and answered rather than sent away. The arrow also holds up far better
                    // at 24dp, being two strokes instead of a folded silhouette that turns to mush.
                    icon = OrbitIcons.ArrowUp,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Medium,
                    // Present but inert on an empty composer. See the class doc on why this is not
                    // simply hidden.
                    state = if (canSend) OrbitButtonState.Active else OrbitButtonState.Disabled,
                )
            }
        }
    }
}

/**
 * The live meter that replaces the text area while recording.
 *
 * Pause sits on the left of the waveform, where the play button sits on a finished clip, so the one
 * control that means "the transport" does not move between recording and playback.
 *
 * The elapsed time is text and not part of the waveform's description, because it changes every
 * second: folded into the canvas's `contentDescription` it would make a screen reader re-announce
 * the whole meter once a second, which is unusable. As its own node it can be read on demand.
 */
@Composable
private fun RecordingMeter(
    mode: OrbitComposerMode.Recording,
    onPauseRecording: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrbitIconButton(
            contentDescription = if (mode.paused) "Resume recording" else "Pause recording",
            onClick = onPauseRecording,
            icon = if (mode.paused) OrbitIcons.Play else OrbitIcons.Pause,
            style = OrbitIconButtonStyle.Neutral,
            size = OrbitIconButtonSize.Small,
        )
        key(mode.amplitudes.size, mode.amplitudes.lastOrNull()) {
            OrbitAudioWave(
                amplitudes = mode.amplitudes,
                // Everything captured so far is "played" — there is no playhead while recording.
                progress = 1f,
                live = true,
                paused = mode.paused,
                contentDescription = if (mode.paused) "Recording paused" else "Recording",
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = mode.elapsed,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = content.textSecondary,
            maxLines = 1,
        )
    }
}

private const val SelectionAlpha = 0.28f
private const val ShapeMs = 140
private val CardRadius = 16.dp
