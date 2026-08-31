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
import com.orbitai.erp.core.designsystem.component.overlay.OrbitInfoField

/**
 * Your own face in the app bar, and the account panel behind it.
 *
 * ### Why the tenancy line is a parameter rather than a branch
 *
 * A CEO's panel names the organisation and a site user's names the project, and that is the whole
 * difference between the two variants the design calls for. Passing it in as a label and a value
 * keeps the difference where it belongs — with whoever knows the user's scope — instead of putting a
 * role check inside a component whose job is to draw four lines of text. The third variant, whenever
 * it arrives, is a call site rather than an edit here.
 *
 * @param tenancyLabel what the fourth line is: "Organisation" for a CEO, "Project" for a site user.
 *   Spoken, not drawn, like every other label in these bubbles.
 * @param onSignOut ending a session touches storage, navigation and whatever the platform does with
 *   credentials, none of which belongs in a shared component. It is raised to the caller.
 */
@Composable
fun AccountAvatar(
    name: String,
    role: String,
    phone: String,
    tenancy: String,
    tenancyLabel: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
    size: OrbitAvatarSize = OrbitAvatarSize.Sm,
) {
    var expanded by remember { mutableStateOf(false) }

    // The popover is composed inside the avatar's own box so the platform anchors it to that face.
    // Measuring the avatar's window position and placing a panel at it would re-derive something
    // the layout already knows, and re-derive it wrongly the first time the bar scrolls.
    Box(modifier = modifier) {
        OrbitAvatar(
            contentDescription = "Account, $name",
            painter = avatar,
            initials = name.initials(),
            size = size,
            // Tapping the same face twice closes the panel rather than reopening it: a tap on a
            // control that is already showing its panel means "I am done here" far more often than
            // it means "show me that again".
            onClick = { expanded = !expanded },
        )

        OrbitAccountPopover(
            expanded = expanded,
            onDismiss = { expanded = false },
            fields = listOf(
                OrbitInfoField("Name", name),
                OrbitInfoField("Role", role),
                // The one value on the panel that exists to end up somewhere else.
                OrbitInfoField("Phone", phone, copyable = true),
                OrbitInfoField(tenancyLabel, tenancy),
            ),
            onSignOut = onSignOut,
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
