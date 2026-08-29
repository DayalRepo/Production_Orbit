package com.orbitai.erp.core.designsystem.foundation

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowSizeTest {

    @Test
    fun classifiesWidthAtBreakpoints() {
        assertEquals(WindowWidthClass.Compact, WindowSize.of(360.dp, 800.dp).widthClass)
        assertEquals(WindowWidthClass.Compact, WindowSize.of(599.dp, 800.dp).widthClass)
        assertEquals(WindowWidthClass.Medium, WindowSize.of(600.dp, 800.dp).widthClass)
        assertEquals(WindowWidthClass.Medium, WindowSize.of(839.dp, 800.dp).widthClass)
        assertEquals(WindowWidthClass.Expanded, WindowSize.of(840.dp, 800.dp).widthClass)
    }

    @Test
    fun navigationLayoutFollowsWidthClass() {
        assertEquals(NavigationLayout.BottomBar, WindowSize.of(360.dp, 800.dp).navigationLayout)
        assertEquals(NavigationLayout.Rail, WindowSize.of(700.dp, 800.dp).navigationLayout)
        assertEquals(
            NavigationLayout.PermanentDrawer,
            WindowSize.of(1280.dp, 800.dp).navigationLayout,
        )
    }

    @Test
    fun twoPaneRequiresMoreThanCompactWidth() {
        assertFalse(WindowSize.of(360.dp, 800.dp).supportsTwoPane)
        assertTrue(WindowSize.of(700.dp, 800.dp).supportsTwoPane)
        assertTrue(WindowSize.of(1280.dp, 800.dp).supportsTwoPane)
    }

    @Test
    fun dashboardColumnsScaleWithWidth() {
        assertEquals(1, WindowSize.of(360.dp, 800.dp).dashboardColumns)
        assertEquals(2, WindowSize.of(700.dp, 800.dp).dashboardColumns)
        assertEquals(4, WindowSize.of(1280.dp, 800.dp).dashboardColumns)
    }

    @Test
    fun classifiesHeightAtBreakpoints() {
        assertEquals(WindowHeightClass.Compact, WindowSize.of(800.dp, 400.dp).heightClass)
        assertEquals(WindowHeightClass.Medium, WindowSize.of(800.dp, 600.dp).heightClass)
        assertEquals(WindowHeightClass.Expanded, WindowSize.of(800.dp, 1000.dp).heightClass)
    }
}
