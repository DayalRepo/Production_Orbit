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
 * Account samples use [GalleryAccountSamples] — the same ids, short roles and phones
 * [com.orbitai.erp.core.data.session.FakeSessionRepository] and the eventual OTP session will carry.
 */
@Composable
internal fun AvatarGalleryPage(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val painters = rememberGalleryPainters()

    val faces = listOf(
        Res.drawable.avatar_02,
        Res.drawable.avatar_03,
        Res.drawable.avatar_04,
        Res.drawable.avatar_05,
        Res.drawable.avatar_01,
    )

    GallerySection("Avatars") {
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

    GallerySection("Avatar initials") {
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

    GallerySection("Account menu") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AccountAvatar(
                name = GalleryAccountSamples.CeoName,
                role = GalleryAccountSamples.CeoRole,
                phone = GalleryAccountSamples.CeoPhone,
                onSignOut = {},
                avatar = painters.avatar01,
                size = OrbitAvatarSize.Md,
                themeDark = isDark,
                onThemeChange = { onToggleTheme() },
            )
            AccountAvatar(
                name = GalleryAccountSamples.SiteName,
                role = GalleryAccountSamples.SiteRole,
                phone = GalleryAccountSamples.SitePhone,
                onSignOut = {},
                avatar = painters.avatar03,
                size = OrbitAvatarSize.Md,
                themeDark = isDark,
                onThemeChange = { onToggleTheme() },
            )
        }
    }
}
