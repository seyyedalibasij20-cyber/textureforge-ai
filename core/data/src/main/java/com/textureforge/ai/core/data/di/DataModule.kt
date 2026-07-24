package com.textureforge.ai.core.data.di

import android.content.Context
import androidx.room.Room
import com.textureforge.ai.core.data.local.ConnectivityObserverImpl
import com.textureforge.ai.core.data.local.TextureForgeDatabase
import com.textureforge.ai.core.data.local.UserPrefsRepositoryImpl
import com.textureforge.ai.core.data.local.dao.AnalysisDao
import com.textureforge.ai.core.data.local.dao.KnowledgeDao
import com.textureforge.ai.core.data.local.dao.MaterialSwatchDao
import com.textureforge.ai.core.data.local.dao.ProjectDao
import com.textureforge.ai.core.data.local.dao.PromptHistoryDao
import com.textureforge.ai.core.data.local.dao.QaReportDao
import com.textureforge.ai.core.data.local.dao.WorkflowHistoryDao
import com.textureforge.ai.core.data.repository.AnalysisRepositoryImpl
import com.textureforge.ai.core.data.repository.KnowledgeRepositoryImpl
import com.textureforge.ai.core.data.repository.MaterialSwatchRepositoryImpl
import com.textureforge.ai.core.data.repository.ProjectRepositoryImpl
import com.textureforge.ai.core.data.repository.PromptHistoryRepositoryImpl
import com.textureforge.ai.core.data.repository.QaReportRepositoryImpl
import com.textureforge.ai.core.data.repository.WorkflowHistoryRepositoryImpl
import com.textureforge.ai.core.domain.repository.AnalysisRepository
import com.textureforge.ai.core.domain.repository.ConnectivityObserver
import com.textureforge.ai.core.domain.repository.KnowledgeRepository
import com.textureforge.ai.core.domain.repository.MaterialSwatchRepository
import com.textureforge.ai.core.domain.repository.ProjectRepository
import com.textureforge.ai.core.domain.repository.PromptHistoryRepository
import com.textureforge.ai.core.domain.repository.QaReportRepository
import com.textureforge.ai.core.domain.repository.UserPrefsRepository
import com.textureforge.ai.core.domain.repository.WorkflowHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TextureForgeDatabase =
        Room.databaseBuilder(context, TextureForgeDatabase::class.java, TextureForgeDatabase.DATABASE_NAME)
            // No destructive fallback in production: a failed migration should
            // surface as a clear error, not silent data loss. Kept here as an
            // explicit, documented decision for the eventual migration story.
            .build()

    @Provides fun provideProjectDao(db: TextureForgeDatabase): ProjectDao = db.projectDao()
    @Provides fun provideAnalysisDao(db: TextureForgeDatabase): AnalysisDao = db.analysisDao()
    @Provides fun provideQaReportDao(db: TextureForgeDatabase): QaReportDao = db.qaReportDao()
    @Provides fun providePromptHistoryDao(db: TextureForgeDatabase): PromptHistoryDao = db.promptHistoryDao()
    @Provides fun provideWorkflowHistoryDao(db: TextureForgeDatabase): WorkflowHistoryDao = db.workflowHistoryDao()
    @Provides fun provideKnowledgeDao(db: TextureForgeDatabase): KnowledgeDao = db.knowledgeDao()
    @Provides fun provideMaterialSwatchDao(db: TextureForgeDatabase): MaterialSwatchDao = db.materialSwatchDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {
    @Binds @Singleton abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository
    @Binds @Singleton abstract fun bindAnalysisRepository(impl: AnalysisRepositoryImpl): AnalysisRepository
    @Binds @Singleton abstract fun bindQaReportRepository(impl: QaReportRepositoryImpl): QaReportRepository
    @Binds @Singleton abstract fun bindWorkflowHistoryRepository(impl: WorkflowHistoryRepositoryImpl): WorkflowHistoryRepository
    @Binds @Singleton abstract fun bindPromptHistoryRepository(impl: PromptHistoryRepositoryImpl): PromptHistoryRepository
    @Binds @Singleton abstract fun bindKnowledgeRepository(impl: KnowledgeRepositoryImpl): KnowledgeRepository
    @Binds @Singleton abstract fun bindMaterialSwatchRepository(impl: MaterialSwatchRepositoryImpl): MaterialSwatchRepository
    @Binds @Singleton abstract fun bindUserPrefsRepository(impl: UserPrefsRepositoryImpl): UserPrefsRepository
    @Binds @Singleton abstract fun bindConnectivityObserver(impl: ConnectivityObserverImpl): ConnectivityObserver
}
