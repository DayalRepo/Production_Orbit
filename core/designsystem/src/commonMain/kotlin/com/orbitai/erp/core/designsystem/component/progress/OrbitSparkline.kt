package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Compact health trend graph (7–14 samples): baseline + soft grid, filled area, stroked line,
 * and point markers. Values are normalised against the series min/max.
 */
@Composable
fun OrbitTrendGraph(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color,
    fillColor: Color = lineColor.copy(alpha = if (OrbitTheme.isDark) 0.22f else 0.14f),
    gridColor: Color = OrbitTheme.controlColors.controlBorder.copy(alpha = 0.55f),
    height: Dp = 56.dp,
    strokeWidth: Dp = 2.dp,
) {
    if (values.size < 2) return
    val dark = OrbitTheme.isDark

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val min = values.minOrNull() ?: return@Canvas
        val max = values.maxOrNull() ?: return@Canvas
        val span = (max - min).coerceAtLeast(0.04f)
        val padX = 2.dp.toPx()
        val padY = 6.dp.toPx()
        val usableW = (size.width - padX * 2f).coerceAtLeast(1f)
        val usableH = (size.height - padY * 2f).coerceAtLeast(1f)
        val stepX = usableW / values.lastIndex.toFloat()

        fun point(i: Int, raw: Float): Offset {
            val t = ((raw - min) / span).coerceIn(0f, 1f)
            return Offset(padX + i * stepX, padY + usableH * (1f - t))
        }

        repeat(3) { row ->
            val y = padY + usableH * (row / 2f)
            drawLine(
                color = gridColor,
                start = Offset(padX, y),
                end = Offset(padX + usableW, y),
                strokeWidth = 1.dp.toPx(),
            )
        }

        val linePath = Path()
        val fillPath = Path()
        values.forEachIndexed { i, raw ->
            val p = point(i, raw)
            if (i == 0) {
                linePath.moveTo(p.x, p.y)
                fillPath.moveTo(p.x, padY + usableH)
                fillPath.lineTo(p.x, p.y)
            } else {
                linePath.lineTo(p.x, p.y)
                fillPath.lineTo(p.x, p.y)
            }
        }
        val last = point(values.lastIndex, values.last())
        fillPath.lineTo(last.x, padY + usableH)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillColor, fillColor.copy(alpha = 0f)),
                startY = padY,
                endY = padY + usableH,
            ),
        )
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )

        values.forEachIndexed { i, raw ->
            val p = point(i, raw)
            val isLast = i == values.lastIndex
            drawCircle(
                color = lineColor,
                radius = if (isLast) 3.25.dp.toPx() else 2.dp.toPx(),
                center = p,
            )
            if (isLast) {
                drawCircle(
                    color = Color.White.copy(alpha = if (dark) 0.18f else 0.9f),
                    radius = 1.35.dp.toPx(),
                    center = p,
                )
            }
        }
    }
}

/** Thin alias for older call sites — prefer [OrbitTrendGraph]. */
@Composable
fun OrbitSparkline(
    values: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = OrbitTheme.contentColors.textSecondary,
    height: Dp = 56.dp,
    strokeWidth: Dp = 2.dp,
) {
    OrbitTrendGraph(
        values = values,
        modifier = modifier,
        lineColor = color,
        height = height,
        strokeWidth = strokeWidth,
    )
}
