package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A full-pill search field with a leading magnifier.
 *
 * A pill rather than the rounded rectangle the other fields use, and that difference is load-bearing:
 * search is the one input that appears at the top of a list of results it is filtering, and the pill
 * is what separates it at a glance from the form fields and the list rows below it. Every platform
 * has converged on the same shape for the same reason.
 *
 * The magnifier is decorative — [placeholder] and the accessible name already say what the field is,
 * and "magnifying glass, Search, Search" is three ways of saying one thing to a screen reader.
 *
 * A clear button appears only once there is something to clear. Reserving space for it while the
 * field is empty would leave a permanent gap the user cannot explain, and showing a disabled one is
 * worse: a dead control invites a tap that does nothing.
 *
 * @param onSearch fired by the keyboard's search key. Leave null for a field that filters as you
 *   type, which is the better default — an explicit submit on a filter is a round trip the user did
 *   not ask for.
 */
@Composable
fun OrbitSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    label: String = placeholder,
    enabled: Boolean = true,
    onSearch: (() -> Unit)? = null,
) {
    val control = OrbitTheme.controlColors
    val contentColors = OrbitTheme.contentColors
    val sizing = OrbitTheme.sizing

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }

    val textStyle = OrbitTheme.typography.bodyLarge
    val ink = if (enabled) contentColors.textPrimary else contentColors.textDisabled
    val hint = if (enabled) contentColors.textTertiary else contentColors.textDisabled

    OrbitFieldShell(
        focused = focused,
        shape = OrbitTheme.shapeTokens.chip,
        minHeight = sizing.minTouchTarget,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        horizontalPadding = OrbitTheme.spacing.lg,
        onRequestFocus = { focusRequester.requestFocus() },
    ) {
        Icon(
            imageVector = OrbitIcons.Search,
            contentDescription = null,
            tint = if (enabled) control.controlContent else contentColors.iconDisabled,
            modifier = Modifier.size(sizing.iconSm),
        )
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = textStyle.copy(color = ink),
                cursorBrush = SolidColor(control.controlContent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = if (onSearch != null) ImeAction.Search else ImeAction.Done,
                ),
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            )
            if (value.isEmpty()) {
                Text(text = placeholder, style = textStyle, color = hint)
            }
        }
        if (value.isNotEmpty()) {
            OrbitIconButton(
                contentDescription = "Clear $label",
                onClick = { onValueChange("") },
                icon = OrbitIcons.Cancel,
                style = OrbitIconButtonStyle.Neutral,
                size = OrbitIconButtonSize.Small,
            )
        }
    }
}
