package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One place to sign off design-system coverage before feature modules land.
 *
 * Status is library-facing only — not product QA. Covered = gallery sample exists;
 * Partial = used via another component; Gap = exists in `:core:designsystem` without a review row.
 */
@Composable
internal fun SignOffGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    GallerySection("Gallery sign-off · component coverage") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Confirm each row on device (light + dark) before wiring role screens.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            SignOffEntries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = entry.status.label,
                        style = OrbitTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = when (entry.status) {
                            SignOffStatus.Covered -> content.textPrimary
                            SignOffStatus.Partial -> content.textSecondary
                            SignOffStatus.Gap -> content.textTertiary
                        },
                        modifier = Modifier.fillMaxWidth(0.22f),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.component,
                            style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = content.textPrimary,
                        )
                        Text(
                            text = entry.note,
                            style = OrbitTheme.typography.bodySmall,
                            color = content.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

private enum class SignOffStatus(val label: String) {
    Covered("Covered"),
    Partial("Partial"),
    Gap("Gap"),
}

private data class SignOffEntry(
    val status: SignOffStatus,
    val component: String,
    val note: String,
)

private val SignOffEntries = listOf(
    SignOffEntry(SignOffStatus.Covered, "OrbitAvatar / AccountAvatar", "Avatar + account gallery"),
    SignOffEntry(SignOffStatus.Covered, "TeamAvatarGroup / OrbitAvatarGroup", "Display gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitRoleBadge", "Badge gallery + account/info cards"),
    SignOffEntry(SignOffStatus.Covered, "OrbitAssignField", "Assign gallery · API-shaped members"),
    SignOffEntry(SignOffStatus.Covered, "OrbitBadge / StatusBadge", "Badge gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitButton / ActionButton", "Button gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitChip", "Button gallery · filters"),
    SignOffEntry(SignOffStatus.Covered, "OrbitIconButton", "Button gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitDonut / Segmented / Stage / Step", "Progress gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitChecklist", "Checklist gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitDateTime*", "DateTime gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitMessageBubble / Thread", "Message bubble gallery"),
    SignOffEntry(SignOffStatus.Covered, "OrbitMessageField / Voice", "Composer gallery"),
    SignOffEntry(SignOffStatus.Covered, "Role bottom navs / OrbitTabBar", "Navigation gallery"),
    SignOffEntry(SignOffStatus.Covered, "Inputs (text, search, qty, dropdown…)", "Input gallery"),
    SignOffEntry(SignOffStatus.Covered, "Skeleton / Confirm / Rename", "State gallery"),
    SignOffEntry(SignOffStatus.Partial, "OrbitInfoPopover / OrbitBubblePopover", "Via avatar + assign only"),
    SignOffEntry(SignOffStatus.Partial, "OrbitCopyButton", "Inside info mobile row"),
    SignOffEntry(SignOffStatus.Partial, "OrbitFileUpload / Attachments", "Display gallery wrappers"),
    SignOffEntry(SignOffStatus.Partial, "OrbitDialog base", "Via confirm/rename"),
    SignOffEntry(SignOffStatus.Gap, "OrbitScrollbar", "Used inside menus; no dedicated section"),
    SignOffEntry(SignOffStatus.Gap, "OrbitLoadingIcon", "Used inside BusyButton"),
    SignOffEntry(SignOffStatus.Gap, "OrbitPillProgress", "Not in progress gallery yet"),
    SignOffEntry(SignOffStatus.Gap, "OrbitMultiSelectField (raw)", "Covered via assign patterns"),
    SignOffEntry(SignOffStatus.Gap, "OrbitCreateDialog", "No gallery sample yet"),
)
