package com.orbitai.erp.ui.ceo

import com.orbitai.erp.core.designsystem.component.navigation.OrbitCeoNavIds

/**
 * CEO top-level pages, ordered to match [com.orbitai.erp.core.designsystem.component.navigation.OrbitCeoNavItems]
 * left-to-right then the circular AI action — so pager index and nav id stay aligned.
 */
enum class CeoDestination(
    val id: String,
    val title: String,
) {
    Dashboard(OrbitCeoNavIds.Dashboard, "Dashboard"),
    Projects(OrbitCeoNavIds.Projects, "Projects"),
    Messages(OrbitCeoNavIds.Messages, "Notifications"),
    Assistant(OrbitCeoNavIds.Assistant, "Orbit AI"),
    ;

    companion object {
        fun fromId(id: String): CeoDestination =
            entries.firstOrNull { it.id == id } ?: Dashboard
    }
}
