package com.orbitai.erp.ui.ceo

import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.WindowSize
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import com.orbitai.erp.core.designsystem.theme.OrbitSpacing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CeoLayoutTest {

    private val spacing = OrbitSpacing()
    private val sizing = OrbitSizing()

    @Test
    fun compactPhoneUsesSingleColumnAndScreenHorizontal() {
        val metrics = ceoLayoutMetrics(
            spacing = spacing,
            sizing = sizing,
            window = WindowSize.of(width = 390.dp, height = 844.dp),
        )
        assertEquals(1, metrics.columns)
        assertEquals(spacing.screenHorizontal, metrics.horizontal)
        assertTrue(metrics.bottomContent > sizing.bottomNavHeight)
    }

    @Test
    fun mediumTabletUsesTwoColumns() {
        val metrics = ceoLayoutMetrics(
            spacing = spacing,
            sizing = sizing,
            window = WindowSize.of(width = 700.dp, height = 900.dp),
        )
        assertEquals(2, metrics.columns)
        assertEquals(spacing.xxl, metrics.horizontal)
    }

    @Test
    fun expandedUsesFourColumnsAndWiderInset() {
        val metrics = ceoLayoutMetrics(
            spacing = spacing,
            sizing = sizing,
            window = WindowSize.of(width = 1100.dp, height = 900.dp),
        )
        assertEquals(4, metrics.columns)
        assertEquals(spacing.xxxl, metrics.horizontal)
        assertEquals(sizing.maxContentWidth, metrics.maxContentWidth)
    }
}
