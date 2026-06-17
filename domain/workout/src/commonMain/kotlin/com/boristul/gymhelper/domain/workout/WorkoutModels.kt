package com.boristul.gymhelper.domain.workout

data class WorkoutSession(
    val id: String,
    val title: String,
    val status: WorkoutStatus,
    val stages: List<WorkoutStage>,
)

enum class WorkoutStatus {
    Planned,
    InProgress,
    Completed,
}

data class WorkoutStage(
    val id: String,
    val description: String,
    val sets: List<WorkoutSet>,
)

data class WorkoutSet(
    val id: String,
    val order: Int,
    val weightKg: Double,
    val reps: Int,
)
