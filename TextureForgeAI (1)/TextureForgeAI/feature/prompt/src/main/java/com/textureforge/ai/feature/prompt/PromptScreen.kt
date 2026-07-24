package com.textureforge.ai.feature.prompt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.6): structured prompt form ->
 * optimized reference-image prompt output with Copy / Send-to-provider
 * actions. Tracked WIP stand-in wired into real navigation, per the build
 * order in Section 11.
 */
@Composable
fun PromptStudioRoute(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Prompt, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "Prompt Studio — build in progress",
                description = "The prompt-engineering form and optimized output land in the next implementation pass.",
                icon = Icons.Filled.AutoAwesome
            )
        }
    }
}
