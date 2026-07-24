package com.textureforge.ai.core.data.repository

import com.textureforge.ai.core.data.local.dao.AnalysisDao
import com.textureforge.ai.core.data.local.dao.KnowledgeDao
import com.textureforge.ai.core.data.local.dao.MaterialSwatchDao
import com.textureforge.ai.core.data.local.dao.ProjectDao
import com.textureforge.ai.core.data.local.dao.PromptHistoryDao
import com.textureforge.ai.core.data.local.dao.QaReportDao
import com.textureforge.ai.core.data.local.dao.WorkflowHistoryDao
import com.textureforge.ai.core.data.mapper.toDomain
import com.textureforge.ai.core.data.mapper.toEntity
import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.DomainFailure
import com.textureforge.ai.core.domain.model.DomainResult
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.MaterialCategory
import com.textureforge.ai.core.domain.model.MaterialSwatch
import com.textureforge.ai.core.domain.model.Project
import com.textureforge.ai.core.domain.model.PromptHistoryEntry
import com.textureforge.ai.core.domain.model.QaReport
import com.textureforge.ai.core.domain.model.WorkflowHistoryEntry
import com.textureforge.ai.core.domain.repository.AnalysisRepository
import com.textureforge.ai.core.domain.repository.KnowledgeRepository
import com.textureforge.ai.core.domain.repository.MaterialSwatchRepository
import com.textureforge.ai.core.domain.repository.ProjectRepository
import com.textureforge.ai.core.domain.repository.PromptHistoryRepository
import com.textureforge.ai.core.domain.repository.QaReportRepository
import com.textureforge.ai.core.domain.repository.WorkflowHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Every method wraps its Room call in try/catch and surfaces a
 * [DomainFailure.Storage] rather than letting a SQLiteException propagate to
 * the UI (Section 10, zero-crash target / Law #6).
 */

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val dao: ProjectDao
) : ProjectRepository {
    override fun observeProjects(): Flow<List<Project>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override fun observeProject(id: String): Flow<Project?> = dao.observeById(id).map { it?.toDomain() }

    override suspend fun upsert(project: Project): DomainResult<Unit> = runCatching {
        dao.upsert(project.toEntity())
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })

    override suspend fun delete(id: String): DomainResult<Unit> = runCatching {
        dao.delete(id)
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}

@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val dao: AnalysisDao
) : AnalysisRepository {
    override fun observeRecent(limit: Int): Flow<List<AnalysisResult>> =
        dao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun observeForProject(projectId: String): Flow<List<AnalysisResult>> =
        dao.observeForProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<AnalysisResult?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun save(result: AnalysisResult): DomainResult<Unit> = runCatching {
        dao.upsert(result.toEntity())
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })

    override suspend fun delete(id: String): DomainResult<Unit> = runCatching {
        dao.delete(id)
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}

@Singleton
class QaReportRepositoryImpl @Inject constructor(
    private val dao: QaReportDao
) : QaReportRepository {
    override fun observeForProject(projectId: String): Flow<List<QaReport>> =
        dao.observeForProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(report: QaReport): DomainResult<Unit> = runCatching {
        dao.upsert(report.toEntity())
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })

    override suspend fun markExported(id: String): DomainResult<Unit> = runCatching {
        dao.markExported(id)
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}

@Singleton
class WorkflowHistoryRepositoryImpl @Inject constructor(
    private val dao: WorkflowHistoryDao
) : WorkflowHistoryRepository {
    override fun observeAll(): Flow<List<WorkflowHistoryEntry>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun save(entry: WorkflowHistoryEntry): DomainResult<Unit> = runCatching {
        dao.upsert(entry.toEntity())
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}

@Singleton
class PromptHistoryRepositoryImpl @Inject constructor(
    private val dao: PromptHistoryDao
) : PromptHistoryRepository {
    override fun observeAll(): Flow<List<PromptHistoryEntry>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun save(entry: PromptHistoryEntry): DomainResult<Unit> = runCatching {
        dao.upsert(entry.toEntity())
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}

@Singleton
class KnowledgeRepositoryImpl @Inject constructor(
    private val dao: KnowledgeDao,
    private val seeder: KnowledgeBaseSeeder
) : KnowledgeRepository {
    override fun observeAll(): Flow<List<KnowledgeEntry>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String, category: MaterialCategory?): Flow<List<KnowledgeEntry>> =
        dao.search(query, category?.name).map { list -> list.map { it.toDomain() } }

    override suspend fun findRelevant(tags: List<String>, category: MaterialCategory?, limit: Int): List<KnowledgeEntry> =
        dao.findRelevant(category?.name, limit).map { it.toDomain() }

    override suspend fun toggleUserSaved(id: String, saved: Boolean): DomainResult<Unit> = runCatching {
        dao.setUserSaved(id, saved)
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })

    override suspend fun ensureSeeded(): DomainResult<Unit> = runCatching {
        if (dao.count() == 0) {
            dao.insertAllIfAbsent(seeder.loadBundledEntries().map { it.toEntity() })
        }
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}

@Singleton
class MaterialSwatchRepositoryImpl @Inject constructor(
    private val dao: MaterialSwatchDao
) : MaterialSwatchRepository {
    override fun observeAll(): Flow<List<MaterialSwatch>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override fun observeForProject(projectId: String): Flow<List<MaterialSwatch>> =
        dao.observeForProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(swatch: MaterialSwatch): DomainResult<Unit> = runCatching {
        dao.upsert(swatch.toEntity())
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })

    override suspend fun delete(id: String): DomainResult<Unit> = runCatching {
        dao.delete(id)
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}
