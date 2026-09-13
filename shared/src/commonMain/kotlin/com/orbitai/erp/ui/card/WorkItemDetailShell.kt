package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.platform.OrbitBackHandler
import kotlinx.coroutines.launch

@Composable
internal fun WorkItemDetailShell(
    record: WorkItemRecord,
    onBack: () -> Unit,
    onRecordChange: (WorkItemRecord) -> Unit,
    modifier: Modifier = Modifier,
    startInInbox: Boolean = false,
    footer: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = if (startInInbox) 1 else 0,
        pageCount = { 2 },
    )
    val inInbox = pagerState.currentPage == 1

    fun goDetails() {
        scope.launch { pagerState.animateScrollToPage(0) }
    }

    fun goInbox() {
        scope.launch { pagerState.animateScrollToPage(1) }
    }

    LaunchedEffect(inInbox, record.id, record.inboxUnreadCount) {
        if (inInbox && record.inboxUnreadCount > 0) {
            onRecordChange(record.withInboxOpened())
        }
    }

    OrbitBackHandler(onBack = { if (inInbox) goDetails() else onBack() })

    Column(
        modifier = modifier
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
                contentDescription = "Back",
                onClick = { if (inInbox) goDetails() else onBack() },
                icon = OrbitIcons.ArrowLeft,
                style = OrbitIconButtonStyle.Neutral,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal)
                .padding(bottom = spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            WorkItemStageTitle(
                record = record,
                modifier = Modifier.weight(1f),
                stageStyle = OrbitTheme.extendedTypography.sectionLabel,
                titleStyle = OrbitTheme.typography.headlineSmall.copy(
                    fontWeight = OrbitTheme.fontWeights.title,
                ),
                maxTitleLines = 4,
            )
            WorkItemInboxEntry(
                unread = record.inboxUnreadCount,
                selected = inInbox,
                onClick = { if (inInbox) goDetails() else goInbox() },
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            if (page == 0) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = spacing.screenHorizontal)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = spacing.md),
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                        content = content,
                    )
                    footer()
                }
            } else {
                WorkItemInboxPane(
                    record = record,
                    onRecordChange = onRecordChange,
                )
            }
        }
    }
}
