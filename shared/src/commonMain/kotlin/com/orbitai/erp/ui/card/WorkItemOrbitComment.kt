package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.orbitSuggestedUnitForMaterial
import kotlin.random.Random

enum class WorkItemMention(val token: String, val label: String) {
    Messages("Messages", "Messages"),
    RaiseIssue("RaiseIssue", "Raise issue"),
    Material("Material", "Get material"),
    ;

    val insert: String get() = "@$token "
}

fun parseWorkItemMentions(text: String): Set<WorkItemMention> {
    val tokens = Regex("""@([A-Za-z]+)""").findAll(text).map { it.groupValues[1] }
    return WorkItemMention.entries.filter { mention ->
        tokens.any { it.equals(mention.token, ignoreCase = true) }
    }.toSet()
}

fun materialNameFromMention(text: String): String? {
    val after = Regex("""@Material\s+(.+)""", RegexOption.IGNORE_CASE)
        .find(text)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
        .orEmpty()
    return after.takeIf { it.isNotBlank() }?.substringBefore(" @")?.trim()
}

fun issueNoteFromMention(text: String): String {
    val after = Regex("""@RaiseIssue\s*(.*)""", RegexOption.IGNORE_CASE)
        .find(text)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
        .orEmpty()
    return after.ifBlank { "Site issue on ${text.take(48)}" }
}

fun orbitAiReply(text: String, issueNumber: String? = null, materialName: String? = null): String {
    val mentions = parseWorkItemMentions(text)
    return when {
        WorkItemMention.RaiseIssue in mentions && issueNumber != null ->
            "Raised $issueNumber. I'll track it against this task and notify QA."
        WorkItemMention.Material in mentions && materialName != null ->
            "Logged $materialName on the materials log so the store can issue it."
        WorkItemMention.Material in mentions ->
            "Tell me the material name after @Material and I will add it to the log."
        WorkItemMention.Messages in mentions ->
            "Noted. I have the update and will keep this thread on the task."
        else ->
            "Got it. I have added your comment to this task."
    }
}

fun WorkItemRecord.applyOrbitComment(text: String): WorkItemRecord {
    val body = text.trim()
    if (body.isEmpty()) return this
    val mentions = parseWorkItemMentions(body)
    val issueNumber = if (WorkItemMention.RaiseIssue in mentions) {
        nextWorkItemNumber(WorkItemKind.Issue)
    } else {
        null
    }
    val materialName = materialNameFromMention(body)
    var next = withComment(
        WorkItemComment(
            id = "c${Random.nextLong()}",
            author = "You",
            body = body,
            time = "Now",
            mine = true,
        ),
    )
    if (issueNumber != null) {
        next = next.copy(raisedIssues = next.raisedIssues + issueNumber)
    }
    if (WorkItemMention.Material in mentions && !materialName.isNullOrBlank()) {
        val line = OrbitMaterialUsageLine(
            id = "u${Random.nextLong()}",
            material = materialName,
            quantity = 1,
            unit = orbitSuggestedUnitForMaterial(materialName),
        )
        next = next.copy(usedMaterials = next.usedMaterials + line)
    }
    return next.withComment(
        WorkItemComment(
            id = "c${Random.nextLong()}",
            author = "Orbit AI",
            body = orbitAiReply(body, issueNumber = issueNumber, materialName = materialName),
            time = "Now",
            fromAi = true,
        ),
    )
}
