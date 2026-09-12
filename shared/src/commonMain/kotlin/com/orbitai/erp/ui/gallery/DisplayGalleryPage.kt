package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarGroup
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarGroupMember
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarSize
import com.orbitai.erp.core.designsystem.component.display.OrbitCountBadge
import com.orbitai.erp.core.designsystem.component.display.OrbitPresenceDot
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.attachment.ManagedFileUpload
import com.orbitai.erp.ui.component.team.TeamAvatarGroup
import com.orbitai.erp.ui.component.attachment.ManagedAttachmentRow
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.avatar_01
import com.orbitai.erp.resources.avatar_02
import com.orbitai.erp.resources.avatar_03
import com.orbitai.erp.resources.avatar_04
import com.orbitai.erp.resources.avatar_05
import org.jetbrains.compose.resources.painterResource

/**
 * The display group: avatar stacks, counts, attachments and dividers.
 *
 * One thing here is worth checking on a device rather than in a screenshot. The avatar stack's
 * separating rings have to match the surface behind them, so the stack is shown on both the page and
 * inside a card — if the ring token is ever wired to the wrong colour, the card version is where it
 * shows.
 */
@Composable
internal fun DisplayGalleryPage() {
    val spacing = OrbitTheme.spacing
    val painters = rememberGalleryPainters()
    val crew = rememberGalleryCrew(painters)

    val team = remember(painters) {
        listOf(
            OrbitAvatarGroupMember("Priya Sharma", painters.avatar02, "u-pm"),
            OrbitAvatarGroupMember("Ravi Menon", painters.avatar03, "u-eng"),
            OrbitAvatarGroupMember("Anita Desai", painters.avatar04, "u-qa"),
            OrbitAvatarGroupMember("Sanjay Iyer", painters.avatar05, "u-con"),
            OrbitAvatarGroupMember("Meera Nair", painters.avatar01, "u-wh"),
            OrbitAvatarGroupMember("Vikram Rao", id = "u-proc"),
            OrbitAvatarGroupMember("Kavita Joshi", id = "u-eng-2"),
            OrbitAvatarGroupMember("Arjun Pillai", id = "u-c4"),
            OrbitAvatarGroupMember("Neha Gupta", id = "u-c5"),
        )
    }

    // The interactive version, and the one worth spending time on with a device in hand: tap the
    // stack to expand it into a wrapped grid, tap a face for its details, tap the same face again
    // to close. The last two members have no photograph, so the monogram fallback is exercised at
    // the same time.
    GallerySection("Avatar group") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            TeamAvatarGroup(members = crew)

            // On a card, because the separating rings have to be the card's colour rather than the
            // page's, and the expanded grid is where a wrong ring is most obvious.
            OrbitCard(padding = spacing.md) {
                TeamAvatarGroup(
                    members = crew,
                    background = OrbitTheme.controlColors.cardContainer,
                )
            }
        }
    }

    GallerySection("Avatar group overflow") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            OrbitAvatarGroup(members = team, size = OrbitAvatarSize.Sm)
            // Only three, so no overflow chip at all.
            OrbitAvatarGroup(members = team.take(3), size = OrbitAvatarSize.Sm)
            // The last four have no photos, so the stack falls back to monograms.
            OrbitAvatarGroup(members = team.drop(5), size = OrbitAvatarSize.Sm)

            // On a card, where the rings have to be the card's colour rather than the page's.
            OrbitCard(padding = spacing.md) {
                OrbitAvatarGroup(
                    members = team,
                    size = OrbitAvatarSize.Xs,
                    background = OrbitTheme.controlColors.cardContainer,
                )
            }
        }
    }

    GallerySection("Count badge") {
        GalleryFlow {
            OrbitCountBadge(count = 1, label = "unread messages")
            OrbitCountBadge(count = 12, label = "open issues", tone = OrbitBadgeTone.Amber)
            OrbitCountBadge(count = 99, label = "pending approvals", tone = OrbitBadgeTone.Blue)
            OrbitCountBadge(count = 340, label = "audit entries", tone = OrbitBadgeTone.Slate)
            OrbitCountBadge(count = 7, label = "completed tasks", tone = OrbitBadgeTone.Green)
            OrbitPresenceDot(label = "New activity")
        }
    }

    GallerySection("File upload") {
        ManagedFileUpload(modifier = Modifier.fillMaxWidth())
    }

    // Both attachment sections are live rather than stubbed: the rows below hold real state, so
    // removing one removes it, and renaming one renames it. Wired dead — `onRemove = {}` — the
    // dialogs could be opened but never answered, and the one thing worth reviewing here is what
    // happens *after* Yes.
    GallerySection("Attachments") {
        ManagedAttachmentList(
            initial = listOf(
                DemoFile("1.pdf", "2 MB"),
                DemoFile("2.pdf", "10 MB"),
                DemoFile("boq-revision-3.xlsx", "840 KB"),
                DemoFile("site-notes.docx", "126 KB"),
                // No artwork for this format, so it falls back to the pin.
                DemoFile("structural-drawing-level-4-rev-c.dwg", "18 MB"),
                // An image wins over its extension: a photo should show the photo.
                DemoFile("east-elevation.jpg", "3.4 MB", preview = true),
            ),
            removable = true,
        )
    }

    // The managing view of the same component: a document library rather than a composer, where a
    // file already exists and can be renamed or destroyed. Same rows, different verbs — which is
    // the point of showing both, since the visual difference is two glyphs and the difference in
    // consequence is total. It is also where the two confirmations can be compared side by side:
    // remove is an ordinary question, delete is a red one that will not dismiss on a stray tap.
    GallerySection("Attachment library") {
        ManagedAttachmentList(
            initial = listOf(
                DemoFile("1.pdf", "2 MB"),
                DemoFile("rfi-042-response.pdf", "640 KB"),
                DemoFile("boq-revision-3.xlsx", "840 KB"),
                DemoFile("handover-checklist.docx", "212 KB"),
                DemoFile("east-elevation.jpg", "3.4 MB", preview = true),
            ),
            manageable = true,
        )
    }

    // Read-only: an attachment on a saved record is evidence, not a draft. Download is the only
    // action offered.
    GallerySection("Attachment read only") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FileAttachmentRow(
                fileName = "signed-challan.pdf",
                fileSize = "1.1 MB",
                onDownload = {},
            )
        }
    }

    GallerySection("Dividers") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            OrbitDivider()
            OrbitDivider(inset = OrbitTheme.sizing.avatarSm + spacing.md)
        }
    }
}

