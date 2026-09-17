import re
from pathlib import Path

NAV = {
    "DashboardCircle": "dashboard-circle",
    "Archive04": "archive-04",
    "BubbleChat": "bubble-chat",
    "ReceiptIndianRupee": "receipt-indian-rupee",
    "NotepadText": "notepad-text",
    "NotepadTextDashed": "notepad-text-dashed",
    "NotebookText": "notebook-text",
    "Calendar01": "calendar-01",
    "CalendarSchedule": "calendar-03",
    "CircleCheck": "checkmark-circle-02",
    "ClipboardCheck": "clipboard-check",
    "Warehouse": "warehouse",
    "ListBullet": "left-to-right-list-bullet",
    "ShoppingCart": "shopping-cart-02",
    "BadgeCheck": "badge-check",
}


def parse_svg(svg: str):
    paths = []
    for m in re.finditer(r"<path\b([^>]*)/?>", svg):
        attrs = m.group(1)
        d = re.search(r'\bd="([^"]+)"', attrs)
        if not d:
            continue
        evenodd = "evenodd" in attrs.lower()
        paths.append({"d": d.group(1), "evenodd": evenodd})
    return paths


def esc(s: str) -> str:
    return s.replace("\\", "\\\\").replace('"', '\\"')


parts = [
    """package com.orbitai.erp.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Solid-rounded Hugeicons for bottom-nav only.
 *
 * Body fills with Icon tint; even-odd cutouts punch through to the surface so light theme
 * shows a dark silhouette with light holes and dark theme the inverse — matching the solid
 * receipt / chat references.
 */
object OrbitNavSolidIcons {
"""
]

for name, slug in NAV.items():
    svg = Path(f"tools/solid_svg/{slug}.svg").read_text(encoding="utf-8")
    paths = parse_svg(svg)
    print(f"{name}: {len(paths)} paths evenodd={[p['evenodd'] for p in paths]}")
    block = [
        f"    val {name}: ImageVector by lazy {{",
        "        solidVector(",
        f'            name = "{name}Solid",',
        "            fills = listOf(",
    ]
    for p in paths:
        # Solid nav icons rely on cutouts (₹, dots, check) punching to the surface.
        block.append(f'                SolidFill("{esc(p["d"])}", evenOdd = true),')
    block += ["            ),", "        )", "    }", ""]
    parts.append("\n".join(block))

parts.append(
    """
}

private data class SolidFill(val path: String, val evenOdd: Boolean = false)

private fun solidVector(name: String, fills: List<SolidFill>): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        fills.forEach { fill ->
            addPath(
                pathData = PathParser().parsePathString(fill.path).toNodes(),
                fill = SolidColor(Color.Black),
                pathFillType = if (fill.evenOdd) PathFillType.EvenOdd else PathFillType.NonZero,
            )
        }
    }.build()
"""
)

out = Path(
    "core/designsystem/src/commonMain/kotlin/com/orbitai/erp/core/designsystem/icon/OrbitNavSolidIcons.kt"
)
out.write_text("".join(parts), encoding="utf-8")
print("wrote", out, "chars", out.stat().st_size)
