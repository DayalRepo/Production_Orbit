package com.orbitai.erp.core.designsystem.component.display

import com.orbitai.erp.core.designsystem.theme.AndroidPlatformTokens
import com.orbitai.erp.core.designsystem.theme.IosPlatformTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The two things about a stacked avatar group that break silently.
 *
 * The monogram derivation is one of them: it is the fallback for members with no photo, and a bug
 * there does not crash or look broken — it just produces a row of avatars reading "PR", "PR", "PR"
 * for three people whose names happen to start the same way, which nobody notices until a user
 * cannot tell who is on their team.
 *
 * The overlap arithmetic is the other. It has to leave enough of each face visible to count, and the
 * failure is a stack that looks fine with two members and turns into a smear with five.
 */
class AvatarGroupTest {

    private fun monogram(name: String) = OrbitAvatarGroupMember(name).monogram

    @Test
    fun `a monogram is the initials of the first two words`() {
        assertEquals("PS", monogram("Priya Sharma"))
        assertEquals("RM", monogram("Ravi Menon"))
    }

    @Test
    fun `a single name gives a single letter rather than two of the same word`() {
        assertEquals("R", monogram("Ravi"))
    }

    @Test
    fun `a third name is ignored`() {
        assertEquals("AK", monogram("Arjun Kumar Pillai"))
    }

    @Test
    fun `surrounding and repeated whitespace does not become an initial`() {
        assertEquals("PS", monogram("  Priya   Sharma  "))
    }

    @Test
    fun `a lowercase name still gives an uppercase monogram`() {
        assertEquals("PS", monogram("priya sharma"))
    }

    /**
     * The overlap has to hide less than half of each avatar. Past halfway the covered face loses
     * one eye and the centre line, at which point the stack is concealing the identities it exists
     * to preview — and the ring, which is what separates two adjacent faces, starts eating into the
     * visible sliver rather than sitting between them.
     */
    @Test
    fun `the overlap leaves most of each face visible on both platforms`() {
        listOf(
            "Android" to AndroidPlatformTokens.sizing,
            "iOS" to IosPlatformTokens.sizing,
        ).forEach { (platform, sizing) ->
            val overlap = sizing.avatarStackOverlap
            assertTrue(
                overlap in 0.2f..0.45f,
                "$platform overlap is $overlap; outside 0.2..0.45 the stack reads as either a " +
                    "crowded row or an unreadable smear",
            )
        }
    }

    /**
     * The separating ring has to be thicker than the avatar's own hairline. They are drawn in
     * different colours for different reasons — the hairline closes the circle against the page, the
     * ring holds two faces apart — and if the ring ever collapses to a hairline the second job stops
     * being done at arm's length.
     */
    @Test
    fun `the stack ring is heavier than the avatar hairline`() {
        listOf(
            "Android" to AndroidPlatformTokens.sizing,
            "iOS" to IosPlatformTokens.sizing,
        ).forEach { (platform, sizing) ->
            assertTrue(
                sizing.avatarStackRing > sizing.avatarBorderWidth,
                "$platform stack ring ${sizing.avatarStackRing} is no heavier than the " +
                    "avatar hairline ${sizing.avatarBorderWidth}",
            )
        }
    }
}
