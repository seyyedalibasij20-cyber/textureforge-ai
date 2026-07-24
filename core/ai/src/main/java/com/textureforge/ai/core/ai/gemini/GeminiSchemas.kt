package com.textureforge.ai.core.ai.gemini

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Response schemas passed as `generationConfig.response_schema`. Gemini
 * enforces the shape of its own output against these, which is what lets
 * [com.textureforge.ai.core.ai.gemini.GeminiResponseParser] deserialize
 * directly into strongly-typed DTOs without defensive prose-parsing
 * (Section 6.3, Law #5's "AI Estimate" flag is baked in as a required field).
 */
object GeminiSchemas {

    private val json = Json { ignoreUnknownKeys = true }

    val analysisResultSchema: JsonElement = json.parseToJsonElement(
        """
        {
          "type": "OBJECT",
          "properties": {
            "materialCategory": { "type": "STRING", "enum": ["STONE","METAL","WOOD","ORGANIC","FABRIC","SYNTHETIC","GROUND_TERRAIN","GLASS_CERAMIC","UNKNOWN"] },
            "materialCategoryConfidence": { "type": "STRING", "enum": ["HIGH","MEDIUM","LOW","UNKNOWN"] },
            "summary": { "type": "STRING" },
            "estimatedRoughnessMin": { "type": "NUMBER" },
            "estimatedRoughnessMax": { "type": "NUMBER" },
            "estimatedMetallicMin": { "type": "NUMBER" },
            "estimatedMetallicMax": { "type": "NUMBER" },
            "pbrConfidence": { "type": "STRING", "enum": ["HIGH","MEDIUM","LOW","UNKNOWN"] },
            "detectedIssues": {
              "type": "ARRAY",
              "items": {
                "type": "OBJECT",
                "properties": {
                  "channel": { "type": "STRING", "enum": ["BASE_COLOR","ROUGHNESS","METALLIC","NORMAL","HEIGHT","AMBIENT_OCCLUSION","EMISSION","ORM_PACKED"] },
                  "severity": { "type": "STRING", "enum": ["CRITICAL","WARNING","INFO","SUCCESS"] },
                  "title": { "type": "STRING" },
                  "description": { "type": "STRING" },
                  "recommendation": { "type": "STRING" }
                },
                "required": ["severity","title","description"]
              }
            },
            "recommendations": { "type": "ARRAY", "items": { "type": "STRING" } },
            "suggestedWorkflow": {
              "type": "ARRAY",
              "items": {
                "type": "OBJECT",
                "properties": {
                  "order": { "type": "INTEGER" },
                  "title": { "type": "STRING" },
                  "description": { "type": "STRING" },
                  "requiredMaps": { "type": "ARRAY", "items": { "type": "STRING", "enum": ["BASE_COLOR","ROUGHNESS","METALLIC","NORMAL","HEIGHT","AMBIENT_OCCLUSION","EMISSION","ORM_PACKED"] } },
                  "toolNotes": { "type": "STRING" }
                },
                "required": ["order","title","description","requiredMaps"]
              }
            }
          },
          "required": ["materialCategory","materialCategoryConfidence","summary","detectedIssues","recommendations","suggestedWorkflow"]
        }
        """.trimIndent()
    )

    val workflowStepsSchema: JsonElement = json.parseToJsonElement(
        """
        {
          "type": "ARRAY",
          "items": {
            "type": "OBJECT",
            "properties": {
              "order": { "type": "INTEGER" },
              "title": { "type": "STRING" },
              "description": { "type": "STRING" },
              "requiredMaps": { "type": "ARRAY", "items": { "type": "STRING" } },
              "toolNotes": { "type": "STRING" }
            },
            "required": ["order","title","description","requiredMaps"]
          }
        }
        """.trimIndent()
    )

    val promptTextSchema: JsonElement = json.parseToJsonElement(
        """
        { "type": "OBJECT", "properties": { "prompt": { "type": "STRING" } }, "required": ["prompt"] }
        """.trimIndent()
    )
}
