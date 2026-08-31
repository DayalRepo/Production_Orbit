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

    // There was an `Inactive = 0.65f` here, for a dimmed-but-tappable control. It went when
    // `OrbitButtonState.Inactive` did — see that enum for why the state itself was a bad idea. The
    // value is not worth keeping around for a future caller: any *new* use would be live UI, which
    // owes the full 4.5:1 rather than the 3:1 non-text floor, and that calculation depends on which
    // ink is being faded onto which surface. Whoever needs it next should redo it rather than
    // inherit a number tuned for a component that no longer exists.

    /** Pressed-state overlay. */
    const val Pressed = 0.12f

    /** Subtle fill for a container that should read as a surface tint rather than a colour. */
    const val SurfaceTint = 0.08f
}
