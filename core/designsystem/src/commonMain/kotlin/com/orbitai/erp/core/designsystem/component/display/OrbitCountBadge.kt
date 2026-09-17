package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors

/**
 * Numeric count badge for tab icons and list rows.
 *
 * **Shape:** full circle for a single digit (1–9); full pill for two or three characters (10–99,
 * "99+").
 *
 * **Finish:** solid tone fill — no glass. Digit uses [OrbitBadgeColors.onSolidContainer].
 *
 * @param compact smaller size for bottom-nav icons.
 * @param count items pending. Zero renders nothing.
 * @param label what is being counted, for the spoken description.
 */
@Composable
fun OrbitCountBadge(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    tone: OrbitBadgeTone = OrbitBadgeTone.Red,
    compact: Boolean = false,
) {
    if (count <= 0) return

    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val palette = tone.colors
    val shown = if (count > MaxDisplayed) "$MaxDisplayed+" else "$count"
    val singleDigit = count in 1..9
    val shape = if (singleDigit) CircleShape else RoundedCornerShape(percent = 50)
    val minSize = if (compact) sizing.countBadgeCompactSize else sizing.countBadgeMinSize
    val hPad = when {
        singleDigit -> 0.dp
        compact -> 4.dp
        else -> spacing.xs
    }
    val digitStyle = if (compact) {
        OrbitTheme.extendedTypography.metricCaption.copy(fontSize = 9.sp, lineHeight = 10.sp)
    } else {
        OrbitTheme.extendedTypography.metricCaption
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minSize, minHeight = minSize)
            .clip(shape)
            .background(palette.solidContainer)
            .border(width = sizing.hairline, color = palette.label.copy(alpha = 0.35f), shape = shape)
            .padding(horizontal = hPad)
            .semantics { contentDescription = "$count $label" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = shown,
            style = digitStyle,
            color = palette.onSolidContainer,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

/**
 * Presence-only mark — no number. Solid fill at 8dp.
 */
@Composable
fun OrbitPresenceDot(
    label: String,
    modifier: Modifier = Modifier,
    tone: OrbitBadgeTone = OrbitBadgeTone.Red,
) {
    val palette = tone.colors
    Box(
        modifier = modifier
            .size(OrbitTheme.sizing.countBadgeDot)
            .clip(CircleShape)
            .background(palette.solidContainer)
            .semantics { contentDescription = label },
    )
}

/** Where the count stops being a number and becomes "a lot". */
const val MaxDisplayed = 99
