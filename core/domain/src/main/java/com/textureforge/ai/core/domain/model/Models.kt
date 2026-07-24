package com.textureforge.ai.core.domain.model

import kotlin.time.Duration

/**
 * Confidence level attached to any AI-derived value. Per Engineering Law #5,
 * no AI-estimated property may be presented as measured fact — every
 * estimate must carry one of these, surfaced in the UI via ConfidenceBadge.
 */
enum class ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW,
    /** The model could not confidently place this in High/Medium/Low. */
    UNKNOWN
}

enum class MaterialCategory {
    STONE,
    METAL,
    WOOD,
    ORGANIC,
    FABRIC,
    SYNTHETIC,
    GROUND_TERRAIN,
    GLASS_CERAMIC,
    UNKNOWN
}

enum class TargetEngine { UNREAL, UNITY, MARMOSET, BLENDER_EEVEE_CYCLES, OTHER }
enum class TargetPlatform { PC, CONSOLE, MOBILE, VR_AR, WEB }

enum class TextureChannel {
    BASE_COLOR,
    ROUGHNESS,
    METALLIC,
    NORMAL,
    HEIGHT,
    AMBIENT_OCCLUSION,
    EMISSION,
    ORM_PACKED
}

enum class IssueSeverity { CRITICAL, WARNING, INFO, SUCCESS }

enum class KnowledgeSourceConfidence { VERIFIED, AI_ASSISTED }

/**
 * A value that may be an AI estimate rather than a measured/ground-truth
 * fact. Forces every call site to consciously handle the confidence,
 * satisfying Law #5 at the type level rather than by UI convention alone.
 */
data class Estimate<T>(
    val value: T,
    val confidence: ConfidenceLevel,
    val isEstimate: Boolean = true
)

data class PbrRangeEstimate(
    val roughness: Estimate<ClosedFloatingPointRange<Float>>,
    val metallic: Estimate<ClosedFloatingPointRange<Float>>,
    val indexOfRefraction: Estimate<ClosedFloatingPointRange<Float>>? = null
)

data class DetectedIssue(
    val id: String,
    val channel: TextureChannel?,
    val severity: IssueSeverity,
    val title: String,
    val description: String,
    val recommendation: String?
)

data class WorkflowStep(
    val order: Int,
    val title: String,
    val description: String,
    val requiredMaps: List<TextureChannel>,
    val toolNotes: String?
)

/**
 * The strongly-typed, schema-validated result of a single AI vision/analysis
 * call (Section 6.3). Never parsed as free-form prose by the UI layer.
 */
data class AnalysisResult(
    val id: String,
    val projectId: String?,
    val createdAtEpochMillis: Long,
    val sourceImageUris: List<String>,
    val materialCategory: MaterialCategory,
    val materialCategoryConfidence: ConfidenceLevel,
    val summary: String,
    val estimatedPbrRanges: PbrRangeEstimate?,
    val detectedIssues: List<DetectedIssue>,
    val recommendations: List<String>,
    val suggestedWorkflow: List<WorkflowStep>
)

data class QaReport(
    val id: String,
    val analysisId: String?,
    val projectId: String?,
    val createdAtEpochMillis: Long,
    val channelResults: Map<TextureChannel, List<DetectedIssue>>,
    val overallSeverity: IssueSeverity,
    val exported: Boolean
)

data class PromptRequest(
    val targetModel: String,
    val material: String,
    val style: String,
    val lighting: String,
    val weatheringAge: String,
    val cameraAngle: String,
    val tileabilityRequired: Boolean
)

data class PromptHistoryEntry(
    val id: String,
    val createdAtEpochMillis: Long,
    val request: PromptRequest,
    val generatedPrompt: String
)

data class WorkflowHistoryEntry(
    val id: String,
    val projectId: String?,
    val createdAtEpochMillis: Long,
    val materialType: String,
    val targetEngine: TargetEngine,
    val targetPlatform: TargetPlatform,
    val targetResolution: Int,
    val steps: List<WorkflowStep>
)

data class KnowledgeEntry(
    val id: String,
    val title: String,
    val category: MaterialCategory?,
    val subcategory: String?,
    val body: String,
    val tags: List<String>,
    val relatedIds: List<String>,
    val confidenceSource: KnowledgeSourceConfidence,
    val version: Int,
    val isUserSaved: Boolean
)

data class MaterialSwatch(
    val id: String,
    val projectId: String?,
    val title: String,
    val category: MaterialCategory,
    val imageUri: String,
    val analysisId: String?,
    val createdAtEpochMillis: Long
)

data class Project(
    val id: String,
    val name: String,
    val defaultEngine: TargetEngine,
    val defaultPlatform: TargetPlatform,
    val defaultResolution: Int,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)

data class UserPrefs(
    val userId: String?,
    val isGuestMode: Boolean,
    val ambientMotionEnabled: Boolean,
    val reduceMotionEnabled: Boolean,
    val glassBlurIntensity: Float, // 0f..1f, drives GlassCard elevation tier
    val liteMotionModeAutoEnabled: Boolean
)

/** Coarse pipeline stage, surfaced by the "Classify → Analyze → Validate → Report" UI (4.6). */
enum class ReasoningStage { CLASSIFY, ANALYZE, VALIDATE, REPORT }

data class PipelineProgress(
    val stage: ReasoningStage,
    val elapsed: Duration
)
