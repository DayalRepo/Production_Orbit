package com.orbitai.erp.ui.ceo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.display.OrbitAvatarSize
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.kpi.CeoActionInsightRow
import com.orbitai.erp.ui.component.kpi.CeoAiBriefCard
import com.orbitai.erp.ui.component.kpi.CeoDashboardDemoData
import com.orbitai.erp.ui.component.kpi.HealthCard
import com.orbitai.erp.ui.component.kpi.InvoicesEntryCard
import com.orbitai.erp.ui.component.kpi.MaterialsSavingsCard
import com.orbitai.erp.ui.component.team.AccountAvatar
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * CEO dashboard — Brief → Health → action tiles → Materials & savings → Invoices.
 */
@Composable
fun CeoDashboardScreen(
    userName: String,
    userRole: String,
    userPhone: String,
    isDark: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
    onViewInvoices: (() -> Unit)? = null,
    onViewMaterials: (() -> Unit)? = null,
    onChaseInvoice: ((invoiceId: String) -> Unit)? = null,
) {
    val metrics = rememberCeoLayoutMetrics()
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors

    CeoScreenScaffold(
        modifier = modifier.fillMaxSize(),
        metrics = metrics,
        header = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CeoGreetingHeader(
                    name = userName,
                    trailing = {
                        AccountAvatar(
                            name = userName,
                            role = userRole,
                            phone = userPhone,
                            onSignOut = onSignOut,
                            avatar = avatar,
                            size = OrbitAvatarSize.Md,
                            themeDark = isDark,
                            onThemeChange = onThemeChange,
                        )
                    },
                )
                Spacer(Modifier.height(spacing.md))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = OrbitTheme.sizing.hairline,
                    color = control.controlBorder,
                )
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(metrics.cardGap),
        ) {
            item {
                CeoAiBriefCard(
                    markdown = CeoDashboardDemoData.AiBriefMarkdown,
                )
            }
            item {
                HealthCard(
                    progress = CeoDashboardDemoData.HealthProgress,
                    healthyProjects = CeoDashboardDemoData.HealthyProjects,
                    atRiskProjects = CeoDashboardDemoData.AtRiskProjects,
                    criticalProjects = CeoDashboardDemoData.CriticalProjects,
                    delta = CeoDashboardDemoData.HealthProgressDelta,
                )
            }
            item {
                CeoActionInsightRow()
            }
            item {
                MaterialsSavingsCard(
                    onViewMaterials = onViewMaterials ?: {},
                )
            }
            item {
                InvoicesEntryCard(
                    onViewInvoices = onViewInvoices ?: {},
                    onChaseInvoice = onChaseInvoice ?: {},
                )
            }
        }
    }
}

/**
 * Replaces the page title: time-of-day greeting + name, avatar trailing.
 */
@Composable
fun CeoGreetingHeader(
    name: String,
    modifier: Modifier = Modifier,
    greeting: String = rememberCeoDayGreeting(),
    trailing: (@Composable () -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = greeting,
                style = OrbitTheme.typography.titleMedium,
                color = content.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = name,
                style = OrbitTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (trailing != null) {
            Box(contentAlignment = Alignment.TopEnd) {
                trailing()
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun rememberCeoDayGreeting(): String = remember {
    val hour = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .hour
    when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
