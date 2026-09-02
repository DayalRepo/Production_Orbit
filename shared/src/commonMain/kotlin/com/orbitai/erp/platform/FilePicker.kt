package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/**
 * Opens the platform file chooser. The returned lambda launches the picker; [onPicked] receives
 * `null` when the user backs out.
 */
@Composable
expect fun rememberFilePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit
