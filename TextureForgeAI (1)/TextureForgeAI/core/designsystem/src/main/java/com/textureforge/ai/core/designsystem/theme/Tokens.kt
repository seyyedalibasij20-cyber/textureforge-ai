package com.textureforge.ai.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Section 4.5: "consistent padding scale of 4/8/12/16/24/32dp". This is the
 * single source of truth for spacing — no Composable in the app should ever
 * write a raw `.dp` literal for padding/gaps; it references [Spacing].
 */
object Spacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
}

/** Section 4.5: "corner radius 20-28dp" for glass surfaces, plus smaller radii for chips/pills. */
object Radius {
    val chip: Dp = 12.dp
    val standard: Dp = 20.dp
    val prominent: Dp = 28.dp
    val pill: Dp = 999.dp
}

/** Minimum touch target size (Law #9, accessibility). */
val MinTouchTarget: Dp = 48.dp

/**
 * Blur/elevation intensity tiers referenced by GlassCard (Section 4.5).
 * Actual blur radius is additionally scaled by the user's
 * "glass blur strength" Settings slider (Section 7.9).
 */
enum class GlassTier(val blurRadius: Dp, val surfaceAlpha: Float, val borderAlpha: Float, val elevation: Dp) {
    Subtle(blurRadius = 8.dp, surfaceAlpha = 0.08f, borderAlpha = 0.10f, elevation = 2.dp),
    Standard(blurRadius = 16.dp, surfaceAlpha = 0.12f, borderAlpha = 0.15f, elevation = 6.dp),
    Prominent(blurRadius = 24.dp, surfaceAlpha = 0.18f, borderAlpha = 0.20f, elevation = 12.dp)
}

/** Motion durations in ms, shared across screen transitions and micro-interactions (4.6). */
object MotionDuration {
    const val FAST = 150
    const val STANDARD = 300
    const val SLOW = 450
    const val AMBIENT_BLOB_MIN_MS = 20_000
    const val AMBIENT_BLOB_MAX_MS = 45_000
}
