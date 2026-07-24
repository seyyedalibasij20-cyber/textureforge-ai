package com.textureforge.ai.feature.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.7): searchable/filterable
 * Knowledge Base + user material grid, semantic search bar. Tracked WIP
 * stand-in wired into real navigation, per the build order in Section 11.
 */
@Composable
fun LibraryRoute(onOpenEntry: (String) -> Unit, initialSelectedEntryId: String? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Library, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "Library — build in progress",
                description = "The searchable material and knowledge library lands in the next implementation pass.",
                icon = Icons.Filled.LibraryBooks
            )
        }
    }
}
