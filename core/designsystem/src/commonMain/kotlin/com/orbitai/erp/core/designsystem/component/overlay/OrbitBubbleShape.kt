package com.orbitai.erp.core.designsystem.component.overlay

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * A rounded panel with a pointer growing out of one edge, as a single closed outline.
 *
 * ### Why this is a `Shape` and not a triangle drawn next to a card
 *
 * The obvious build is a `Canvas` triangle stacked above a rounded box, and it is what this
 * replaced. It looks correct in a mock-up and wrong on a device, for three reasons that all come
 * from the pointer being a separate object:
 *
 *  - **The border breaks.** The panel strokes its own rim all the way round, including across the
 *    span the pointer is supposed to open into, so there is a line drawn across the mouth. The
 *    pointer ends up looking like a separate diamond parked against the card rather than part of it.
 *  - **The shadow is wrong.** Elevation is cast per-shape, so a stacked pointer casts its own shadow
 *    onto the panel behind it and the seam between them is darker than either. On glass, where the
 *    shadow is most of what sells the material, that reads as two pieces of paper.
 *  - **The fill is wrong.** The panel's fill is a vertical gradient. A separately-filled triangle
 *    samples one flat colour, so at the top of a light-theme bubble the pointer is visibly a
 *    different shade from the edge it touches.
 *
 * Expressed as one `Shape`, all three problems stop existing rather than being worked around: the
 * rim strokes the real silhouette and so travels up and over the pointer, one shadow is cast by the
 * whole outline, and the fill gradient runs through the pointer because it is the same fill. That
 * is what "the pointer is part of the container" has to mean structurally, not just visually.
 *
 * It also composes with the rest of the system for free, because `orbitGlass` and `orbitGlassShadow`
 * both take a `Shape` — the bubble gets the standard glass stack with no special-casing.
 *
 * ### The tip is rounded, and the flanks ease out of the edge
 *
 * A hard spike off a panel whose corners are 16dp round is a different vocabulary from everything
 * else on screen. Each flank leaves the edge with a curve and the two meet in a short arc, so the
 * silhouette is continuous with the rim it grows out of. [TipRadiusFraction] holds the tip a little
 * short of the outer boundary; a curve that reaches the boundary loses its outermost pixel to
 * clipping and the tip goes visibly flat.
 *
 * @param pointingUp true when the pointer is on the top edge — which is the case when the panel has
 *   been flipped *below* its anchor and has to point back up at it.
 * @param pointerOffsetPx how far the pointer sits from the panel's horizontal centre, in pixels.
 *   Signed, and supplied by the position provider: when the panel is clamped against a screen edge
 *   the pointer has to travel the other way to stay over the anchor it belongs to.
 * @param arrowWidth the pointer's span along the edge. Wide relative to its height on purpose — see
 *   the note on the ratio where the defaults are declared.
 * @param arrowHeight how far the pointer protrudes. The panel body is inset by exactly this much on
 *   the pointing side, so the caller's content never lands underneath it.
 */
class OrbitBubbleShape(
    private val pointingUp: Boolean,
    private val pointerOffsetPx: Float,
    private val arrowWidth: Dp,
    private val arrowHeight: Dp,
    private val cornerRadius: Dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val arrowW = with(density) { arrowWidth.toPx() }
        val arrowH = with(density) { arrowHeight.toPx() }

        // The radius cannot exceed half the shorter side or the corner arcs overlap and the path
        // folds back on itself. Clamped rather than trusted, because the panel's height is driven by
        // its content and a one-line bubble at a large corner radius is a real case.
        val bodyHeight = size.height - arrowH
        val radius = with(density) { cornerRadius.toPx() }
            .coerceAtMost(minOf(size.width, bodyHeight) / 2f)
            .coerceAtLeast(0f)

        // The two horizontals of the body, in the panel's own coordinates.
        val top = if (pointingUp) arrowH else 0f
        val bottom = if (pointingUp) size.height else bodyHeight

        // The edge the pointer opens into, and the direction it travels.
        val edgeY = if (pointingUp) top else bottom
        val tipY = if (pointingUp) 0f else size.height

        val half = arrowW / 2f
        // Kept clear of the corner arcs at both ends. A pointer overlapping a rounded corner does
        // not produce a corner and a pointer, it produces a notch out of the silhouette — the arc
        // and the flank cross and the outline briefly doubles back.
        val minCentre = radius + half
        val maxCentre = size.width - radius - half
        val centre = if (minCentre > maxCentre) {
            // The panel is too narrow to hold the pointer anywhere legal. Centring it is the least
            // wrong answer and only happens on a bubble narrower than about 60dp, which the width
            // floor already rules out.
            size.width / 2f
        } else {
            (size.width / 2f + pointerOffsetPx).coerceIn(minCentre, maxCentre)
        }

        // How far the tip is pulled back from the outer boundary, so its rounding is not clipped.
        val tip = if (pointingUp) {
            tipY + arrowH * TipRadiusFraction
        } else {
            tipY - arrowH * TipRadiusFraction
        }
        val shoulder = arrowW * ShoulderFraction

        val path = Path().apply {
            // Start just past the top-left corner arc and run clockwise.
            moveTo(radius, top)

            if (pointingUp) {
                lineTo(centre - half, top)
                // Out of the edge, into the tip, and back down to the edge. Two cubics rather than
                // straight flanks: the first control point sits on the edge itself, which is what
                // makes the pointer leave the rim tangentially instead of at a visible crease.
                cubicTo(centre - half + shoulder, top, centre - shoulder, tip, centre, tip)
                cubicTo(centre + shoulder, tip, centre + half - shoulder, top, centre + half, top)
            }

            lineTo(size.width - radius, top)
            quadraticTo(size.width, top, size.width, top + radius)

            lineTo(size.width, bottom - radius)
            quadraticTo(size.width, bottom, size.width - radius, bottom)

            if (!pointingUp) {
                lineTo(centre + half, bottom)
                cubicTo(centre + half - shoulder, bottom, centre + shoulder, tip, centre, tip)
                cubicTo(centre - shoulder, tip, centre - half + shoulder, bottom, centre - half, bottom)
            }

            lineTo(radius, bottom)
            quadraticTo(0f, bottom, 0f, bottom - radius)

            lineTo(0f, top + radius)
            quadraticTo(0f, top, radius, top)

            close()
        }

        return Outline.Generic(path)
    }
}

/**
 * Where each flank leaves the edge, as a fraction of the pointer's width.
 *
 * This is the knob that decides whether the pointer reads as a spike or as a swelling of the rim.
 * Near zero the cubics degenerate into straight lines and it is a triangle again; past about a third
 * the flanks bulge outward and the pointer looks inflated.
 */
private const val ShoulderFraction = 0.28f

/** How far the tip is held back from the outer boundary, so its rounding is not clipped flat. */
private const val TipRadiusFraction = 0.12f
