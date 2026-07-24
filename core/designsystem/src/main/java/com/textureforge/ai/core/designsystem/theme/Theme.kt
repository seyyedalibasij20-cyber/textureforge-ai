package com.textureforge.ai.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Dark-mode-first (4.1). We do not support a light theme: this is a
 * professional production tool modeled on color-grading/DAW software, not a
 * consumer app expected to follow system light/dark preference.
 */
private val TextureForgeColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentViolet,
    background = CanvasBase,
    surface = CanvasElevated,
    error = SeverityCritical,
    onPrimary = CanvasBase,
    onBackground = Color(0xFFEDEFF4),
    onSurface = Color(0xFFEDEFF4)
)

/** Carries the user's reduce-motion + blur-strength Settings into every screen (4.6, 7.9). */
data class MotionPreferences(
    val ambientMotionEnabled: Boolean = true,
    val reduceMotionEnabled: Boolean = false,
    val glassBlurStrength: Float = 1.0f,
    val liteMotionMode: Boolean = false
)

val LocalMotionPreferences = staticCompositionLocalOf { MotionPreferences() }

@Composable
fun TextureForgeTheme(
    motionPreferences: MotionPreferences = MotionPreferences(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalMotionPreferences provides motionPreferences) {
        MaterialTheme(
            colorScheme = TextureForgeColorScheme,
            typography = TextureForgeTypography,
            content = content
        )
    }
}

// re-export to avoid every call site importing androidx.compose.ui.graphics.Color separately
private typealias Color = androidx.compose.ui.graphics.Color
