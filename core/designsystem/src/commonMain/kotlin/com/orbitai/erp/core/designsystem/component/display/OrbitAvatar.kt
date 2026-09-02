package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitCircularPressIndication
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The five avatar tiers, from `User Profile Avatars (Android & iOS).xlsx`.
 *
 * The sheet gives different figures per platform for three of the five — iOS runs smaller at [Sm]
 * and [Md] and larger at [Lg] — so these resolve through `OrbitTheme.sizing` rather than carrying
 * their own dp values. Naming the tier by role instead of by size is what lets the two platforms
 * disagree without every call site knowing.
 */
enum class OrbitAvatarSize {
    /** Inline in comment threads, dense tables, secondary tags. */
    Xs,

    /** List rows, chat member pills, message sender icons. */
    Sm,

    /** Profile list cards and card headers. The default. */
    Md,

    /** Profile headers and settings hubs. */
    Lg,

    /** Dedicated profile and account detail screens. */
    Xl,
}

/**
 * A circular user avatar: a photo when there is one, initials when there is not.
 *
 * ### The rim is not decoration
 *
 * Avatars are the one image in the product whose content is arbitrary, and an arbitrary image has
 * arbitrary edges. A photo with a dark background bleeds into a dark container and a portrait shot
 * against a white wall bleeds into a light card, and in both cases the avatar stops reading as a
 * round object and starts reading as a shape cut out of the page. The hairline
 * (`contentColors.avatarBorder`) is what guarantees a closed circle regardless of what is inside it,
 * which is why it is always drawn rather than being an option.
 *
 * The glass treatment on top is a specular arc across the upper edge plus a contact shadow beneath —
 * the same two cues the icon button's ring uses. What it deliberately does *not* do is tint the
 * image: the rest of the glass stack draws a translucent fill, and a fill over a face shifts skin
 * tones. So the fill layer is skipped here and only the light and the shadow remain, which is enough
 * to read as a lens over the photo without altering the photo.
 *
 * ### Initials
 *
 * The fallback is a monogram on a neutral fill rather than a generic person glyph, because a column
 * of identical silhouettes in a task list tells the reader nothing while a column of monograms is
 * scannable. The fill stays achromatic — deriving a colour from the name is a common trick and it
 * quietly assigns people a colour they did not choose, which then has to mean nothing anywhere else
 * in a product where colour means something specific.
 *
 * ### Accessibility
 *
 * [contentDescription] is the person, not the picture: "Priya Sharma", not "Profile photo". Pass
 * `null` only when the avatar sits directly beside that person's name, where announcing it repeats
 * what the row already says.
 *
 * When [onClick] is set the whole avatar becomes a button and its hit area grows to
 * `sizing.minTouchTarget` without the circle growing with it, so an [Xs] avatar in a dense table is
 * still tappable. A clickable avatar with no description is an unlabelled button, so pass one.
 *
 * @param painter the photo. `null` falls back to [initials].
 * @param initials one or two characters. Longer strings are truncated rather than shrunk, since
 *   shrinking to fit is how a monogram becomes illegible at [Xs].
 */
@Composable
fun OrbitAvatar(
    contentDescription: String?,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    initials: String = "",
    size: OrbitAvatarSize = OrbitAvatarSize.Md,
    onClick: (() -> Unit)? = null,
) {
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val shape = CircleShape

    val diameter: Dp = when (size) {
        OrbitAvatarSize.Xs -> sizing.avatarXs
        OrbitAvatarSize.Sm -> sizing.avatarSm
        OrbitAvatarSize.Md -> sizing.avatarMd
        OrbitAvatarSize.Lg -> sizing.avatarLg
        OrbitAvatarSize.Xl -> sizing.avatarXl
    }

    val monogram = initials.take(2).uppercase()
    val label = when (size) {
        OrbitAvatarSize.Xs, OrbitAvatarSize.Sm -> OrbitTheme.typography.labelSmall
        OrbitAvatarSize.Md -> OrbitTheme.typography.labelLarge
        OrbitAvatarSize.Lg -> OrbitTheme.typography.titleMedium
        OrbitAvatarSize.Xl -> OrbitTheme.typography.headlineSmall
    }

    val interactionSource = remember { MutableInteractionSource() }

    // Read here rather than inside the draw lambda, which is not a composable scope.
    val highlight = OrbitGlass.RingHighlightLight * HighlightScale

    // The circle itself. Kept separate from the touch target below so that growing the target does
    // not grow the artwork — the same split the icon button uses.
    val circle = @Composable {
        Box(
            modifier = Modifier
                .size(diameter)
                .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
                .clip(shape)
                .background(control.cardContainer, shape)
                .then(
                    if (onClick != null) {
                        Modifier.indication(interactionSource, orbitCircularPressIndication())
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (painter != null) {
                Image(
                    painter = painter,
                    // The wrapper carries the description; a second one here would have screen
                    // readers announce the person twice.
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(diameter),
                )
            } else if (monogram.isNotEmpty()) {
                Text(
                    text = monogram,
                    style = label,
                    color = content.iconPrimary,
                    textAlign = TextAlign.Center,
                    // The monogram is a picture of the name, not a word. Read aloud it becomes "P S",
                    // which is noise on top of a description that already names the person.
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }

            // Light and edge, drawn over the photo rather than under it. `drawWithContent` puts both
            // above the image without a second Box, and the highlight is clipped to the circle by the
            // parent so it curves with the rim instead of cutting across it.
            Box(
                modifier = Modifier
                    .size(diameter)
                    .drawWithContent {
                        drawContent()
                        drawCircle(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = highlight),
                                    Color.White.copy(alpha = 0f),
                                ),
                                endY = this.size.height * HighlightFalloff,
                            ),
                        )
                    }
                    .border(sizing.avatarBorderWidth, content.avatarBorder, shape),
            )
        }
    }

    if (onClick == null) {
        Box(
            modifier = modifier.then(
                if (contentDescription == null) {
                    Modifier
                } else {
                    Modifier.semantics { this.contentDescription = contentDescription }
                },
            ),
        ) {
            circle()
        }
    } else {
        Box(
            modifier = modifier
                .size(maxOf(diameter, sizing.minTouchTarget))
                .clip(CircleShape)
                .orbitHandCursor()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics(mergeDescendants = true) {
                    if (contentDescription != null) this.contentDescription = contentDescription
                },
            contentAlignment = Alignment.Center,
        ) {
            circle()
        }
    }
}

/**
 * The avatar highlight as a fraction of the icon button's ring highlight.
 *
 * Applied to the light-theme value in both themes, which is the opposite of how the rest of the
 * glass stack works. Both facts follow from what is underneath: everywhere else the highlight lands
 * on a known token and can be tuned per theme, but here it lands on a photograph, and the theme says
 * nothing about those pixels — a dark portrait on a light page needs the same treatment as the same
 * portrait on a dark one. Half strength is what stays legible as light on a dark image without
 * washing out a pale one.
 */
private const val HighlightScale = 0.5f

/** How far down the circle the highlight has faded to nothing — an arc, not a wash. */
private const val HighlightFalloff = 0.45f
