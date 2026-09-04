package com.orbitai.erp.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material type scale, tightened one step from the prior ladder so surfaces feel sharper without
 * going square. Full-pill roles ([OrbitShapeTokens.button], chip, badge, avatar) stay at 50%.
 */
internal val OrbitShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

/**
 * Component-role shapes, so a card's radius can change in one place.
 *
 * ### Nested surfaces
 *
 * When a rounded box sits *inside* another, use [nestedCorner]: subtract [nestedDelta] (4dp) from
 * the parent's radius, floored at 2dp. That keeps the inner curve concentric with the outer padding
 * rhythm — the common product metric (`inner ≈ outer − padding step`) — so nested fields, chips and
 * inset panels do not fight the parent corner.
 */
@Immutable
data class OrbitShapeTokens(
    val card: CornerBasedShape = RoundedCornerShape(10.dp),
    val cardCompact: CornerBasedShape = RoundedCornerShape(6.dp),
    /**
     * Buttons are full pills, matching badges, chips and avatars.
     *
     * `percent = 50` rather than a fixed radius, so the shape stays a true pill as the button grows
     * — which it does whenever the user scales text up. The trade-off is that the silhouette tracks
     * the height rather than being constant, which is why buttons carry a generous minimum width:
     * without one, a short label makes the pill collapse toward a circle.
     */
    val button: CornerBasedShape = RoundedCornerShape(percent = 50),
    /** Text fields and dropdown shells — not search pills (those stay [button] / 50%). */
    val field: CornerBasedShape = RoundedCornerShape(8.dp),

    val chip: CornerBasedShape = RoundedCornerShape(percent = 50),

    /**
     * A chosen value sitting inside a field, as opposed to a filter chip sitting above a list.
     *
     * Cornered rather than a full pill. Sized via [nestedCorner] relative to [field] (8 → 4).
     */
    val inputChip: CornerBasedShape = RoundedCornerShape(4.dp),
    val badge: CornerBasedShape = RoundedCornerShape(percent = 50),
    val avatar: CornerBasedShape = RoundedCornerShape(percent = 50),
    val sheet: CornerBasedShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    val dialog: CornerBasedShape = RoundedCornerShape(16.dp),
    val tooltip: CornerBasedShape = RoundedCornerShape(4.dp),

    /**
     * Subtract this from a parent corner when drawing a nested surface inside it.
     *
     * Matches a typical `sm` padding step so an inset control looks concentric with its card or
     * field rather than repeating the same radius and looking like a second outer rim.
     */
    val nestedDelta: Dp = 4.dp,
) {
    /** Inner corner for a surface nested in a parent of [outer] radius. Floored at 2dp. */
    fun nestedCorner(outer: Dp): Dp = (outer - nestedDelta).coerceAtLeast(2.dp)
}

internal val LocalOrbitShapes = staticCompositionLocalOf { OrbitShapeTokens() }
