package com.boristul.gymhelper.data.workout.di

import com.boristul.gymhelper.data.workout.SqlWorkoutRepository
import com.boristul.gymhelper.domain.workout.WorkoutRepository
import org.koin.dsl.module

val workoutDataModule = module {
    single<WorkoutRepository> { SqlWorkoutRepository(get()) }
}
