package com.boristul.gymhelper.di

import com.boristul.gymhelper.core.coroutines.di.coroutinesModule
import com.boristul.gymhelper.data.workout.di.workoutDataModule
import com.boristul.gymhelper.database.di.databaseModule
import com.boristul.gymhelper.feature.workoutlog.di.workoutLogFeatureModule

val appModules = listOf(
    coroutinesModule,
    databaseModule,
    workoutDataModule,
    workoutLogFeatureModule,
)
