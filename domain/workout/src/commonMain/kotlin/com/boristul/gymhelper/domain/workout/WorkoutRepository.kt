package com.boristul.gymhelper.domain.workout

import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeActiveWorkout(): Flow<WorkoutSession>

    suspend fun addStage(description: String, sets: List<WorkoutSet>)

    suspend fun updateStage(stageId: String, description: String, sets: List<WorkoutSet>)

    suspend fun deleteStage(stageId: String)
}
