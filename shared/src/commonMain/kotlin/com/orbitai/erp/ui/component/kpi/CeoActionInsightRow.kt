package com.orbitai.erp.ui.component.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.brand.OrbitMark
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.overlay.OrbitBottomSheet
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

private enum class InsightKind { Confidence, Decision, AiAdvice }

/**
 * Three CEO action tiles — title, metric, caption; tap opens a detail sheet.
 */
@Composable
fun CeoActionInsightRow(
    modifier: Modifier = Modifier,
    confidencePercent: Int = CeoDashboardDemoData.ConfidencePercent,
    decisionCount: Int = CeoDashboardDemoData.DecisionCount,
    aiAdviceCount: Int = CeoDashboardDemoData.AiAdviceCount,
) {
    val spacing = OrbitTheme.spacing
    var openSheet by remember { mutableStateOf<InsightKind?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        CeoActionInsightCard(
            title = "Confidence",
            value = "$confidencePercent%",
            caption = "On time",
            useOrbitMark = false,
            onClick = { openSheet = InsightKind.Confidence },
            modifier = Modifier.weight(1f),
        )
        CeoActionInsightCard(
            title = "Decision",
            value = decisionCount.toString(),
            caption = "Awaiting",
            useOrbitMark = false,
            onClick = { openSheet = InsightKind.Decision },
            modifier = Modifier.weight(1f),
        )
        CeoActionInsightCard(
            title = "AI advice",
            value = aiAdviceCount.toString(),
            caption = "Act now",
            useOrbitMark = true,
            onClick = { openSheet = InsightKind.AiAdvice },
            modifier = Modifier.weight(1f),
        )
    }

    val sheet = openSheet
    if (sheet != null) {
        val title: String
        val lines: List<String>
        when (sheet) {
            InsightKind.Confidence -> {
                title = "Confidence"
                lines = CeoDashboardDemoData.ConfidenceDetailLines
            }
            InsightKind.Decision -> {
                title = "Decision"
                lines = CeoDashboardDemoData.DecisionDetailLines
            }
            InsightKind.AiAdvice -> {
                title = "AI advice"
                lines = CeoDashboardDemoData.AiAdviceDetailLines
            }
        }
        OrbitBottomSheet(
            onDismiss = { openSheet = null },
            title = title,
        ) {
            lines.forEach { line ->
                Text(
                    text = "· $line",
                    style = OrbitTheme.typography.bodyMedium,
                    color = OrbitTheme.contentColors.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md, vertical = spacing.xs),
                )
            }
            Spacer(Modifier.height(spacing.sm))
        }
    }
}

@Composable
private fun CeoActionInsightCard(
    title: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
    useOrbitMark: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val iconTint = content.iconPrimary
    val iconSize = 14.dp

    OrbitCard(
        modifier = modifier.height(140.dp),
        padding = spacing.none,
        container = OrbitTheme.controlColors.cardContainer,
        onClick = onClick,
        contentDescription = "$title, $value, $caption",
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.sm, vertical = spacing.sm)
                .clearAndSetSemantics {},
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (useOrbitMark) {
                    OrbitMark(
                        size = iconSize,
                        color = iconTint,
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(spacing.xxs))
                }
                Text(
                    text = title,
                    style = OrbitTheme.typography.labelSmall,
                    color = content.textSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = value,
                style = OrbitTheme.extendedTypography.metricLarge.copy(
                    fontWeight = FontWeight.Normal,
                ),
                color = content.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.align(Alignment.Center),
            )

            Text(
                text = caption,
                style = OrbitTheme.extendedTypography.metricCaption,
                color = content.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
