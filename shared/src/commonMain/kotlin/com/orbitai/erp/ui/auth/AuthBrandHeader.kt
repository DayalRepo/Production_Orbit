package com.orbitai.erp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.brand.OrbitLauncherIconDefaults
import com.orbitai.erp.core.designsystem.component.brand.OrbitMark
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Shared auth header: brand mark without plate + screen title.
 */
@Composable
fun AuthBrandHeader(
    title: String,
    modifier: Modifier = Modifier,
    brandSize: Dp = AuthBrandDefaults.BrandSize,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val markColor = if (OrbitTheme.isDark) {
        OrbitLauncherIconDefaults.DarkMark
    } else {
        OrbitLauncherIconDefaults.LightMark
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        OrbitMark(
            size = brandSize,
            color = markColor,
            contentDescription = "Orbit.ai",
            modifier = Modifier.size(brandSize),
        )
        Text(
            text = title.uppercase(),
            style = OrbitTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = content.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

object AuthBrandDefaults {
    val BrandSize = 56.dp
    val FormMaxWidth = 360.dp
}
