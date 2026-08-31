package com.orbitai.erp.core.designsystem.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The pane every Orbit dialog is drawn on: glass panel, title row with a close button, content,
 * actions.
 *
 * ### Why not `AlertDialog`
 *
 * Material's dialog brings its own container colour, its own shape, its own typography and its own
 * button styling, and overriding all four leaves a component that is Material in structure and Orbit
 * in appearance — which is exactly the arrangement that breaks on the next Material release. The
 * platform [Dialog] wrapper is kept, because that is the part that is genuinely hard: it owns the
 * scrim, the window, focus capture and the back gesture.
 *
 * ### Glass here, unlike fields
 *
 * Input fields opt out of `orbitGlass` because a white wash over small dark text costs contrast on
 * the one surface whose whole job is to disappear behind its own text. A dialog is the opposite
 * case: it is an object floating over a dimmed screen, it *should* read as a distinct pane of
 * material, and the highlight along its top edge is what sells that. The surface highlight is used
 * rather than the ring's, for the same reason cards use it — this is a wide panel where the wash
 * falls as a gradient along a long edge.
 *
 * ### Dismissal, and the one case where it is taken away
 *
 * By default a tap on the scrim or a press of back closes the dialog, because a modal you cannot
 * escape from is a trap and users reach for those two exits before they read anything. [dismissible]
 * turns both off, and it exists for the narrow case of a destructive confirmation where an
 * accidental scrim tap would be read as an answer. Even then the close button stays: there must
 * always be at least one visible way out.
 *
 * @param onDismiss called for every route out — scrim, back, and the close button. Treat it as "the
 *   user declined", never as a confirmation.
 * @param title required. A dialog without a title is a dialog whose purpose has to be inferred from
 *   its buttons, and it is also what a screen reader announces on arrival.
 * @param actions the buttons, laid out end-aligned in a row. Left to the caller rather than
 *   generated from labels, so a confirmation and a rename can use the same shell without this
 *   component growing a parameter per button.
 */
@Composable
fun OrbitDialog(
    onDismiss: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    dismissible: Boolean = true,
    content: (@Composable ColumnScopeMarker.() -> Unit)? = null,
    actions: @Composable () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val contentColors = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.card

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible,
            // The platform's own width clamp is turned off so the panel can size itself between
            // `menuMinWidth` and `dialogMaxWidth`. Left on, it forces a fixed proportion of the
            // screen, which on a tablet is a very long single line of text.
            usePlatformDefaultWidth = false,
        ),
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = sizing.dialogEdgeInset)
                .widthIn(max = sizing.dialogMaxWidth)
                .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
                .orbitGlass(
                    fill = control.cardContainer,
                    shape = shape,
                    highlightAlpha = if (OrbitTheme.isDark) {
                        OrbitGlass.SurfaceHighlightDark
                    } else {
                        OrbitGlass.SurfaceHighlightLight
                    },
                    // A stronger rim on light than `controlBorder` gives, and this is the one
                    // surface that needs it. The light theme runs from #FFFFFF to #F5F5F5, so a
                    // white dialog over a white page has almost no fill difference to separate it —
                    // the shadow does the work, and a shadow alone leaves the panel's own edge
                    // undefined, so the dialog appeared to bleed into the dimmed page behind it
                    // rather than sitting on top as a distinct object. Dark does not have the
                    // problem: a near-black panel against a dimmed near-black page is separated by
                    // the highlight along its top edge, and adding a hard rim there just outlines
                    // it. Same value the info popover uses, for the same reason.
                    edge = if (OrbitTheme.isDark) {
                        control.controlBorder
                    } else {
                        contentColors.textPrimary.copy(alpha = LightEdgeAlpha)
                    },
                    edgeWidth = sizing.hairline,
                )
                .padding(vertical = spacing.sm),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = spacing.lg, end = spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    // A step up from the small tier it used to take. A dialog title competes with
                    // nothing — it is the top line of a panel that has taken over the screen — and at
                    // the smaller size it sat level with the guidance text beneath it, so the panel
                    // opened with no clear entry point.
                    style = OrbitTheme.typography.titleMedium,
                    // Medium rather than the tier's default. Bold at this size reads as an alert,
                    // which is the wrong register for a dialog that is only asking for a name.
                    fontWeight = FontWeight.Medium,
                    color = contentColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                OrbitIconButton(
                    contentDescription = "Close",
                    onClick = onDismiss,
                    icon = OrbitIcons.Cancel,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Small,
                )
            }

            OrbitDivider(modifier = Modifier.padding(vertical = spacing.sm))

            if (content != null) {
                Column(
                    modifier = Modifier.padding(horizontal = spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    ColumnScopeMarker.content()
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(spacing.xs))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.xs),
                // End-aligned, and the affirmative goes last. On both platforms the rightmost
                // button is where the thumb rests and where "proceed" is expected; putting the
                // destructive answer under the resting thumb is how people confirm things they
                // meant to cancel.
                horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions()
            }
        }
    }
}

/**
 * The rim alpha a light-theme dialog draws over its own primary text colour.
 *
 * Low enough to read as an edge rather than an outline, high enough to separate a white panel from a
 * white page once the scrim has dimmed it. Shared with the info popover, which has the same problem.
 */
private const val LightEdgeAlpha = 0.14f

/**
 * A marker receiver for dialog content.
 *
 * Exists only so a content lambda cannot accidentally capture the outer `Column`'s scope and call
 * `weight` on itself, which compiles and then silently collapses the dialog to nothing at runtime.
 */
object ColumnScopeMarker
