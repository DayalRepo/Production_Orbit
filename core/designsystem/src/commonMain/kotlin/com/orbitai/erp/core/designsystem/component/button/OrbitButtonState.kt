package com.orbitai.erp.core.designsystem.component.button

/**
 * What a control is currently able to do, in one value.
 *
 * This replaces a `Boolean` mainly so that call sites cannot pass an `enabled` flag *and* dim the
 * thing themselves, which is how two screens end up with different ideas of what an unavailable
 * button looks like.
 *
 * ### There used to be a third state
 *
 * `Inactive` sat between these two: interactive, but stepped back to `OrbitAlpha.Inactive` for a
 * control that works and just is not the point right now. It is gone, and the reason is worth
 * keeping.
 *
 * A dimmed-but-tappable button is a claim nobody can read. Sighted users cannot reliably tell it
 * from a disabled one — dimming is the universal visual language for "you cannot press this" — so
 * the state had to publish a `stateDescription` to be legible to a screen reader at all, meaning it
 * said one thing visually and another thing out loud. It also could not be dimmed honestly: the
 * control is live, so it still owes the full 4.5:1, which forced a per-variant rule about *which*
 * single layer was safe to fade. That is a lot of machinery for a distinction that never
 * corresponded to a real decision in this product.
 *
 * De-emphasis is a job for the variant. A supporting action should be `Secondary` or `Text` — which
 * says "less important" at full contrast, in a way that reads the same to everyone.
 */
enum class OrbitButtonState {
    /** Normal and interactive. */
    Active,

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
