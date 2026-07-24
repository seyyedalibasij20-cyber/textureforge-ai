package com.textureforge.ai.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.AccentCyan
import com.textureforge.ai.core.designsystem.theme.AccentViolet
import com.textureforge.ai.core.designsystem.theme.MotionDuration
import com.textureforge.ai.core.designsystem.theme.Radius
import com.textureforge.ai.core.designsystem.theme.Spacing

/** Mirrors [com.textureforge.ai.core.domain.model.ReasoningStage] (kept enum-parallel, see note in ConfidenceBadge). */
enum class TfPipelineStage(val label: String) {
    Classify("Classify"),
    Analyze("Analyze"),
    Validate("Validate"),
    Report("Report")
}

/**
 * The dedicated, branded "thinking" state referenced in 4.6 — visually
 * communicates the multi-stage reasoning pipeline from Section 6.2 rather
 * than a generic spinner. Every AI-backed screen (Analyze, QA, Workflow,
 * Prompt Studio) uses this same component for its loading state (Law #10).
 */
@Composable
fun PipelineProgressIndicator(
    currentStage: TfPipelineStage,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth(), contentPadding = Spacing.md) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TfPipelineStage.entries.forEachIndexed { index, stage ->
                StageDot(stage = stage, currentStage = currentStage)
                if (index != TfPipelineStage.entries.lastIndex) {
                    StageConnector(
                        completed = stage.ordinal < currentStage.ordinal
                    )
                }
            }
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "${currentStage.label}…",
            style = MaterialTheme.typography.labelMedium,
            color = AccentCyan
        )
    }
}

@Composable
private fun StageDot(stage: TfPipelineStage, currentStage: TfPipelineStage) {
    val isActive = stage == currentStage
    val isDone = stage.ordinal < currentStage.ordinal
    val color by animateColorAsState(
        targetValue = when {
            isDone -> AccentCyan
            isActive -> AccentViolet
            else -> Color.White.copy(alpha = 0.15f)
        },
        animationSpec = tween(MotionDuration.STANDARD),
        label = "stage_dot_color"
    )
    Box(
        modifier = Modifier
            .height(10.dp)
            .clip(RoundedCornerShape(Radius.pill))
            .background(color)
            .then(Modifier)
            .width(if (isActive) 24.dp else 10.dp)
    )
}

@Composable
private fun RowScope.StageConnector(completed: Boolean) {
    val color by animateColorAsState(
        targetValue = if (completed) AccentCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
        animationSpec = tween(MotionDuration.STANDARD),
        label = "connector_color"
    )
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .background(color)
    )
}
