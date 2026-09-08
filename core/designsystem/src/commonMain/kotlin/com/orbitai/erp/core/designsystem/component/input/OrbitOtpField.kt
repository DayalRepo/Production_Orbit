package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Six rounded digit cells for one-time password entry.
 *
 * Filled digits render as `*` (mask), never the typed numeral. Cells use the same solid fill +
 * opaque border as [OrbitFieldShell] (no glass / shadow).
 */
@Composable
fun OrbitOtpField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    length: Int = OrbitOtpDefaults.Length,
    cellSize: Dp = OrbitOtpDefaults.CellSize,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    requestFocus: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maskChar: Char = '*',
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }

    val digits = remember(value, length) {
        value.filter { it.isDigit() }.take(length)
    }

    LaunchedEffect(requestFocus) {
        if (requestFocus && enabled) {
            focusRequester.requestFocus()
        }
    }

    val shape = RoundedCornerShape(percent = 40)
    val dark = OrbitTheme.isDark
    val errorRim = if (dark) OrbitPalette.Red70 else OrbitPalette.Red40
    val successRim = if (dark) OrbitPalette.Green70 else OrbitPalette.Green40
    val rim = when (state) {
        OrbitFieldState.Error -> errorRim
        OrbitFieldState.Success -> successRim
        OrbitFieldState.Default -> control.controlBorder
    }
    val rimWidth = if (state != OrbitFieldState.Default) sizing.borderFocus else sizing.hairline
    val fill = if (dark) Color.Black else Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$label, ${digits.length} of $length digits entered" },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(cellSize),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(length) { index ->
                val filled = index < digits.length
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .clip(shape)
                        .background(fill, shape)
                        .border(width = rimWidth, color = rim, shape = shape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (filled) {
                        Text(
                            text = maskChar.toString(),
                            style = OrbitTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = content.textPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        BasicTextField(
            value = digits,
            onValueChange = { raw ->
                onValueChange(raw.filter { it.isDigit() }.take(length))
            },
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(
                color = content.textPrimary.copy(alpha = 0f),
                textAlign = TextAlign.Center,
            ),
            cursorBrush = SolidColor(content.textPrimary.copy(alpha = 0f)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.02f)
                .focusRequester(focusRequester)
                .orbitReleaseFocusWithKeyboard(),
        )
    }
}

object OrbitOtpDefaults {
    const val Length = 6
    val CellSize: Dp = 44.dp
}
