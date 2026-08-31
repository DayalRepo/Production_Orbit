package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The fixed top of a dropdown: a filter, a way to add what is missing, and a rule under both.
 *
 * ### Search and "add" belong together, in that order
 *
 * They are the two answers to the same moment — the user is looking for something and cannot see it.
 * Search is tried first and works almost always, so it is first; the add row is the fallback and
 * sits directly beneath, which means the user finds it at exactly the point where searching has
 * failed. Separating them, or putting the add row at the bottom of the list, breaks that sequence
 * and leaves the fallback somewhere the user has no reason to look.
 *
 * Both are pinned by [OrbitDropdownMenu] rather than scrolled. See there for why a filter that
 * scrolls out of view is worse than no filter.
 *
 * ### The rule is full-bleed and the ones between options are not
 *
 * This one separates two kinds of thing — controls above, values below — so it runs edge to edge and
 * reads as a structural boundary. The rules between options separate items of the same kind, so they
 * are inset from both edges and read as a lighter, list-internal separator. Using the same rule for
 * both would flatten a real distinction into decoration.
 *
 * @param query the current filter text. Owned by the caller, because the caller also owns the
 *   filtered list and the two must not be able to disagree.
 * @param onAdd null to leave the add row out entirely — a dropdown over a fixed vocabulary should
 *   not offer to extend it.
 */
@Composable
internal fun OrbitDropdownHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    searchPlaceholder: String,
    modifier: Modifier = Modifier,
    addLabel: String? = null,
    onAdd: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing

    OrbitSearchField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = searchPlaceholder,
        label = searchPlaceholder,
        // The smallest field size. This is a tool inside a panel, not a field on a form, and at the
        // panel's own field size it would be as tall as the control that opened it — which makes the
        // dropdown look like it contains a second form rather than a filter.
        size = OrbitFieldSize.Small,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.sm, vertical = spacing.xxs),
    )

    if (addLabel != null && onAdd != null) {
        OrbitDropdownAddRow(label = addLabel, onClick = onAdd)
    }

    OrbitDivider()
}
