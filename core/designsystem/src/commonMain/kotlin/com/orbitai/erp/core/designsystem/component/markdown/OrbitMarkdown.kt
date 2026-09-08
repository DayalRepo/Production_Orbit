package com.orbitai.erp.core.designsystem.component.markdown

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.orbitGlassHorizontalScrollbar
import com.orbitai.erp.core.designsystem.component.container.orbitGlassScrollbar
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Block-level markdown nodes supported by [OrbitMarkdown].
 */
@Immutable
sealed interface OrbitMarkdownBlock {
    data class Heading(val level: Int, val text: String) : OrbitMarkdownBlock
    data class Paragraph(val text: String) : OrbitMarkdownBlock
    data class Bullet(val text: String) : OrbitMarkdownBlock
    data class Table(val headers: List<String>, val rows: List<List<String>>) : OrbitMarkdownBlock
}

/**
 * Parses a focused markdown subset for CEO / AI brief surfaces.
 *
 * Supports `#`–`###` headings, paragraphs, `-` / `*` bullets, pipe tables,
 * and inline `**bold**`, `*italic*` / `_italic_`, `__underline__`, `~~strike~~`.
 */
fun parseOrbitMarkdown(source: String): List<OrbitMarkdownBlock> {
    val lines = source.replace("\r\n", "\n").lines()
    val blocks = mutableListOf<OrbitMarkdownBlock>()
    var i = 0
    while (i < lines.size) {
        val line = lines[i].trimEnd()
        if (line.isBlank()) {
            i++
            continue
        }

        val heading = HEADING.matchEntire(line.trim())
        if (heading != null) {
            blocks += OrbitMarkdownBlock.Heading(
                level = heading.groupValues[1].length.coerceIn(1, 3),
                text = heading.groupValues[2].trim(),
            )
            i++
            continue
        }

        if (line.trim().startsWith("|") && line.count { it == '|' } >= 2) {
            val tableLines = mutableListOf<String>()
            while (i < lines.size) {
                val candidate = lines[i].trim()
                if (!candidate.startsWith("|") || candidate.count { it == '|' } < 2) break
                tableLines += candidate
                i++
            }
            val parsed = parseTable(tableLines)
            if (parsed != null) {
                blocks += parsed
                continue
            }
            i -= tableLines.size
        }

        val bullet = BULLET.matchEntire(line.trim())
        if (bullet != null) {
            blocks += OrbitMarkdownBlock.Bullet(bullet.groupValues[1].trim())
            i++
            continue
        }

        // One blank line separates paragraphs; keep sentence spacing via paragraph blocks.
        val paragraph = StringBuilder(line.trim())
        i++
        while (i < lines.size) {
            val next = lines[i].trimEnd()
            if (next.isBlank()) break
            val trimmed = next.trim()
            if (HEADING.matches(trimmed) || BULLET.matches(trimmed) ||
                (trimmed.startsWith("|") && trimmed.count { it == '|' } >= 2)
            ) {
                break
            }
            paragraph.append(' ').append(trimmed)
            i++
        }
        blocks += OrbitMarkdownBlock.Paragraph(paragraph.toString())
    }
    return blocks
}

/** Flatten markdown to plain prose for collapsed previews / a11y. */
fun orbitMarkdownPlainText(source: String): String =
    parseOrbitMarkdown(source).joinToString(separator = " ") { block ->
        when (block) {
            is OrbitMarkdownBlock.Heading -> block.text
            is OrbitMarkdownBlock.Paragraph -> block.text
            is OrbitMarkdownBlock.Bullet -> block.text
            is OrbitMarkdownBlock.Table -> {
                val head = block.headers.joinToString(", ")
                val body = block.rows.joinToString("; ") { it.joinToString(", ") }
                listOf(head, body).filter { it.isNotBlank() }.joinToString(". ")
            }
        }
    }

