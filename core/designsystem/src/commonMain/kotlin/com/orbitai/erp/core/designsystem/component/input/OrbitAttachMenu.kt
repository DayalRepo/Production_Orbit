package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The composer's attach menu: a small panel of ways to add something to a message.
 *
 * ### Why a Popup and not a Column in the layout
 *
 * A menu drawn inline has to push the composer — and everything above it — out of the way, which
 * moves the text you were writing while you decide what to attach. A [Popup] draws in its own window
 * above everything, so opening the menu changes nothing underneath it. It is also what gives us
 * outside-tap dismissal for free and correctly: `onDismissRequest` fires for a tap anywhere outside
 * the panel *and* for the Android system back gesture, which is the same intent expressed two ways
 * and would otherwise need two separate handlers, one of which always gets forgotten.
 *
 * ### Dismissal has three routes, on purpose
 *
 * Tap outside, press back, or hit the close button. The explicit close button is not redundant with
 * the other two: outside-tap is undiscoverable, and back is a system gesture that a user in the
 * middle of composing may reasonably fear will discard their draft. A visible X is the one exit that
 * looks safe, and on a menu attached to a half-written message that matters more than the pixels it
 * costs.
 *
 * ### It grows from the button
 *
 * The panel scales up from its bottom-left corner, which is where the plus sits. A menu that fades
 * in centred looks like it arrived from nowhere; one that grows from the control that opened it
 * carries the causal link, which is the whole reason the animation is there. It is short — long
 * enough to be seen as motion, short enough not to be waited on.
 *
 * @param expanded whether the panel is showing. Hoisted, because the plus button that toggles it
 *   lives in the composer and needs to render its own selected state from the same flag.
 * @param items what the menu offers. A list rather than fixed image/file parameters so a screen can
 *   drop the camera on a desktop build, or add "Scan document" later, without this component
 *   growing a boolean per option.
 */
@Composable
fun OrbitAttachMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    items: List<OrbitAttachOption>,
    modifier: Modifier = Modifier,
) {
    if (!expanded) return

    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.card

    Popup(
        popupPositionProvider = AboveAnchorStart,
        onDismissRequest = onDismiss,
        // Focusable, so the platform routes the back gesture here rather than to the screen behind.
        // Without it, back closes the whole screen while a menu is open, which loses the draft.
        properties = androidx.compose.ui.window.PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(OpenMs)) +
                scaleIn(tween(OpenMs), initialScale = 0.92f, transformOrigin = FromPlus),
            exit = fadeOut(tween(OpenMs)) + scaleOut(tween(OpenMs), transformOrigin = FromPlus),
        ) {
            Column(
                modifier = modifier
                    // Bounded at both ends, and the maximum is the load-bearing half.
                    //
                    // A `Popup` hands its content the whole window as the maximum constraint, and
                    // the header and the rows inside here both use `fillMaxWidth` to put the close
                    // button at one end and to make each row tappable across its full width. Given
                    // an unbounded maximum, `fillMaxWidth` means *the window*, so the panel
                    // stretched edge to edge — a menu with its rounded corners off both sides of the
                    // screen, reading as a misplaced sheet rather than a dropdown. The minimum stops
                    // a one-word menu from being a sliver.
                    //
                    // `IntrinsicSize.Max` on top of that is what makes the panel *hug its longest
                    // row* rather than simply sitting at the maximum. The clamp alone was enough to
                    // stop the edge-to-edge stretch, but every menu then came out exactly
                    // `menuMaxWidth` wide regardless of its contents, because `fillMaxWidth` takes
                    // whatever bound it is given. Measuring the column at its children's maximum
                    // intrinsic width resolves `fillMaxWidth` against the widest label instead, so
                    // the rows still span the panel for tapping while the panel itself is only as
                    // wide as "Upload image" plus its padding.
                    .width(IntrinsicSize.Max)
                    .widthIn(min = sizing.menuMinWidth, max = sizing.menuMaxWidth)
                    .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
                    .orbitGlass(
                        fill = control.cardContainer,
                        shape = shape,
                        highlightAlpha = if (OrbitTheme.isDark) {
                            OrbitGlass.SurfaceHighlightDark
                        } else {
                            OrbitGlass.SurfaceHighlightLight
                        },
                        edge = control.controlBorder,
                        edgeWidth = sizing.hairline,
                    )
                    .padding(vertical = spacing.xs),
            ) {
                // Close button alone on the top row, hard right. The title that used to sit beside
                // it was removed: a two-item menu whose rows say "Upload image" and "Attach file"
                // does not need a heading saying "Attach", and the heading was the widest thing in
                // the panel — it was setting the menu's width while carrying no information.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.xxs),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OrbitIconButton(
                        contentDescription = "Close attach menu",
                        onClick = onDismiss,
                        icon = OrbitIcons.Cancel,
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Small,
                    )
                }

                // Full-bleed under the close button, so it reads as separating the panel's chrome
                // from its contents.
                OrbitDivider()

                items.forEachIndexed { index, item ->
                    if (index > 0) {
                        // Inset, unlike the one above. A rule that stops short of both edges says
                        // "these are two items in one list"; a full-bleed one says "these are two
                        // different sections", which would be wrong for two ways of doing the same
                        // thing.
                        OrbitDivider(modifier = Modifier.padding(horizontal = spacing.md))
                    }
                    OrbitAttachMenuRow(item = item, onDismiss = onDismiss)
                }
            }
        }
    }
}

