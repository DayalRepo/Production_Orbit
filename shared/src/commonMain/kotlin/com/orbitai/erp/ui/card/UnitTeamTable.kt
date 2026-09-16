package com.orbitai.erp.ui.card

import com.orbitai.erp.ui.component.team.TeamMember

/** Markdown table of assignees for the unit detail team section. */
fun teamMembersMarkdown(members: List<TeamMember>): String {
    if (members.isEmpty()) return ""
    val rows = members.joinToString("\n") { member ->
        val name = member.name.trim().ifBlank { "—" }
        val role = member.role.trim().ifBlank { "—" }
        val phone = member.phone.trim().ifBlank { "—" }
        "| $name | $role | $phone |"
    }
    return """
        | Name | Role | Mobile |
        | --- | --- | --- |
        $rows
    """.trimIndent()
}
