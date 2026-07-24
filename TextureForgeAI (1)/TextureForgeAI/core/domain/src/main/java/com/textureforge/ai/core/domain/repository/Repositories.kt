package com.textureforge.ai.core.domain.repository

import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.DomainResult
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.MaterialCategory
import com.textureforge.ai.core.domain.model.MaterialSwatch
import com.textureforge.ai.core.domain.model.Project
import com.textureforge.ai.core.domain.model.PromptHistoryEntry
import com.textureforge.ai.core.domain.model.QaReport
import com.textureforge.ai.core.domain.model.UserPrefs
import com.textureforge.ai.core.domain.model.WorkflowHistoryEntry
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeProjects(): Flow<List<Project>>
    fun observeProject(id: String): Flow<Project?>
    suspend fun upsert(project: Project): DomainResult<Unit>
    suspend fun delete(id: String): DomainResult<Unit>
}

interface AnalysisRepository {
    fun observeRecent(limit: Int = 20): Flow<List<AnalysisResult>>
    fun observeForProject(projectId: String): Flow<List<AnalysisResult>>
    fun observeById(id: String): Flow<AnalysisResult?>
    suspend fun save(result: AnalysisResult): DomainResult<Unit>
    suspend fun delete(id: String): DomainResult<Unit>
}

interface QaReportRepository {
    fun observeForProject(projectId: String): Flow<List<QaReport>>
    suspend fun save(report: QaReport): DomainResult<Unit>
    suspend fun markExported(id: String): DomainResult<Unit>
}

interface WorkflowHistoryRepository {
    fun observeAll(): Flow<List<WorkflowHistoryEntry>>
    suspend fun save(entry: WorkflowHistoryEntry): DomainResult<Unit>
}

interface PromptHistoryRepository {
    fun observeAll(): Flow<List<PromptHistoryEntry>>
    suspend fun save(entry: PromptHistoryEntry): DomainResult<Unit>
}

interface KnowledgeRepository {
    fun observeAll(): Flow<List<KnowledgeEntry>>
    fun search(query: String, category: MaterialCategory? = null): Flow<List<KnowledgeEntry>>
    /** Used by the Knowledge Package Build stage (6.2) — small, relevance-ranked subset. */
    suspend fun findRelevant(tags: List<String>, category: MaterialCategory?, limit: Int = 6): List<KnowledgeEntry>
    suspend fun toggleUserSaved(id: String, saved: Boolean): DomainResult<Unit>
    suspend fun ensureSeeded(): DomainResult<Unit>
}

interface MaterialSwatchRepository {
    fun observeAll(): Flow<List<MaterialSwatch>>
    fun observeForProject(projectId: String): Flow<List<MaterialSwatch>>
    suspend fun save(swatch: MaterialSwatch): DomainResult<Unit>
    suspend fun delete(id: String): DomainResult<Unit>
}

interface UserPrefsRepository {
    fun observePrefs(): Flow<UserPrefs>
    suspend fun update(transform: (UserPrefs) -> UserPrefs): DomainResult<Unit>
}

/** Simple network reachability signal consumed by use cases for offline gating (Law #6). */
interface ConnectivityObserver {
    fun isOnline(): Flow<Boolean>
}
