package com.boristul.gymhelper.feature.workoutlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.boristul.gymhelper.core.uikit.AppLoadingState
import com.boristul.gymhelper.core.uikit.AppScreenColumn
import com.boristul.gymhelper.core.uikit.AppScreenHeader
import com.boristul.gymhelper.core.uikit.AppValidationMessage
import com.boristul.gymhelper.domain.workout.WorkoutSet
import com.boristul.gymhelper.domain.workout.WorkoutStage

@Composable
fun WorkoutLogScreen(
    state: WorkoutLogState,
    onIntent: (WorkoutLogIntent) -> Unit,
) {
    WorkoutLogContent(
        state = state,
        onStartWorkout = { onIntent(WorkoutLogIntent.StartWorkoutClicked) },
        onAddStage = { onIntent(WorkoutLogIntent.AddStageClicked) },
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
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
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
    AppScreenColumn {
        val stagesCount = state.workout?.stages?.size ?: 0

        Text(
            text = "GymHelper",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = if (stagesCount == 0) {
                "No stages in today's workout yet"
            } else {
                "$stagesCount stages in today's workout"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStartWorkout,
        ) {
            Text("Start workout")
        }
    }
}

@Composable
private fun WorkoutOverviewContent(
    state: WorkoutLogState,
    onAddStage: () -> Unit,
    onBack: () -> Unit,
) {
    val workout = state.workout ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            AppScreenHeader(
                title = workout.title,
                subtitle = "${workout.stages.size} stages",
                onBack = onBack,
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddStage,
            ) {
                Text("Add stage")
            }
        }

        if (workout.stages.isEmpty()) {
            item {
                EmptyWorkoutCard()
            }
        } else {
            items(workout.stages, key = { it.id }) { stage ->
                StageCard(stage = stage)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
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
    AppScreenColumn {
        AppScreenHeader(
            title = "New stage",
            subtitle = "Describe the exercise, machine, or block",
            onBack = onBack,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.stageDescriptionInput,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            singleLine = false,
            minLines = 3,
        )
        AppValidationMessage(state.validationMessage)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContinue,
        ) {
            Text("Continue")
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            AppScreenHeader(
                title = state.draftStageDescription,
                subtitle = "Add working sets",
                onBack = onBack,
            )
            AppValidationMessage(state.validationMessage)
        }

        items(state.draftSets, key = { it.localId }) { draft ->
            DraftSetCard(
                draft = draft,
                onWeightChange = { value -> onWeightChange(draft.localId, value) },
                onRepsChange = { value -> onRepsChange(draft.localId, value) },
            )
        }

        item {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddSet,
            ) {
                Text("Add another set")
            }
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onFinishStage,
            ) {
                Text("Finish stage")
            }
            Spacer(Modifier.height(12.dp))
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
private fun StageCard(stage: WorkoutStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stage.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stage.sets.forEach { set ->
                    SavedSetRow(set = set)
                }
            }
        }
    }
}

@Composable
private fun DraftSetCard(
    draft: WorkoutSetDraft,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Set ${draft.order}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = draft.weightInput,
                    onValueChange = onWeightChange,
                    label = { Text("Weight") },
                    suffix = { Text("kg") },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = draft.repsInput,
                    onValueChange = onRepsChange,
                    label = { Text("Reps") },
                    singleLine = true,
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
