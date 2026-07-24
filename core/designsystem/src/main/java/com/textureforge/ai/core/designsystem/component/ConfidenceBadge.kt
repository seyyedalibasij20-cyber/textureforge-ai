package com.textureforge.ai.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.Radius
import com.textureforge.ai.core.designsystem.theme.SeverityInfo
import com.textureforge.ai.core.designsystem.theme.SeveritySuccess
import com.textureforge.ai.core.designsystem.theme.SeverityWarning
import com.textureforge.ai.core.designsystem.theme.Spacing

/** Mirrors [com.textureforge.ai.core.domain.model.ConfidenceLevel] without a :core:domain dependency (kept enum-parallel intentionally, per Law #1 layering). */
enum class BadgeConfidence { HIGH, MEDIUM, LOW, ESTIMATED }

/**
 * Shown next to EVERY AI-derived value in the app (Law #5). Never color
 * alone — always icon + text label, satisfying accessibility requirements
 * (Section 4.4, Law #9).
 */
@Composable
fun ConfidenceBadge(
    confidence: BadgeConfidence,
    modifier: Modifier = Modifier,
    label: String = confidence.defaultLabel()
) {
    val (color, icon) = when (confidence) {
        BadgeConfidence.HIGH -> SeveritySuccess to Icons.Filled.TrendingUp
        BadgeConfidence.MEDIUM -> SeverityWarning to Icons.Filled.TrendingFlat
        BadgeConfidence.LOW -> Color(0xFFFF6B6B) to Icons.Filled.TrendingDown
        BadgeConfidence.ESTIMATED -> SeverityInfo to Icons.Filled.HelpOutline
    }

    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.16f), RoundedCornerShape(Radius.pill))
            .padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
            .semantics { contentDescription = "AI confidence: $label" },
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text(text = label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

private fun BadgeConfidence.defaultLabel(): String = when (this) {
    BadgeConfidence.HIGH -> "High confidence"
    BadgeConfidence.MEDIUM -> "Medium confidence"
    BadgeConfidence.LOW -> "Low confidence"
    BadgeConfidence.ESTIMATED -> "AI estimate"
}

