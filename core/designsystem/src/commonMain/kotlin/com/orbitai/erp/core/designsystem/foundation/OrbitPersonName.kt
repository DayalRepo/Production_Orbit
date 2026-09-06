package com.orbitai.erp.core.designsystem.foundation

/**
 * Person names in account and avatar info bubbles.
 *
 * Eighteen characters is the sweet spot for these panels: long enough for "ANANYA KRISHNAMURTHY"
 * (20) to lose only the last letters, short enough that a single line of body type still fits
 * beside a glyph or a copy button without wrapping. All caps matches the assign-field convention.
 */
const val OrbitPersonNameMaxChars = 18

fun orbitPersonDisplayName(name: String, maxChars: Int = OrbitPersonNameMaxChars): String {
    val caps = name.trim().uppercase()
    if (caps.isEmpty()) return caps
    if (caps.length <= maxChars) return caps
    return caps.take((maxChars - 1).coerceAtLeast(1)) + "…"
}
