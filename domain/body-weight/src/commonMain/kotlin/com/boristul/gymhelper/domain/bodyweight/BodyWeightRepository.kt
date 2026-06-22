package com.boristul.gymhelper.domain.bodyweight

import kotlinx.coroutines.flow.Flow

interface BodyWeightRepository {
    fun observeEntries(): Flow<List<BodyWeightEntry>>

    suspend fun saveTodayWeight(weightKg: Double)

    suspend fun deleteEntry(entryId: String)
}
