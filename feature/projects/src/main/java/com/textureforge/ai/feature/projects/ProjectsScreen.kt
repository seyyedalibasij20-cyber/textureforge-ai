package com.textureforge.ai.feature.projects

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.8): Project entities with engine/
 * platform/resolution defaults, linked analyses/QA/workflow history.
 * Tracked WIP stand-in wired into real navigation, per the build order in
 * Section 11.
 */
@Composable
fun ProjectsRoute(onOpenProject: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Library, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "Projects — build in progress",
                description = "Project management lands in the next implementation pass.",
                icon = Icons.Filled.Folder
            )
        }
    }
}

@Composable
fun ProjectDetailRoute(projectId: String, onBack: () -> Unit) {
    ProjectsRoute(onOpenProject = {})
}
