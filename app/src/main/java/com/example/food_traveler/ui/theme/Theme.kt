package com.example.food_traveler.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BurgundyPrimary,
    onPrimary = White,
    primaryContainer = BurgundyLight,
    onPrimaryContainer = White,
    
    secondary = GoldAccent,
    onSecondary = Black,
    secondaryContainer = GoldAccent.copy(alpha = 0.3f),
    onSecondaryContainer = Black,
    
    tertiary = BurgundyDark,
    onTertiary = White,
    
    background = IvoryBackground,
    onBackground = TextPrimary,
    
    surface = White,
    onSurface = TextPrimary,
    
    surfaceVariant = LightGray,
    onSurfaceVariant = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = BurgundyPrimary,
    onPrimary = White,
    primaryContainer = BurgundyDark,
    onPrimaryContainer = White,
    
    secondary = GoldAccent,
    onSecondary = Black,
    secondaryContainer = GoldAccent.copy(alpha = 0.7f),
    onSecondaryContainer = Black,
    
    tertiary = BurgundyLight,
    onTertiary = White,
    
    background = Black,
    onBackground = White,
    
    surface = Color(0xFF121212),
    onSurface = White,
    
    surfaceVariant = Gray,
    onSurfaceVariant = LightGray
)

@Composable
fun FoodTravelerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}