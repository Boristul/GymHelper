package com.boristul.gymhelper.feature.bodyweightlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.boristul.gymhelper.core.uikit.AppBottomActions
import com.boristul.gymhelper.core.uikit.AppLoadingState
import com.boristul.gymhelper.core.uikit.AppScreenScaffold
import com.boristul.gymhelper.core.uikit.AppSpacing
import com.boristul.gymhelper.core.uikit.AppValidationMessage
import com.boristul.gymhelper.domain.bodyweight.BodyWeightEntry

@Composable
fun BodyWeightLogScreen(
    state: BodyWeightLogState,
    onIntent: (BodyWeightLogIntent) -> Unit,
) {
    BodyWeightLogContent(
        state = state,
        onWeightChange = { onIntent(BodyWeightLogIntent.WeightChanged(it)) },
        onSaveTodayWeight = { onIntent(BodyWeightLogIntent.SaveTodayWeightClicked) },
        onDeleteEntry = { entryId -> onIntent(BodyWeightLogIntent.DeleteEntryClicked(entryId)) },
    )
}

@Composable
fun BodyWeightLogContent(
    state: BodyWeightLogState,
    onWeightChange: (String) -> Unit,
    onSaveTodayWeight: () -> Unit,
    onDeleteEntry: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        if (state.isLoading) {
            AppLoadingState()
        } else {
            BodyWeightMainContent(
                state = state,
                onWeightChange = onWeightChange,
                onSaveTodayWeight = onSaveTodayWeight,
                onDeleteEntry = onDeleteEntry,
            )
        }
    }
}

@Composable
private fun BodyWeightMainContent(
    state: BodyWeightLogState,
    onWeightChange: (String) -> Unit,
    onSaveTodayWeight: () -> Unit,
    onDeleteEntry: (String) -> Unit,
) {
    var weightFieldValue by remember {
        mutableStateOf(state.weightInput.asTextFieldValueAtEnd())
    }

    LaunchedEffect(state.weightInput) {
        if (state.weightInput != weightFieldValue.text) {
            weightFieldValue = state.weightInput.asTextFieldValueAtEnd()
        }
    }

    AppScreenScaffold(
        title = "Body weight",
        subtitle = "Daily log",
        bottomBar = {
            AppBottomActions(navigationBarsPadding = false) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveTodayWeight,
                ) {
                    Text("Save today's weight")
                }
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = AppSpacing.screenHorizontal,
                top = contentPadding.calculateTopPadding() + AppSpacing.screenVertical,
                end = AppSpacing.screenHorizontal,
                bottom = contentPadding.calculateBottomPadding() + AppSpacing.screenVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.item),
        ) {
            item {
                LatestWeightPanel(entry = state.entries.firstOrNull())
            }

            item {
                WeightInputCard(
                    value = weightFieldValue,
                    validationMessage = state.validationMessage,
                    onValueChange = { value ->
                        val limitedValue = value.limitedTo(MAX_WEIGHT_INPUT_LENGTH)
                        weightFieldValue = limitedValue
                        onWeightChange(limitedValue.text)
                    },
                    onSave = onSaveTodayWeight,
                )
            }

            if (state.entries.isEmpty()) {
                item {
                    EmptyBodyWeightCard()
                }
            } else {
                items(state.entries, key = { it.id }) { entry ->
                    BodyWeightEntryCard(
                        entry = entry,
                        onDelete = { onDeleteEntry(entry.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LatestWeightPanel(entry: BodyWeightEntry?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.section),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            Text(
                text = "Current status",
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = entry?.let { "${it.weightKg.formatWeight()} kg" } ?: "No baseline yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = entry?.date ?: "Save today's weight to start the timeline",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun WeightInputCard(
    value: TextFieldValue,
    validationMessage: String?,
    onValueChange: (TextFieldValue) -> Unit,
    onSave: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.section),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            Text(
                text = "Log today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                label = { Text("Weight") },
                suffix = { Text("kg") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSave() },
                ),
            )
            AppValidationMessage(validationMessage)
        }
    }
}

@Composable
private fun EmptyBodyWeightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Text(
            modifier = Modifier.padding(AppSpacing.section),
            text = "No weight entries yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BodyWeightEntryCard(
    entry: BodyWeightEntry,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.section, vertical = AppSpacing.item),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${entry.weightKg.formatWeight()} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}

private fun Double.formatWeight(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }
}

private fun String.asTextFieldValueAtEnd(): TextFieldValue {
    return TextFieldValue(
        text = this,
        selection = TextRange(length),
    )
}

private fun TextFieldValue.limitedTo(maxLength: Int): TextFieldValue {
    if (text.length <= maxLength) {
        return this
    }

    val limitedText = text.take(maxLength)
    return copy(
        text = limitedText,
        selection = TextRange(selection.start.coerceAtMost(limitedText.length)),
    )
}

private const val MAX_WEIGHT_INPUT_LENGTH = 6
