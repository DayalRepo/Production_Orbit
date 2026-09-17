package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.navigation.OrbitCeoNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitCeoNavIds
import com.orbitai.erp.core.designsystem.component.navigation.OrbitContractorNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitContractorNavIds
import com.orbitai.erp.core.designsystem.component.navigation.OrbitProcurementManagerNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitProcurementManagerNavIds
import com.orbitai.erp.core.designsystem.component.navigation.OrbitProjectManagerNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitProjectManagerNavIds
import com.orbitai.erp.core.designsystem.component.navigation.OrbitQaQcNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitQaQcNavIds
import com.orbitai.erp.core.designsystem.component.navigation.OrbitSiteEngineerNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitSiteEngineerNavIds
import com.orbitai.erp.core.designsystem.component.navigation.OrbitTab
import com.orbitai.erp.core.designsystem.component.navigation.OrbitTabBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitWarehouseManagerNavBar
import com.orbitai.erp.core.designsystem.component.navigation.OrbitWarehouseManagerNavIds
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Navigation samples: icons-only role bottom navs (colour-only selection), then underline pages bar.
 */
@Composable
internal fun NavigationGalleryPage() {
    val spacing = OrbitTheme.spacing

    GallerySection("Bottom nav") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            RoleNavSample(role = "CEO") {
                var selected by remember { mutableStateOf(OrbitCeoNavIds.Dashboard) }
                OrbitCeoNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                    notificationCount = 3,
                    insightCount = 2,
                )
            }
            RoleNavSample(role = "Project Manager") {
                var selected by remember { mutableStateOf(OrbitProjectManagerNavIds.Dashboard) }
                OrbitProjectManagerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                    notificationCount = 12,
                    insightCount = 1,
                )
            }
            RoleNavSample(role = "Site Engineer") {
                var selected by remember { mutableStateOf(OrbitSiteEngineerNavIds.Dashboard) }
                OrbitSiteEngineerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                    notificationCount = 1,
                )
            }
            RoleNavSample(role = "Contractor") {
                var selected by remember { mutableStateOf(OrbitContractorNavIds.Dashboard) }
                OrbitContractorNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                    notificationCount = 99,
                )
            }
            RoleNavSample(role = "Warehouse Manager") {
                var selected by remember { mutableStateOf(OrbitWarehouseManagerNavIds.Dashboard) }
                OrbitWarehouseManagerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                    notificationCount = 5,
                )
            }
            RoleNavSample(role = "Procurement Manager") {
                var selected by remember {
                    mutableStateOf(OrbitProcurementManagerNavIds.Dashboard)
                }
                OrbitProcurementManagerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                    notificationCount = 2,
                )
            }
            RoleNavSample(role = "QA / QC") {
                var selected by remember { mutableStateOf(OrbitQaQcNavIds.Dashboard) }
                OrbitQaQcNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
        }
    }

    GallerySection("Tab bar") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            TabSample(
                tabs = listOf(
                    OrbitTab("task", "Task"),
                    OrbitTab("issue", "Issue"),
                ),
            )
            TabSample(
                tabs = listOf(
                    OrbitTab("approval", "Approval"),
                    OrbitTab("material", "Material"),
                    OrbitTab("task", "Task"),
                    OrbitTab("issue", "Issue"),
                ),
            )
            TabSample(
                tabs = listOf(
                    OrbitTab("done", "Done"),
                    OrbitTab("inprogress", "Inprogress"),
                    OrbitTab("scheduled", "Scheduled"),
                    OrbitTab("raise", "Raise"),
                ),
            )
            TabSample(
                tabs = listOf(
                    OrbitTab("done", "Done"),
                    OrbitTab("inprogress", "Inprogress"),
                    OrbitTab("scheduled", "Scheduled"),
                    OrbitTab("create", "Create"),
                ),
            )
            TabSample(
                tabs = listOf(
                    OrbitTab("create", "Create"),
                    OrbitTab("order", "Order"),
                ),
            )
            TabSample(
                tabs = listOf(
                    OrbitTab("inprogress", "Inprogress"),
                    OrbitTab("scheduled", "Scheduled"),
                    OrbitTab("done", "Done"),
                ),
            )
            TabSample(
                tabs = listOf(
                    OrbitTab("updates", "Updates"),
                    OrbitTab("attachments", "Attachments"),
                ),
            )
        }
    }
}

@Composable
private fun RoleNavSample(role: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm)) {
        Text(
            text = role,
            style = OrbitTheme.typography.labelLarge,
            color = OrbitTheme.contentColors.textSecondary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            content()
        }
    }
}

@Composable
private fun TabSample(tabs: List<OrbitTab>) {
    var selected by remember(tabs) { mutableStateOf(tabs.first().id) }
    OrbitTabBar(
        tabs = tabs,
        selectedId = selected,
        onSelect = { selected = it },
        modifier = Modifier.fillMaxWidth(),
    )
}
