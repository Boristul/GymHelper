package com.boristul.gymhelper.feature.workoutlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.boristul.gymhelper.core.uikit.AppBottomActions
import com.boristul.gymhelper.core.uikit.AppLoadingState
import com.boristul.gymhelper.core.uikit.AppScreenColumn
import com.boristul.gymhelper.core.uikit.AppScreenScaffold
import com.boristul.gymhelper.core.uikit.AppSpacing
import com.boristul.gymhelper.core.uikit.AppValidationMessage
import com.boristul.gymhelper.domain.workout.WorkoutSet
import com.boristul.gymhelper.domain.workout.WorkoutStage

private enum class DraftSetFocusTarget {
    Weight,
    Reps,
}

@Composable
fun WorkoutLogScreen(
    state: WorkoutLogState,
    onIntent: (WorkoutLogIntent) -> Unit,
) {
    WorkoutLogContent(
        state = state,
        onStartWorkout = { onIntent(WorkoutLogIntent.StartWorkoutClicked) },
        onAddStage = { onIntent(WorkoutLogIntent.AddStageClicked) },
        onEditStage = { stageId -> onIntent(WorkoutLogIntent.EditStageClicked(stageId)) },
        onDeleteStage = { stageId -> onIntent(WorkoutLogIntent.DeleteStageClicked(stageId)) },
        onDescriptionChange = { onIntent(WorkoutLogIntent.StageDescriptionChanged(it)) },
        onContinueToSets = { onIntent(WorkoutLogIntent.ContinueToSetsClicked) },
        onWeightChange = { localId, value ->
            onIntent(WorkoutLogIntent.DraftSetWeightChanged(localId, value))
        },
        onRepsChange = { localId, value ->
            onIntent(WorkoutLogIntent.DraftSetRepsChanged(localId, value))
        },
        onAddSet = { onIntent(WorkoutLogIntent.AddDraftSetClicked) },
        onFinishStage = { onIntent(WorkoutLogIntent.FinishStageClicked) },
        onBack = { onIntent(WorkoutLogIntent.BackClicked) },
    )
}

@Composable
fun WorkoutLogContent(
    state: WorkoutLogState,
    onStartWorkout: () -> Unit,
    onAddStage: () -> Unit,
    onEditStage: (String) -> Unit,
    onDeleteStage: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onContinueToSets: () -> Unit,
    onWeightChange: (String, String) -> Unit,
    onRepsChange: (String, String) -> Unit,
    onAddSet: () -> Unit,
    onFinishStage: () -> Unit,
    onBack: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            state.isLoading -> AppLoadingState()
            state.screen == WorkoutLogScreenState.Home -> HomeContent(
                state = state,
                onStartWorkout = onStartWorkout,
            )
            state.screen == WorkoutLogScreenState.Workout -> WorkoutOverviewContent(
                state = state,
                onAddStage = onAddStage,
                onEditStage = onEditStage,
                onDeleteStage = onDeleteStage,
                onBack = onBack,
            )
            state.screen == WorkoutLogScreenState.StageDescription -> StageDescriptionContent(
                state = state,
                onDescriptionChange = onDescriptionChange,
                onContinue = onContinueToSets,
                onBack = onBack,
            )
            state.screen == WorkoutLogScreenState.StageEditor -> StageEditorContent(
                state = state,
                onWeightChange = onWeightChange,
                onRepsChange = onRepsChange,
                onAddSet = onAddSet,
                onFinishStage = onFinishStage,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: WorkoutLogState,
    onStartWorkout: () -> Unit,
) {
    val stagesCount = state.workout?.stages?.size ?: 0

    AppScreenScaffold(
        title = "GymHelper",
        subtitle = "Training log",
        bottomBar = {
            AppBottomActions {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartWorkout,
                ) {
                    Text("Start workout")
                }
            }
        },
    ) { contentPadding ->
        AppScreenColumn(
            modifier = Modifier.padding(contentPadding),
        ) {
            Text(
                text = if (stagesCount == 0) {
                    "No stages in today's workout yet"
                } else {
                    "$stagesCount stages in today's workout"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Start a local workout, add stages, and record sets with weight and reps.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WorkoutOverviewContent(
    state: WorkoutLogState,
    onAddStage: () -> Unit,
    onEditStage: (String) -> Unit,
    onDeleteStage: (String) -> Unit,
    onBack: () -> Unit,
) {
    val workout = state.workout ?: return

    AppScreenScaffold(
        title = workout.title,
        subtitle = "${workout.stages.size} stages",
        onBack = onBack,
        bottomBar = {
            AppBottomActions {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAddStage,
                ) {
                    Text("Add stage")
                }
            }
        },
    ) { contentPadding ->
        WorkoutStagesList(
            contentPadding = contentPadding,
            stages = workout.stages,
            onEditStage = onEditStage,
            onDeleteStage = onDeleteStage,
        )
    }
}

@Composable
private fun WorkoutStagesList(
    contentPadding: PaddingValues,
    stages: List<WorkoutStage>,
    onEditStage: (String) -> Unit,
    onDeleteStage: (String) -> Unit,
) {
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
        if (stages.isEmpty()) {
            item { EmptyWorkoutCard() }
        } else {
            items(stages, key = { it.id }) { stage ->
                StageCard(
                    stage = stage,
                    onEdit = { onEditStage(stage.id) },
                    onDelete = { onDeleteStage(stage.id) },
                )
            }
        }
    }
}

@Composable
private fun StageDescriptionContent(
    state: WorkoutLogState,
    onDescriptionChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
) {
    val focusRequester = FocusRequester()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AppScreenScaffold(
        title = if (state.editingStageId == null) "New stage" else "Edit stage",
        subtitle = "Describe the exercise, machine, or block",
        onBack = onBack,
        bottomBar = {
            AppBottomActions {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onContinue,
                ) {
                    Text("Continue")
                }
            }
        },
    ) { contentPadding ->
        AppScreenColumn(
            modifier = Modifier.padding(contentPadding),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.stageDescriptionInput,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onContinue() },
                ),
            )
            AppValidationMessage(state.validationMessage)
        }
    }
}

