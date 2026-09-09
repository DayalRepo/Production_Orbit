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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonIconPosition
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitOtpDefaults
import com.orbitai.erp.core.designsystem.component.input.OrbitOtpField
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlinx.coroutines.delay

/**
 * OTP verify — six digit cells, verify primary, resend with 60s cooldown.
 *
 * While verifying, the Verify button stays in place and turns [OrbitButtonState.Disabled]
 * (no busy / pill swap). Back arrow + Change number return to the login step.
 */
@Composable
fun OtpVerifyScreen(
    otp: String,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onChangeNumber: () -> Unit,
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    maskedPhone: String? = null,
    fieldState: OrbitFieldState = OrbitFieldState.Default,
    errorMessage: String? = null,
    loading: Boolean = false,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val authShape = OrbitTheme.shapeTokens.card
    val canVerify = otp.length == OrbitOtpDefaults.Length && !loading

    var resendSecondsLeft by remember { mutableIntStateOf(0) }
    LaunchedEffect(resendSecondsLeft) {
        if (resendSecondsLeft > 0) {
            delay(1_000)
            resendSecondsLeft -= 1
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = OrbitTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(start = spacing.sm, top = spacing.sm),
                contentAlignment = Alignment.TopStart,
            ) {
                OrbitIconButton(
                    contentDescription = "Change number",
                    onClick = onChangeNumber,
                    icon = OrbitIcons.ArrowLeft,
                    style = OrbitIconButtonStyle.Neutral,
                    state = if (loading) OrbitButtonState.Disabled else OrbitButtonState.Active,
                )
            }

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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AuthBrandHeader(title = "Verify")
                        if (maskedPhone != null) {
                            Text(
                                text = "Code sent to $maskedPhone",
                                style = OrbitTheme.typography.bodySmall,
                                color = content.textSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }
                        OrbitButton(
                            label = "Change number",
                            onClick = onChangeNumber,
                            variant = OrbitButtonVariant.Text,
                            size = OrbitButtonSize.Small,
                            state = if (loading) {
                                OrbitButtonState.Disabled
                            } else {
                                OrbitButtonState.Active
                            },
                            pressIndication = false,
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        OrbitOtpField(
                            value = otp,
                            onValueChange = onOtpChange,
                            label = "One-time password",
                            state = fieldState,
                            enabled = !loading,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardActions = KeyboardActions(
                                onDone = { if (canVerify) onVerify() },
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

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        OrbitButton(
                            label = "Verify",
                            onClick = onVerify,
                            modifier = Modifier.fillMaxWidth(),
                            variant = OrbitButtonVariant.Primary,
                            size = OrbitButtonSize.Medium,
                            state = if (canVerify) {
                                OrbitButtonState.Active
                            } else {
                                OrbitButtonState.Disabled
                            },
                            shape = authShape,
                            // Frosted glass on light (see OrbitButton); keep specular on both themes.
                            glassHighlight = true,
                            shadowElevation = OrbitTheme.sizing.bottomNavShadow,
                        )

                        if (resendSecondsLeft > 0) {
                            Text(
                                text = "Resend in ${resendSecondsLeft}s",
                                style = OrbitTheme.typography.bodySmall,
                                color = content.textSecondary,
                                textAlign = TextAlign.Center,
                            )
                        } else {
                            OrbitButton(
                                label = "Resend code",
                                onClick = {
                                    onResend()
                                    resendSecondsLeft = ResendCooldownSeconds
                                },
                                variant = OrbitButtonVariant.Text,
                                size = OrbitButtonSize.Small,
                                state = if (!loading) {
                                    OrbitButtonState.Active
                                } else {
                                    OrbitButtonState.Disabled
                                },
                                icon = OrbitIcons.Clock,
                                iconPosition = OrbitButtonIconPosition.Leading,
                                pressIndication = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val ResendCooldownSeconds = 60
