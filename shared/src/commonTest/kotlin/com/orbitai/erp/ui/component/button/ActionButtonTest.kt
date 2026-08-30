package com.orbitai.erp.ui.component.button

import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Guards the intent encoded in [ActionKind] and [BusyKind], not their spelling.
 *
 * The catalogue exists so that "Approve" looks and behaves the same on a purchase order as on an
 * RFI. That guarantee is only worth something if the risky pairings cannot drift, so the assertions
 * here are about the pairings that would actually cause harm — a Reject that stops looking
 * destructive, a Cancel that starts looking like the action it escapes.
 */
class ActionButtonTest {

    @Test
    fun `every action has a label and a distinct busy label`() {
        ActionKind.entries.forEach { action ->
            assertTrue(action.label.isNotBlank(), "$action has a blank label")
            assertTrue(action.busyLabel.isNotBlank(), "$action has a blank busy label")
            assertNotEquals(
                action.label,
                action.busyLabel,
                "$action does not change its wording while in flight, so the button just freezes",
            )
        }
    }

    @Test
    fun `rejection is destructive`() {
        assertEquals(
            OrbitButtonVariant.Destructive,
            ActionKind.Reject.variant,
            "Reject is not destructive; it would sit at the same visual weight as Approve",
        )
    }

    @Test
    fun `backing out is never the loud half`() {
        // Cancel must not carry the emphasis of the thing it escapes. It may look like Reject — it
        // deliberately does, see below — but it must never look like Send or Create, or a user
        // skimming a form will tap the wrong end of the decision.
        assertNotEquals(OrbitButtonVariant.Primary, ActionKind.Cancel.variant)
    }

    @Test
    fun `cancel and reject share a treatment on purpose`() {
        // Asserted rather than merely allowed, because the previous scheme had Cancel neutral and
        // Reject red, and it made the same gesture look like two different kinds of act. The two are
        // never offered together — Cancel pairs with Send or Create, Reject with Approve — so there
        // is no screen on which sharing a colour makes them ambiguous, and the labels differ anyway.
        assertEquals(
            ActionKind.Reject.variant,
            ActionKind.Cancel.variant,
            "backing out looks like one thing in one place and another elsewhere",
        )
    }

    @Test
    fun `the committing half of every decision pair is the loud one`() {
        // The pairs `ActionButtonRow` is built for. Equal width is enforced by the layout; equal
        // emphasis would defeat the point of having variants at all, so each confirm must outrank
        // its dismiss.
        val pairs = listOf(
            ActionKind.Reject to ActionKind.Approve,
            ActionKind.Cancel to ActionKind.Send,
            ActionKind.Cancel to ActionKind.Create,
        )
        pairs.forEach { (dismiss, confirm) ->
            assertEquals(
                OrbitButtonVariant.Primary,
                confirm.variant,
                "$confirm does not carry the emphasis of the pair it confirms",
            )
            assertNotEquals(
                confirm.variant,
                dismiss.variant,
                "$dismiss and $confirm are the same variant, so the pair reads as one choice twice",
            )
        }
    }

    @Test
    fun `busy wording is present-tense and unpunctuated`() {
        BusyKind.entries.forEach { kind ->
            assertTrue(kind.label.isNotBlank(), "$kind has a blank label")
            assertTrue(
                kind.label.endsWith("ing"),
                "$kind does not read as ongoing, so the spinner and the label disagree",
            )
            // The spinner already says the wait is ongoing; an ellipsis beside a moving glyph is
            // punctuation doing nothing.
            assertTrue(
                !kind.label.contains("."),
                "$kind carries an ellipsis, which the spinner already conveys",
            )
        }
    }

    @Test
    fun `no two busy states share wording`() {
        val labels = BusyKind.entries.map { it.label }
        assertEquals(
            labels.size,
            labels.toSet().size,
            "two busy states say the same thing, so the label stops telling the user what is happening",
        )
    }
}
