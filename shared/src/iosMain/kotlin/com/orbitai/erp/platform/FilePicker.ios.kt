package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/**
 * iOS picker wiring lands with screen development; gallery uses the Android path today.
 * Info.plist carries photo-library and document usage strings for when pickers ship.
 */
@Composable
actual fun rememberImagePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit = { onPicked(null) }

@Composable
actual fun rememberDocumentPicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit = { onPicked(null) }

@Composable
actual fun rememberFilePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit = rememberDocumentPicker(onPicked)
