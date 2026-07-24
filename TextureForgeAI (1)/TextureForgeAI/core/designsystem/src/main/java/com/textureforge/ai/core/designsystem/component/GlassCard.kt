package com.textureforge.ai.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.GlassTier
import com.textureforge.ai.core.designsystem.theme.LocalMotionPreferences
import com.textureforge.ai.core.designsystem.theme.Radius
import com.textureforge.ai.core.designsystem.theme.Spacing

/**
 * The primary surface component (4.1, 4.5). EVERY card-like surface in the
 * app must be built from this — never a one-off `Modifier.background(...)`
 * (Law #10). Simulates backdrop blur via a layered semi-transparent surface
 * + [RenderEffect] blur where available (API 31+), gracefully falling back
 * to a translucent scrim with a slightly higher alpha on older APIs so the
 * card still reads clearly against the ambient background.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    tier: GlassTier = GlassTier.Standard,
    cornerRadius: Dp = Radius.standard,
    contentPadding: Dp = Spacing.md,
    content: @Composable () -> Unit
) {
    val motionPrefs = LocalMotionPreferences.current
    val blurStrength = motionPrefs.glassBlurStrength.coerceIn(0f, 1f)
    val density = LocalDensity.current
    val shape = RoundedCornerShape(cornerRadius)

    val surfaceAlpha = if (android.os.Build.VERSION.SDK_INT >= 31) {
        tier.surfaceAlpha * blurStrength.coerceAtLeastVisible()
    } else {
        // Older APIs get no true blur-through, so raise the scrim alpha to
        // preserve legibility (still translucent, just less see-through).
        (tier.surfaceAlpha * 2.2f).coerceAtMost(0.42f)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (android.os.Build.VERSION.SDK_INT >= 31 && blurStrength > 0f) {
                    val blurPx = with(density) { (tier.blurRadius * blurStrength).toPx() }
                    Modifier.graphicsLayer {
                        renderEffect = RenderEffect
                            .createBlurEffect(blurPx, blurPx, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
                } else Modifier
            )
            .background(Color.White.copy(alpha = surfaceAlpha), shape)
            .border(1.dp, Color.White.copy(alpha = tier.borderAlpha), shape)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

private fun Float.coerceAtLeastVisible(): Float = if (this < 0.15f) 0.15f else this
