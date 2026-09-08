package com.orbitai.erp.ui.ceo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** CEO notifications — blank shell until threads land. */
@Composable
fun CeoMessagesScreen(modifier: Modifier = Modifier) {
    CeoScreenScaffold(
        modifier = modifier,
        header = {
            CeoScreenHeader(title = "Notifications")
        },
    ) {}
}
