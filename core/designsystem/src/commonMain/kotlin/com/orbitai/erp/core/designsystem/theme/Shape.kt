package com.orbitai.erp.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

internal val OrbitShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

/** Component-role shapes, so a card's radius can change in one place. */
@Immutable
data class OrbitShapeTokens(
    val card: CornerBasedShape = RoundedCornerShape(12.dp),
    val cardCompact: CornerBasedShape = RoundedCornerShape(8.dp),
    /**
     * Buttons are full pills, matching badges, chips and avatars.
     *
     * `percent = 50` rather than a fixed radius, so the shape stays a true pill as the button grows
     * — which it does whenever the user scales text up. The trade-off is that the silhouette tracks
     * the height rather than being constant, which is why buttons carry a generous minimum width:
     * without one, a short label makes the pill collapse toward a circle.
     */
    val button: CornerBasedShape = RoundedCornerShape(percent = 50),
    val field: CornerBasedShape = RoundedCornerShape(10.dp),

    val chip: CornerBasedShape = RoundedCornerShape(percent = 50),

    /**
     * A chosen value sitting inside a field, as opposed to a filter chip sitting above a list.
     *
     * Cornered rather than a full pill, and the difference is doing real work. A pill is the shape
     * of every control in this system — buttons, filter chips, badges — so a row of pills inside a
     * text field reads as a row of buttons the user is expected to press. These are entered data.
     * Squaring the corners keeps them legible as content while staying soft enough to sit inside a
     * 10dp field without the two radii fighting.
     */
    val inputChip: CornerBasedShape = RoundedCornerShape(8.dp),
    val badge: CornerBasedShape = RoundedCornerShape(percent = 50),
    val avatar: CornerBasedShape = RoundedCornerShape(percent = 50),
    val sheet: CornerBasedShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    val dialog: CornerBasedShape = RoundedCornerShape(20.dp),
    val tooltip: CornerBasedShape = RoundedCornerShape(6.dp),
)

internal val LocalOrbitShapes = staticCompositionLocalOf { OrbitShapeTokens() }
