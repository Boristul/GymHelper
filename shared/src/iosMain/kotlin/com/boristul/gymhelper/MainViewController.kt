package com.boristul.gymhelper

import androidx.compose.ui.window.ComposeUIViewController
import com.boristul.gymhelper.di.initKoin

fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}
