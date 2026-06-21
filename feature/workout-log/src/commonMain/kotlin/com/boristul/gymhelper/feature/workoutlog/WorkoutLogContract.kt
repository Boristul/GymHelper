package com.boristul.gymhelper.feature.workoutlog

import com.boristul.gymhelper.core.mvi.MviEffect
import com.boristul.gymhelper.core.mvi.MviIntent
import com.boristul.gymhelper.domain.workout.WorkoutSession

data class WorkoutLogState(
    val isLoading: Boolean = true,
    val screen: WorkoutLogScreenState = WorkoutLogScreenState.Home,
    val workout: WorkoutSession? = null,
    val stageDescriptionInput: String = "",
    val draftStageDescription: String = "",
    val draftSets: List<WorkoutSetDraft> = emptyList(),
    val editingStageId: String? = null,
    val validationMessage: String? = null,
)

enum class WorkoutLogScreenState {
    Home,
    Workout,
    StageDescription,
    StageEditor,
}

data class WorkoutSetDraft(
    val localId: String,
    val order: Int,
    val weightInput: String = "",
    val repsInput: String = "",
)

sealed interface WorkoutLogIntent : MviIntent {
    data class ActiveWorkoutChanged(
        val workout: WorkoutSession,
    ) : WorkoutLogIntent

    data object StartWorkoutClicked : WorkoutLogIntent

    data object AddStageClicked : WorkoutLogIntent

    data class EditStageClicked(
        val stageId: String,
    ) : WorkoutLogIntent

    data class DeleteStageClicked(
        val stageId: String,
    ) : WorkoutLogIntent

    data class StageDescriptionChanged(
        val value: String,
    ) : WorkoutLogIntent

    data object ContinueToSetsClicked : WorkoutLogIntent

    data class DraftSetWeightChanged(
        val localId: String,
        val value: String,
    ) : WorkoutLogIntent

    data class DraftSetRepsChanged(
        val localId: String,
        val value: String,
    ) : WorkoutLogIntent

    data object AddDraftSetClicked : WorkoutLogIntent

    data object FinishStageClicked : WorkoutLogIntent

    data object StageSaved : WorkoutLogIntent

    data object BackClicked : WorkoutLogIntent
}

sealed interface WorkoutLogEffect : MviEffect
