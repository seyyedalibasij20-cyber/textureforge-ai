package com.textureforge.ai.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- 4.1 Base Canvas: deep near-black, never pure #000000 ---
val CanvasBase = Color(0xFF0A0B0F)
val CanvasElevated = Color(0xFF0D0E14)

// --- 4.4 Signature Accent Gradient: cyan -> violet ---
val AccentCyan = Color(0xFF39E7FF)
val AccentViolet = Color(0xFF9C6BFF)
val SignatureAccentGradient = Brush.linearGradient(listOf(AccentCyan, AccentViolet))

// --- Glass surface tones ---
val GlassWhite = Color(0xFFFFFFFF)
val GlassHairline = Color(0xFFFFFFFF)

// --- 4.4 Semantic QA severity colors (always paired with icon + text, never color alone) ---
val SeverityCritical = Color(0xFFFF6B6B) // red-coral
val SeverityWarning = Color(0xFFFFB74D)  // amber
val SeverityInfo = Color(0xFF39E7FF)     // cyan
val SeveritySuccess = Color(0xFF4CD9A0)  // emerald

// --- 4.4 Material category accent colors (chips/icons only, never full backgrounds) ---
val CategoryStone = Color(0xFFB0A99F)
val CategoryMetal = Color(0xFFB8C4D0)
val CategoryWood = Color(0xFFC98A55)
val CategoryOrganic = Color(0xFF7FBF7F)
val CategoryFabric = Color(0xFFE0A6C4)
val CategorySynthetic = Color(0xFF7FA8FF)
val CategoryGroundTerrain = Color(0xFFA98957)
val CategoryGlassCeramic = Color(0xFF9FE3E0)

// --- Ambient Flow Field palettes per screen context (4.2) ---
data class AmbientPalette(val colors: List<Color>)

object AmbientPalettes {
    val Home = AmbientPalette(listOf(Color(0xFF2BD1E8), Color(0xFF8A5CF7), Color(0xFFF7B84C)))
    val Analyze = AmbientPalette(listOf(Color(0xFF1FBFA8), Color(0xFFE04FA0)))
    val Qa = AmbientPalette(listOf(Color(0xFFF7A84C), Color(0xFFFF7A59)))
    val Workflow = AmbientPalette(listOf(Color(0xFF4C8CF7), Color(0xFF2BD1E8)))
    val Prompt = AmbientPalette(listOf(Color(0xFF8A5CF7), Color(0xFFE04FA0)))
    val Library = AmbientPalette(listOf(Color(0xFF2BD1E8), Color(0xFF7FBF7F)))
    val Settings = AmbientPalette(listOf(Color(0xFF6B7280), Color(0xFF8A5CF7)))
}
