package com.orbitai.erp.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.data.session.FakeSessionRepository
import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.designsystem.component.input.OrbitCountries
import com.orbitai.erp.core.designsystem.component.input.OrbitCountry
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed interface AuthStep {
    data object Login : AuthStep
    data class Otp(val e164Digits: String, val displayPhone: String) : AuthStep
}

/**
 * Auth flow after splash: mobile login → OTP verify. No sign-up.
 *
 * Uses [FakeSessionRepository.signInWithOtp] against [MockDirectory] until SMS OTP is wired.
 */
@Composable
fun AuthFlowScreen(
    sessionRepository: FakeSessionRepository,
    onAuthenticated: () -> Unit,
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var step by remember { mutableStateOf<AuthStep>(AuthStep.Login) }
    var country by remember { mutableStateOf(OrbitCountries.India) }
    var nationalNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fieldState by remember { mutableStateOf(OrbitFieldState.Default) }

    when (val current = step) {
        AuthStep.Login -> LoginScreen(
            nationalNumber = nationalNumber,
            onNationalNumberChange = {
                nationalNumber = it
                errorMessage = null
                fieldState = OrbitFieldState.Default
            },
            country = country,
            onCountryChange = {
                country = it
                errorMessage = null
                fieldState = OrbitFieldState.Default
            },
            onContinue = {
                val digits = nationalNumber.filter { it.isDigit() }
                if (digits.length < 10) {
                    fieldState = OrbitFieldState.Error
                    errorMessage = "Enter a valid 10-digit mobile number"
                    return@LoginScreen
                }
                val e164 = composeE164Digits(country, digits)
                val user = MockDirectory.userByPhone(e164)
                if (user == null) {
                    fieldState = OrbitFieldState.Error
                    errorMessage = "This number is not onboarded. Ask your admin for access."
                    return@LoginScreen
                }
                scope.launch {
                    loading = true
                    errorMessage = null
                    delay(350)
                    loading = false
                    fieldState = OrbitFieldState.Default
                    otp = ""
                    step = AuthStep.Otp(
                        e164Digits = e164,
                        displayPhone = formatMaskedPhone(country, digits),
                    )
                }
            },
            isDark = isDark,
            onThemeChange = onThemeChange,
            modifier = modifier,
            fieldState = fieldState,
            errorMessage = errorMessage,
            loading = loading,
        )

        is AuthStep.Otp -> OtpVerifyScreen(
            otp = otp,
            onOtpChange = {
                otp = it
                errorMessage = null
                fieldState = OrbitFieldState.Default
            },
            onVerify = {
                scope.launch {
                    loading = true
                    errorMessage = null
                    delay(280)
                    val ok = sessionRepository.signInWithOtp(current.e164Digits, otp)
                    loading = false
                    if (ok) {
                        fieldState = OrbitFieldState.Success
                        onAuthenticated()
                    } else {
                        fieldState = OrbitFieldState.Error
                        errorMessage = "Incorrect code. Try again or resend."
                    }
                }
            },
            onResend = {
                scope.launch {
                    errorMessage = null
                    fieldState = OrbitFieldState.Default
                    otp = ""
                    delay(200)
                }
            },
            onChangeNumber = {
                otp = ""
                errorMessage = null
                fieldState = OrbitFieldState.Default
                loading = false
                step = AuthStep.Login
            },
            isDark = isDark,
            onThemeChange = onThemeChange,
            modifier = modifier,
            maskedPhone = current.displayPhone,
            fieldState = fieldState,
            errorMessage = errorMessage,
            loading = loading,
        )
    }
}

/** Digits only, country code + national number — matches [MockDirectory.userByPhone] filtering. */
internal fun composeE164Digits(country: OrbitCountry, nationalDigits: String): String =
    country.dialCode.filter { it.isDigit() } + nationalDigits.filter { it.isDigit() }

/**
 * Masks the national number with dots, keeping the last [visibleDigits] visible.
 * Example: `+91 ••••••1001`
 */
internal fun formatMaskedPhone(
    country: OrbitCountry,
    nationalDigits: String,
    visibleDigits: Int = 4,
): String {
    val digits = nationalDigits.filter { it.isDigit() }
    if (digits.isEmpty()) return country.dialCode
    val keep = visibleDigits.coerceAtMost(digits.length)
    val hidden = digits.length - keep
    val dots = "•".repeat(hidden.coerceAtLeast(0))
    val tail = digits.takeLast(keep)
    return "${country.dialCode} $dots$tail"
}
