package com.orbitai.erp.platform

import androidx.compose.runtime.Immutable

/**
 * A file the user chose from the device. Metadata only — bytes stay on disk until an upload layer
 * streams them.
 */
@Immutable
data class PickedFile(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    /** Set for image picks on platforms that expose a content URI for preview. */
    val previewUri: String? = null,
)
