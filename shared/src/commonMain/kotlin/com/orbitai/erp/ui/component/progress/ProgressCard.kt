package com.orbitai.erp.ui.component.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.display.OrbitDelta
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A dashboard card: a labelled percentage, how it has moved, and a segmented bar beneath.
 *
 * This is a preset rather than a primitive. `:core:designsystem` supplies the card, the bar and the
 * delta chip and has no opinion about how they are arranged; this file decides that OrbitAI's
 * progress card puts the label top-left, the figure under it, the change beside the figure, and the
 * bar across the bottom. Keeping that here is what stops `OrbitCard` growing a `showDelta` flag.
 *
 * ### The figure is the subject, not the bar
 *
 * The percentage is set in the largest type on the card and the bar is given the full width beneath
 * it, which is the opposite of how progress is usually laid out — normally the bar leads and a small
 * number annotates it. Inverting that is deliberate. A segmented bar quantises, so it cannot be the
 * authoritative reading; putting the exact figure first and the bar second makes the bar what it
 * actually is, which is a shape that tells you at a glance roughly where you are and how that
 * compares to the card above it.
 *
 * ### Reading order
 *
 * The whole card is one announcement, in the order a person would say it out loud: what it is, where
 * it stands, which way it is going. Left to the default, a screen reader would read four separate
 * nodes — the label, a bare number, a bare percentage, and then the bar's own range info repeating
 * the number a second time.
 *
 * @param label names the measurement. Pass `null` only when the surrounding screen already says what
 *   the figure is — a card on a project detail page under a "Progress" section header does not need
 *   to repeat it, and the repetition costs a line of height on every card in the column. The label
 *   still has to exist somewhere for the card's spoken description to be intelligible, so
 *   [contentDescription] becomes required when it is dropped.
 * @param delta change in percentage points since [comparisonLabel]. Pass `null` when there is no
 *   previous reading; a first-week project genuinely has no delta, and rendering "0%" there claims a
 *   measurement that was never taken.
 * @param higherIsBetter forwarded to the delta chip. Leave `true` for completion and progress, set
 *   `false` for anything where a rise is bad news — overdue counts, cost variance, rework.
 */
@Composable
fun ProgressCard(
    label: String?,
    progress: Float,
    modifier: Modifier = Modifier,
    delta: Float? = null,
    comparisonLabel: String = "vs last week",
    higherIsBetter: Boolean = true,
    contentDescription: String? = null,
) {
    require(label != null || contentDescription != null) {
        "A ProgressCard with no visible label needs a contentDescription: without one the card " +
            "announces a bare percentage and a screen reader user has no way to know what it measures."
    }
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val fraction = progress.coerceIn(0f, 1f)
    val percent = (fraction * 100f).roundToInt()

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        // Tighter than the default card inset. This card is four short lines and a bar, and the
        // standard padding leaves it looking like a mostly-empty box; a dashboard is a column of
        // these, so every unnecessary point of height costs a card off the bottom of the screen.
        padding = spacing.md,
        contentDescription = contentDescription ?: buildString {
            append(label)
            append(", ")
            append(percent)
            append(" percent")
            if (delta != null) {
                append(", ")
                append(if (delta >= 0f) "up " else "down ")
                append(abs(delta).describe())
                append(" percent ")
                append(comparisonLabel)
            }
        },
    ) {
        // One description on the card, so the parts below are silent. Without this the figure is
        // announced twice — once as text and once as the bar's range info.
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            if (label != null) {
                Text(
                    // Set in caps. At the size a card label wants to be, small caps and sentence
                    // case are nearly the same height, but caps read as a *category* rather than as
                    // a sentence — which is what this is, and it stops the label competing with the
                    // figure for the role of "the thing the card says".
                    //
                    // Uppercased here rather than expected from the caller, so a label can be
                    // written once in normal case and used for both this and the spoken description
                    // below. Screen readers never see the caps: the column is semantically cleared
                    // and the card's own description uses the original string, which matters because
                    // TalkBack will spell out short all-caps words as initialisms.
                    text = label.uppercase(),
                    style = OrbitTheme.extendedTypography.cardLabel,
                    // Muted, unlike the figure below it. Both were full-strength ink, and at that
                    // weight the eye had no reason to prefer one over the other — the card read as
                    // two equally important things stacked rather than as a number with a name.
                    // Dropping the label to secondary is what makes the figure the subject; the
                    // label is still well clear of 4.5:1, it just stops arguing.
                    color = content.textSecondary,
                )

                Spacer(Modifier.height(spacing.xxs))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = "$percent%",
                    // One step down from the hero size. At `metricLarge` the figure was taller than
                    // the bar and the label together, which made a card of four short lines read as
                    // a number with some annotations rather than as a labelled measurement.
                    style = OrbitTheme.extendedTypography.metricMedium,
                    color = content.textPrimary,
                    textAlign = TextAlign.Start,
                )
                if (delta != null) {
                    OrbitDelta(
                        value = delta,
                        higherIsBetter = higherIsBetter,
                        // Consumed by the card's own description above; this is here because the
                        // parameter is required, not because anything will read it.
                        contentDescription = "",
                    )
                    Text(
                        text = comparisonLabel,
                        // The one thing on the card that stays muted. It is a fixed caption that
                        // says nothing a returning user does not already know, so it should be
                        // legible on inspection and invisible at a glance.
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(spacing.sm))

            OrbitSegmentedProgress(progress = fraction)
        }
    }
}

/** Matches the delta chip's own formatting, so the spoken figure equals the printed one. */
private fun Float.describe(): String {
    val rounded = (this * 10f).toInt() / 10f
    val whole = rounded.toInt()
    return if (rounded == whole.toFloat()) "$whole" else "$rounded"
}
