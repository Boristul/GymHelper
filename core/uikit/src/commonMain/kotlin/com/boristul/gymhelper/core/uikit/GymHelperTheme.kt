package com.boristul.gymhelper.core.uikit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val GymHelperDarkColors = darkColorScheme(
    primary = Color(0xFFB7F34D),
    onPrimary = Color(0xFF172100),
    primaryContainer = Color(0xFF263900),
    onPrimaryContainer = Color(0xFFD2FF78),
    secondary = Color(0xFF80D8FF),
    onSecondary = Color(0xFF003548),
    secondaryContainer = Color(0xFF084D64),
    onSecondaryContainer = Color(0xFFB9EAFF),
    tertiary = Color(0xFFFFB86B),
    onTertiary = Color(0xFF442A00),
    tertiaryContainer = Color(0xFF633F00),
    onTertiaryContainer = Color(0xFFFFDDB2),
    background = Color(0xFF0B0F0E),
    onBackground = Color(0xFFE4E8E3),
    surface = Color(0xFF101513),
    onSurface = Color(0xFFE4E8E3),
    surfaceVariant = Color(0xFF3F4942),
    onSurfaceVariant = Color(0xFFBFC9C1),
    surfaceContainerLowest = Color(0xFF070A09),
    surfaceContainerLow = Color(0xFF141A17),
    surfaceContainer = Color(0xFF1A211E),
    surfaceContainerHigh = Color(0xFF222A26),
    surfaceContainerHighest = Color(0xFF2D3531),
    outline = Color(0xFF89938B),
    outlineVariant = Color(0xFF3F4942),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val GymHelperLightColors = lightColorScheme(
    primary = Color(0xFF4E6D00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2FF78),
    onPrimaryContainer = Color(0xFF172100),
    secondary = Color(0xFF006781),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB9EAFF),
    onSecondaryContainer = Color(0xFF001F29),
    tertiary = Color(0xFF805600),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB2),
    onTertiaryContainer = Color(0xFF281900),
    background = Color(0xFFFBFDF7),
    onBackground = Color(0xFF191D1A),
    surface = Color(0xFFFBFDF7),
    onSurface = Color(0xFF191D1A),
    surfaceVariant = Color(0xFFDBE5DD),
    onSurfaceVariant = Color(0xFF3F4942),
    outline = Color(0xFF6F7971),
)

private val GymHelperTypography: Typography
    @Composable get() = MaterialTheme.typography.copy(
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Medium,
        ),
    )

private val GymHelperShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
)

@Composable
fun GymHelperTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) GymHelperDarkColors else GymHelperLightColors,
        typography = GymHelperTypography,
        shapes = GymHelperShapes,
        content = content,
    )
}
