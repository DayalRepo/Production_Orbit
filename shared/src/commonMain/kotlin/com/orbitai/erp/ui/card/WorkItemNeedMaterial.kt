package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.model.StockLevel
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.ui.component.badge.BadgeKind
import com.orbitai.erp.ui.component.badge.StatusBadge
import com.orbitai.erp.ui.component.badge.StockLevelBadge
import com.orbitai.erp.ui.component.button.ActionButtonRow
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.form.page.MaterialsPage
import kotlin.random.Random

internal fun workItemCanNeed(role: UserRole): Boolean =
    role == UserRole.SiteEngineer || role == UserRole.Contractor

@Composable
internal fun MaterialsGivenHeader(
    onNeedClick: (() -> Unit)?,
) {
    val needRed = if (OrbitTheme.isDark) {
        OrbitBadgeTone.Red.colors.label
    } else {
        OrbitBadgeTone.Red.colors.solidContainer
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        SectionHeading("Materials given")
        Spacer(Modifier.weight(1f))
        if (onNeedClick != null) {
            Row(
                modifier = Modifier.clickable(
                    role = Role.Button,
                    onClick = onNeedClick,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xs),
            ) {
                OrbitGlyph(
                    icon = OrbitIcons.ShoppingCart,
                    size = 12.dp,
                    tint = needRed,
                    contentDescription = "Need material",
                    minimumStroke = 1.dp,
                    maximumStroke = 1.dp,
                )
                Text(
                    text = "NEED",
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = needRed,
                )
            }
        }
    }
}

private enum class NeedConfirm { Done, Cancel }

@Composable
internal fun NeedMaterialDialog(
    lines: List<OrbitMaterialUsageLine>,
    extraMaterials: List<String>,
    extraUnits: List<String>,
    onLinesChange: (List<OrbitMaterialUsageLine>) -> Unit,
    onMaterialCreated: (String) -> Unit,
    onUnitCreated: (String) -> Unit,
    onDone: () -> Unit,
    onCancel: () -> Unit,
) {
    var confirm by remember { mutableStateOf<NeedConfirm?>(null) }
    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val scroll = rememberScrollState()

    OrbitBackHandler(onBack = onCancel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrbitIconButton(
                contentDescription = "Close",
                onClick = onCancel,
                icon = OrbitIcons.Cancel,
                style = OrbitIconButtonStyle.Neutral,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "NEED",
                style = OrbitTheme.extendedTypography.sectionLabel,
                color = OrbitTheme.contentColors.textSecondary,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal)
                .verticalScroll(scroll)
                .padding(bottom = spacing.md),
        ) {
            NeedMaterialEditor(
                lines = lines,
                extraMaterials = extraMaterials,
                extraUnits = extraUnits,
                onLinesChange = onLinesChange,
                onMaterialCreated = onMaterialCreated,
                onUnitCreated = onUnitCreated,
            )
        }
        ActionButtonRow(
            dismiss = ActionKind.Cancel,
            confirm = ActionKind.Done,
            onDismiss = { confirm = NeedConfirm.Cancel },
            onConfirm = { confirm = NeedConfirm.Done },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal)
                .padding(vertical = spacing.sm),
            size = OrbitButtonSize.Medium,
        )
    }

    when (confirm) {
        NeedConfirm.Cancel -> OrbitConfirmDialog(
            title = "Discard need",
            message = "Discard this need request?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            destructive = true,
            onConfirm = onCancel,
            onDismiss = { confirm = null },
        )
        NeedConfirm.Done -> OrbitConfirmDialog(
            title = "Order materials",
            message = "Order these materials?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = onDone,
            onDismiss = { confirm = null },
        )
        null -> Unit
    }
}

@Composable
internal fun NeedMaterialEditor(
    lines: List<OrbitMaterialUsageLine>,
    extraMaterials: List<String>,
    extraUnits: List<String>,
    onLinesChange: (List<OrbitMaterialUsageLine>) -> Unit,
    onMaterialCreated: (String) -> Unit,
    onUnitCreated: (String) -> Unit,
) {
    MaterialsPage(
        label = "",
        lines = lines,
        extraMaterials = extraMaterials,
        extraUnits = extraUnits,
        onMaterialSelect = { id, material ->
            onLinesChange(upsertNeedLine(lines, id) { it.copy(material = material) })
        },
        onQuantityChange = { id, quantity ->
            onLinesChange(upsertNeedLine(lines, id) { it.copy(quantity = quantity) })
        },
        onUnitSelect = { id, unit ->
            onLinesChange(upsertNeedLine(lines, id) { it.copy(unit = unit) })
        },
        onAddLine = {
            onLinesChange(lines + OrbitMaterialUsageLine(id = "need${Random.nextLong()}"))
        },
        onRemoveLine = { id ->
            val next = lines.filterNot { it.id == id }
            onLinesChange(
                next.ifEmpty { listOf(OrbitMaterialUsageLine(id = "need-open")) },
            )
        },
        onMaterialCreated = onMaterialCreated,
        onUnitCreated = onUnitCreated,
    )
}

@Composable
internal fun OrderedMaterialsList(
    requests: List<WorkItemMaterialRequest>,
    canReceive: Boolean,
    onReceive: (String) -> Unit,
) {
    if (requests.isEmpty()) return
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        SectionHeading("On order")
        requests.forEach { request ->
            OrbitCard(modifier = Modifier.fillMaxWidth(), padding = spacing.md) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = request.material,
                            style = OrbitTheme.typography.bodyLarge,
                            color = content.textPrimary,
                        )
                        Text(
                            text = "${request.quantity} ${request.unit}",
                            style = OrbitTheme.typography.bodyMedium,
                            color = content.textSecondary,
                        )
                    }
                    MaterialRequestBadge(status = request.status)
                    if (canReceive) {
                        OrbitIconButton(
                            contentDescription = "Mark ${request.material} received",
                            onClick = { onReceive(request.id) },
                            icon = OrbitIcons.Tick,
                            style = OrbitIconButtonStyle.Positive,
                            size = OrbitIconButtonSize.Small,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun MaterialRequestBadge(
    status: MaterialRequestStatus,
    modifier: Modifier = Modifier,
) {
    val size = OrbitBadgeSize.Small
    when (status) {
        MaterialRequestStatus.LowStock ->
            StockLevelBadge(level = StockLevel.Low, modifier = modifier, size = size)
        MaterialRequestStatus.OutOfStock ->
            StockLevelBadge(level = StockLevel.OutOfStock, modifier = modifier, size = size)
        MaterialRequestStatus.Paused ->
            StatusBadge(kind = BadgeKind.Paused, modifier = modifier, size = size)
        MaterialRequestStatus.Ordered ->
            StatusBadge(kind = BadgeKind.PurchaseOrder, modifier = modifier, size = size)
    }
}

private fun upsertNeedLine(
    lines: List<OrbitMaterialUsageLine>,
    id: String,
    transform: (OrbitMaterialUsageLine) -> OrbitMaterialUsageLine,
): List<OrbitMaterialUsageLine> {
    val index = lines.indexOfFirst { it.id == id }
    if (index < 0) return lines + transform(OrbitMaterialUsageLine(id = id))
    return lines.mapIndexed { i, line -> if (i == index) transform(line) else line }
}
