package com.textureforge.ai.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.PrimaryButton
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.1): animated 4-pillar carousel +
 * Google Sign-In (Firebase Auth) with a Guest Mode fallback. This stand-in
 * still lets the app be run end-to-end (it advances straight to Home),
 * which is why it's wired for real rather than left unreachable. Tracked
 * WIP per the build order in Section 11.
 */
@Composable
fun OnboardingRoute(onFinished: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Home, modifier = Modifier.fillMaxSize())
        Column(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            PrimaryButton(text = "Continue as Guest", onClick = onFinished)
        }
    }
}
