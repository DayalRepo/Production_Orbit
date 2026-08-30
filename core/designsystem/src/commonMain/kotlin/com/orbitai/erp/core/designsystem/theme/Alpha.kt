package com.orbitai.erp.core.designsystem.theme

/**
 * Opacity tokens for component state.
 *
 * These apply to interactive state — a disabled button, an inactive tab — where the colour is
 * derived at draw time. Text and icon colours are not built this way: those are pre-composited flat
 * values in [OrbitContentColors], because compositing against an unknown background is exactly how
 * a label silently drifts below its contrast requirement.
 */
object OrbitAlpha {
    /** Disabled controls. WCAG 1.4.3 exempts them from the contrast minimum. */
    const val Disabled = 0.38f

    /**
     * Inactive but still interactive — an unselected tab icon, a stepped-back button.
     *
     * 0.65 rather than the more usual 0.60 because inactive content is live UI and owes the full
     * 4.5:1 for text, not just the 3:1 non-text floor. The binding case is the deep charcoal
     * `controlContent` on a white surface, which lands at 4.47:1 at 0.60 and 5.3:1 at 0.65 — so the
     * conventional value fails by a hair and this one passes with room. `ControlContrastTest` holds
     * the line.
     */
    const val Inactive = 0.65f

    /** Pressed-state overlay. */
    const val Pressed = 0.12f

    /** Subtle fill for a container that should read as a surface tint rather than a colour. */
    const val SurfaceTint = 0.08f
}
