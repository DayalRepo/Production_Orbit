package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Groups national mobile digits as `XXXXX XXXXX` (5 + space + rest) while typing.
 */
object OrbitPhoneGroupingTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val grouped = buildString {
            digits.forEachIndexed { index, c ->
                if (index == 5) append(' ')
                append(c)
            }
        }
        return TransformedText(
            AnnotatedString(grouped),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val clamped = offset.coerceIn(0, digits.length)
                    return if (clamped <= 5) clamped else clamped + 1
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val maxTransformed = grouped.length
                    val clamped = offset.coerceIn(0, maxTransformed)
                    return if (clamped <= 5) {
                        clamped.coerceAtMost(digits.length)
                    } else {
                        (clamped - 1).coerceIn(0, digits.length)
                    }
                }
            },
        )
    }
}
