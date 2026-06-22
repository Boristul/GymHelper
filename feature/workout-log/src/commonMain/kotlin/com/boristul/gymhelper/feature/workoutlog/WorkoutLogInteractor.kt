package com.boristul.gymhelper.feature.workoutlog

import com.boristul.gymhelper.core.coroutines.AppDispatchers
import com.boristul.gymhelper.core.mvi.Interactor
import com.boristul.gymhelper.core.mvi.Reducer
import com.boristul.gymhelper.domain.workout.WorkoutRepository
import com.boristul.gymhelper.domain.workout.WorkoutSet

class WorkoutLogInteractor(
    private val workoutRepository: WorkoutRepository,
    dispatchers: AppDispatchers,
) : Interactor.Abstract<WorkoutLogState, WorkoutLogIntent, WorkoutLogEffect>(
    initialState = WorkoutLogState(),
    reducer = workoutLogReducer,
    dispatchers = dispatchers,
) {
    override fun initialBind() {
        workoutRepository
            .observeActiveWorkout()
            .launchToIntent { workout ->
                WorkoutLogIntent.ActiveWorkoutChanged(workout)
            }
    }

    override suspend fun handleIntent(intent: WorkoutLogIntent) {
        when (intent) {
            is WorkoutLogIntent.FinishStageClicked -> saveDraftStageIfValid()
            is WorkoutLogIntent.DeleteStageClicked -> workoutRepository.deleteStage(intent.stageId)

            is WorkoutLogIntent.ActiveWorkoutChanged,
            is WorkoutLogIntent.StartWorkoutClicked,
            is WorkoutLogIntent.AddStageClicked,
            is WorkoutLogIntent.EditStageClicked,
            is WorkoutLogIntent.StageDescriptionChanged,
            is WorkoutLogIntent.ContinueToSetsClicked,
            is WorkoutLogIntent.DraftSetWeightChanged,
            is WorkoutLogIntent.DraftSetRepsChanged,
            is WorkoutLogIntent.AddDraftSetClicked,
            is WorkoutLogIntent.StageSaved,
            is WorkoutLogIntent.BackClicked -> Unit
        }
    }

    private suspend fun saveDraftStageIfValid() {
        val currentState = state.value
        val description = currentState.draftStageDescription.trim()
        val sets = currentState.draftSets.mapNotNull { draft ->
            val weight = draft.weightInput.normalizedDecimalOrNull()
            val reps = draft.repsInput.toIntOrNull()

            if (weight == null || reps == null || weight <= 0.0 || reps <= 0) {
                null
            } else {
                WorkoutSet(
                    id = draft.localId,
                    order = draft.order,
                    weightKg = weight,
                    reps = reps,
                )
            }
        }

        if (description.isBlank() || sets.size != currentState.draftSets.size || sets.isEmpty()) {
            return
        }

        val editingStageId = currentState.editingStageId
        if (editingStageId == null) {
            workoutRepository.addStage(
                description = description,
                sets = sets,
            )
        } else {
            workoutRepository.updateStage(
                stageId = editingStageId,
                description = description,
                sets = sets,
            )
        }
        emit(WorkoutLogIntent.StageSaved)
    }
}

