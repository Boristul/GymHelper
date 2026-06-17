package com.boristul.gymhelper.data.workout

import com.boristul.gymhelper.database.GymHelperDatabase
import com.boristul.gymhelper.domain.workout.WorkoutRepository
import com.boristul.gymhelper.domain.workout.WorkoutSession
import com.boristul.gymhelper.domain.workout.WorkoutSet
import com.boristul.gymhelper.domain.workout.WorkoutStage
import com.boristul.gymhelper.domain.workout.WorkoutStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SqlWorkoutRepository(
    private val database: GymHelperDatabase,
) : WorkoutRepository {
    private val queries = database.gymHelperQueries
    private val activeWorkout = MutableStateFlow(loadActiveWorkout())

    init {
        ensureActiveWorkoutExists()
        activeWorkout.value = loadActiveWorkout()
    }

    override fun observeActiveWorkout(): Flow<WorkoutSession> = activeWorkout

    override suspend fun addStage(description: String, sets: List<WorkoutSet>) {
        val workout = activeWorkout.value
        val nextStageOrder = workout.stages.size + 1
        val stageId = "${workout.id}-stage-$nextStageOrder"

        database.transaction {
            queries.updateWorkoutStatus(
                status = WorkoutStatus.InProgress.name,
                id = workout.id,
            )
            queries.insertWorkoutStage(
                id = stageId,
                workout_id = workout.id,
                description = description,
                position = nextStageOrder.toLong(),
            )

            sets.forEachIndexed { index, set ->
                val setOrder = index + 1
                queries.insertWorkoutSet(
                    id = "$stageId-set-$setOrder",
                    stage_id = stageId,
                    position = setOrder.toLong(),
                    weight_kg = set.weightKg,
                    reps = set.reps.toLong(),
                )
            }
        }

        activeWorkout.value = loadActiveWorkout()
    }

    private fun ensureActiveWorkoutExists() {
        queries.insertWorkoutSession(
            id = ACTIVE_WORKOUT_ID,
            title = "Today's training",
            status = WorkoutStatus.Planned.name,
        )
    }

    private fun loadActiveWorkout(): WorkoutSession {
        ensureActiveWorkoutExists()

        val session = queries
            .selectWorkoutSession(ACTIVE_WORKOUT_ID)
            .executeAsOne()

        val stages = queries
            .selectStagesForWorkout(session.id)
            .executeAsList()
            .map { stage ->
                WorkoutStage(
                    id = stage.id,
                    description = stage.description,
                    sets = queries
                        .selectSetsForStage(stage.id)
                        .executeAsList()
                        .map { set ->
                            WorkoutSet(
                                id = set.id,
                                order = set.position.toInt(),
                                weightKg = set.weight_kg,
                                reps = set.reps.toInt(),
                            )
                        },
                )
            }

        return WorkoutSession(
            id = session.id,
            title = session.title,
            status = WorkoutStatus.valueOf(session.status),
            stages = stages,
        )
    }

    private companion object {
        const val ACTIVE_WORKOUT_ID = "today"
    }
}
