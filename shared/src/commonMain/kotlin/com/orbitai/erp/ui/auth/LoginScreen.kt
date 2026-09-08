package com.orbitai.erp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.input.OrbitCountries
import com.orbitai.erp.core.designsystem.component.input.OrbitCountry
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitPhoneField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Mobile login — no sign-up. Brand mark, title, phone field, continue.
 */
@Composable
fun LoginScreen(
    nationalNumber: String,
    onNationalNumberChange: (String) -> Unit,
    country: OrbitCountry,
    onCountryChange: (OrbitCountry) -> Unit,
    onContinue: () -> Unit,
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    countries: List<OrbitCountry> = OrbitCountries.All,
    fieldState: OrbitFieldState = OrbitFieldState.Default,
    errorMessage: String? = null,
    loading: Boolean = false,
) {
    val spacing = OrbitTheme.spacing
    val authShape = OrbitTheme.shapeTokens.card
    val digitsOk = nationalNumber.filter { it.isDigit() }.length >= 10
    val continueEnabled = digitsOk && !loading

    Surface(
        modifier = modifier.fillMaxSize(),
        color = OrbitTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AuthThemeToggle(
                isDark = isDark,
                onThemeChange = onThemeChange,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding()
                    .padding(horizontal = spacing.screenHorizontal),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = AuthBrandDefaults.FormMaxWidth)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.xl),
                ) {
                    AuthBrandHeader(title = "Log in")

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        OrbitPhoneField(
                            nationalNumber = nationalNumber,
                            onNationalNumberChange = onNationalNumberChange,
                            country = country,
                            onCountryChange = onCountryChange,
                            countries = countries,
                            label = "Mobile number",
                            placeholder = "Mobile number",
                            size = OrbitFieldSize.Small,
                            state = fieldState,
                            enabled = !loading,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardActions = KeyboardActions(
                                onDone = { if (continueEnabled) onContinue() },
                            ),
                        )
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                style = OrbitTheme.typography.bodySmall,
                                color = OrbitTheme.semanticColors.danger.content,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    OrbitButton(
                        label = "Continue",
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        variant = OrbitButtonVariant.Primary,
                        size = OrbitButtonSize.Medium,
                        state = if (continueEnabled) {
                            OrbitButtonState.Active
                        } else {
                            OrbitButtonState.Disabled
                        },
                        shape = authShape,
                        // Light auth: no white specular wash. Dark keeps default glass highlight.
                        glassHighlight = isDark,
                    )
                }
            }
        }
    }
}
