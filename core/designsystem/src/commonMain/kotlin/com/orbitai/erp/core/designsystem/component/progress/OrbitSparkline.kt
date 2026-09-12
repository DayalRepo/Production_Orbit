package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Compact area trend: solid history line + gradient fill, hollow start/now markers, optional
 * dashed forecast past a vertical “now” marker — matches CEO materials/savings reference charts.
 *
 * Press-and-drag scrubbing clears on finger up. Secondary series uses the same X step as primary
 * (pad/hold last if shorter). Set [showAxes] false for a bare sparkline.
 */
@Composable
fun OrbitTrendGraph(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color,
    fillColor: Color = lineColor.copy(alpha = if (OrbitTheme.isDark) 0.28f else 0.18f),
    gridColor: Color = OrbitTheme.controlColors.controlBorder.copy(
        alpha = if (OrbitTheme.isDark) 0.45f else 0.35f,
    ),
    forecastColor: Color = OrbitTheme.contentColors.textSecondary,
    forecast: List<Float> = emptyList(),
    secondaryValues: List<Float> = emptyList(),
    secondaryForecast: List<Float> = emptyList(),
    secondaryColor: Color = OrbitTheme.contentColors.textTertiary,
    primaryLabel: String = "Savings",
    secondaryLabel: String = "Materials",
    xLabels: List<String> = emptyList(),
    height: Dp = 148.dp,
    strokeWidth: Dp = 1.75.dp,
    showNowMarker: Boolean = forecast.isNotEmpty(),
    interactive: Boolean = true,
    showAxes: Boolean = true,
    formatValue: (Float) -> String = { defaultTrendValueLabel(it) },
) {
    if (values.size < 2) return

    val dark = OrbitTheme.isDark
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val history = values
    val future = forecast
    val nowIndex = history.lastIndex
    val totalPoints = (history.size + future.size).coerceAtLeast(2)
    val lastIndex = (totalPoints - 1).coerceAtLeast(1)

    val secondarySeries = remember(secondaryValues, secondaryForecast, totalPoints, history.size) {
        alignedSecondarySeries(
            secondaryHistory = secondaryValues,
            secondaryForecast = secondaryForecast,
            historyCount = history.size,
            totalPoints = totalPoints,
        )
    }

    val all = history + future + secondarySeries
    val dataMin = all.minOrNull() ?: return
    val dataMax = all.maxOrNull() ?: return
    val yTicks = remember(dataMin, dataMax) { niceAxisTicks(dataMin, dataMax, tickCount = 5) }
    val axisMin = yTicks.first()
    val axisMax = yTicks.last()
    val span = (axisMax - axisMin).coerceAtLeast(0.04f)

    var selectedIndex by remember(history, future) { mutableStateOf<Int?>(null) }
    var plotWidthPx by remember { mutableIntStateOf(0) }
    val yGutter = if (showAxes) 28.dp else 0.dp
    val plotHeight = if (showAxes) height - 18.dp else height

    val resolvedXLabels = remember(xLabels, totalPoints, nowIndex, showAxes) {
        when {
            !showAxes -> emptyList()
            xLabels.isNotEmpty() -> xLabels
            else -> defaultTrendXLabels(totalPoints = totalPoints, nowIndex = nowIndex)
        }
    }

    fun formatTick(v: Float): String {
        val rounded = v.roundToInt()
        return if (abs(v - rounded) < 0.05f) {
            rounded.toString()
        } else {
            ((v * 10f).roundToInt() / 10f).toString()
        }
    }

    fun periodLabel(index: Int): String = when {
        index == nowIndex -> "Now"
        index > nowIndex -> "+${index - nowIndex}w forecast"
        else -> "W${index + 1}"
    }

    val tipSurface = control.cardContainer

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$primaryLabel trend chart"
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(plotHeight),
        ) {
            if (showAxes) {
                Column(
                    modifier = Modifier
                        .width(yGutter)
                        .fillMaxHeight()
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                ) {
                    yTicks.asReversed().forEach { tick ->
                        Text(
                            text = formatTick(tick),
                            style = OrbitTheme.extendedTypography.metricCaption,
                            color = content.textTertiary,
                            maxLines = 1,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .onSizeChanged { plotWidthPx = it.width }
                    .then(
                        if (interactive) {
                            Modifier
                                .orbitHandCursor()
                                .pointerInput(totalPoints, lastIndex) {
                                    fun indexFromX(x: Float): Int {
                                        val usable = size.width.toFloat().coerceAtLeast(1f)
                                        val t = (x / usable).coerceIn(0f, 1f)
                                        return (t * lastIndex).roundToInt().coerceIn(0, lastIndex)
                                    }
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        selectedIndex = indexFromX(down.position.x)
                                        val pointerId = down.id
                                        try {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val change = event.changes.firstOrNull { it.id == pointerId }
                                                    ?: break
                                                if (!change.pressed) break
                                                selectedIndex = indexFromX(change.position.x)
                                                change.consume()
                                            }
                                        } finally {
                                            selectedIndex = null
                                        }
                                    }
                                }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val padL = 2.dp.toPx()
                    val padR = 2.dp.toPx()
                    val padT = if (showAxes) 14.dp.toPx() else 4.dp.toPx()
                    val padB = if (showAxes) 4.dp.toPx() else 2.dp.toPx()
                    val usableW = (size.width - padL - padR).coerceAtLeast(1f)
                    val usableH = (size.height - padT - padB).coerceAtLeast(1f)
                    val stepX = usableW / lastIndex.toFloat()
                    val gridAlpha = if (dark) 0.45f else 0.32f
                    val scrubAlpha = if (dark) 0.65f else 0.45f

                    fun yFor(raw: Float): Float {
                        val t = ((raw - axisMin) / span).coerceIn(0f, 1f)
                        return padT + usableH * (1f - t)
                    }

                    fun pointAt(index: Int, raw: Float): Offset =
                        Offset(padL + index * stepX, yFor(raw))

                    if (showAxes) {
                        yTicks.forEach { tick ->
                            val y = yFor(tick)
                            drawLine(
                                color = gridColor.copy(alpha = gridAlpha),
                                start = Offset(padL, y),
                                end = Offset(padL + usableW, y),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }
                        val tickCount = totalPoints.coerceIn(4, 16)
                        repeat(tickCount) { i ->
                            val x = padL + usableW * (i / (tickCount - 1f).coerceAtLeast(1f))
                            val tall = i % 4 == 0
                            drawLine(
                                color = gridColor.copy(alpha = gridAlpha),
                                start = Offset(x, padT + usableH),
                                end = Offset(
                                    x,
                                    padT + usableH + if (tall) 5.dp.toPx() else 3.dp.toPx(),
                                ),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }
                    }

                    if (secondarySeries.size >= 2) {
                        val secPath = Path()
                        secondarySeries.forEachIndexed { i, raw ->
                            val p = pointAt(i, raw)
                            if (i == 0) secPath.moveTo(p.x, p.y) else secPath.lineTo(p.x, p.y)
                        }
                        drawPath(
                            path = secPath,
                            color = secondaryColor,
                            style = Stroke(
                                width = (strokeWidth * 0.9f).toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                            ),
                        )
                    }

                    val linePath = Path()
                    val fillPath = Path()
                    history.forEachIndexed { i, raw ->
                        val p = pointAt(i, raw)
                        if (i == 0) {
                            linePath.moveTo(p.x, p.y)
                            fillPath.moveTo(p.x, padT + usableH)
                            fillPath.lineTo(p.x, p.y)
                        } else {
                            linePath.lineTo(p.x, p.y)
                            fillPath.lineTo(p.x, p.y)
                        }
                    }
                    val lastHistory = pointAt(nowIndex, history.last())
                    fillPath.lineTo(lastHistory.x, padT + usableH)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(fillColor, fillColor.copy(alpha = 0f)),
                            startY = padT,
                            endY = padT + usableH,
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

                    if (future.isNotEmpty()) {
                        val forecastPath = Path()
                        forecastPath.moveTo(lastHistory.x, lastHistory.y)
                        future.forEachIndexed { i, raw ->
                            val p = pointAt(nowIndex + 1 + i, raw)
                            forecastPath.lineTo(p.x, p.y)
                        }
                        drawPath(
                            path = forecastPath,
                            color = forecastColor.copy(alpha = if (dark) 0.8f else 0.75f),
                            style = Stroke(
                                width = strokeWidth.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(6.dp.toPx(), 4.dp.toPx()),
                                    0f,
                                ),
                            ),
                        )
                    }

                    if (showNowMarker) {
                        val nowX = lastHistory.x
                        drawLine(
                            color = gridColor.copy(alpha = if (dark) 0.7f else 0.5f),
                            start = Offset(nowX, padT),
                            end = Offset(nowX, padT + usableH),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(4.dp.toPx(), 3.dp.toPx()),
                                0f,
                            ),
                        )
                        drawCircle(
                            color = forecastColor.copy(alpha = 0.55f),
                            radius = 2.25.dp.toPx(),
                            center = Offset(nowX, padT),
                        )
                    }

                    fun hollowMarker(center: Offset, color: Color) {
                        val r = 3.5.dp.toPx()
                        drawCircle(
                            color = if (dark) Color(0xFF1C1C1E) else Color.White,
                            radius = r,
                            center = center,
                        )
                        drawCircle(
                            color = color,
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx()),
                        )
                    }
                    if (showAxes || showNowMarker) {
                        hollowMarker(pointAt(0, history.first()), lineColor)
                        hollowMarker(lastHistory, lineColor)
                    }

                    val scrub = selectedIndex
                    if (scrub != null && scrub in 0..lastIndex) {
                        val primaryValue = seriesValueAt(scrub, history, future, nowIndex)
                        val scrubX = padL + scrub * stepX
                        drawLine(
                            color = content.textSecondary.copy(alpha = scrubAlpha),
                            start = Offset(scrubX, padT),
                            end = Offset(scrubX, padT + usableH),
                            strokeWidth = 1.dp.toPx(),
                        )
                        if (primaryValue != null) {
                            val p = pointAt(scrub, primaryValue)
                            drawCircle(color = lineColor, radius = 4.5.dp.toPx(), center = p)
                            drawCircle(
                                color = if (dark) Color(0xFF1C1C1E) else Color.White,
                                radius = 2.25.dp.toPx(),
                                center = p,
                            )
                        }
                        secondarySeries.getOrNull(scrub)?.let { secRaw ->
                            val p = pointAt(scrub, secRaw)
                            drawCircle(color = secondaryColor, radius = 4.dp.toPx(), center = p)
                            drawCircle(
                                color = if (dark) Color(0xFF1C1C1E) else Color.White,
                                radius = 2.dp.toPx(),
                                center = p,
                            )
                        }
                    }
                }

                val scrub = selectedIndex
                if (interactive && scrub != null && scrub in 0..lastIndex) {
                    val primaryValue = seriesValueAt(scrub, history, future, nowIndex)
                    val secondaryValue = secondarySeries.getOrNull(scrub)
                    if (primaryValue != null) {
                        val line2 = buildString {
                            append(primaryLabel.take(1))
                            append(" ")
                            append(formatValue(primaryValue))
                            if (secondaryValue != null) {
                                append(" · ")
                                append(secondaryLabel.take(1))
                                append(" ")
                                append(formatValue(secondaryValue))
                            }
                        }
                        val xFrac = scrub / lastIndex.toFloat()
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset {
                                    val tipWidth = 200.dp.roundToPx()
                                    val plotW = plotWidthPx.coerceAtLeast(1)
                                    val ideal = (plotW * xFrac - tipWidth / 2f).roundToInt()
                                    val x = ideal.coerceIn(0, (plotW - tipWidth).coerceAtLeast(0))
                                    IntOffset(x, 0)
                                }
                                .wrapContentWidth()
                                .widthIn(min = 120.dp, max = 220.dp)
                                .clip(OrbitTheme.shapeTokens.tooltip)
                                .background(tipSurface)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = periodLabel(scrub),
                                style = OrbitTheme.extendedTypography.metricCaption,
                                color = content.textSecondary,
                                maxLines = 1,
                            )
                            Text(
                                text = line2,
                                style = OrbitTheme.extendedTypography.metricCaption,
                                color = content.textPrimary,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }

        if (resolvedXLabels.isNotEmpty()) {
            Spacer(modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = yGutter),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                resolvedXLabels.forEach { label ->
                    Text(
                        text = label,
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
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
        showNowMarker = false,
        interactive = false,
        showAxes = false,
    )
}

private fun defaultTrendValueLabel(value: Float): String {
    val tenths = (value * 10f).roundToInt() / 10f
    val whole = tenths.toInt()
    return if (abs(tenths - whole) < 0.05f) whole.toString() else tenths.toString()
}

private fun seriesValueAt(
    index: Int,
    history: List<Float>,
    future: List<Float>,
    nowIndex: Int,
): Float? = when {
    index <= nowIndex -> history.getOrNull(index)
    else -> future.getOrNull(index - nowIndex - 1)
}

private fun alignedSecondarySeries(
    secondaryHistory: List<Float>,
    secondaryForecast: List<Float>,
    historyCount: Int,
    totalPoints: Int,
): List<Float> {
    if (secondaryHistory.size < 2) return emptyList()
    val out = MutableList(totalPoints) { 0f }
    val lastHist = secondaryHistory.last()
    for (i in 0 until totalPoints) {
        out[i] = when {
            i < secondaryHistory.size -> secondaryHistory[i]
            i < historyCount -> lastHist
            else -> {
                val fi = i - historyCount
                secondaryForecast.getOrElse(fi) { lastHist }
            }
        }
    }
    return out
}

private fun defaultTrendXLabels(totalPoints: Int, nowIndex: Int): List<String> {
    val last = (totalPoints - 1).coerceAtLeast(1)
    val anchors = listOf(0, nowIndex / 2, nowIndex, ((nowIndex + last) / 2), last)
        .distinct()
        .sorted()
    return anchors.map { i ->
        when {
            i == nowIndex -> "Now"
            i > nowIndex -> "+${i - nowIndex}w"
            else -> "W${i + 1}"
        }
    }
}

internal fun niceAxisTicks(min: Float, max: Float, tickCount: Int = 5): List<Float> {
    val lo = min.coerceAtMost(max)
    val hi = max.coerceAtLeast(min)
    if (hi - lo < 0.0001f) {
        val base = lo.roundToInt().toFloat()
        return listOf(base - 20f, base, base + 20f, base + 40f, base + 60f)
    }
    val rawStep = (hi - lo) / (tickCount - 1).coerceAtLeast(1)
    val mag = 10.0.pow(floor(log10(rawStep.toDouble()))).toFloat()
    val residual = rawStep / mag
    val niceResidual = when {
        residual <= 1f -> 1f
        residual <= 2f -> 2f
        residual <= 5f -> 5f
        else -> 10f
    }
    val step = niceResidual * mag
    val start = (floor(lo / step.toDouble()) * step).toFloat()
    val end = (ceil(hi / step.toDouble()) * step).toFloat()
    val ticks = mutableListOf<Float>()
    var v = start
    var guard = 0
    while (v <= end + step * 0.5f && guard < 24) {
        ticks += v
        v += step
        guard++
    }
    return if (ticks.size >= 2) ticks else listOf(lo, hi)
}
