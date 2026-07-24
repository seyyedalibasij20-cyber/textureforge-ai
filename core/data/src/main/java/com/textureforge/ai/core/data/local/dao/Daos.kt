package com.textureforge.ai.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.textureforge.ai.core.data.local.entity.AnalysisEntity
import com.textureforge.ai.core.data.local.entity.KnowledgeEntryEntity
import com.textureforge.ai.core.data.local.entity.MaterialSwatchEntity
import com.textureforge.ai.core.data.local.entity.ProjectEntity
import com.textureforge.ai.core.data.local.entity.PromptHistoryEntity
import com.textureforge.ai.core.data.local.entity.QaReportEntity
import com.textureforge.ai.core.data.local.entity.WorkflowHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun observeById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analyses ORDER BY createdAtEpochMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analyses WHERE projectId = :projectId ORDER BY createdAtEpochMillis DESC")
    fun observeForProject(projectId: String): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analyses WHERE id = :id")
    fun observeById(id: String): Flow<AnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AnalysisEntity)

    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface QaReportDao {
    @Query("SELECT * FROM qa_reports WHERE projectId = :projectId ORDER BY createdAtEpochMillis DESC")
    fun observeForProject(projectId: String): Flow<List<QaReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: QaReportEntity)

    @Query("UPDATE qa_reports SET exported = 1 WHERE id = :id")
    suspend fun markExported(id: String)
}

@Dao
interface WorkflowHistoryDao {
    @Query("SELECT * FROM workflow_history ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<WorkflowHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WorkflowHistoryEntity)
}

@Dao
interface PromptHistoryDao {
    @Query("SELECT * FROM prompt_history ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<PromptHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PromptHistoryEntity)
}

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_entries ORDER BY title ASC")
    fun observeAll(): Flow<List<KnowledgeEntryEntity>>

    @Query(
        "SELECT * FROM knowledge_entries WHERE " +
            "(:category IS NULL OR category = :category) AND " +
            "(title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR tags_csv LIKE '%' || :query || '%') " +
            "ORDER BY title ASC"
    )
    fun search(query: String, category: String?): Flow<List<KnowledgeEntryEntity>>

    @Query(
        "SELECT * FROM knowledge_entries WHERE " +
            "(:category IS NULL OR category = :category) " +
            "ORDER BY isUserSaved DESC LIMIT :limit"
    )
    suspend fun findRelevant(category: String?, limit: Int): List<KnowledgeEntryEntity>

    @Query("UPDATE knowledge_entries SET isUserSaved = :saved WHERE id = :id")
    suspend fun setUserSaved(id: String, saved: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(entities: List<KnowledgeEntryEntity>)

    @Query("SELECT COUNT(*) FROM knowledge_entries")
    suspend fun count(): Int
}

@Dao
interface MaterialSwatchDao {
    @Query("SELECT * FROM material_swatches ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<MaterialSwatchEntity>>

    @Query("SELECT * FROM material_swatches WHERE projectId = :projectId ORDER BY createdAtEpochMillis DESC")
    fun observeForProject(projectId: String): Flow<List<MaterialSwatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MaterialSwatchEntity)

    @Query("DELETE FROM material_swatches WHERE id = :id")
    suspend fun delete(id: String)
}
