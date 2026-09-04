package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class OrbitShapeNestedTest {

    @Test
    fun `nested corner subtracts the delta and floors at two`() {
        val tokens = OrbitShapeTokens()
        assertEquals(4.dp, tokens.nestedDelta)
        assertEquals(6.dp, tokens.nestedCorner(10.dp))
        assertEquals(4.dp, tokens.nestedCorner(8.dp))
        assertEquals(2.dp, tokens.nestedCorner(4.dp))
        assertEquals(2.dp, tokens.nestedCorner(2.dp))
    }
}
