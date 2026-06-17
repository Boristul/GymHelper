package com.boristul.gymhelper.feature.workoutlog.di

import com.boristul.gymhelper.feature.workoutlog.WorkoutLogInteractor
import org.koin.dsl.module

val workoutLogFeatureModule = module {
    factory {
        WorkoutLogInteractor(
            workoutRepository = get(),
            dispatchers = get(),
        )
    }
}
