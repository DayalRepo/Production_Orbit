package com.orbitai.erp.core.designsystem.component.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class OrbitMarkdownTest {

    @Test
    fun `parses headings bullets and a table`() {
        val blocks = parseOrbitMarkdown(
            """
                # Tower A
                ## Sequence
                Hold the **pump**.

                - Move Friday
                - Confirm crane

                | Stage | Crew |
                | --- | --- |
                | Pour | Crew C |
            """.trimIndent(),
        )

        assertEquals(6, blocks.size)
        assertIs<OrbitMarkdownBlock.Heading>(blocks[0]).also {
            assertEquals(1, it.level)
            assertEquals("Tower A", it.text)
        }
        assertIs<OrbitMarkdownBlock.Heading>(blocks[1]).also {
            assertEquals(2, it.level)
        }
        assertIs<OrbitMarkdownBlock.Paragraph>(blocks[2])
        assertIs<OrbitMarkdownBlock.Bullet>(blocks[3])
        assertIs<OrbitMarkdownBlock.Bullet>(blocks[4])
        val table = assertIs<OrbitMarkdownBlock.Table>(blocks[5])
        assertEquals(listOf("Stage", "Crew"), table.headers)
        assertEquals(listOf("Pour", "Crew C"), table.rows.single())
    }

    @Test
    fun `plain text drops markup`() {
        val plain = orbitMarkdownPlainText("Hold the **pump** and *confirm* Priya.")
        assertTrue(plain.contains("Hold the"))
        assertTrue(plain.contains("pump"))
        assertTrue(!plain.contains("**"))
    }
}
