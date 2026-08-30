package com.orbitai.erp.core.designsystem.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Wraps preview content in [OrbitTheme] on a themed surface.
 *
 * Every component's `@Preview` goes through this so previews cannot accidentally render with
 * default Material styling and hide a missing token.
 */
@Composable
fun OrbitPreview(
    darkTheme: Boolean = false,
    padding: Int = 16,
    content: @Composable () -> Unit,
) {
    OrbitTheme(darkTheme = darkTheme) {
        Surface {
            Box(modifier = Modifier.padding(padding.dp)) {
                content()
            }
        }
    }
}
