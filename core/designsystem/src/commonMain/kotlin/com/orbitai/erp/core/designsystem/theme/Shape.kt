package com.orbitai.erp.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material type scale. Surfaces stay slightly rounded. Controls that you act with — buttons,
 * badges, chips, composers, nav — are full pills.
 *
 * Android uses Material-adjacent 8 / 12 / 16 on surfaces. iOS sits a step softer on cards.
 */
internal val OrbitShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

/**
 * Component-role shapes, so a card's radius can change in one place.
 *
 * Pills (`percent = 50`) are the control vocabulary. Fields and cards stay modest rounded
 * rectangles. Avatars stay circular. When a rounded box sits inside another, use [nestedCorner]:
 * subtract [nestedDelta] (4dp) from the parent, floored at 2dp.
 */
@Immutable
data class OrbitShapeTokens(
    val card: CornerBasedShape = RoundedCornerShape(12.dp),
    val cardCompact: CornerBasedShape = RoundedCornerShape(8.dp),
    /** Labelled buttons, choice cells, floating nav bar, composers at one line. */
    val button: CornerBasedShape = RoundedCornerShape(percent = 50),
    val field: CornerBasedShape = RoundedCornerShape(8.dp),
    val chip: CornerBasedShape = RoundedCornerShape(percent = 50),
    /** A chosen value sitting inside a field — tighter than [chip], not a pill. */
    val inputChip: CornerBasedShape = RoundedCornerShape(6.dp),
    val badge: CornerBasedShape = RoundedCornerShape(percent = 50),
    val roleBadge: CornerBasedShape = RoundedCornerShape(percent = 50),
    /** Faces and icon-button rings stay circular. */
    val avatar: CornerBasedShape = RoundedCornerShape(percent = 50),
    val sheet: CornerBasedShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    val dialog: CornerBasedShape = RoundedCornerShape(16.dp),
    val tooltip: CornerBasedShape = RoundedCornerShape(6.dp),
    /** Progress tracks and scrollbar thumbs. */
    val progress: CornerBasedShape = RoundedCornerShape(percent = 50),

    val nestedDelta: Dp = 4.dp,
) {
    /** Inner corner for a surface nested in a parent of [outer] radius. Floored at 2dp. */
    fun nestedCorner(outer: Dp): Dp = (outer - nestedDelta).coerceAtLeast(2.dp)
}

internal val AndroidShapeTokens = OrbitShapeTokens()

internal val IosShapeTokens = OrbitShapeTokens(
    card = RoundedCornerShape(14.dp),
    cardCompact = RoundedCornerShape(10.dp),
    field = RoundedCornerShape(10.dp),
    inputChip = RoundedCornerShape(6.dp),
    sheet = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
    dialog = RoundedCornerShape(14.dp),
    tooltip = RoundedCornerShape(6.dp),
)

internal val LocalOrbitShapes = staticCompositionLocalOf { AndroidShapeTokens }
