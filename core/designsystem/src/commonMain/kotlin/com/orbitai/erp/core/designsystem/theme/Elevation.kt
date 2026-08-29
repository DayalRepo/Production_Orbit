package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class OrbitElevation(
    val none: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,

    val card: Dp = 1.dp,
    val cardRaised: Dp = 3.dp,
    val topBar: Dp = 0.dp,
    val topBarScrolled: Dp = 3.dp,
    val bottomBar: Dp = 3.dp,
    val dialog: Dp = 6.dp,
    val menu: Dp = 3.dp,
    val fab: Dp = 6.dp,
)

internal val LocalOrbitElevation = staticCompositionLocalOf { OrbitElevation() }
