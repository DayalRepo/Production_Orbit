package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitVerticalDivider
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Mobile number field with an in-shell country-code picker.
 *
 * ```
 *  --------------------------------------------------------------
 * | 🇮🇳 +91 ▼ |           Mobile number                          |
 *  --------------------------------------------------------------
 * ```
 *
 * National digits are shown grouped as `XXXXX XXXXX` via [OrbitPhoneGroupingTransformation].
 */
@Composable
fun OrbitPhoneField(
    nationalNumber: String,
    onNationalNumberChange: (String) -> Unit,
    country: OrbitCountry,
    onCountryChange: (OrbitCountry) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    countries: List<OrbitCountry> = OrbitCountries.All,
    placeholder: String? = "Mobile number",
    size: OrbitFieldSize = OrbitFieldSize.Small,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }
    var expanded by rememberDropdownExpanded()
    val density = LocalDensity.current
    var anchorWidth by remember { mutableStateOf(0.dp) }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)
    val chevronSize = size.pick(sizing.iconSm, sizing.iconSm, sizing.iconMd)

    val base: TextStyle = size.pick(
        OrbitTheme.typography.bodyMedium,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.extendedTypography.fieldLarge,
    )
    val ink = if (enabled) content.textPrimary else content.textPrimary.copy(OrbitAlpha.Disabled)
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(OrbitDropdownOpenMs),
        label = "orbit-phone-country-chevron",
    )

    fun close() {
        expanded = false
    }

    Box(modifier = modifier) {
        OrbitFieldShell(
            interactionSource = interactionSource,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = minHeight,
            horizontalPadding = padding,
            enabled = enabled,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidth = with(density) { it.width.toDp() } }
                .semantics {
                    contentDescription = "$label, ${country.dialCode} $nationalNumber"
                },
            contentGap = spacing.sm,
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = enabled,
                        role = Role.DropdownList,
                        onClick = { if (expanded) close() else expanded = true },
                    )
                    .semantics {
                        contentDescription = "Country code, ${country.name} ${country.dialCode}"
                    }
                    .padding(end = spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                OrbitCountryFlag(country = country)
                Text(
                    text = country.dialCode,
                    style = base,
                    fontWeight = FontWeight.Medium,
                    color = ink,
                    maxLines = 1,
                    modifier = Modifier.clearAndSetSemantics {},
                )
                OrbitGlyph(
                    icon = OrbitIcons.ChevronDown,
                    size = chevronSize,
                    tint = if (enabled) {
                        content.iconInactive
                    } else {
                        content.iconInactive.copy(OrbitAlpha.Disabled)
                    },
                    contentDescription = null,
                    minimumStroke = sizing.iconStrokeLight,
                    modifier = Modifier.rotate(rotation),
                )
            }

            Box(
                modifier = Modifier
                    .height(sizing.iconMd)
                    .width(sizing.dividerThickness),
            ) {
                OrbitVerticalDivider(color = control.controlBorder)
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (nationalNumber.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = base,
                        color = hint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clearAndSetSemantics {},
                    )
                }
                CompositionLocalProvider(
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = control.controlContent,
                        backgroundColor = control.controlContent.copy(alpha = 0.24f),
                    ),
                ) {
                    BasicTextField(
                        value = nationalNumber,
                        onValueChange = { raw ->
                            onNationalNumberChange(
                                raw.filter { it.isDigit() }.take(OrbitPhoneDefaults.MaxNationalDigits),
                            )
                        },
                        enabled = enabled,
                        singleLine = true,
                        textStyle = base.copy(color = ink, fontWeight = FontWeight.Medium),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        keyboardActions = keyboardActions,
                        visualTransformation = OrbitPhoneGroupingTransformation,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .orbitReleaseFocusWithKeyboard(),
                    )
                }
            }
        }

        OrbitDropdownMenu(
            expanded = expanded,
            onDismiss = { close() },
            width = anchorWidth.coerceAtLeast(280.dp),
        ) {
            countries.forEach { option ->
                OrbitCountryRow(
                    country = option,
                    selected = option.iso2 == country.iso2,
                    onClick = {
                        onCountryChange(option)
                        close()
                    },
                )
            }
        }
    }
}

@Composable
private fun OrbitCountryRow(
    country: OrbitCountry,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val success = OrbitTheme.semanticColors.success.content

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitCountryFlag(country = country, size = 18.dp)
        Text(
            text = country.name,
            style = OrbitTheme.typography.bodyLarge,
            color = content.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = country.dialCode,
            style = OrbitTheme.typography.bodyMedium,
            color = content.textSecondary,
            maxLines = 1,
        )
        if (selected) {
            OrbitGlyph(
                icon = OrbitIcons.Tick,
                size = sizing.iconSm,
                tint = success,
                contentDescription = "Selected",
                minimumStroke = sizing.iconStrokeLight,
            )
        } else {
            Box(modifier = Modifier.size(sizing.iconSm))
        }
    }
}

/** National phone digit limits used by [OrbitPhoneField]. */
object OrbitPhoneDefaults {
    /** Auth gate: exactly ten national digits before Continue unlocks. */
    const val MaxNationalDigits = 10
}
