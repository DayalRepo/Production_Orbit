package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.launch

/**
 * The press effect that matches the platform the app is actually running on.
 *
 * These two platforms disagree about what a press looks like, and picking one loses either way.
 * Material's ripple spreading from the contact point is the Android idiom, and its absence reads as
 * an unresponsive button; on iOS a ripple immediately marks the app as a port, because UIKit
 * buttons respond by shrinking slightly and never emit anything from the touch point.
 *
 * So: ripple on Android, [OrbitPressIndication] on iOS.
 */
@Composable
internal fun orbitPressIndication(): Indication = when (currentPlatform) {
    OrbitPlatform.Android -> ripple()
    OrbitPlatform.Ios -> OrbitPressIndication
}

/**
 * A UIKit-style press: shrink to [PressedScale] on a short curve, quicker down than up.
 *
 * Implemented as an [IndicationNodeFactory] rather than by hoisting `isPressed` into the caller and
 * animating a scale there. That would put the animation in the button's own layout, so every press
 * would re-measure it and shove its neighbours around; done here it is a draw-time transform, which
 * moves nothing.
 */
internal object OrbitPressIndication : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        PressNode(interactionSource)

    // Indication instances are compared to decide whether the node needs recreating. This one is a
    // stateless singleton, so identity is the correct answer.
    override fun hashCode(): Int = OrbitPressIndication::class.hashCode()
    override fun equals(other: Any?): Boolean = other === this

    private class PressNode(
        private val interactionSource: InteractionSource,
    ) : Modifier.Node(), DrawModifierNode {

        private val press = Animatable(0f)

        override fun onAttach() {
            coroutineScope.launch {
                // Presses can overlap — a second finger down before the first is released — so
                // depth is a count rather than a flag. Lifting one of two touches should not pop
                // the button back up.
                var depth = 0
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> depth++
                        is PressInteraction.Release, is PressInteraction.Cancel -> depth--
                        else -> return@collect
                    }
                    val target = if (depth > 0) 1f else 0f
                    if (press.targetValue != target) {
                        press.animateTo(target, tween(if (target == 1f) 90 else 160))
                    }
                }
            }
        }

        override fun ContentDrawScope.draw() {
            val t = press.value
            if (t == 0f) {
                drawContent()
            } else {
                scale(1f - (1f - PressedScale) * t) { this@draw.drawContent() }
            }
        }
    }

    private const val PressedScale = 0.97f
}

/**
 * A hand cursor over anything clickable.
 *
 * Inert on a touch screen, where no cursor exists, so this is safe to apply unconditionally. It
 * earns its keep wherever a pointer is present: a tablet or foldable with a trackpad, a ChromeOS
 * window, an Android device in desktop mode, and a desktop target if one is added. Hover itself is
 * handled by the button, which lifts its glass highlight rather than washing a colour over itself.
 */
internal fun Modifier.orbitHandCursor(): Modifier = pointerHoverIcon(PointerIcon.Hand)
