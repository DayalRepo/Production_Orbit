package com.orbitai.erp.ui.component.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLog
import com.orbitai.erp.ui.component.dropdown.ConstructionMaterials
import com.orbitai.erp.ui.component.dropdown.ConstructionUnits
import kotlin.random.Random

@Composable
fun ManagedMaterialUsageLog(
    modifier: Modifier = Modifier,
    title: String = "Materials used",
    initialLines: List<OrbitMaterialUsageLine> = listOf(
        OrbitMaterialUsageLine(
            id = "sample-cement",
            material = "Cement (OPC 53)",
            quantity = 42,
            unit = "Bags",
        ),
        OrbitMaterialUsageLine(
            id = "sample-steel",
            material = "Reinforcement Steel Fe500D",
            quantity = 1,
            unit = "Kg",
        ),
    ),
) {
    val lines = remember {
        mutableStateListOf<OrbitMaterialUsageLine>().also { it.addAll(initialLines) }
    }

    OrbitMaterialUsageLog(
        lines = lines.toList(),
        materials = ConstructionMaterials,
        units = ConstructionUnits,
        onMaterialSelect = { id, material ->
            val index = lines.indexOfFirst { it.id == id }
            if (index >= 0) lines[index] = lines[index].copy(material = material)
        },
        onQuantityChange = { id, quantity ->
            val index = lines.indexOfFirst { it.id == id }
            if (index >= 0) lines[index] = lines[index].copy(quantity = quantity)
        },
        onUnitSelect = { id, unit ->
            val index = lines.indexOfFirst { it.id == id }
            if (index >= 0) lines[index] = lines[index].copy(unit = unit)
        },
        onAdd = {
            lines += OrbitMaterialUsageLine(id = "n${Random.nextLong()}")
        },
        onRemove = { id -> lines.removeAll { it.id == id } },
        modifier = modifier,
        title = title,
    )
}
