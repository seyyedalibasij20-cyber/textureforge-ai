package com.textureforge.ai.core.data.mapper

import com.textureforge.ai.core.data.local.entity.AnalysisEntity
import com.textureforge.ai.core.data.local.entity.KnowledgeEntryEntity
import com.textureforge.ai.core.data.local.entity.MaterialSwatchEntity
import com.textureforge.ai.core.data.local.entity.ProjectEntity
import com.textureforge.ai.core.data.local.entity.PromptHistoryEntity
import com.textureforge.ai.core.data.local.entity.QaReportEntity
import com.textureforge.ai.core.data.local.entity.WorkflowHistoryEntity
import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.ConfidenceLevel
import com.textureforge.ai.core.domain.model.DetectedIssue
import com.textureforge.ai.core.domain.model.IssueSeverity
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.KnowledgeSourceConfidence
import com.textureforge.ai.core.domain.model.MaterialCategory
import com.textureforge.ai.core.domain.model.MaterialSwatch
import com.textureforge.ai.core.domain.model.PbrRangeEstimate
import com.textureforge.ai.core.domain.model.Project
import com.textureforge.ai.core.domain.model.PromptHistoryEntry
import com.textureforge.ai.core.domain.model.PromptRequest
import com.textureforge.ai.core.domain.model.QaReport
import com.textureforge.ai.core.domain.model.TargetEngine
import com.textureforge.ai.core.domain.model.TargetPlatform
import com.textureforge.ai.core.domain.model.TextureChannel
import com.textureforge.ai.core.domain.model.WorkflowHistoryEntry
import com.textureforge.ai.core.domain.model.WorkflowStep
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val dataJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

/** Serializable mirror of [AnalysisResult]'s non-scalar fields, persisted as one JSON blob (Section 6.3). */
@Serializable
private data class AnalysisPayload(
    val estimatedPbrRangesJson: String?,
    val detectedIssues: List<DetectedIssueDto>,
    val recommendations: List<String>,
    val suggestedWorkflow: List<WorkflowStepDto>
)

@Serializable
data class DetectedIssueDto(
    val id: String,
    val channel: String?,
    val severity: String,
    val title: String,
    val description: String,
    val recommendation: String?
)

@Serializable
data class WorkflowStepDto(
    val order: Int,
    val title: String,
    val description: String,
    val requiredMaps: List<String>,
    val toolNotes: String?
)

fun WorkflowStep.toDto() = WorkflowStepDto(order, title, description, requiredMaps.map { it.name }, toolNotes)
fun WorkflowStepDto.toDomain() = WorkflowStep(order, title, description, requiredMaps.map { TextureChannel.valueOf(it) }, toolNotes)
fun DetectedIssue.toDto() = DetectedIssueDto(id, channel?.name, severity.name, title, description, recommendation)
fun DetectedIssueDto.toDomain() = DetectedIssue(id, channel?.let { TextureChannel.valueOf(it) }, IssueSeverity.valueOf(severity), title, description, recommendation)

fun AnalysisResult.toEntity(): AnalysisEntity {
    val payload = AnalysisPayload(
        estimatedPbrRangesJson = null, // kept simple: ranges are re-derivable/estimate-only, not critical to round-trip losslessly
        detectedIssues = detectedIssues.map { it.toDto() },
        recommendations = recommendations,
        suggestedWorkflow = suggestedWorkflow.map { it.toDto() }
    )
    return AnalysisEntity(
        id = id,
        projectId = projectId,
        createdAtEpochMillis = createdAtEpochMillis,
        sourceImageUris = sourceImageUris.joinToString(","),
        materialCategory = materialCategory.name,
        materialCategoryConfidence = materialCategoryConfidence.name,
        summary = summary,
        resultJson = dataJson.encodeToString(AnalysisPayload.serializer(), payload)
    )
}

fun AnalysisEntity.toDomain(): AnalysisResult {
    val payload = runCatching { dataJson.decodeFromString(AnalysisPayload.serializer(), resultJson) }
        .getOrDefault(AnalysisPayload(null, emptyList(), emptyList(), emptyList()))
    return AnalysisResult(
        id = id,
        projectId = projectId,
        createdAtEpochMillis = createdAtEpochMillis,
        sourceImageUris = if (sourceImageUris.isBlank()) emptyList() else sourceImageUris.split(","),
        materialCategory = runCatching { MaterialCategory.valueOf(materialCategory) }.getOrDefault(MaterialCategory.UNKNOWN),
        materialCategoryConfidence = runCatching { ConfidenceLevel.valueOf(materialCategoryConfidence) }.getOrDefault(ConfidenceLevel.UNKNOWN),
        summary = summary,
        estimatedPbrRanges = null as PbrRangeEstimate?,
        detectedIssues = payload.detectedIssues.map { it.toDomain() },
        recommendations = payload.recommendations,
        suggestedWorkflow = payload.suggestedWorkflow.map { it.toDomain() }
    )
}