/** One row in the attach menu. */
@androidx.compose.runtime.Immutable
data class OrbitAttachOption(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
private fun OrbitAttachMenuRow(item: OrbitAttachOption, onDismiss: () -> Unit) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    // Choosing an option closes the menu. A picker is about to cover the screen
                    // anyway, and leaving the panel open behind it means it is still there when the
                    // user cancels out of the picker — which reads as the cancel not having worked.
                    item.onClick()
                    onDismiss()
                },
            )
            .indication(interactionSource, orbitPressIndication())
            .padding(horizontal = spacing.md)
            .semantics(mergeDescendants = true) { contentDescription = item.label },
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrbitGlyph(
            // A step up from `iconSm`, to match the heavier label beside it. A 16dp glyph next to
            // 17sp Medium text reads as a bullet point rather than as the row's icon.
            icon = item.icon,
            size = sizing.iconMd,
            tint = content.iconPrimary,
            contentDescription = null,
        )
        Text(
            text = item.label,
            // Larger and heavier than a list row elsewhere. A menu item is a *command* — it is read
            // once, under time pressure, with a thumb already moving toward it — so it wants the
            // weight of a button label rather than the weight of body copy. At bodyMedium regular
            // the two options read as captions and the panel looked like a tooltip.
            style = OrbitTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = content.textPrimary,
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

/**
 * Puts the panel above the anchor, left edges aligned, and flips it below if there is no room.
 *
 * Above by default because the composer lives at the bottom of a chat screen with a keyboard under
 * it; a menu below would open into the keyboard. The flip is not a nicety — in landscape on a short
 * screen there genuinely is not room above, and without it the panel is drawn off the top of the
 * window and simply is not there.
 */
private object AboveAnchorStart : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: androidx.compose.ui.unit.IntRect,
        windowSize: androidx.compose.ui.unit.IntSize,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        popupContentSize: androidx.compose.ui.unit.IntSize,
    ): IntOffset {
        val above = anchorBounds.top - popupContentSize.height
        val y = if (above >= 0) above else anchorBounds.bottom
        val x = anchorBounds.left.coerceAtMost(windowSize.width - popupContentSize.width).coerceAtLeast(0)
        return IntOffset(x, y.coerceAtLeast(0))
    }
}

/** Bottom-left: the corner the plus button sits at. */
private val FromPlus = TransformOrigin(0f, 1f)

private const val OpenMs = 120
