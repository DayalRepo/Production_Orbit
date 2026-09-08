package com.orbitai.erp.ui.ceo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageBubble
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageBubbleRole
import com.orbitai.erp.core.designsystem.component.navigation.orbitBottomNavMetrics
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.composer.ComposerAttachment
import com.orbitai.erp.ui.component.composer.ManagedVoiceNoteRow
import com.orbitai.erp.ui.component.composer.MessageComposer
import com.orbitai.erp.ui.component.composer.OrbitComposerPlaceholder
import com.orbitai.erp.ui.component.composer.VoiceClip
import com.orbitai.erp.ui.component.composer.formatDuration
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private sealed interface AiThreadItem {
    data class TextMessage(
        val id: Long,
        val text: String,
        val role: OrbitMessageBubbleRole,
        val timestamp: String,
    ) : AiThreadItem

    data class VoiceMessage(
        val id: Long,
        val clip: VoiceClip,
    ) : AiThreadItem

    data class AttachmentMessage(
        val id: Long,
        val attachment: ComposerAttachment,
    ) : AiThreadItem
}

/**
 * CEO AI assistant: session thread + composer sized to the floating nav pill.
 */
@Composable
fun CeoAssistantScreen(modifier: Modifier = Modifier) {
    val layout = rememberCeoLayoutMetrics(includeNavClearance = true)
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    val thread = remember { mutableStateListOf<AiThreadItem>() }
    var playingId by remember { mutableStateOf<Long?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(thread.size) {
        if (thread.isNotEmpty()) {
            listState.animateScrollToItem(thread.lastIndex)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        val navMetrics = remember(maxWidth, sizing) {
            orbitBottomNavMetrics(maxWidth, sizing, sizing.minTouchTarget)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = layout.top),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = layout.maxContentWidth)
                    .fillMaxWidth()
                    .padding(horizontal = layout.horizontal)
                    .align(Alignment.CenterHorizontally),
            ) {
                CeoScreenHeader(title = "Orbit AI")
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = layout.maxContentWidth)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = layout.horizontal),
                contentPadding = PaddingValues(vertical = spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                items(thread, key = { item ->
                    when (item) {
                        is AiThreadItem.TextMessage -> "t-${item.id}"
                        is AiThreadItem.VoiceMessage -> "v-${item.id}"
                        is AiThreadItem.AttachmentMessage -> "a-${item.id}"
                    }
                }) { item ->
                    when (item) {
                        is AiThreadItem.TextMessage -> {
                            OrbitMessageBubble(
                                text = item.text,
                                role = item.role,
                                senderLabel = when (item.role) {
                                    OrbitMessageBubbleRole.User -> "You"
                                    OrbitMessageBubbleRole.Ai -> "Orbit AI"
                                    OrbitMessageBubbleRole.Other -> null
                                },
                                timestamp = item.timestamp,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        is AiThreadItem.VoiceMessage -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                ManagedVoiceNoteRow(
                                    amplitudes = item.clip.amplitudes,
                                    progress = if (playingId == item.id) 0.45f else 0f,
                                    duration = formatDuration(item.clip.seconds),
                                    playing = playingId == item.id,
                                    onPlayPause = {
                                        playingId = if (playingId == item.id) null else item.id
                                    },
                                    modifier = Modifier.fillMaxWidth(0.92f),
                                    label = "Voice prompt",
                                )
                            }
                        }

                        is AiThreadItem.AttachmentMessage -> {
                            val picked = item.attachment.picked
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                FileAttachmentRow(
                                    fileName = picked.name,
                                    fileSize = formatThreadBytes(picked.sizeBytes),
                                    modifier = Modifier.fillMaxWidth(0.92f),
                                )
                            }
                        }
                    }
                }
            }

            MessageComposer(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = navMetrics.barMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = navMetrics.edgeInset)
                    .heightIn(min = navMetrics.height)
                    .then(
                        if (imeVisible) {
                            Modifier
                                .windowInsetsPadding(WindowInsets.ime)
                                .padding(bottom = spacing.sm)
                        } else {
                            Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(bottom = layout.bottomContent)
                        },
                    ),
                label = "Prompt",
                placeholder = OrbitComposerPlaceholder.Ai,
                onSend = { text, clip, attachments ->
                    val stamp = formatThreadClock()
                    var seq = System.currentTimeMillis()
                    if (text.isNotBlank()) {
                        thread += AiThreadItem.TextMessage(
                            id = seq++,
                            text = text.trim(),
                            role = OrbitMessageBubbleRole.User,
                            timestamp = stamp,
                        )
                    }
                    attachments.forEach { attachment ->
                        thread += AiThreadItem.AttachmentMessage(
                            id = seq++,
                            attachment = attachment,
                        )
                    }
                    if (clip != null) {
                        thread += AiThreadItem.VoiceMessage(
                            id = clip.id,
                            clip = clip,
                        )
                    }
                    thread += AiThreadItem.TextMessage(
                        id = seq,
                        text = "Got it — I'll work with that for this session.",
                        role = OrbitMessageBubbleRole.Ai,
                        timestamp = stamp,
                    )
                },
            )
        }
    }
}

private fun formatThreadBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    else -> {
        val tenths = (bytes * 10f / (1024f * 1024f)).toInt()
        "${tenths / 10}.${tenths % 10} MB"
    }
}

@OptIn(ExperimentalTime::class)
private fun formatThreadClock(): String {
    val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour24 = local.hour
    val minute = local.minute.toString().padStart(2, '0')
    val amPm = if (hour24 < 12) "am" else "pm"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    return "$hour12:$minute $amPm"
}
