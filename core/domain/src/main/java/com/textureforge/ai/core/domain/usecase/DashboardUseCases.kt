package com.textureforge.ai.core.domain.usecase

import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.Project
import com.textureforge.ai.core.domain.repository.AnalysisRepository
import com.textureforge.ai.core.domain.repository.KnowledgeRepository
import com.textureforge.ai.core.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.random.Random

/** Powers the Home dashboard's Recent Analyses carousel + Active Project summary (7.2). */
class ObserveHomeDashboardUseCase @Inject constructor(
    private val analysisRepository: AnalysisRepository,
    private val projectRepository: ProjectRepository,
    private val knowledgeRepository: KnowledgeRepository
) {
    data class HomeDashboardState(
        val recentAnalyses: List<AnalysisResult>,
        val projects: List<Project>,
        val dailyTip: KnowledgeEntry?
    )

    operator fun invoke(): Flow<HomeDashboardState> = combine(
        analysisRepository.observeRecent(limit = 10),
        projectRepository.observeProjects(),
        knowledgeRepository.observeAll()
    ) { analyses, projects, knowledge ->
        val tip = knowledge.randomOrNull(Random(seedForToday()))
        HomeDashboardState(recentAnalyses = analyses, projects = projects, dailyTip = tip)
    }

    /** Deterministic per-day seed so the "daily tip" is stable across the day. */
    private fun seedForToday(): Long = System.currentTimeMillis() / (1000L * 60 * 60 * 24)
}

/** Saves a captured material as a swatch linked to the analysis that produced it (Capture -> Analyze -> Result -> Save to Project). */
class SaveAnalysisToProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val analysisRepository: AnalysisRepository
) {
    suspend operator fun invoke(analysis: AnalysisResult, projectId: String) {
        analysisRepository.save(analysis.copy(projectId = projectId))
    }
}
