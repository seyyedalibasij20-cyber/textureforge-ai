package com.textureforge.ai.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Section 9 schema. Foreign keys use SET_NULL on delete for Analysis/QaReport
 * links to Project so deleting a project never destroys analysis history —
 * only detaches it — matching the "offline-first / never silently lose
 * data" spirit of Law #6.
 */

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val defaultEngine: String,
    val defaultPlatform: String,
    val defaultResolution: Int,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)

@Entity(
    tableName = "analyses",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("projectId"), Index("createdAtEpochMillis")]
)
data class AnalysisEntity(
    @PrimaryKey val id: String,
    val projectId: String?,
    val createdAtEpochMillis: Long,
    /** Comma-joined image URIs; kept simple over a join table given low cardinality (<=6 images/analysis). */
    val sourceImageUris: String,
    val materialCategory: String,
    val materialCategoryConfidence: String,
    val summary: String,
    /** Full structured AI JSON payload (Section 6.3), for lossless replay/debug and forward-compat fields. */
    val resultJson: String
)

@Entity(
    tableName = "qa_reports",
    foreignKeys = [
        ForeignKey(entity = AnalysisEntity::class, parentColumns = ["id"], childColumns = ["analysisId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("analysisId"), Index("projectId")]
)
data class QaReportEntity(
    @PrimaryKey val id: String,
    val analysisId: String?,
    val projectId: String?,
    val createdAtEpochMillis: Long,
    val channelResultsJson: String,
    val overallSeverity: String,
    val exported: Boolean
)

@Entity(tableName = "prompt_history")
data class PromptHistoryEntity(
    @PrimaryKey val id: String,
    val createdAtEpochMillis: Long,
    val requestJson: String,
    val generatedPrompt: String
)

@Entity(
    tableName = "workflow_history",
    foreignKeys = [ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.SET_NULL)],
    indices = [Index("projectId")]
)
data class WorkflowHistoryEntity(
    @PrimaryKey val id: String,
    val projectId: String?,
    val createdAtEpochMillis: Long,
    val materialType: String,
    val targetEngine: String,
    val targetPlatform: String,
    val targetResolution: Int,
    val stepsJson: String
)

@Entity(tableName = "knowledge_entries")
data class KnowledgeEntryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String?,
    val subcategory: String?,
    val body: String,
    @ColumnInfo(name = "tags_csv") val tagsCsv: String,
    @ColumnInfo(name = "related_ids_csv") val relatedIdsCsv: String,
    val confidenceSource: String,
    val version: Int,
    val isUserSaved: Boolean
)

@Entity(
    tableName = "material_swatches",
    foreignKeys = [
        ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = AnalysisEntity::class, parentColumns = ["id"], childColumns = ["analysisId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("projectId"), Index("analysisId")]
)
data class MaterialSwatchEntity(
    @PrimaryKey val id: String,
    val projectId: String?,
    val title: String,
    val category: String,
    val imageUri: String,
    val analysisId: String?,
    val createdAtEpochMillis: Long
)
