package com.orbitai.erp.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.component.button.ActionButtonRow
import com.orbitai.erp.ui.component.button.ActionKind

/**
 * Full-screen single-page form chrome: title, scrolling body, decision footer.
 *
 * Task forms use Cancel / Create. Issue forms use Cancel / Raise.
 */
@Composable
fun WizardScaffold(
    title: String,
    subtitle: String,
    dismiss: ActionKind,
    confirm: ActionKind,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    OrbitBackHandler(onBack = onDismiss)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .padding(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.screenVertical,
            ),
    ) {
        Text(
            text = title.uppercase(),
            style = OrbitTheme.typography.headlineSmall.copy(
                fontWeight = OrbitTheme.fontWeights.heading,
            ),
            color = OrbitTheme.contentColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = subtitle.uppercase(),
            style = OrbitTheme.extendedTypography.sectionLabel,
            color = OrbitTheme.contentColors.textTertiary,
        )
        Spacer(modifier = Modifier.height(spacing.xl))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
            content = content,
        )

        Spacer(modifier = Modifier.height(spacing.lg))
        ActionButtonRow(
            dismiss = dismiss,
            confirm = confirm,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        )
    }
}
