package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Shared Orbit pixel mark — hollow orbit-ring "O" with a four-pointed AI sparkle (v4 geometry).
 *
 * Prefer the role-specific wrappers for product surfaces:
 * - [OrbitLauncherIcon] — home-screen / application icon plate
 * - [OrbitOpeningMark] — brand-intro splash letter O
 * - [OrbitNavBrandMark] — bottom-nav AI circle
 */
@Composable
fun OrbitMark(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    color: Color = OrbitMarkDefaults.color(),
    contentDescription: String? = "Orbit.ai",
) {
    val cells = remember { OrbitMarkGeometry.Cells }
    Canvas(
        modifier = modifier
            .size(size)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
    ) {
        drawOrbitMark(cells = cells, color = color)
    }
}

/** Theme-resolved colours for the mark and the splash / chrome surfaces that carry it. */
object OrbitMarkDefaults {
    /** Brand accent — tracks light/dark with the rest of the UI chrome. */
    @Composable
    fun color(): Color = OrbitTheme.contentColors.iconAccent

    @Composable
    fun surfaceColor(): Color = OrbitTheme.colorScheme.background
}

@Immutable
object OrbitMarkGeometry {
    const val Grid = 13

    /**
     * Outer orbit ring (v4): spaced cardinals, diamond clusters on the diagonals, three-pixel
     * sides so the O closes.
     */
    val Ring: List<Pair<Int, Int>> = listOf(
        5 to 0, 7 to 0,
        3 to 1, 9 to 1,
        2 to 2, 4 to 2, 8 to 2, 10 to 2,
        1 to 3, 3 to 3, 9 to 3, 11 to 3,
        2 to 4, 10 to 4,
        0 to 5, 12 to 5,
        0 to 6, 12 to 6,
        0 to 7, 12 to 7,
        2 to 8, 10 to 8,
        1 to 9, 3 to 9, 9 to 9, 11 to 9,
        2 to 10, 4 to 10, 8 to 10, 10 to 10,
        3 to 11, 9 to 11,
        5 to 12, 7 to 12,
    )

    /**
     * Hollow four-point sparkle — tips and shoulders, no centre (reads as a star, not a plus).
     */
    val Sparkle: List<Pair<Int, Int>> = listOf(
        6 to 4,
        5 to 5, 7 to 5,
        4 to 6, 8 to 6,
        5 to 7, 7 to 7,
        6 to 8,
    )

    val Cells: List<Pair<Int, Int>> = Ring + Sparkle

    const val CellFill = 0.78f
}

internal fun DrawScope.drawOrbitMark(
    cells: List<Pair<Int, Int>>,
    color: Color,
) {
    val cell = size.minDimension / OrbitMarkGeometry.Grid
    val square = cell * OrbitMarkGeometry.CellFill
    val inset = (cell - square) / 2f
    val originX = (size.width - cell * OrbitMarkGeometry.Grid) / 2f
    val originY = (size.height - cell * OrbitMarkGeometry.Grid) / 2f

    for ((col, row) in cells) {
        drawRect(
            color = color,
            topLeft = Offset(originX + col * cell + inset, originY + row * cell + inset),
            size = Size(square, square),
        )
    }
}
