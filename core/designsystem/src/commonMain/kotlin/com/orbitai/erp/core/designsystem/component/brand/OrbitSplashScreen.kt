package com.orbitai.erp.core.designsystem.component.brand

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Opening beat on a transparent canvas (no plate / no themed fill — window colour shows through):
 * 1. Theme-aware launcher mark colour starts compressed at the O centre, expands to the default ring
 * 2. Types `rbit.ai` in theme ink, holds, types out
 * 3. Mark compresses back to the centre and fades out into the next screen
 */
@Composable
fun OrbitSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val markColor = OrbitMarkDefaults.color()
    val wordColor = OrbitTheme.contentColors.textPrimary

    val contentAlpha = remember { Animatable(1f) }
    val markSpread = remember { Animatable(0f) }
    val letterAlphas = remember {
        List(Word.length) { Animatable(0f) }
    }
    var typedCount by remember { mutableIntStateOf(0) }

    val wordStyle = OrbitTheme.typography.displayLarge.copy(
        fontSize = OrbitOpeningLockup.WordSize,
        lineHeight = OrbitOpeningLockup.WordSize,
        fontWeight = OrbitTheme.fontWeights.display,
        letterSpacing = OrbitOpeningLockup.WordTracking,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Proportional,
            trim = LineHeightStyle.Trim.Both,
        ),
    )

    LaunchedEffect(Unit) {
        // Intro only: compress → expand into the letter O.
        markSpread.snapTo(0f)
        markSpread.animateTo(
            1f,
            tween(durationMillis = SpreadExpandMs.toInt(), easing = FastOutSlowInEasing),
        )

        delay(TypeStartDelayMs)

        for (i in Word.indices) {
            if (!isActive) return@LaunchedEffect
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
        if (!isActive) return@LaunchedEffect

        for (i in Word.lastIndex downTo 0) {
            if (!isActive) return@LaunchedEffect
            letterAlphas[i].animateTo(
                0f,
                tween(durationMillis = LetterFadeOutMs.toInt(), easing = FastOutSlowInEasing),
            )
            typedCount = i
        }

        delay(HoldMarkAloneMs)
        if (!isActive) return@LaunchedEffect

        // Exit: already expanded — compress to centre, fade, hand off (no second expand).
        markSpread.animateTo(
            0f,
            tween(durationMillis = SpreadCompressMs.toInt(), easing = FastOutSlowInEasing),
        )

        contentAlpha.animateTo(
            0f,
            tween(durationMillis = FadeOutMs.toInt(), easing = FastOutSlowInEasing),
        )
        if (isActive) onFinished()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.alpha(contentAlpha.value),
            horizontalArrangement = Arrangement.spacedBy(OrbitOpeningLockup.MarkToWordGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrbitOpeningMark(
                color = markColor,
                spread = markSpread.value,
                contentDescription = "Orbit.ai",
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
                )
            }
        }
    }
}

/** Letters typed after the pixel O so the word reads Orbit.ai. */
private const val Word = "rbit.ai"

private const val SpreadExpandMs = 520L
private const val SpreadCompressMs = 420L
private const val TypeStartDelayMs = 220L
private const val TypeInCharMs = 95L
private const val LetterFadeInMs = 180L
private const val HoldFullWordMs = 800L
private const val LetterFadeOutMs = 120L
private const val HoldMarkAloneMs = 280L
private const val FadeOutMs = 220L
