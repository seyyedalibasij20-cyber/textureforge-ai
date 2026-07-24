package com.textureforge.ai.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.textureforge.ai.core.designsystem.theme.AccentCyan
import com.textureforge.ai.core.designsystem.theme.GlassTier
import com.textureforge.ai.core.designsystem.theme.MinTouchTarget
import com.textureforge.ai.core.designsystem.theme.Radius
import com.textureforge.ai.core.designsystem.theme.Spacing

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

/** A no-ripple clickable modifier for icon-only nav targets, still exposing the min touch target and semantics upstream. */
@Composable
private fun Modifier.tfClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
}

/**
 * Floating, blurred, rounded-corner bottom navigation — explicitly NOT an
 * edge-to-edge flat Material bar (4.5). Used on phone-portrait / medium
 * width; on Expanded width, use [GlassNavRail] instead (4.7).
 */
@Composable
fun GlassBottomNav(
    items: List<NavItem>,
    selectedRoute: String,
    onSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        tier = GlassTier.Prominent,
        cornerRadius = Radius.pill,
        contentPadding = Spacing.xs
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                NavItemContent(item = item, selected = item.route == selectedRoute, onSelect = onSelect)
            }
        }
    }
}

@Composable
private fun RowScope.NavItemContent(item: NavItem, selected: Boolean, onSelect: (NavItem) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .size(MinTouchTarget)
            .tfClickable { onSelect(item) }
            .semantics { contentDescription = item.label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = null,
            tint = if (selected) AccentCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) AccentCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/** Persistent side navigation rail replacing bottom nav on large tablets (4.7). */
@Composable
fun GlassNavRail(
    items: List<NavItem>,
    selectedRoute: String,
    onSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.width(88.dp),
        tier = GlassTier.Prominent,
        cornerRadius = Radius.prominent,
        contentPadding = Spacing.sm
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEach { item ->
                val selected = item.route == selectedRoute
                Column(
                    modifier = Modifier
                        .size(MinTouchTarget)
                        .tfClickable { onSelect(item) }
                        .semantics { contentDescription = item.label },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = null,
                        tint = if (selected) AccentCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        tier = GlassTier.Standard,
        cornerRadius = Radius.prominent,
        contentPadding = Spacing.md
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            trailing()
        }
    }
}
