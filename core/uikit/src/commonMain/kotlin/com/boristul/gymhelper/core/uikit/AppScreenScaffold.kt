package com.boristul.gymhelper.core.uikit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun AppScreenScaffold(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val controller = LocalAppScaffoldController.current
    if (controller == null) {
        Scaffold(
            bottomBar = bottomBar,
            content = content,
        )
        return
    }

    val owner = remember { Any() }
    val currentBottomBar = rememberUpdatedState(bottomBar)

    SideEffect {
        controller.setScreenState(
            owner = owner,
            screenState = AppScaffoldScreenState(
                title = title,
                onBack = onBack,
                bottomBar = currentBottomBar.value,
            ),
        )
    }

    DisposableEffect(owner) {
        onDispose {
            controller.clearScreenState(owner)
        }
    }

    content(controller.contentPadding)
}
