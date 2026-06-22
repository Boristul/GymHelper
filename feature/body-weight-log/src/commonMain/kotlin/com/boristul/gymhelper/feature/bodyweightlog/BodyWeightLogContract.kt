package com.boristul.gymhelper.feature.bodyweightlog

import com.boristul.gymhelper.core.mvi.MviEffect
import com.boristul.gymhelper.core.mvi.MviIntent
import com.boristul.gymhelper.domain.bodyweight.BodyWeightEntry

data class BodyWeightLogState(
    val isLoading: Boolean = true,
    val entries: List<BodyWeightEntry> = emptyList(),
    val weightInput: String = "",
    val validationMessage: String? = null,
)

sealed interface BodyWeightLogIntent : MviIntent {
    data class EntriesChanged(
        val entries: List<BodyWeightEntry>,
    ) : BodyWeightLogIntent

    data class WeightChanged(
        val value: String,
    ) : BodyWeightLogIntent

    data object SaveTodayWeightClicked : BodyWeightLogIntent

    data object TodayWeightSaved : BodyWeightLogIntent

    data class DeleteEntryClicked(
        val entryId: String,
    ) : BodyWeightLogIntent
}

sealed interface BodyWeightLogEffect : MviEffect
