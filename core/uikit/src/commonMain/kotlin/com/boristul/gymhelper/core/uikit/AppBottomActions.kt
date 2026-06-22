package com.boristul.gymhelper.core.uikit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppBottomActions(
    navigationBarsPadding: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bottomPaddingModifier = if (navigationBarsPadding) {
        Modifier.navigationBarsPadding()
    } else {
        Modifier
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = AppSpacing.compact,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .then(bottomPaddingModifier)
                .padding(
                    horizontal = AppSpacing.screenHorizontal,
                    vertical = AppSpacing.item,
                ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
            content = content,
        )
    }
}
