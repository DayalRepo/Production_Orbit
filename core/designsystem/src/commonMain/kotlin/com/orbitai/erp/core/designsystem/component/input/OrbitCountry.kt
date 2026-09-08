package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.runtime.Immutable

/**
 * One dialling destination for [OrbitPhoneField].
 *
 * [flag] is a Unicode regional-indicator emoji (🇮🇳) so the library stays asset-free across
 * Android and iOS. [dialCode] includes the leading `+`.
 */
@Immutable
data class OrbitCountry(
    val iso2: String,
    val name: String,
    val dialCode: String,
    val flag: String,
) {
    /** Compact chip label: flag + dial code. */
    val codeLabel: String get() = "$flag  $dialCode"
}

/**
 * Curated country list for mobile OTP login. India first (product default); the rest cover the
 * markets the demo roster and common diaspora numbers use. Screens can pass a subset if needed.
 */
object OrbitCountries {
    val India = OrbitCountry("IN", "India", "+91", "🇮🇳")
    val UnitedArabEmirates = OrbitCountry("AE", "United Arab Emirates", "+971", "🇦🇪")
    val Singapore = OrbitCountry("SG", "Singapore", "+65", "🇸🇬")
    val UnitedKingdom = OrbitCountry("GB", "United Kingdom", "+44", "🇬🇧")
    val UnitedStates = OrbitCountry("US", "United States", "+1", "🇺🇸")
    val SaudiArabia = OrbitCountry("SA", "Saudi Arabia", "+966", "🇸🇦")
    val Qatar = OrbitCountry("QA", "Qatar", "+974", "🇶🇦")
    val Australia = OrbitCountry("AU", "Australia", "+61", "🇦🇺")
    val Germany = OrbitCountry("DE", "Germany", "+49", "🇩🇪")
    val Canada = OrbitCountry("CA", "Canada", "+1", "🇨🇦")

    val All: List<OrbitCountry> = listOf(
        India,
        UnitedArabEmirates,
        Singapore,
        UnitedKingdom,
        UnitedStates,
        SaudiArabia,
        Qatar,
        Australia,
        Germany,
        Canada,
    )

    fun byIso2(iso2: String): OrbitCountry =
        All.firstOrNull { it.iso2.equals(iso2, ignoreCase = true) } ?: India
}
