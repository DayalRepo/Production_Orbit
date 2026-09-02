package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/** iOS picker wiring lands with screen development; gallery uses the Android path today. */
@Composable
actual fun rememberFilePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit = { onPicked(null) }
