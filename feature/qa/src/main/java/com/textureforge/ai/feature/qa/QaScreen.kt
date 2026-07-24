package com.textureforge.ai.feature.qa

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * PENDING FULL IMPLEMENTATION (Section 7.4): per-channel QA checklist,
 * severity-tagged QaIssueRow list, PDF export. Tracked WIP stand-in wired
 * into real navigation, per the build order in Section 11.
 */
@Composable
fun QaStudioRoute(
    onReportSaved: (String) -> Unit,
    onBack: () -> Unit,
    initialReportId: String? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Qa, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
            EmptyState(
                title = "QA Studio — build in progress",
                description = "The per-channel review flow and exportable QA report land in the next implementation pass.",
                icon = Icons.Filled.FactCheck
            )
        }
    }
}
