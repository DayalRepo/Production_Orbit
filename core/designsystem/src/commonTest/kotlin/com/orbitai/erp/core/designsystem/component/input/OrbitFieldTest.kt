package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.theme.OrbitDarkControlColors
import com.orbitai.erp.core.designsystem.theme.OrbitLightControlColors
import com.orbitai.erp.core.designsystem.theme.platformTokens
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Guards the two things about an input field that are easy to break without noticing: that it is
 * still tappable, and that its chrome is still visible.
 *
 * The composables need an instrumented test to exercise properly. What can be checked here is the
 * token contract they are built on, which is where the silent regressions live — a field is not
 * obviously broken when its rim fades below the threshold of visibility, it just stops looking like
 * a field, and nobody can say when that happened.
 */
class OrbitFieldTest {

    private val platforms = OrbitPlatform.entries.map { it to platformTokens(it) }

    @Test
    fun `a field is tall enough to tap`() {
        // Unlike a small button, a field has no expanded hit area to fall back on — tapping it is
        // how you focus it, and the target is the visible box. So the box itself has to clear
        // 48dp/44pt. The search and message fields pin their minimum to minTouchTarget directly;
        // this asserts the general field height does not fall below it either.
        platforms.forEach { (name, tokens) ->
            assertTrue(
                tokens.sizing.fieldHeight >= tokens.sizing.minTouchTarget,
                "$name field is ${tokens.sizing.fieldHeight}, under the " +
                    "${tokens.sizing.minTouchTarget} touch target, and a field has no expanded " +
                    "hit area to compensate",
            )
        }
    }

    @Test
    fun `a focused rim is thicker than a resting one`() {
        // The shell swaps hairline for borderStrong on focus, and deliberately not for `border`:
        // `border` is the same 1dp as `hairline`, so that pairing would leave focus carried by tint
        // alone, which fails for anyone who cannot distinguish the two tints. On a touch screen
        // focus is the only indication of which field the keyboard is about to type into.
        platforms.forEach { (name, tokens) ->
            assertTrue(
                tokens.sizing.borderStrong > tokens.sizing.hairline,
                "$name has no width difference between a resting and a focused field rim",
            )
        }
    }

    @Test
    fun `a focused rim is stronger than a resting one`() {
        // Width alone is a sub-pixel difference at hairline scale, so the tint has to move too. The
        // shell uses outlineBorder for focus and controlBorder at rest.
        listOf(
            "light" to OrbitLightControlColors,
            "dark" to OrbitDarkControlColors,
        ).forEach { (name, control) ->
            assertTrue(
                control.outlineBorder.alpha > control.controlBorder.alpha,
                "the $name focused rim (${control.outlineBorder.alpha}) is no more opaque than " +
                    "the resting one (${control.controlBorder.alpha}), so focus is invisible",
            )
        }
    }

    @Test
    fun `the field fill is a tint and not a fill`() {
        // The container is translucent on purpose: it is a tint of the foreground, so a field reads
        // as part of whatever surface it sits on. An opaque fill would make one field look correct
        // on a card and like a cut-out hole on a sheet, and nobody catches that in review because
        // the two are never on screen at the same time.
        listOf(
            "light" to OrbitLightControlColors,
            "dark" to OrbitDarkControlColors,
        ).forEach { (name, control) ->
            assertTrue(
                control.controlContainer.alpha < MaxTintAlpha,
                "the $name field fill is ${control.controlContainer.alpha} opaque, which stops " +
                    "being a tint and starts being a surface of its own",
            )
        }
    }
}

/** Above roughly a third, a translucent fill stops taking colour from what is behind it. */
private const val MaxTintAlpha = 0.35f

/**
 * The composer's shape reports whether it holds anything: a pill when empty, a rounded rectangle
 * once there is text. Both ends of that animation have to stay meaningfully apart, or the transition
 * costs a frame budget and communicates nothing.
 */
class OrbitComposerShapeTest {

    @Test
    fun `the empty composer is a true pill`() {
        // RoundedCornerShape clamps a radius larger than half the height, which is what turns this
        // into a pill at any height. It has to stay comfortably above any height the composer can
        // reach, including four lines at 200% font scale.
        assertTrue(
            PillCorner > MaxPlausibleComposerHeight,
            "the empty-composer corner ($PillCorner) is small enough that a tall composer would " +
                "render as a rounded rectangle instead of clamping to a pill",
        )
    }

    @Test
    fun `typing tightens the corner by a visible amount`() {
        assertTrue(
            ComposerTypingCorner < PillCorner,
            "the composer does not change shape when text is entered",
        )
        // The pill corner is a clamp rather than a real radius, so comparing against it says
        // nothing about how far the corner actually travels. What the eye sees is the typing corner
        // against half the composer's height, and below roughly a third of that the shape stops
        // reading as a container and starts reading as a pill that failed to render.
        assertTrue(
            ComposerTypingCorner < MinComposerHeight / 2,
            "the typing corner ($ComposerTypingCorner) is at or past the pill radius for a " +
                "single-line composer, so the shape change would be invisible",
        )
    }
}

/** A single-line composer, which is the shortest it ever draws. */
private val MinComposerHeight = 48.dp

/** Four lines of body text at 200% font scale, with room to spare. */
private val MaxPlausibleComposerHeight = 400.dp
