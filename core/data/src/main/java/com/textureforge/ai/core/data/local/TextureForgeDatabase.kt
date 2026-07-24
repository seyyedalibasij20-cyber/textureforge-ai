package com.textureforge.ai.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.textureforge.ai.core.data.local.dao.AnalysisDao
import com.textureforge.ai.core.data.local.dao.KnowledgeDao
import com.textureforge.ai.core.data.local.dao.MaterialSwatchDao
import com.textureforge.ai.core.data.local.dao.ProjectDao
import com.textureforge.ai.core.data.local.dao.PromptHistoryDao
import com.textureforge.ai.core.data.local.dao.QaReportDao
import com.textureforge.ai.core.data.local.dao.WorkflowHistoryDao
import com.textureforge.ai.core.data.local.entity.AnalysisEntity
import com.textureforge.ai.core.data.local.entity.KnowledgeEntryEntity
import com.textureforge.ai.core.data.local.entity.MaterialSwatchEntity
import com.textureforge.ai.core.data.local.entity.ProjectEntity
import com.textureforge.ai.core.data.local.entity.PromptHistoryEntity
import com.textureforge.ai.core.data.local.entity.QaReportEntity
import com.textureforge.ai.core.data.local.entity.WorkflowHistoryEntity

/**
 * Section 9: "migration strategy from v1 using Room AutoMigration where
 * possible". v1 is the schema shipped in this build. Any additive schema
 * change (new nullable column, new table) should be handled via
 * @AutoMigration(from = N, to = N+1) entries in the `autoMigrations` list
 * below; only structurally-breaking changes (column type/removal) need a
 * hand-written Migration object, which should live in
 * core/data/src/main/java/.../local/migration/.
 */
@Database(
    entities = [
        ProjectEntity::class,
        AnalysisEntity::class,
        QaReportEntity::class,
        PromptHistoryEntity::class,
        WorkflowHistoryEntity::class,
        KnowledgeEntryEntity::class,
        MaterialSwatchEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class TextureForgeDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun analysisDao(): AnalysisDao
    abstract fun qaReportDao(): QaReportDao
    abstract fun promptHistoryDao(): PromptHistoryDao
    abstract fun workflowHistoryDao(): WorkflowHistoryDao
    abstract fun knowledgeDao(): KnowledgeDao
    abstract fun materialSwatchDao(): MaterialSwatchDao

    companion object {
        const val DATABASE_NAME = "textureforge.db"
    }
}