fun Project.toEntity() = ProjectEntity(id, name, defaultEngine.name, defaultPlatform.name, defaultResolution, createdAtEpochMillis, updatedAtEpochMillis)
fun ProjectEntity.toDomain() = Project(
    id, name,
    runCatching { TargetEngine.valueOf(defaultEngine) }.getOrDefault(TargetEngine.OTHER),
    runCatching { TargetPlatform.valueOf(defaultPlatform) }.getOrDefault(TargetPlatform.PC),
    defaultResolution, createdAtEpochMillis, updatedAtEpochMillis
)

fun KnowledgeEntry.toEntity() = KnowledgeEntryEntity(
    id, title, category?.name, subcategory, body,
    tags.joinToString(","), relatedIds.joinToString(","),
    confidenceSource.name, version, isUserSaved
)
fun KnowledgeEntryEntity.toDomain() = KnowledgeEntry(
    id, title,
    category?.let { runCatching { MaterialCategory.valueOf(it) }.getOrNull() },
    subcategory, body,
    if (tagsCsv.isBlank()) emptyList() else tagsCsv.split(","),
    if (relatedIdsCsv.isBlank()) emptyList() else relatedIdsCsv.split(","),
    runCatching { KnowledgeSourceConfidence.valueOf(confidenceSource) }.getOrDefault(KnowledgeSourceConfidence.AI_ASSISTED),
    version, isUserSaved
)

fun MaterialSwatch.toEntity() = MaterialSwatchEntity(id, projectId, title, category.name, imageUri, analysisId, createdAtEpochMillis)
fun MaterialSwatchEntity.toDomain() = MaterialSwatch(
    id, projectId, title,
    runCatching { MaterialCategory.valueOf(category) }.getOrDefault(MaterialCategory.UNKNOWN),
    imageUri, analysisId, createdAtEpochMillis
)

@Serializable
private data class PromptRequestDto(
    val targetModel: String, val material: String, val style: String,
    val lighting: String, val weatheringAge: String, val cameraAngle: String,
    val tileabilityRequired: Boolean
)

fun PromptHistoryEntry.toEntity() = PromptHistoryEntity(
    id, createdAtEpochMillis,
    dataJson.encodeToString(
        PromptRequestDto.serializer(),
        PromptRequestDto(request.targetModel, request.material, request.style, request.lighting, request.weatheringAge, request.cameraAngle, request.tileabilityRequired)
    ),
    generatedPrompt
)
fun PromptHistoryEntity.toDomain(): PromptHistoryEntry {
    val dto = runCatching { dataJson.decodeFromString(PromptRequestDto.serializer(), requestJson) }
        .getOrDefault(PromptRequestDto("", "", "", "", "", "", false))
    return PromptHistoryEntry(
        id, createdAtEpochMillis,
        PromptRequest(dto.targetModel, dto.material, dto.style, dto.lighting, dto.weatheringAge, dto.cameraAngle, dto.tileabilityRequired),
        generatedPrompt
    )
}

fun WorkflowHistoryEntry.toEntity() = WorkflowHistoryEntity(
    id, projectId, createdAtEpochMillis, materialType, targetEngine.name, targetPlatform.name, targetResolution,
    dataJson.encodeToString(kotlinx.serialization.builtins.ListSerializer(WorkflowStepDto.serializer()), steps.map { it.toDto() })
)
fun WorkflowHistoryEntity.toDomain(): WorkflowHistoryEntry {
    val steps = runCatching {
        dataJson.decodeFromString(kotlinx.serialization.builtins.ListSerializer(WorkflowStepDto.serializer()), stepsJson)
    }.getOrDefault(emptyList())
    return WorkflowHistoryEntry(
        id, projectId, createdAtEpochMillis, materialType,
        runCatching { TargetEngine.valueOf(targetEngine) }.getOrDefault(TargetEngine.OTHER),
        runCatching { TargetPlatform.valueOf(targetPlatform) }.getOrDefault(TargetPlatform.PC),
        targetResolution, steps.map { it.toDomain() }
    )
}

fun QaReport.toEntity() = QaReportEntity(
    id, analysisId, projectId, createdAtEpochMillis,
    dataJson.encodeToString(
        kotlinx.serialization.builtins.MapSerializer(
            kotlinx.serialization.builtins.serializer<String>(),
            kotlinx.serialization.builtins.ListSerializer(DetectedIssueDto.serializer())
        ),
        channelResults.entries.associate { it.key.name to it.value.map { issue -> issue.toDto() } }
    ),
    overallSeverity.name, exported
)
fun QaReportEntity.toDomain(): QaReport {
    val map = runCatching {
        dataJson.decodeFromString(
            kotlinx.serialization.builtins.MapSerializer(
                kotlinx.serialization.builtins.serializer<String>(),
                kotlinx.serialization.builtins.ListSerializer(DetectedIssueDto.serializer())
            ),
            channelResultsJson
        )
    }.getOrDefault(emptyMap())
    return QaReport(
        id, analysisId, projectId, createdAtEpochMillis,
        map.entries.associate { TextureChannel.valueOf(it.key) to it.value.map { d -> d.toDomain() } },
        runCatching { IssueSeverity.valueOf(overallSeverity) }.getOrDefault(IssueSeverity.INFO),
        exported
    )
}
