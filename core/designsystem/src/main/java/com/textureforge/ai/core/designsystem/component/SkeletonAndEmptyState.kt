package com.textureforge.ai.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.LocalMotionPreferences
import com.textureforge.ai.core.designsystem.theme.Radius
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * Shimmer-over-glass skeleton for every async surface (4.5) — never a bare
 * spinner. Respects reduce-motion by falling back to a static pulse-free
 * placeholder.
 */
@Composable
fun GlassSkeletonBlock(modifier: Modifier = Modifier, height: androidx.compose.ui.unit.Dp = 80.dp) {
    val motionPrefs = LocalMotionPreferences.current
    val shimmerAlpha = if (motionPrefs.reduceMotionEnabled) {
        0.12f
    } else {
        val transition = rememberInfiniteTransition(label = "skeleton_shimmer")
        val anim by transition.animateFloat(
            initialValue = 0.06f,
            targetValue = 0.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "skeleton_alpha"
        )
        anim
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(Radius.standard))
            .background(Color.White.copy(alpha = shimmerAlpha))
    )
}

@Composable
fun GlassSkeletonList(count: Int = 3, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        repeat(count) { GlassSkeletonBlock() }
    }
}

/** On-brand line-art empty state — used for Library, History, Projects when empty (4.5). */
@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Inbox,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier,
            tint = Color.White.copy(alpha = 0.35f)
        )
        Text(text = title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        action?.invoke()
    }
}
