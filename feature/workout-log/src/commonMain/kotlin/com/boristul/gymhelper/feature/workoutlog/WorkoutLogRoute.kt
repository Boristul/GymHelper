package com.boristul.gymhelper.feature.workoutlog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun WorkoutLogRoute(interactor: WorkoutLogInteractor) {
    DisposableEffect(interactor) {
        interactor.start()
        onDispose {
            interactor.close()
        }
    }

    val state by interactor.state.collectAsState()

    WorkoutLogScreen(
        state = state,
        onIntent = interactor::emit,
    )
}