@Composable
fun OrbitMarkdown(
    source: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val blocks = remember(source) { parseOrbitMarkdown(source) }
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val align = if (centered) TextAlign.Center else TextAlign.Start
    val cross = if (centered) Alignment.CenterHorizontally else Alignment.Start

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
        horizontalAlignment = cross,
    ) {
        blocks.forEach { block ->
            when (block) {
                is OrbitMarkdownBlock.Heading -> {
                    val style = when (block.level) {
                        1 -> OrbitTheme.typography.titleLarge
                        2 -> OrbitTheme.typography.titleMedium
                        else -> OrbitTheme.typography.titleSmall
                    }.copy(fontWeight = FontWeight.SemiBold)
                    Text(
                        text = annotatedInline(block.text),
                        style = style,
                        color = content.textPrimary,
                        textAlign = align,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                is OrbitMarkdownBlock.Paragraph -> {
                    Text(
                        text = annotatedInline(block.text),
                        style = OrbitTheme.typography.bodyMedium.copy(
                            fontWeight = OrbitTheme.typography.titleMedium.fontWeight,
                        ),
                        color = content.textSecondary,
                        textAlign = align,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                is OrbitMarkdownBlock.Bullet -> {
                    if (centered) {
                        Text(
                            text = annotatedInline("• ${block.text}"),
                            style = OrbitTheme.typography.bodyMedium.copy(
                                fontWeight = OrbitTheme.typography.titleMedium.fontWeight,
                            ),
                            color = content.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            Text(
                                text = "•",
                                style = OrbitTheme.typography.bodyMedium,
                                color = content.textSecondary,
                            )
                            Text(
                                text = annotatedInline(block.text),
                                style = OrbitTheme.typography.bodyMedium.copy(
                                    fontWeight = OrbitTheme.typography.titleMedium.fontWeight,
                                ),
                                color = content.textSecondary,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                is OrbitMarkdownBlock.Table -> {
                    OrbitMarkdownTable(
                        headers = block.headers,
                        rows = block.rows,
                        centered = centered,
                    )
                }
            }
        }
    }
}

@Composable
private fun OrbitMarkdownTable(
    headers: List<String>,
    rows: List<List<String>>,
    centered: Boolean,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val dark = OrbitTheme.isDark
    val cols = headers.size.coerceAtLeast(rows.maxOfOrNull { it.size } ?: 0)
    if (cols == 0) return

    val shape = OrbitTheme.shapeTokens.card
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()
    val colWidth = 112.dp
    val tableWidth = colWidth * cols + spacing.sm * (cols - 1).coerceAtLeast(0) + spacing.xs * 2

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge)
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (dark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
                sheen = if (dark) 1f else OrbitGlass.Sheen,
            )
            .padding(spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .orbitGlassScrollbar(scrollState = vScroll)
                .orbitGlassHorizontalScrollbar(scrollState = hScroll),
        ) {
            Column(
                modifier = Modifier
                    .horizontalScroll(hScroll)
                    .verticalScroll(vScroll)
                    .width(tableWidth),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                TableRow(
                    cells = headers.padTo(cols),
                    header = true,
                    centered = centered,
                    colWidth = colWidth,
                )
                HorizontalDivider(
                    thickness = sizing.hairline,
                    color = control.controlBorder,
                )
                rows.forEachIndexed { index, row ->
                    TableRow(
                        cells = row.padTo(cols),
                        header = false,
                        centered = centered,
                        colWidth = colWidth,
                    )
                    if (index != rows.lastIndex) {
                        HorizontalDivider(
                            thickness = sizing.hairline,
                            color = control.controlBorder.copy(alpha = 0.55f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableRow(
    cells: List<String>,
    header: Boolean,
    centered: Boolean,
    colWidth: Dp,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    Row(
        modifier = Modifier.padding(horizontal = spacing.xs, vertical = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        cells.forEach { cell ->
            Text(
                text = annotatedInline(cell),
                style = if (header) {
                    OrbitTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                } else {
                    OrbitTheme.typography.bodySmall.copy(
                        fontWeight = OrbitTheme.typography.titleMedium.fontWeight,
                    )
                },
                color = if (header) content.textPrimary else content.textSecondary,
                textAlign = if (centered) TextAlign.Center else TextAlign.Start,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(colWidth),
            )
        }
    }
}

@Composable
private fun annotatedInline(text: String): AnnotatedString {
    val primary = OrbitTheme.contentColors.textPrimary
    val secondary = OrbitTheme.contentColors.textSecondary
    return remember(text, primary, secondary) {
        buildAnnotatedString {
            var i = 0
            while (i < text.length) {
                when {
                    text.startsWith("**", i) -> {
                        val end = text.indexOf("**", i + 2)
                        if (end > i) {
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = primary)) {
                                append(text.substring(i + 2, end))
                            }
                            i = end + 2
                        } else {
                            append(text[i]); i++
                        }
                    }
                    text.startsWith("__", i) -> {
                        val end = text.indexOf("__", i + 2)
                        if (end > i) {
                            withStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = primary,
                                ),
                            ) {
                                append(text.substring(i + 2, end))
                            }
                            i = end + 2
                        } else {
                            append(text[i]); i++
                        }
                    }
                    text.startsWith("~~", i) -> {
                        val end = text.indexOf("~~", i + 2)
                        if (end > i) {
                            withStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.LineThrough,
                                    color = secondary,
                                ),
                            ) {
                                append(text.substring(i + 2, end))
                            }
                            i = end + 2
                        } else {
                            append(text[i]); i++
                        }
                    }
                    text.startsWith("*", i) && !text.startsWith("**", i) -> {
                        val end = text.indexOf('*', i + 1)
                        if (end > i) {
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = secondary)) {
                                append(text.substring(i + 1, end))
                            }
                            i = end + 1
                        } else {
                            append(text[i]); i++
                        }
                    }
                    text.startsWith("_", i) && !text.startsWith("__", i) -> {
                        val end = text.indexOf('_', i + 1)
                        if (end > i) {
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = secondary)) {
                                append(text.substring(i + 1, end))
                            }
                            i = end + 1
                        } else {
                            append(text[i]); i++
                        }
                    }
                    else -> {
                        append(text[i])
                        i++
                    }
                }
            }
        }
    }
}

private fun parseTable(lines: List<String>): OrbitMarkdownBlock.Table? {
    if (lines.isEmpty()) return null
    fun cells(line: String): List<String> =
        line.trim()
            .removePrefix("|")
            .removeSuffix("|")
            .split("|")
            .map { it.trim() }

    val headers = cells(lines.first())
    if (headers.isEmpty()) return null
    val bodyStart = if (lines.size > 1 && lines[1].contains("---")) 2 else 1
    val rows = lines.drop(bodyStart).map { cells(it).padTo(headers.size) }
    return OrbitMarkdownBlock.Table(headers = headers, rows = rows)
}

private fun List<String>.padTo(size: Int): List<String> =
    if (this.size >= size) take(size) else this + List(size - this.size) { "" }

private val HEADING = Regex("""^(#{1,3})\s+(.+)$""")
private val BULLET = Regex("""^[-*]\s+(.+)$""")
