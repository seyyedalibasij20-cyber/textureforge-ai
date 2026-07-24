package com.textureforge.ai.core.data.local

import android.content.Context
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.KnowledgeSourceConfidence
import com.textureforge.ai.core.domain.model.MaterialCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads assets/knowledge_base_seed.json (Section 8: "bundled, versioned
 * local knowledge base ... pre-seeded via a JSON asset imported on first
 * launch"). Only called once, gated by [KnowledgeRepositoryImpl.ensureSeeded]
 * checking the row count, so re-installs / cold starts never duplicate rows.
 */
@Singleton
class KnowledgeBaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @Serializable
    private data class SeedEntry(
        val id: String,
        val title: String,
        val category: String?,
        val subcategory: String?,
        val body: String,
        val tags: List<String>,
        val relatedIds: List<String> = emptyList(),
        val confidenceSource: String = "VERIFIED",
        val version: Int = 1
    )

    fun loadBundledEntries(): List<KnowledgeEntry> {
        val json = context.assets.open("knowledge_base_seed.json").bufferedReader().use { it.readText() }
        val seedEntries = Json { ignoreUnknownKeys = true }
            .decodeFromString(kotlinx.serialization.builtins.ListSerializer(SeedEntry.serializer()), json)
        return seedEntries.map { entry ->
            KnowledgeEntry(
                id = entry.id,
                title = entry.title,
                category = entry.category?.let { runCatching { MaterialCategory.valueOf(it) }.getOrNull() },
                subcategory = entry.subcategory,
                body = entry.body,
                tags = entry.tags,
                relatedIds = entry.relatedIds,
                confidenceSource = runCatching { KnowledgeSourceConfidence.valueOf(entry.confidenceSource) }
                    .getOrDefault(KnowledgeSourceConfidence.VERIFIED),
                version = entry.version,
                isUserSaved = false
            )
        }
    }
}
