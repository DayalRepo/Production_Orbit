package com.orbitai.erp.ui.auth

import com.orbitai.erp.core.designsystem.component.input.OrbitCountries
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthPhoneFormatTest {

    @Test
    fun composeE164DigitsMatchesMockCeoPhoneDigits() {
        assertEquals(
            "919845011001",
            composeE164Digits(OrbitCountries.India, "9845011001"),
        )
    }

    @Test
    fun formatMaskedPhoneShowsLastFourDigits() {
        assertEquals(
            "+91 ••••••1001",
            formatMaskedPhone(OrbitCountries.India, "9845011001", visibleDigits = 4),
        )
    }

    @Test
    fun formatMaskedPhoneCanShowLastThree() {
        assertEquals(
            "+91 •••••••001",
            formatMaskedPhone(OrbitCountries.India, "9845011001", visibleDigits = 3),
        )
    }
}
