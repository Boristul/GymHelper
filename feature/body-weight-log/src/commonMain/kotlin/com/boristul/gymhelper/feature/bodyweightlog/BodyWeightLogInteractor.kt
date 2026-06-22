package com.boristul.gymhelper.feature.bodyweightlog

import com.boristul.gymhelper.core.coroutines.AppDispatchers
import com.boristul.gymhelper.core.mvi.Interactor
import com.boristul.gymhelper.core.mvi.Reducer
import com.boristul.gymhelper.domain.bodyweight.BodyWeightRepository

class BodyWeightLogInteractor(
    private val bodyWeightRepository: BodyWeightRepository,
    dispatchers: AppDispatchers,
) : Interactor.Abstract<BodyWeightLogState, BodyWeightLogIntent, BodyWeightLogEffect>(
    initialState = BodyWeightLogState(),
    reducer = bodyWeightLogReducer,
    dispatchers = dispatchers,
) {
    override fun initialBind() {
        bodyWeightRepository
            .observeEntries()
            .launchToIntent { entries ->
                BodyWeightLogIntent.EntriesChanged(entries)
            }
    }

    override suspend fun handleIntent(intent: BodyWeightLogIntent) {
        when (intent) {
            is BodyWeightLogIntent.SaveTodayWeightClicked -> saveTodayWeightIfValid()
            is BodyWeightLogIntent.DeleteEntryClicked -> bodyWeightRepository.deleteEntry(intent.entryId)

            is BodyWeightLogIntent.EntriesChanged,
            is BodyWeightLogIntent.WeightChanged,
            is BodyWeightLogIntent.TodayWeightSaved -> Unit
        }
    }

    private suspend fun saveTodayWeightIfValid() {
        val weight = state.value.weightInput.normalizedDecimalOrNull()
        if (weight == null || weight <= 0.0) {
            return
        }

        bodyWeightRepository.saveTodayWeight(weightKg = weight)
        emit(BodyWeightLogIntent.TodayWeightSaved)
    }
}

private val bodyWeightLogReducer: Reducer<BodyWeightLogIntent, BodyWeightLogState> = { intent, state ->
    when (intent) {
        is BodyWeightLogIntent.EntriesChanged -> state.copy(
            isLoading = false,
            entries = intent.entries,
        )

        is BodyWeightLogIntent.WeightChanged -> state.copy(
            weightInput = intent.value.onlyDecimalInput().take(MAX_WEIGHT_INPUT_LENGTH),
            validationMessage = null,
        )

        is BodyWeightLogIntent.SaveTodayWeightClicked -> {
            val weight = state.weightInput.normalizedDecimalOrNull()
            if (weight == null || weight <= 0.0) {
                state.copy(validationMessage = "Enter weight")
            } else {
                state.copy(validationMessage = null)
            }
        }

        is BodyWeightLogIntent.TodayWeightSaved -> state.copy(
            weightInput = "",
            validationMessage = null,
        )

        is BodyWeightLogIntent.DeleteEntryClicked -> state.copy(
            validationMessage = null,
        )
    }
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

private fun String.normalizedDecimalOrNull(): Double? {
    return replace(',', '.').toDoubleOrNull()
}

private const val MAX_WEIGHT_INPUT_LENGTH = 6
