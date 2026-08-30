package com.orbitai.erp.core.designsystem.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorNode
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * An icon drawn at [size] with its stroke corrected for that size.
 *
 * ### Why this exists
 *
 * The Hugeicons set is authored on a 24-unit viewport with the stroke expressed in those same units,
 * so the stroke scales with the glyph. Render a 24dp icon and the line is 1.8dp; render the identical
 * vector at 16dp and the line is 1.2dp. That is fine while every icon in the app is roughly one size
 * — the set stays internally consistent — but it stops being fine the moment the same glyph appears
 * at 16dp in one place and 24dp in another, because the two now look like they came from different
 * icon libraries. It also drops under the 1.5dp floor that both platforms treat as the thinnest a
 * stroked icon may be before it starts disappearing on a low-DPI screen or against a busy surface.
 *
 * So the stroke is recomputed from the rendered size rather than inherited from the author's
 * viewport. Where the natural scaled value already lands inside the platform band it is left alone;
 * where it falls out, it is clamped back in. See [orbitGlyphStroke].
 *
 * ### Why not just fix the source
 *
 * Because there is no single right answer at generation time. A heavier stroke baked into the vector
 * would fix the 16dp case and ruin the 32dp one. The stroke has to be a function of the size it is
 * drawn at, which means it belongs here and not in the generator.
 *
 * The rebuilt vector is remembered against the icon and the stroke, so the walk happens once per
 * distinct pairing rather than once per frame.
 *
 * @param contentDescription null when an enclosing control already carries the description, which is
 *   the usual case inside a button. A non-null value here on top of a labelled parent makes screen
 *   readers announce the control twice.
 */
@Composable
fun OrbitGlyph(
    icon: ImageVector,
    size: Dp,
    tint: Color,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val stroke = orbitGlyphStroke(size)
    val corrected = remember(icon, stroke) { icon.orbitRestroked(stroke) }

    Icon(
        imageVector = corrected,
        contentDescription = contentDescription,
        tint = tint,
        // `layout` rather than `size`, because a stroke wider than the author's leaks past the
        // viewport edges. Compose clips an ImageVector to its viewport, so a 2.25-unit stroke centred
        // on a path that runs to x=2 would have its outer half cut off. Drawing into a slightly
        // larger box and then reporting the requested size back to the parent gives the stroke room
        // to breathe without the glyph occupying more space in the layout than it was asked for.
        modifier = modifier
            .layout { measurable, _ ->
                val reported = size.roundToPx()
                val drawn = (size * (1f + Bleed)).roundToPx()
                val placeable = measurable.measure(Constraints.fixed(drawn, drawn))
                layout(reported, reported) {
                    placeable.place(
                        (reported - placeable.width) / 2,
                        (reported - placeable.height) / 2,
                    )
                }
            },
    )
}

/**
 * The stroke width, in viewport units, that draws a [size] glyph at a legible weight.
 *
 * Both platforms converge on the same band for stroked icons — Material asks for 1.5 to 2dp, and
 * SF Symbols' regular weight sits inside it — so the rule is the same on each: take the weight the
 * vector would naturally scale to, and clamp it into that band.
 *
 * The floor is what does the work. At the sizes an icon button uses, the natural weight is 1.2 to
 * 1.5dp, so small and medium glyphs are lifted to the floor and large is already there; the result is
 * three sizes of the same glyph that look like one another. The ceiling matters much less often, but
 * without it a 12dp glyph would be asked for a stroke thick enough to close its own counters.
 *
 * Note that the band is in *dp* while the return value is in viewport units, which is the entire
 * point: converting between them is what makes the weight independent of the rendered size.
 */
@Composable
fun orbitGlyphStroke(size: Dp): Float {
    val sizing = OrbitTheme.sizing
    val floor = sizing.iconStrokeWidth
    val ceiling = sizing.borderStrong
    val natural = size * (AuthoredStroke / Viewport)
    val target = natural.coerceIn(floor, ceiling)
    return Viewport * (target / size)
}

/**
 * A copy of this vector with every stroked path redrawn at [strokeLineWidth] viewport units.
 *
 * Filled paths are copied untouched: a path with no stroke has no stroke to correct, and giving it
 * one would outline shapes the designer drew as solids.
 */
private fun ImageVector.orbitRestroked(strokeLineWidth: Float): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = defaultWidth,
        defaultHeight = defaultHeight,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
        tintColor = tintColor,
        tintBlendMode = tintBlendMode,
        autoMirror = autoMirror,
    )

    fun copy(group: VectorGroup) {
        group.forEach { node: VectorNode ->
            when (node) {
                is VectorPath -> builder.addPath(
                    pathData = node.pathData,
                    pathFillType = node.pathFillType,
                    name = node.name,
                    fill = node.fill,
                    fillAlpha = node.fillAlpha,
                    stroke = node.stroke,
                    strokeAlpha = node.strokeAlpha,
                    strokeLineWidth = if (node.stroke != null) {
                        strokeLineWidth
                    } else {
                        node.strokeLineWidth
                    },
                    strokeLineCap = node.strokeLineCap,
                    strokeLineJoin = node.strokeLineJoin,
                    strokeLineMiter = node.strokeLineMiter,
                    trimPathStart = node.trimPathStart,
                    trimPathEnd = node.trimPathEnd,
                    trimPathOffset = node.trimPathOffset,
                )

                is VectorGroup -> {
                    builder.addGroup(
                        name = node.name,
                        rotate = node.rotation,
                        pivotX = node.pivotX,
                        pivotY = node.pivotY,
                        scaleX = node.scaleX,
                        scaleY = node.scaleY,
                        translationX = node.translationX,
                        translationY = node.translationY,
                        clipPathData = node.clipPathData,
                    )
                    copy(node)
                    builder.clearGroup()
                }
            }
        }
    }

    copy(root)
    return builder.build()
}

/** The viewport the set is authored on. */
private const val Viewport = 24f

/** The stroke baked into the generated vectors, in viewport units. */
private const val AuthoredStroke = 1.8f

/**
 * Extra room around the glyph, as a fraction of its size, so a corrected stroke is not clipped by
 * the viewport edge. A tenth covers the widest correction the clamp can produce.
 */
private const val Bleed = 0.1f
