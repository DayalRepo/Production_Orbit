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
 * Navigation samples: floating role bottom navs, then underline pages bar.
 *
 * Role bars are library chrome — icons only — reviewed here before screens wire them above the
 * platform gesture / navigation bar. Tab bar and bottom nav share the same chrome edge inset so
 * the column grid lines up on Android and iOS.
 */
@Composable
internal fun NavigationGalleryPage() {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val sizing = OrbitTheme.sizing

    GallerySection("Bottom nav · role bars") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            Text(
                text = "Full pill + separate assistant circle. Icons only; soft glass shadow; " +
                    "spring micro-animation on the active glyph. Edge inset ${sizing.bottomNavEdgeInset} " +
                    "matches the tab bar column.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
            RoleNavSample(label = "CEO") {
                var selected by remember { mutableStateOf(OrbitCeoNavIds.Dashboard) }
                OrbitCeoNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
            RoleNavSample(label = "Project Manager") {
                var selected by remember { mutableStateOf(OrbitProjectManagerNavIds.Dashboard) }
                OrbitProjectManagerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
            RoleNavSample(label = "Site Engineer") {
                var selected by remember { mutableStateOf(OrbitSiteEngineerNavIds.Dashboard) }
                OrbitSiteEngineerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
            RoleNavSample(label = "Contractor") {
                var selected by remember { mutableStateOf(OrbitContractorNavIds.Dashboard) }
                OrbitContractorNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
            RoleNavSample(label = "Warehouse Manager") {
                var selected by remember { mutableStateOf(OrbitWarehouseManagerNavIds.Dashboard) }
                OrbitWarehouseManagerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
            RoleNavSample(label = "Procurement Manager") {
                var selected by remember {
                    mutableStateOf(OrbitProcurementManagerNavIds.Dashboard)
                }
                OrbitProcurementManagerNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
            RoleNavSample(label = "QA/QC") {
                var selected by remember { mutableStateOf(OrbitQaQcNavIds.Dashboard) }
                OrbitQaQcNavBar(
                    selectedId = selected,
                    onSelect = { selected = it },
                    applyNavigationBarInset = false,
                )
            }
        }
    }

    GallerySection("Tab bar · pages bar") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            Text(
                text = "Underline tabs (pages bar): active label + glass underline. Edge inset " +
                    "${sizing.tabBarEdgeInset} and min height ${sizing.minTouchTarget} follow the " +
                    "platform chrome grid with the bottom nav.",
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
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
private fun RoleNavSample(label: String, content: @Composable () -> Unit) {
    val spacing = OrbitTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(
            text = label,
            style = OrbitTheme.typography.labelLarge,
            color = OrbitTheme.contentColors.textPrimary,
        )
        // Gallery already pads for safe drawing, so skip the nav-bar inset here.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
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
