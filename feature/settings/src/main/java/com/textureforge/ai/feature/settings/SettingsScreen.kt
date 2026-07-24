package com.textureforge.ai.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.9): AI provider status, ambient
 * motion / reduce-motion toggles, blur intensity slider, offline data
 * management, export/delete account data. Tracked WIP stand-in wired into
 * real navigation, per the build order in Section 11.
 */
@Composable
fun SettingsRoute() {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Settings, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "Settings — build in progress",
                description = "AI provider status and motion/appearance controls land in the next implementation pass.",
                icon = Icons.Filled.Settings
            )
        }
    }
}
