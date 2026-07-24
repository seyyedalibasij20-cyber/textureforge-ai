package com.textureforge.ai.feature.workflow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.5): structured material/engine/
 * platform/resolution input -> WorkflowStepCard list output. Tracked WIP
 * stand-in wired into real navigation, per the build order in Section 11.
 */
@Composable
fun WorkflowGeneratorRoute(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Workflow, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "Workflow Generator — build in progress",
                description = "Structured production workflow generation lands in the next implementation pass.",
                icon = Icons.Filled.AccountTree
            )
        }
    }
}
