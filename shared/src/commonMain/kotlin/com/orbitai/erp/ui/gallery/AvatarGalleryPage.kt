package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatar
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarSize
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.team.AccountAvatar
import com.orbitai.erp.resources.Res
import com.orbitai.erp.resources.avatar_01
import com.orbitai.erp.resources.avatar_02
import com.orbitai.erp.resources.avatar_03
import com.orbitai.erp.resources.avatar_04
import com.orbitai.erp.resources.avatar_05
import org.jetbrains.compose.resources.painterResource

/**
 * The avatar, at every tier and in both of its states.
 *
 * Rows are bottom-aligned so the tier progression reads as a step up rather than as five circles
 * scattered around a centre line — the sizes come from a spreadsheet with different figures per
 * platform, and a misordered tier is far easier to spot against a shared baseline.
 */
@Composable
internal fun AvatarGalleryPage() {
    val spacing = OrbitTheme.spacing

    val faces = listOf(
        Res.drawable.avatar_02,
        Res.drawable.avatar_03,
        Res.drawable.avatar_04,
        Res.drawable.avatar_05,
        Res.drawable.avatar_01,
    )

    GallerySection("Avatar tiers · Xs to Xl") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.Bottom,
        ) {
            OrbitAvatarSize.entries.forEachIndexed { index, size ->
                OrbitAvatar(
                    contentDescription = null,
                    painter = painterResource(faces[index]),
                    size = size,
                )
            }
        }
    }

    GallerySection("Avatar fallback · initials") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.Bottom,
        ) {
            val monograms = listOf("AK", "PS", "RM", "DV", "SN")
            OrbitAvatarSize.entries.forEachIndexed { index, size ->
                OrbitAvatar(
                    contentDescription = null,
                    initials = monograms[index],
                    size = size,
                )
            }
        }
    }

    GallerySection("Account menu · organisation and project") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // The two variants side by side, which is the only way to check the thing that matters
            // about them: they are the same panel, and only the fourth line differs.
            AccountAvatar(
                name = "Anita Kulkarni",
                role = "Chief Executive",
                phone = "+91 98200 41122",
                tenancy = "Meridian Infra Pvt Ltd",
                tenancyLabel = "Organisation",
                onSignOut = {},
                avatar = painterResource(Res.drawable.avatar_01),
                size = OrbitAvatarSize.Md,
            )
            AccountAvatar(
                name = "Ravi Menon",
                role = "Site Engineer",
                phone = "+91 90040 77310",
                tenancy = "Tower B, Andheri East",
                tenancyLabel = "Project",
                onSignOut = {},
                avatar = painterResource(Res.drawable.avatar_03),
                size = OrbitAvatarSize.Md,
            )
        }
    }
}
