package com.orbitai.erp.ui.form.page

/**
 * Sequential issue photo name: `issueimage1.jpg`, `issueimage2.png`, …
 *
 * The extension follows the source file when it is a known image type; camera captures default
 * to jpg. Existing names are skipped so a removed `issueimage1` does not reuse the number while
 * later files are still on the form.
 */
internal fun nextIssueImageName(
    existingNames: Collection<String>,
    sourceName: String,
): String {
    val ext = when (sourceName.substringAfterLast('.', missingDelimiterValue = "").lowercase()) {
        "png" -> "png"
        "heic" -> "heic"
        "webp" -> "webp"
        else -> "jpg"
    }
    val used = existingNames.map { it.substringBeforeLast('.').lowercase() }.toSet()
    var index = 1
    while ("issueimage$index" in used) {
        index += 1
    }
    return "issueimage$index.$ext"
}
