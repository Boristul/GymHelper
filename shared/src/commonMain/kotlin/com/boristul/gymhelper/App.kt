package com.boristul.gymhelper

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.boristul.gymhelper.feature.workoutlog.WorkoutLogInteractor
import com.boristul.gymhelper.feature.workoutlog.WorkoutLogRoute
import org.koin.compose.koinInject

@Composable
fun App() {
    val workoutLogInteractor = koinInject<WorkoutLogInteractor>()

    MaterialTheme {
        WorkoutLogRoute(
            interactor = workoutLogInteractor,
        )
    }
}
