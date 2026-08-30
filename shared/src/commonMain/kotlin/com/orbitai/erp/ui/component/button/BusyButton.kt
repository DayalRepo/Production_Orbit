package com.orbitai.erp.ui.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant

/**
 * The three things the app makes a user wait for, each with fixed wording.
 *
 * These are states rather than actions, which is why they are separate from [ActionKind]: nobody
 * taps "Verifying". They exist as presets because the wording is the only thing the user has to go
 * on while they wait, and "Loading" where "Verifying" was meant is genuinely misleading — one says
 * the app is fetching, the other says it is checking something the user just submitted.
 *
 * The present participle is deliberate and there is no ellipsis. The spinner already says the wait
 * is ongoing, so a trailing "…" is redundant punctuation next to a moving glyph.
 */
enum class BusyKind(val label: String) {
    /** A dispatch is in flight — a message, an order, an approval. */
    Sending("Sending"),

    /** Content is being fetched. The generic case; prefer a more specific one where it fits. */
    Loading("Loading"),

    /** Something the user submitted is being checked — an OTP, a credential, a signature. */
    Verifying("Verifying"),
}

/**
 * A button that is already working.
 *
 * Non-interactive by construction: there is no `onClick`, because the whole point of the state is
 * that the action has been fired and must not be fired again. A screen shows this in place of the
 * button that started the work, at the same size, so the layout does not jump at the moment the user
 * is least able to tolerate it.
 *
 * The spinner is `OrbitLoadingIcon`, and the label is the accessible name, so a screen reader
 * announces "Sending" rather than announcing nothing and leaving a blind user to wonder whether the
 * tap registered.
 */
@Composable
fun BusyButton(
    kind: BusyKind,
    modifier: Modifier = Modifier,
    variant: OrbitButtonVariant = OrbitButtonVariant.Primary,
    size: OrbitButtonSize = OrbitButtonSize.Medium,
) {
    OrbitButton(
        label = kind.label,
        onClick = {},
        modifier = modifier,
        variant = variant,
        size = size,
        loading = true,
    )
}
