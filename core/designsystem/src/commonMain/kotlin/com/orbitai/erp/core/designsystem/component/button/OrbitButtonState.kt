package com.orbitai.erp.core.designsystem.component.button

/**
 * What a control is currently able to do, in one value.
 *
 * This replaces a `Boolean` for a reason. Two of these states are interactive and look different,
 * so a boolean forced call sites to pass an `enabled` flag *and* dim the thing themselves, which is
 * how two screens end up with different ideas of what a de-emphasised button looks like.
 *
 * The distinction that matters is [Inactive] versus [Disabled]. They are not the same claim: one
 * says "this works, it just isn't the point right now", the other says "this does not work". Using
 * Disabled for the first is the more common mistake and the more expensive one, because a disabled
 * control gives the user nothing to act on and no reason why.
 */
enum class OrbitButtonState {
    /** Normal and interactive. */
    Active,

    /**
     * Interactive but stepped back, at `OrbitAlpha.Inactive`.
     *
     * For a control that is available but not the current focus — an unselected view toggle, a
     * secondary action in a row that already has a primary one.
     */
    Inactive,

    /**
     * Not interactive, at `OrbitAlpha.Disabled`.
     *
     * Screen readers announce this, so it does not need to be spelled out in the label. It does
     * need a reason available elsewhere on screen; a dead control with no explanation is a dead end.
     */
    Disabled,
    ;

    internal val interactive: Boolean get() = this != Disabled
}
