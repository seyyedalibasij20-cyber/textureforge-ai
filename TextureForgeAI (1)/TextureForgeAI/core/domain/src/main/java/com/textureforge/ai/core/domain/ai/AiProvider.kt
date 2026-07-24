package com.textureforge.ai.core.domain.ai

import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.PromptRequest
import com.textureforge.ai.core.domain.model.ReasoningStage
import com.textureforge.ai.core.domain.model.TargetEngine
import com.textureforge.ai.core.domain.model.TargetPlatform
import com.textureforge.ai.core.domain.model.WorkflowStep
import kotlinx.coroutines.flow.Flow

/**
 * Lives in :core:domain, NOT :core:ai. This is the load-bearing contract for
 * Engineering Law #2 ("no vendor lock-in on the AI layer"): the domain and
 * every feature module depend only on this interface. :core:ai's
 * GeminiAiProvider is one interchangeable implementation of it; a future
 * on-device model, OpenAI, or Claude-backed provider is a drop-in swap with
 * zero changes above this line.
 */
interface AiProvider {

    /** Human-readable id shown in Settings > AI provider status. */
    val providerId: String

    /** Cheap local/remote reachability check for offline-first gating (Law #6). */
    suspend fun isAvailable(): Boolean

    /**
     * Multimodal photo analysis. Emits [PipelineEvent.Progress] for each
     * reasoning stage (Section 6.2) followed by exactly one terminal
     * [PipelineEvent.Success] or [PipelineEvent.Failure].
     */
    fun analyzeMaterial(request: AnalyzeMaterialRequest): Flow<PipelineEvent<AnalysisResult>>

    /** Text-only QA re-check against an already-captured image set. */
    fun runQaCheck(request: QaCheckRequest): Flow<PipelineEvent<AnalysisResult>>

    /** Structured production workflow generation (Section 7.5). */
    fun generateWorkflow(request: WorkflowGenerationRequest): Flow<PipelineEvent<List<WorkflowStep>>>

    /** Prompt-engineering studio output (Section 7.6). Never generates images itself. */
    fun generatePrompt(request: PromptRequest): Flow<PipelineEvent<String>>
}

data class AnalyzeMaterialRequest(
    val imageUris: List<String>,
    val projectId: String?,
    val relevantKnowledge: List<KnowledgeEntry>,
    val userNotes: String?
)

data class QaCheckRequest(
    val analysisId: String?,
    val imageUrisByChannel: Map<String, String>,
    val relevantKnowledge: List<KnowledgeEntry>
)

data class WorkflowGenerationRequest(
    val materialType: String,
    val targetEngine: TargetEngine,
    val targetPlatform: TargetPlatform,
    val targetResolution: Int,
    val relevantKnowledge: List<KnowledgeEntry>
)

/** Emitted by every AiProvider call so the UI can render the branded Classify→Analyze→Validate→Report sequence (4.6). */
sealed interface PipelineEvent<out T> {
    data class Progress(val stage: ReasoningStage) : PipelineEvent<Nothing>
    data class Success<T>(val data: T) : PipelineEvent<T>
    data class Failure(val error: AiProviderError) : PipelineEvent<Nothing>
}

sealed class AiProviderError(val message: String, val cause: Throwable? = null) {
    data object Offline : AiProviderError("No network connection available.")
    data object RateLimited : AiProviderError("The AI provider is temporarily rate-limiting requests.")
    data class InvalidResponse(val raw: String) : AiProviderError("The AI response did not match the expected schema.")
    data class Unknown(val throwable: Throwable) : AiProviderError(throwable.message ?: "Unknown error", throwable)
}