/** One row's worth of demo data. */
private data class DemoFile(val name: String, val size: String, val preview: Boolean = false)

/**
 * A list of attachment rows that actually responds to its own dialogs.
 *
 * The list is `remember`ed rather than hoisted to the screen, because each section wants its own
 * independent copy — deleting `1.pdf` from the library should not empty it out of the composer
 * section above, which is a different document in a different place that happens to share a name.
 */
@Composable
private fun ManagedAttachmentList(
    initial: List<DemoFile>,
    removable: Boolean = false,
    manageable: Boolean = false,
) {
    val spacing = OrbitTheme.spacing
    val files = remember { mutableStateListOf(*initial.toTypedArray()) }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        files.forEachIndexed { index, file ->
            ManagedAttachmentRow(
                fileName = file.name,
                fileSize = file.size,
                preview = if (file.preview) painterResource(Res.drawable.avatar_04) else null,
                onRemoved = if (removable) {
                    { files.remove(file) }
                } else {
                    null
                },
                onDeleted = if (manageable) {
                    { files.remove(file) }
                } else {
                    null
                },
                onRenamed = if (manageable) {
                    { newName -> files[index] = file.copy(name = newName) }
                } else {
                    null
                },
            )
        }
        if (files.isEmpty()) {
            Text(
                text = "Everything here has been removed. Reopen the gallery to restore it.",
                style = OrbitTheme.typography.bodyMedium,
                color = OrbitTheme.contentColors.textSecondary,
            )
        }
    }
}