private val workoutLogReducer: Reducer<WorkoutLogIntent, WorkoutLogState> = { intent, state ->
    when (intent) {
        is WorkoutLogIntent.ActiveWorkoutChanged -> state.copy(
            isLoading = false,
            workout = intent.workout,
        )

        is WorkoutLogIntent.StartWorkoutClicked -> state.copy(
            screen = WorkoutLogScreenState.Workout,
            validationMessage = null,
        )

        is WorkoutLogIntent.AddStageClicked -> state.copy(
            screen = WorkoutLogScreenState.StageDescription,
            stageDescriptionInput = "",
            draftStageDescription = "",
            draftSets = emptyList(),
            editingStageId = null,
            validationMessage = null,
        )

        is WorkoutLogIntent.EditStageClicked -> {
            val stage = state.workout
                ?.stages
                ?.firstOrNull { it.id == intent.stageId }

            if (stage == null) {
                state
            } else {
                state.copy(
                    screen = WorkoutLogScreenState.StageDescription,
                    stageDescriptionInput = stage.description,
                    draftStageDescription = stage.description,
                    draftSets = stage.sets.map { set ->
                        WorkoutSetDraft(
                            localId = set.id,
                            order = set.order,
                            weightInput = set.weightKg.formatWeightInput(),
                            repsInput = set.reps.toString(),
                        )
                    },
                    editingStageId = stage.id,
                    validationMessage = null,
                )
            }
        }

        is WorkoutLogIntent.DeleteStageClicked -> state.copy(
            validationMessage = null,
        )

        is WorkoutLogIntent.StageDescriptionChanged -> state.copy(
            stageDescriptionInput = intent.value,
            validationMessage = null,
        )

        is WorkoutLogIntent.ContinueToSetsClicked -> {
            val description = state.stageDescriptionInput.trim()
            if (description.isBlank()) {
                state.copy(validationMessage = "Add a stage description")
            } else {
                state.copy(
                    screen = WorkoutLogScreenState.StageEditor,
                    draftStageDescription = description,
                    draftSets = state.draftSets.ifEmpty { listOf(emptyDraftSet(order = 1)) },
                    validationMessage = null,
                )
            }
        }

        is WorkoutLogIntent.DraftSetWeightChanged -> state.copy(
            draftSets = state.draftSets.map { draft ->
                if (draft.localId == intent.localId) {
                    draft.copy(weightInput = intent.value.onlyDecimalInput().take(MAX_WEIGHT_INPUT_LENGTH))
                } else {
                    draft
                }
            },
            validationMessage = null,
        )

        is WorkoutLogIntent.DraftSetRepsChanged -> state.copy(
            draftSets = state.draftSets.map { draft ->
                if (draft.localId == intent.localId) {
                    draft.copy(repsInput = intent.value.filter(Char::isDigit).take(MAX_REPS_INPUT_LENGTH))
                } else {
                    draft
                }
            },
            validationMessage = null,
        )

        is WorkoutLogIntent.AddDraftSetClicked -> {
            val previous = state.draftSets.lastOrNull()
            val nextOrder = state.draftSets.size + 1
            state.copy(
                draftSets = state.draftSets + WorkoutSetDraft(
                    localId = "draft-$nextOrder",
                    order = nextOrder,
                    weightInput = previous?.weightInput.orEmpty(),
                    repsInput = previous?.repsInput.orEmpty(),
                ),
                validationMessage = null,
            )
        }

        is WorkoutLogIntent.FinishStageClicked -> {
            if (state.draftStageDescription.isBlank()) {
                state.copy(validationMessage = "Stage description is empty")
            } else if (state.draftSets.isEmpty()) {
                state.copy(validationMessage = "Add at least one set")
            } else if (state.draftSets.any { !it.isValid }) {
                state.copy(validationMessage = "Fill weight and reps for every set")
            } else {
                state.copy(validationMessage = null)
            }
        }

        is WorkoutLogIntent.StageSaved -> state.copy(
            screen = WorkoutLogScreenState.Workout,
            stageDescriptionInput = "",
            draftStageDescription = "",
            draftSets = emptyList(),
            editingStageId = null,
            validationMessage = null,
        )

        is WorkoutLogIntent.BackClicked -> when (state.screen) {
            WorkoutLogScreenState.Home -> state
            WorkoutLogScreenState.Workout -> state.copy(screen = WorkoutLogScreenState.Home)
            WorkoutLogScreenState.StageDescription -> state.copy(
                screen = WorkoutLogScreenState.Workout,
                stageDescriptionInput = "",
                draftStageDescription = "",
                draftSets = emptyList(),
                editingStageId = null,
                validationMessage = null,
            )
            WorkoutLogScreenState.StageEditor -> state.copy(
                screen = WorkoutLogScreenState.StageDescription,
                validationMessage = null,
            )
        }
    }
}

private val WorkoutSetDraft.isValid: Boolean
    get() {
        val weight = weightInput.normalizedDecimalOrNull()
        val reps = repsInput.toIntOrNull()
        return weight != null && reps != null && weight > 0.0 && reps > 0
    }

private fun emptyDraftSet(order: Int): WorkoutSetDraft {
    return WorkoutSetDraft(
        localId = "draft-$order",
        order = order,
    )
}

private fun String.onlyDecimalInput(): String {
    val normalized = replace(',', '.')
    var hasSeparator = false

    return buildString {
        normalized.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char == '.' && !hasSeparator -> {
                    append(char)
                    hasSeparator = true
                }
            }
        }
    }
}

private const val MAX_WEIGHT_INPUT_LENGTH = 6
private const val MAX_REPS_INPUT_LENGTH = 3

private fun String.normalizedDecimalOrNull(): Double? {
    return replace(',', '.').toDoubleOrNull()
}

private fun Double.formatWeightInput(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }
}
