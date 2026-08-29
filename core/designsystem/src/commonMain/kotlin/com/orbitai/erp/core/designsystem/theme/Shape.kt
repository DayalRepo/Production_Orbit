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
    val button: CornerBasedShape = RoundedCornerShape(10.dp),
    val field: CornerBasedShape = RoundedCornerShape(10.dp),
    val chip: CornerBasedShape = RoundedCornerShape(8.dp),
    val badge: CornerBasedShape = RoundedCornerShape(percent = 50),
    val avatar: CornerBasedShape = RoundedCornerShape(percent = 50),
    val sheet: CornerBasedShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    val dialog: CornerBasedShape = RoundedCornerShape(20.dp),
    val tooltip: CornerBasedShape = RoundedCornerShape(6.dp),
)

internal val LocalOrbitShapes = staticCompositionLocalOf { OrbitShapeTokens() }
