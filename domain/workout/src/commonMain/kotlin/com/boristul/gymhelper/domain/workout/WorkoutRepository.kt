package com.boristul.gymhelper.domain.workout

import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeActiveWorkout(): Flow<WorkoutSession>

    suspend fun addStage(description: String, sets: List<WorkoutSet>)
}
