package com.boristul.gymhelper

import com.boristul.gymhelper.di.initKoin
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    initKoin()

    application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "gymhelper",
    ) {
        App()
    }
    }
}
