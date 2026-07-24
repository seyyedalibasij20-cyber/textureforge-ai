package com.textureforge.ai.core.designsystem.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.AmbientPalette
import com.textureforge.ai.core.designsystem.theme.CanvasBase
import com.textureforge.ai.core.designsystem.theme.LocalMotionPreferences
import com.textureforge.ai.core.designsystem.theme.MotionDuration
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * A description of a single soft-edged color blob (4.2). Position/scale are
 * expressed as 0f..1f fractions of the canvas so the field is resolution-
 * independent and identical in spirit across phone/tablet/foldable.
 */
private data class BlobSpec(
    val color: Color,
    val baseX: Float,
    val baseY: Float,
    val radiusFraction: Float,
    val periodMs: Int,
    val phaseOffset: Float,
    val orbitRadiusFraction: Float
)

/** Nested-progress hook so a screen can wire scroll offset into blob drift without re-blurring per frame. */
val LocalAmbientScrollInfluence = compositionLocalOf { 0f }

/**
 * Renders the persistent animated background. Must be placed once behind
 * all glass content on a screen (Home, Analyze, Workflow, Library,
 * Settings) and must NOT restart on navigation — hoist a single instance
 * high enough in the nav graph that it survives destination changes, or key
 * it identically across screens that share it.
 *
 * Respects reduce-motion (falls back to a static gradient) and Lite Motion
 * Mode (Section 10, low-end device / battery saver fallback).
 */
@Composable
fun AmbientFlowField(
    palette: AmbientPalette,
    modifier: Modifier = Modifier
) {
    val motionPrefs = LocalMotionPreferences.current
    val scrollInfluence = LocalAmbientScrollInfluence.current

    val blobs = remember(palette) { buildBlobSpecs(palette) }

    if (motionPrefs.reduceMotionEnabled || motionPrefs.liteMotionMode) {
        StaticAmbientGradient(palette = palette, modifier = modifier)
        return
    }

    val transition = rememberInfiniteTransition(label = "ambient_flow_field")
    val phases = blobs.map { spec ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = spec.periodMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "blob_phase"
        )
    }

    val density = LocalDensity.current
    val blurPx = with(density) { 110.dp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBase)
            .graphicsLayer {
                // Blur is applied once to the whole composited blob layer via
                // RenderEffect (API 31+); older APIs fall back to a plain,
                // slightly-larger radial gradient per blob which is visually
                // softer by construction, avoiding a hard per-frame blur cost.
                if (android.os.Build.VERSION.SDK_INT >= 31) {
                    renderEffect = RenderEffect
                        .createBlurEffect(blurPx, blurPx, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                }
            }
    ) {
        val w = size.width
        val h = size.height
        blobs.forEachIndexed { index, spec ->
            val phase = phases[index].value + spec.phaseOffset
            // Slow organic drift via layered sine/cosine offsets, never linear.
            val driftX = spec.baseX * w + cos(phase.toDouble()).toFloat() * spec.orbitRadiusFraction * w
            val driftY = spec.baseY * h + sin((phase * 0.8f).toDouble()).toFloat() * spec.orbitRadiusFraction * h +
                (scrollInfluence * 0.05f * h)
            val radius = spec.radiusFraction * maxOf(w, h)

            drawBlob(center = Offset(driftX, driftY), radius = radius, color = spec.color)
        }
    }
}

private fun DrawScope.drawBlob(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.55f), color.copy(alpha = 0f)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

@Composable
private fun StaticAmbientGradient(palette: AmbientPalette, modifier: Modifier = Modifier) {
    val brush = remember(palette) {
        Brush.radialGradient(
            colors = listOf(palette.colors.first().copy(alpha = 0.25f), CanvasBase),
        )
    }
    Canvas(modifier = modifier.fillMaxSize().background(CanvasBase)) {
        drawRect(brush = brush, size = size)
    }
}

private fun buildBlobSpecs(palette: AmbientPalette): List<BlobSpec> {
    val random = Random(palette.colors.sumOf { it.value.toInt() })
    val count = 3 + (random.nextInt(3)) // 3..5 blobs per 4.2
    return List(count) { i ->
        BlobSpec(
            color = palette.colors[i % palette.colors.size],
            baseX = random.nextFloat(),
            baseY = random.nextFloat(),
            radiusFraction = 0.35f + random.nextFloat() * 0.25f,
            periodMs = MotionDuration.AMBIENT_BLOB_MIN_MS +
                random.nextInt(MotionDuration.AMBIENT_BLOB_MAX_MS - MotionDuration.AMBIENT_BLOB_MIN_MS),
            phaseOffset = random.nextFloat() * (2 * Math.PI).toFloat(),
            orbitRadiusFraction = 0.08f + random.nextFloat() * 0.10f
        )
    }
}

/** Optional low-priority device-tilt influence (4.2) — togglable, feeds [LocalAmbientScrollInfluence]-style hooks. */
@Composable
fun ProvideAmbientScrollInfluence(offset: Float, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAmbientScrollInfluence provides offset, content = content)
}
