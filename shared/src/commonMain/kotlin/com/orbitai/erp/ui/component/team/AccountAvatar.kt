package com.orbitai.erp.ui.component.team

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatar
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarSize
import com.orbitai.erp.core.designsystem.component.overlay.OrbitAccountPopover

/**
 * Your own face in the app bar, and the account panel behind it.
 *
 * @param role capital short form for the badge under the name (CEO, PM, SE, CONTR, …).
 * @param onSignOut ending a session touches storage, navigation and whatever the platform does with
 *   credentials, none of which belongs in a shared component. It is raised to the caller.
 * @param themeDark current mode, for the panel's theme row. Omit along with [onThemeChange] to leave
 *   the row out.
 * @param onThemeChange fired when the theme row is toggled.
 */
@Composable
fun AccountAvatar(
    name: String,
    role: String,
    phone: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
    size: OrbitAvatarSize = OrbitAvatarSize.Sm,
    themeDark: Boolean? = null,
    onThemeChange: ((Boolean) -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OrbitAvatar(
            contentDescription = "Account, $name",
            painter = avatar,
            initials = name.initials(),
            size = size,
            onClick = { expanded = !expanded },
        )

        OrbitAccountPopover(
            expanded = expanded,
            onDismiss = { expanded = false },
            name = name,
            role = role,
            phone = phone,
            onSignOut = onSignOut,
            themeDark = themeDark,
            onThemeChange = onThemeChange,
        )
    }
}

/**
 * Two letters from a name, for the avatar with no photograph.
 *
 * First and last rather than the first two words, because Indian names on this product routinely
 * carry a middle name or an initial and "Priya K Sharma" should read PS, not PK.
 */
private fun String.initials(): String = trim()
    .split(" ")
    .filter { it.isNotBlank() }
    .let { parts ->
        when (parts.size) {
            0 -> ""
            1 -> parts[0].take(2)
            else -> "${parts.first().first()}${parts.last().first()}"
        }
    }
    .uppercase()
