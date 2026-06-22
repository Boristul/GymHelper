package com.boristul.gymhelper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.boristul.gymhelper.core.uikit.AppScaffoldTopBar
import com.boristul.gymhelper.core.uikit.GymHelperTheme
import com.boristul.gymhelper.core.uikit.LocalAppScaffoldController
import com.boristul.gymhelper.core.uikit.rememberAppScaffoldController
import com.boristul.gymhelper.feature.bodyweightlog.BodyWeightLogInteractor
import com.boristul.gymhelper.feature.bodyweightlog.BodyWeightLogRoute
import com.boristul.gymhelper.feature.workoutlog.WorkoutLogInteractor
import com.boristul.gymhelper.feature.workoutlog.WorkoutLogRoute
import org.koin.compose.koinInject

@Composable
fun App() {
    GymHelperTheme {
        var selectedTab by remember { mutableStateOf(AppTab.Workouts) }
        val appScaffoldController = rememberAppScaffoldController()
        val density = LocalDensity.current
        val isKeyboardVisible = WindowInsets.ime.getBottom(density) > 0

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppScaffoldTopBar(appScaffoldController)
            },
            bottomBar = {
                Column {
                    appScaffoldController.screenState.bottomBar?.invoke()
                    if (!isKeyboardVisible) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ) {
                            AppTab.entries.forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                        )
                                    },
                                    label = { Text(tab.title) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                )
                            }
                        }
                    }
                }
            },
        ) { contentPadding ->
            SideEffect {
                appScaffoldController.contentPadding = contentPadding
            }

            CompositionLocalProvider(
                LocalAppScaffoldController provides appScaffoldController,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        AppTab.Workouts -> {
                            val workoutLogInteractor = koinInject<WorkoutLogInteractor>()
                            WorkoutLogRoute(
                                interactor = workoutLogInteractor,
                            )
                        }

                        AppTab.BodyWeight -> {
                            val bodyWeightLogInteractor = koinInject<BodyWeightLogInteractor>()
                            BodyWeightLogRoute(
                                interactor = bodyWeightLogInteractor,
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class AppTab(
    val title: String,
    val icon: ImageVector,
) {
    Workouts(
        title = "Workouts",
        icon = AppWorkoutIcon,
    ),
    BodyWeight(
        title = "Weight",
        icon = AppWeightIcon,
    ),
}

private val AppWorkoutIcon: ImageVector = ImageVector.Builder(
    name = "AppWorkoutIcon",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(5f, 9f)
        horizontalLineTo(7f)
        verticalLineTo(15f)
        horizontalLineTo(5f)
        verticalLineTo(9f)
        close()
        moveTo(2f, 11f)
        horizontalLineTo(4f)
        verticalLineTo(13f)
        horizontalLineTo(2f)
        verticalLineTo(11f)
        close()
        moveTo(8f, 11f)
        horizontalLineTo(16f)
        verticalLineTo(13f)
        horizontalLineTo(8f)
        verticalLineTo(11f)
        close()
        moveTo(17f, 9f)
        horizontalLineTo(19f)
        verticalLineTo(15f)
        horizontalLineTo(17f)
        verticalLineTo(9f)
        close()
        moveTo(20f, 11f)
        horizontalLineTo(22f)
        verticalLineTo(13f)
        horizontalLineTo(20f)
        verticalLineTo(11f)
        close()
    }
}.build()

private val AppWeightIcon: ImageVector = ImageVector.Builder(
    name = "AppWeightIcon",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(7f, 5f)
        horizontalLineTo(17f)
        curveTo(18.1f, 5f, 19f, 5.9f, 19f, 7f)
        verticalLineTo(18f)
        curveTo(19f, 19.1f, 18.1f, 20f, 17f, 20f)
        horizontalLineTo(7f)
        curveTo(5.9f, 20f, 5f, 19.1f, 5f, 18f)
        verticalLineTo(7f)
        curveTo(5f, 5.9f, 5.9f, 5f, 7f, 5f)
        close()
        moveTo(9f, 8f)
        curveTo(9f, 9.7f, 10.3f, 11f, 12f, 11f)
        curveTo(13.7f, 11f, 15f, 9.7f, 15f, 8f)
        horizontalLineTo(13f)
        curveTo(13f, 8.6f, 12.6f, 9f, 12f, 9f)
        curveTo(11.4f, 9f, 11f, 8.6f, 11f, 8f)
        horizontalLineTo(9f)
        close()
    }
}.build()
