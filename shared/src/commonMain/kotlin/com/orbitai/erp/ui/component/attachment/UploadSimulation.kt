package com.orbitai.erp.ui.component.attachment

import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

/**
 * How long a simulated upload should take. Small files finish quickly; larger ones scale up but
 * cap so the gallery does not stall on a 50 MB pick.
 */
internal fun uploadDurationMs(sizeBytes: Long): Long {
    val kb = sizeBytes / 1024
    return when {
        kb <= 512 -> 280L
        kb <= 2_048 -> 450L
        kb <= 10_240 -> 900L
        else -> min(4_500L, max(1_200L, sizeBytes / (768 * 1024)))
    }
}

internal suspend fun runUploadSimulation(
    totalBytes: Long,
    isCancelled: () -> Boolean,
    onProgress: (uploadedBytes: Long, fraction: Float) -> Unit,
): Boolean {
    if (totalBytes <= 0L) {
        onProgress(0L, 1f)
        return true
    }
    val duration = uploadDurationMs(totalBytes)
    val steps = (duration / StepMs).toInt().coerceIn(6, 40)
    val stepDelay = duration / steps
    for (step in 1..steps) {
        if (isCancelled()) return false
        delay(stepDelay)
        val fraction = step.toFloat() / steps
        val uploaded = (totalBytes * fraction).toLong().coerceAtMost(totalBytes)
        onProgress(uploaded, fraction)
    }
    onProgress(totalBytes, 1f)
    return true
}

internal fun formatByteProgress(uploadedBytes: Long, totalBytes: Long): String =
    "${formatBytes(uploadedBytes)} of ${formatBytes(totalBytes)}"

internal fun formatBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    else -> {
        val tenths = (bytes * 10f / (1024f * 1024f)).toInt()
        "${tenths / 10}.${tenths % 10} MB"
    }
}

private const val StepMs = 16L
