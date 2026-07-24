package com.textureforge.ai.feature.analyze

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.3): CameraX capture + gallery
 * import, glass HUD overlay, multi-image cross-channel analysis, and the
 * branded Classify->Analyze->Validate->Report pipeline UI (4.6) land here
 * next, per the build order in Section 11. This is a tracked WIP stand-in
 * wired into real navigation — not a silently-omitted feature.
 */
@Composable
fun AnalyzeRoute(onResult: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Analyze, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "Analyze — build in progress",
                description = "Camera capture and vision analysis land in the next implementation pass.",
                icon = Icons.Filled.PhotoCamera
            )
        }
    }
}

@Composable
fun AnalysisResultDetailRoute(analysisId: String, onGenerateWorkflow: () -> Unit, onBack: () -> Unit) {
    AnalyzeRoute(onResult = {})
}
