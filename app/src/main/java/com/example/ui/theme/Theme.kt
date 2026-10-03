package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElitePrimary,
    onPrimary = EliteTextPrimary,
    primaryContainer = ElitePrimaryDark,
    onPrimaryContainer = EliteTextPrimary,
    secondary = ElitePrimaryLight,
    onSecondary = EliteTextPrimary,
    background = EliteBackgroundDark,
    onBackground = EliteTextPrimary,
    surface = EliteSurfaceDark,
    onSurface = EliteTextPrimary,
    surfaceVariant = EliteCardDark,
    onSurfaceVariant = EliteTextSecondary,
    outline = EliteBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = ElitePrimary,
    onPrimary = EliteTextPrimary,
    background = EliteBackgroundDark, // Professional video editors stay dark-first
    surface = EliteSurfaceDark,
    onBackground = EliteTextPrimary,
    onSurface = EliteTextPrimary
)

@Composable
fun EliteEditTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Editing app is dark-first for color precision
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
