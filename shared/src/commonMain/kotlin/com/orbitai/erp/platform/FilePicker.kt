package com.orbitai.erp.platform

import androidx.compose.runtime.Composable

/**
 * Opens the device photo gallery. Requests platform permission first when required.
 * [onPicked] receives `null` when the user backs out or permission is denied.
 */
@Composable
expect fun rememberImagePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit

/**
 * Opens the native document chooser. Requests platform permission first when required.
 * [onPicked] receives `null` when the user backs out or permission is denied.
 */
@Composable
expect fun rememberDocumentPicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit

/**
 * Opens the platform document chooser — prefer [rememberImagePicker] or [rememberDocumentPicker]
 * when the source matters.
 */
@Composable
expect fun rememberFilePicker(
    onPicked: (PickedFile?) -> Unit,
): () -> Unit
