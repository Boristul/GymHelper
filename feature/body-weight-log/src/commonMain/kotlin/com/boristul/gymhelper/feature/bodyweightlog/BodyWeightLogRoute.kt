package com.boristul.gymhelper.feature.bodyweightlog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun BodyWeightLogRoute(interactor: BodyWeightLogInteractor) {
    DisposableEffect(interactor) {
        interactor.start()
        onDispose {
            interactor.close()
        }
    }

    val state by interactor.state.collectAsState()

    BodyWeightLogScreen(
        state = state,
        onIntent = interactor::emit,
    )
}
