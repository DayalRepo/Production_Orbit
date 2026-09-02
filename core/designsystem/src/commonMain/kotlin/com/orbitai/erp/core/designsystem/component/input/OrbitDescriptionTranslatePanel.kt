package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.container.OrbitVerticalScrollbar
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/** Major Indian languages offered for description translation. */
val OrbitIndianLanguages: List<String> = listOf(
    "Hindi",
    "English",
    "Bengali",
    "Telugu",
    "Marathi",
    "Tamil",
    "Urdu",
    "Gujarati",
    "Kannada",
    "Malayalam",
    "Odia",
    "Punjabi",
    "Assamese",
    "Kashmiri",
    "Sanskrit",
    "Konkani",
    "Manipuri",
    "Nepali",
    "Sindhi",
    "Dogri",
    "Maithili",
    "Santali",
    "Bodo",
)

/**
 * Language picker shown beneath a description field when the user chooses Translate.
 */
@Composable
fun OrbitDescriptionTranslatePanel(
    onLanguageSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    languages: List<String> = OrbitIndianLanguages,
    searchPlaceholder: String = "Search languages",
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors

    var query by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val visibleLanguages = languages.filterByQuery(query)

    AnimatedVisibility(
        visible = true,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier.fillMaxWidth(),
    ) {
        OrbitCard(padding = spacing.md) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Translate with Orbit AI",
                        style = OrbitTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = content.textPrimary,
                    )
                    OrbitIconButton(
                        contentDescription = "Close translate panel",
                        onClick = onDismiss,
                        icon = OrbitIcons.Cancel,
                        style = OrbitIconButtonStyle.Neutral,
                        size = OrbitIconButtonSize.Small,
                        ringed = false,
                    )
                }
                OrbitSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = searchPlaceholder,
                    label = searchPlaceholder,
                    size = OrbitFieldSize.Small,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = sizing.dropdownMaxHeight),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState),
                    ) {
                        if (visibleLanguages.isEmpty()) {
                            Text(
                                text = if (query.isBlank()) {
                                    "Nothing to choose from"
                                } else {
                                    "No matches for \u201C$query\u201D"
                                },
                                style = OrbitTheme.typography.bodyMedium,
                                color = content.textSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.sm),
                            )
                        } else {
                            visibleLanguages.forEachIndexed { index, language ->
                                OrbitDropdownRow(
                                    label = language,
                                    selected = false,
                                    onClick = { onLanguageSelect(language) },
                                )
                                if (index < visibleLanguages.lastIndex) {
                                    OrbitDivider(color = control.controlBorder)
                                }
                            }
                        }
                    }
                    if (scrollState.maxValue > 0) {
                        OrbitVerticalScrollbar(
                            scrollState = scrollState,
                            modifier = Modifier.padding(end = spacing.xs),
                        )
                    }
                }
            }
        }
    }
}
