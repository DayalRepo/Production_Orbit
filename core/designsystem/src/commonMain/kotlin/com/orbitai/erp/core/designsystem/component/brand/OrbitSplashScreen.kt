package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Opening beat: [OrbitOpeningMark] as the letter O, type in `rbit.ai` on the same baseline with
 * each letter fading on land, hold, type out with each letter fading closed, hold the mark, then
 * hand off.
 *
 * Sizes and gap follow [OrbitOpeningLockup].
 */
@Composable
fun OrbitSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val markColor = OrbitMarkDefaults.color()
    val backgroundColor = OrbitMarkDefaults.surfaceColor()
    val wordColor = OrbitTheme.contentColors.textPrimary

    val contentAlpha = remember { Animatable(1f) }
    val markScale = remember { Animatable(0.94f) }
    // Per-character opacity for smooth land / close while typing.
    val letterAlphas = remember {
        List(Word.length) { Animatable(0f) }
    }
    var typedCount by remember { mutableIntStateOf(0) }

    val wordStyle = TextStyle(
        fontSize = OrbitOpeningLockup.WordSize,
        lineHeight = OrbitOpeningLockup.WordSize,
        fontWeight = FontWeight.W300,
        letterSpacing = OrbitOpeningLockup.WordTracking,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Proportional,
            trim = LineHeightStyle.Trim.Both,
        ),
    )

    LaunchedEffect(Unit) {
        markScale.animateTo(1f, tween(durationMillis = MarkInMs.toInt(), easing = FastOutSlowInEasing))

        delay(TypeStartDelayMs)

        // Type in: reveal next letter, fade it in smoothly.
        for (i in Word.indices) {
            typedCount = i + 1
            launch {
                letterAlphas[i].animateTo(
                    1f,
                    tween(durationMillis = LetterFadeInMs.toInt(), easing = FastOutSlowInEasing),
                )
            }
            delay(TypeInCharMs)
        }

        delay(HoldFullWordMs)

        // Type out: fade the last letter closed, then drop it from the typed prefix.
        for (i in Word.lastIndex downTo 0) {
            letterAlphas[i].animateTo(
                0f,
                tween(durationMillis = LetterFadeOutMs.toInt(), easing = FastOutSlowInEasing),
            )
            typedCount = i
        }

        delay(HoldMarkAloneMs)

        contentAlpha.animateTo(0f, tween(durationMillis = FadeOutMs.toInt(), easing = FastOutSlowInEasing))
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .alpha(contentAlpha.value)
                .scale(markScale.value),
            horizontalArrangement = Arrangement.spacedBy(OrbitOpeningLockup.MarkToWordGap),
        ) {
            OrbitOpeningMark(
                color = markColor,
                contentDescription = "Orbit.ai",
                modifier = Modifier.alignBy { it.measuredHeight },
            )

            if (typedCount > 0) {
                Text(
                    text = buildAnnotatedString {
                        Word.forEachIndexed { index, char ->
                            if (index < typedCount) {
                                val a = letterAlphas[index].value
                                withStyle(SpanStyle(color = wordColor.copy(alpha = a))) {
                                    append(char)
                                }
                            } else {
                                withStyle(SpanStyle(color = Color.Transparent)) {
                                    append(char)
                                }
                            }
                        }
                    },
                    style = wordStyle,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.alignBy(FirstBaseline),
                )
            }
        }
    }
}

/** Letters typed after the pixel O so the word reads Orbit.ai. */
private const val Word = "rbit.ai"

private const val MarkInMs = 280L
private const val TypeStartDelayMs = 380L
/** Beat between starting each letter; fade overlaps the next beat for a soft cascade. */
private const val TypeInCharMs = 95L
private const val LetterFadeInMs = 180L
private const val HoldFullWordMs = 800L
private const val LetterFadeOutMs = 120L
private const val HoldMarkAloneMs = 700L
private const val FadeOutMs = 260L
