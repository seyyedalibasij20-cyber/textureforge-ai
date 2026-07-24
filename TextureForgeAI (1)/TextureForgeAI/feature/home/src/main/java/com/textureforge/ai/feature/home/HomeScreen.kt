package com.textureforge.ai.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textureforge.ai.core.designsystem.background.AmbientFlowField
import com.textureforge.ai.core.designsystem.component.BadgeConfidence
import com.textureforge.ai.core.designsystem.component.ConfidenceBadge
import com.textureforge.ai.core.designsystem.component.EmptyState
import com.textureforge.ai.core.designsystem.component.GlassCard
import com.textureforge.ai.core.designsystem.component.GlassSkeletonList
import com.textureforge.ai.core.designsystem.component.PrimaryButton
import com.textureforge.ai.core.designsystem.theme.AmbientPalettes
import com.textureforge.ai.core.designsystem.theme.GlassTier
import com.textureforge.ai.core.designsystem.theme.Spacing
import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.ConfidenceLevel
import com.textureforge.ai.core.domain.model.MaterialCategory

/**
 * Entry point wired into the nav graph. Section 7.2: Quick Actions grid,
 * Recent Analyses carousel, Active Project summary, daily Knowledge Base tip.
 */
@Composable
fun HomeRoute(
    onNavigateAnalyze: () -> Unit,
    onNavigateQa: () -> Unit,
    onNavigatePromptStudio: () -> Unit,
    onOpenProject: (String) -> Unit,
    onOpenAnalysis: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onNavigateAnalyze = onNavigateAnalyze,
        onNavigateQa = onNavigateQa,
        onNavigatePromptStudio = onNavigatePromptStudio,
        onNavigateNewProject = onOpenProject,
        onOpenAnalysis = onOpenAnalysis,
        onRetry = viewModel::retry
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateAnalyze: () -> Unit,
    onNavigateQa: () -> Unit,
    onNavigatePromptStudio: () -> Unit,
    onNavigateNewProject: (String) -> Unit,
    onOpenAnalysis: (String) -> Unit,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientFlowField(palette = AmbientPalettes.Home, modifier = Modifier.fillMaxSize())

        when (uiState) {
            is HomeUiState.Loading -> HomeLoadingState()
            is HomeUiState.Error -> HomeErrorState(message = uiState.message, onRetry = onRetry)
            is HomeUiState.Content -> HomeContent(
                state = uiState,
                onNavigateAnalyze = onNavigateAnalyze,
                onNavigateQa = onNavigateQa,
                onNavigatePromptStudio = onNavigatePromptStudio,
                onOpenAnalysis = onOpenAnalysis
            )
        }
    }
}

@Composable
private fun HomeLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        GlassSkeletonList(count = 4)
    }
}

@Composable
private fun HomeErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(Spacing.lg)) {
        GlassCard(modifier = Modifier.fillMaxWidth(), tier = GlassTier.Prominent) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                EmptyState(
                    title = "Couldn't load your dashboard",
                    description = message,
                    icon = Icons.Outlined.Insights,
                    action = { PrimaryButton(text = "Retry", onClick = onRetry) }
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Content,
    onNavigateAnalyze: () -> Unit,
    onNavigateQa: () -> Unit,
    onNavigatePromptStudio: () -> Unit,
    onOpenAnalysis: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            Column {
                Text("TextureForge", style = MaterialTheme.typography.headlineMedium)
                if (state.isOffline) {
                    Text(
                        "Offline — showing cached data. Live AI calls are disabled.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        item { QuickActionsGrid(onAnalyze = onNavigateAnalyze, onQa = onNavigateQa, onPromptStudio = onNavigatePromptStudio) }

        item {
            SectionHeader("Recent Analyses")
            if (state.recentAnalyses.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    EmptyState(
                        title = "No analyses yet",
                        description = "Capture or import a material photo to get your first AI breakdown.",
                        icon = Icons.Filled.CameraAlt,
                        action = { PrimaryButton(text = "Analyze a photo", onClick = onNavigateAnalyze) }
                    )
                }
            } else {
                RecentAnalysesCarousel(analyses = state.recentAnalyses, onOpenAnalysis = onOpenAnalysis)
            }
        }

        item {
            SectionHeader("Active Project")
            val project = state.projects.firstOrNull()
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                if (project == null) {
                    EmptyState(
                        title = "No active project",
                        description = "Create a project to group analyses, QA reports, and workflows.",
                        icon = Icons.Filled.CreateNewFolder
                    )
                } else {
                    Column {
                        Text(project.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${project.defaultEngine} · ${project.defaultPlatform} · ${project.defaultResolution}px",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        if (state.dailyTip != null) {
            item {
                SectionHeader("Today's Tip")
                GlassCard(modifier = Modifier.fillMaxWidth(), tier = GlassTier.Subtle) {
                    Column {
                        Text(state.dailyTip.title, style = MaterialTheme.typography.titleSmall)
                        Text(
                            state.dailyTip.body,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = Spacing.xs)
    )
}

@Composable
private fun QuickActionsGrid(onAnalyze: () -> Unit, onQa: () -> Unit, onPromptStudio: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.fillMaxWidth()) {
        QuickActionCard("Analyze", Icons.Filled.CameraAlt, Modifier.weight(1f)) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onAnalyze()
        }
        QuickActionCard("QA Check", Icons.Filled.FactCheck, Modifier.weight(1f)) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onQa()
        }
        QuickActionCard("Prompt", Icons.Filled.AutoAwesome, Modifier.weight(1f)) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onPromptStudio()
        }
    }
}

@Composable
private fun QuickActionCard(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    GlassCard(
        modifier = modifier.padding(0.dp).then(
            Modifier.androidxClickable(onClick)
        ),
        tier = GlassTier.Standard
    ) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            androidx.compose.material3.Icon(imageVector = icon, contentDescription = label)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Local alias kept explicit so QuickActionCard's touch target and semantics stay auditable in one place (Law #9: 48dp min). */
private fun Modifier.androidxClickable(onClick: () -> Unit): Modifier =
    this.then(
        androidx.compose.foundation.clickable(
            interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null,
            onClickLabel = null,
            onClick = onClick
        )
    )

@Composable
private fun RecentAnalysesCarousel(analyses: List<AnalysisResult>, onOpenAnalysis: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        items(analyses, key = { it.id }) { analysis ->
            GlassCard(
                modifier = Modifier
                    .padding(0.dp)
                    .then(Modifier.androidxClickable { onOpenAnalysis(analysis.id) }),
                tier = GlassTier.Standard
            ) {
                Column {
                    Text(analysis.materialCategory.displayName(), style = MaterialTheme.typography.titleSmall)
                    Text(
                        analysis.summary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    ConfidenceBadge(confidence = analysis.materialCategoryConfidence.toBadgeConfidence())
                }
            }
        }
    }
}

private fun MaterialCategory.displayName(): String = name.lowercase()
    .replace('_', ' ')
    .replaceFirstChar { it.uppercase() }

private fun ConfidenceLevel.toBadgeConfidence(): BadgeConfidence = when (this) {
    ConfidenceLevel.HIGH -> BadgeConfidence.HIGH
    ConfidenceLevel.MEDIUM -> BadgeConfidence.MEDIUM
    ConfidenceLevel.LOW -> BadgeConfidence.LOW
    ConfidenceLevel.UNKNOWN -> BadgeConfidence.ESTIMATED
}
