package com.textureforge.ai.app.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe Navigation Compose routes (Section 5). Each top-level
 * destination maps 1:1 to a :feature module's entry composable. Detail
 * routes carry typed args instead of raw string bundles.
 */
sealed interface TfDestination {

    @Serializable
    data object Onboarding : TfDestination

    @Serializable
    data object Home : TfDestination

    @Serializable
    data object Analyze : TfDestination

    @Serializable
    data class AnalysisResultRoute(val analysisId: String) : TfDestination

    @Serializable
    data object QaStudio : TfDestination

    @Serializable
    data class QaReportRoute(val reportId: String) : TfDestination

    @Serializable
    data object WorkflowGenerator : TfDestination

    @Serializable
    data object PromptStudio : TfDestination

    @Serializable
    data object Library : TfDestination

    @Serializable
    data class KnowledgeDetailRoute(val entryId: String) : TfDestination

    @Serializable
    data object Projects : TfDestination

    @Serializable
    data class ProjectDetailRoute(val projectId: String) : TfDestination

    @Serializable
    data object Settings : TfDestination
}

/** The destinations shown in GlassBottomNav / the tablet nav rail (4.5, 4.7). */
enum class TopLevelDestination(
    val route: TfDestination,
    val label: String
) {
    HOME(TfDestination.Home, "Home"),
    ANALYZE(TfDestination.Analyze, "Analyze"),
    LIBRARY(TfDestination.Library, "Library"),
    PROJECTS(TfDestination.Projects, "Projects"),
    SETTINGS(TfDestination.Settings, "Settings")
}
