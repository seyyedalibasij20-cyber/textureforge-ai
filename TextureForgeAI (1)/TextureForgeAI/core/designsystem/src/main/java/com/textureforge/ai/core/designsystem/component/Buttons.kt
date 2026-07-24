package com.textureforge.ai.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.MinTouchTarget
import com.textureforge.ai.core.designsystem.theme.Radius
import com.textureforge.ai.core.designsystem.theme.SignatureAccentGradient
import com.textureforge.ai.core.designsystem.theme.Spacing

enum class TfButtonState { Enabled, Disabled, Loading }

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: TfButtonState = TfButtonState.Enabled,
    contentDescriptionOverride: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val enabled = state == TfButtonState.Enabled

    Box(
        modifier = modifier
            .widthIn(min = 120.dp)
            .height(MinTouchTarget)
            .clip(RoundedCornerShape(Radius.pill))
            .background(SignatureAccentGradient)
            .alpha(if (enabled || state == TfButtonState.Loading) (if (pressed) 0.85f else 1f) else 0.4f)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics { contentDescription = contentDescriptionOverride ?: text },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = state, label = "primary_button_state") { s ->
            when (s) {
                TfButtonState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.padding(Spacing.xxs),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
                else -> Text(text = text, color = Color.Black)
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .widthIn(min = 120.dp)
            .height(MinTouchTarget)
            .clip(RoundedCornerShape(Radius.pill))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(Radius.pill))
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = text },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = LocalContentColor.current)
    }
}

@Composable
fun TertiaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(MinTouchTarget)
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Spacing.sm)
            .semantics { contentDescription = text },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = androidx.compose.ui.graphics.Color(0xFF39E7FF))
    }
}
