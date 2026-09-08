package com.orbitai.erp.ui.ceo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** CEO projects list — blank shell until project cards land. */
@Composable
fun CeoProjectsScreen(modifier: Modifier = Modifier) {
    CeoScreenScaffold(
        modifier = modifier,
        header = {
            CeoScreenHeader(title = "Projects")
        },
    ) {}
}
