package com.boristul.gymhelper.core.uikit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class AppScaffoldScreenState(
    val title: String = "",
    val onBack: (() -> Unit)? = null,
    val bottomBar: (@Composable () -> Unit)? = null,
)

@Stable
class AppScaffoldController {
    var screenState by mutableStateOf(AppScaffoldScreenState())
        private set

    var contentPadding by mutableStateOf(PaddingValues(0.dp))

    private var owner: Any? = null

    fun setScreenState(
        owner: Any,
        screenState: AppScaffoldScreenState,
    ) {
        this.owner = owner
        this.screenState = screenState
    }

    fun clearScreenState(owner: Any) {
        if (this.owner == owner) {
            this.owner = null
            screenState = AppScaffoldScreenState()
        }
    }
}

val LocalAppScaffoldController = compositionLocalOf<AppScaffoldController?> { null }

@Composable
fun rememberAppScaffoldController(): AppScaffoldController {
    return remember { AppScaffoldController() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffoldTopBar(
    controller: AppScaffoldController,
) {
    val screenState = controller.screenState
    if (screenState.title.isBlank()) {
        return
    }

    TopAppBar(
        title = {
            Text(
                text = screenState.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        },
        navigationIcon = {
            val onBack = screenState.onBack
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = AppBackIcon,
                        contentDescription = "Back",
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        expandedHeight = 64.dp,
    )
}

private val AppBackIcon: ImageVector = ImageVector.Builder(
    name = "AppBackIcon",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(20f, 11f)
        horizontalLineTo(7.83f)
        lineTo(13.42f, 5.41f)
        lineTo(12f, 4f)
        lineTo(4f, 12f)
        lineTo(12f, 20f)
        lineTo(13.41f, 18.59f)
        lineTo(7.83f, 13f)
        horizontalLineTo(20f)
        verticalLineTo(11f)
        close()
    }
}.build()
