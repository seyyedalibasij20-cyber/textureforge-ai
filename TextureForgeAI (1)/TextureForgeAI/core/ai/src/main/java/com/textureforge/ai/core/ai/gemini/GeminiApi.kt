package com.textureforge.ai.core.ai.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Minimal Retrofit surface for Gemini's generateContent endpoint, multimodal
 * + structured-output capable (Section 6.3: "must request and parse
 * structured JSON output using Gemini's structured output / function-calling
 * capability"). Model id and base URL are supplied via [GeminiConfig], never
 * hardcoded, so the key/model can be swapped without a code change.
 */
interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiGenerateContentRequest
    ): Response<GeminiGenerateContentResponse>
}

@Serializable
data class GeminiGenerateContentRequest(
    val contents: List<GeminiContent>,
    @SerialName("generationConfig") val generationConfig: GeminiGenerationConfig
)

@Serializable
data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String? = null,
    @SerialName("inline_data") val inlineData: GeminiInlineData? = null
)

@Serializable
data class GeminiInlineData(
    @SerialName("mime_type") val mimeType: String,
    val data: String // base64
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Float = 0.3f,
    @SerialName("response_mime_type") val responseMimeType: String = "application/json",
    /** JSON Schema constraining the structured output — see [GeminiSchemas]. */
    @SerialName("response_schema") val responseSchema: JsonElement? = null
)

@Serializable
data class GeminiGenerateContentResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    @SerialName("finishReason") val finishReason: String? = null
)
