package com.orbitai.erp.core.designsystem.component.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The dropdown's search, checked against the wording it actually has to cope with.
 *
 * Every fixture here is a real stage name from the project's work sequence. That is the point of the
 * file: a filter tested against "apple, banana, cherry" passes trivially and tells you nothing about
 * whether somebody can find "Toilet Dado Tiling" by typing what they remember of it.
 */
class DropdownFilterTest {

    private val stages = listOf(
        "Block Work",
        "Toilet Dado Tiling",
        "Internal Other Area Plastering (Staircase except Lift Door Wall)",
        "2 Coats - Internal Wall Putty",
        "Slab Concreting",
    )

    @Test
    fun `an empty query is not a filter`() {
        // Distinct from "a query that matches everything". Opening the panel must show the whole
        // list, not run a match that happens to be total.
        assertEquals(stages, stages.filterByQuery(""))
    }

    @Test
    fun `whitespace alone is still an empty query`() {
        // A space is what you get from a fat-fingered tap or a phone keyboard's auto-space after a
        // deleted word, and it must not empty the list.
        assertEquals(stages, stages.filterByQuery("   "))
    }

    @Test
    fun `a word from the middle of a name finds it`() {
        // The whole reason this is a substring match and not a prefix one. Nobody looking for the
        // staircase plastering stage starts typing "internal".
        assertEquals(
            listOf("Internal Other Area Plastering (Staircase except Lift Door Wall)"),
            stages.filterByQuery("staircase"),
        )
    }

    @Test
    fun `case does not matter in either direction`() {
        // Site users type in lower case; the source document is title case with the odd all-caps
        // abbreviation. Neither side can be relied on.
        assertEquals(stages.filterByQuery("TILING"), stages.filterByQuery("tiling"))
        assertTrue(stages.filterByQuery("BLOCK WORK").isNotEmpty())
    }

    @Test
    fun `a query is trimmed before it is matched`() {
        // Trailing spaces arrive constantly from keyboard autocomplete, and a filter that returns
        // nothing because of one is indistinguishable from a stage that does not exist.
        assertEquals(stages.filterByQuery("tiling"), stages.filterByQuery("  tiling "))
    }

    @Test
    fun `a query that matches nothing gives an empty list rather than everything`() {
        // The failure mode worth guarding: a filter that silently falls back to the full list on no
        // match looks like it is broken in the other direction, and the user concludes search does
        // not work.
        assertTrue(stages.filterByQuery("scaffolding").isEmpty())
    }

    @Test
    fun `punctuation and digits in a name are searchable`() {
        // "2 Coats - Internal Wall Putty" begins with a digit and carries a dash. Both are things a
        // naive tokenising filter would drop.
        assertEquals(listOf("2 Coats - Internal Wall Putty"), stages.filterByQuery("2 coats"))
    }

    @Test
    fun `matches keep the order the list was given in`() {
        // Sequence order is meaningful here — these are stages of a build. A filter that reordered
        // by relevance would present them out of the order the work happens in.
        val matches = stages.filterByQuery("in")
        assertEquals(matches, stages.filter { it in matches })
    }
}