@Composable
private fun StageEditorContent(
    state: WorkoutLogState,
    onWeightChange: (String, String) -> Unit,
    onRepsChange: (String, String) -> Unit,
    onAddSet: () -> Unit,
    onFinishStage: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.draftSets.size) {
        if (state.draftSets.isNotEmpty()) {
            listState.animateScrollToItem(index = state.draftSets.size)
        }
    }

    AppScreenScaffold(
        title = state.draftStageDescription,
        subtitle = "Add working sets",
        onBack = onBack,
        bottomBar = {
            AppBottomActions {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAddSet,
                ) {
                    Text("Add another set")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onFinishStage,
                ) {
                    Text(if (state.editingStageId == null) "Finish stage" else "Save stage")
                }
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = AppSpacing.screenHorizontal,
                top = contentPadding.calculateTopPadding() + AppSpacing.screenVertical,
                end = AppSpacing.screenHorizontal,
                bottom = contentPadding.calculateBottomPadding() + AppSpacing.screenVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            item {
                AppValidationMessage(state.validationMessage)
            }

            items(state.draftSets, key = { it.localId }) { draft ->
                val isLastDraft = draft.localId == state.draftSets.lastOrNull()?.localId
                val focusTarget = when {
                    !isLastDraft -> null
                    draft.order == 1 && draft.weightInput.isBlank() && draft.repsInput.isBlank() -> {
                        DraftSetFocusTarget.Weight
                    }
                    else -> DraftSetFocusTarget.Reps
                }

                DraftSetCard(
                    draft = draft,
                    focusTarget = focusTarget,
                    onWeightChange = { value -> onWeightChange(draft.localId, value) },
                    onRepsChange = { value -> onRepsChange(draft.localId, value) },
                    onAddSet = onAddSet,
                )
            }
        }
    }
}

@Composable
private fun EmptyWorkoutCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.section),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            Text(
                text = "No stages yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Add the first stage when you start an exercise or machine.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StageCard(
    stage: WorkoutStage,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.section),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.item),
        ) {
            Text(
                text = stage.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.compact)) {
                stage.sets.forEach { set ->
                    SavedSetRow(set = set)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.compact),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onEdit,
                ) {
                    Text("Edit")
                }
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDelete,
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun DraftSetCard(
    draft: WorkoutSetDraft,
    focusTarget: DraftSetFocusTarget?,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onAddSet: () -> Unit,
) {
    val weightFocusRequester = FocusRequester()
    val repsFocusRequester = FocusRequester()
    var weightFieldValue by remember(draft.localId) {
        mutableStateOf(draft.weightInput.asTextFieldValueAtEnd())
    }
    var repsFieldValue by remember(draft.localId) {
        mutableStateOf(draft.repsInput.asTextFieldValueAtEnd())
    }

    LaunchedEffect(draft.weightInput) {
        if (draft.weightInput != weightFieldValue.text) {
            weightFieldValue = draft.weightInput.asTextFieldValueAtEnd()
        }
    }

    LaunchedEffect(draft.repsInput) {
        if (draft.repsInput != repsFieldValue.text) {
            repsFieldValue = draft.repsInput.asTextFieldValueAtEnd()
        }
    }

    LaunchedEffect(focusTarget, draft.localId) {
        when (focusTarget) {
            DraftSetFocusTarget.Weight -> weightFocusRequester.requestFocus()
            DraftSetFocusTarget.Reps -> repsFocusRequester.requestFocus()
            null -> Unit
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            Text(
                text = "Set ${draft.order}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.compact),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(weightFocusRequester),
                    value = weightFieldValue,
                    onValueChange = { value ->
                        weightFieldValue = value
                        onWeightChange(value.text)
                    },
                    label = { Text("Weight") },
                    suffix = { Text("kg") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { repsFocusRequester.requestFocus() },
                    ),
                )
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(repsFocusRequester),
                    value = repsFieldValue,
                    onValueChange = { value ->
                        repsFieldValue = value
                        onRepsChange(value.text)
                    },
                    label = { Text("Reps") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onAddSet() },
                    ),
                )
            }
        }
    }
}

@Composable
private fun SavedSetRow(set: WorkoutSet) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Set ${set.order}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "${set.weightKg.formatWeight()} kg",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "${set.reps} reps",
            style = MaterialTheme.typography.bodyMedium,
        )
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
