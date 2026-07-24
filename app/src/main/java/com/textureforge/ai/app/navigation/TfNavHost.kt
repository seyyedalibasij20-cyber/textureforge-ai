package com.textureforge.ai.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import com.textureforge.ai.core.designsystem.component.GlassBottomNav
import com.textureforge.ai.core.designsystem.component.GlassNavRail
import com.textureforge.ai.core.designsystem.component.NavItem
import com.textureforge.ai.feature.home.HomeRoute
import com.textureforge.ai.feature.analyze.AnalyzeRoute
import com.textureforge.ai.feature.analyze.AnalysisResultDetailRoute
import com.textureforge.ai.feature.qa.QaStudioRoute
import com.textureforge.ai.feature.workflow.WorkflowGeneratorRoute
import com.textureforge.ai.feature.prompt.PromptStudioRoute
import com.textureforge.ai.feature.library.LibraryRoute
import com.textureforge.ai.feature.projects.ProjectsRoute
import com.textureforge.ai.feature.projects.ProjectDetailRoute
import com.textureforge.ai.feature.settings.SettingsRoute
import com.textureforge.ai.feature.onboarding.OnboardingRoute

private val topLevelNavItems = listOf(
    NavItem(TopLevelDestination.HOME.name, "Home", Icons.Filled.Home),
    NavItem(TopLevelDestination.ANALYZE.name, "Analyze", Icons.Filled.PhotoCamera),
    NavItem(TopLevelDestination.LIBRARY.name, "Library", Icons.Filled.LibraryBooks),
    NavItem(TopLevelDestination.PROJECTS.name, "Projects", Icons.Filled.Folder),
    NavItem(TopLevelDestination.SETTINGS.name, "Settings", Icons.Filled.Settings)
)

/**
 * Root scaffold: chooses bottom nav vs nav rail from [WindowWidthSizeClass]
 * (4.7 responsiveness law) and hosts the [NavHost]. The ambient background
 * (per-screen [com.textureforge.ai.core.designsystem.background.AmbientFlowField])
 * is rendered *inside* each feature screen, not here, so it never
 * restarts/flickers on navigation (4.6).
 */
@Composable
fun TfAppScaffold(
    startDestination: TfDestination,
    widthSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination
    val selectedTopLevel = TopLevelDestination.entries.firstOrNull { dest ->
        currentRoute?.hierarchy?.any { it.hasRoute(dest.route::class) } == true
    }

    fun navigateTopLevel(item: NavItem) {
        val dest = TopLevelDestination.valueOf(item.route)
        navController.navigate(dest.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (widthSizeClass == WindowWidthSizeClass.Expanded) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (selectedTopLevel != null) {
                    GlassNavRail(
                        items = topLevelNavItems,
                        selectedRoute = selectedTopLevel.name,
                        onSelect = ::navigateTopLevel,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    TfNavHost(navController = navController, startDestination = startDestination)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                TfNavHost(navController = navController, startDestination = startDestination)
                if (selectedTopLevel != null) {
                    GlassBottomNav(
                        items = topLevelNavItems,
                        selectedRoute = selectedTopLevel.name,
                        onSelect = ::navigateTopLevel,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TfNavHost(navController: NavHostController, startDestination: TfDestination) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable<TfDestination.Onboarding> {
            OnboardingRoute(onFinished = {
                navController.navigate(TfDestination.Home) {
                    popUpTo(TfDestination.Onboarding) { inclusive = true }
                }
            })
        }
        composable<TfDestination.Home> {
            HomeRoute(
                onNavigateAnalyze = { navController.navigate(TfDestination.Analyze) },
                onNavigateQa = { navController.navigate(TfDestination.QaStudio) },
                onNavigatePromptStudio = { navController.navigate(TfDestination.PromptStudio) },
                onOpenProject = { id -> navController.navigate(TfDestination.ProjectDetailRoute(id)) },
                onOpenAnalysis = { id -> navController.navigate(TfDestination.AnalysisResultRoute(id)) }
            )
        }
        composable<TfDestination.Analyze> {
            AnalyzeRoute(
                onResult = { id ->
                    navController.navigate(TfDestination.AnalysisResultRoute(id)) {
                        popUpTo(TfDestination.Analyze) { inclusive = true }
                    }
                }
            )
        }
        composable<TfDestination.AnalysisResultRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<TfDestination.AnalysisResultRoute>()
            AnalysisResultDetailRoute(
                analysisId = args.analysisId,
                onGenerateWorkflow = { navController.navigate(TfDestination.WorkflowGenerator) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<TfDestination.QaStudio> {
            QaStudioRoute(
                onReportSaved = { id -> navController.navigate(TfDestination.QaReportRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<TfDestination.QaReportRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<TfDestination.QaReportRoute>()
            QaStudioRoute(initialReportId = args.reportId, onReportSaved = {}, onBack = { navController.popBackStack() })
        }
        composable<TfDestination.WorkflowGenerator> {
            WorkflowGeneratorRoute(onBack = { navController.popBackStack() })
        }
        composable<TfDestination.PromptStudio> {
            PromptStudioRoute(onBack = { navController.popBackStack() })
        }
        composable<TfDestination.Library> {
            LibraryRoute(onOpenEntry = { id -> navController.navigate(TfDestination.KnowledgeDetailRoute(id)) })
        }
        composable<TfDestination.KnowledgeDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<TfDestination.KnowledgeDetailRoute>()
            LibraryRoute(initialSelectedEntryId = args.entryId, onOpenEntry = {})
        }
        composable<TfDestination.Projects> {
            ProjectsRoute(onOpenProject = { id -> navController.navigate(TfDestination.ProjectDetailRoute(id)) })
        }
        composable<TfDestination.ProjectDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<TfDestination.ProjectDetailRoute>()
            ProjectDetailRoute(projectId = args.projectId, onBack = { navController.popBackStack() })
        }
        composable<TfDestination.Settings> {
            SettingsRoute()
        }
    }
}

private val androidx.navigation.NavDestination.hierarchy: Sequence<androidx.navigation.NavDestination>
    get() = generateSequence(this) { it.parent }
