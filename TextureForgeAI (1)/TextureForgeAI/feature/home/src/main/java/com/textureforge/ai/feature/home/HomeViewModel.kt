package com.textureforge.ai.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textureforge.ai.core.domain.model.AnalysisResult
import com.textureforge.ai.core.domain.model.KnowledgeEntry
import com.textureforge.ai.core.domain.model.Project
import com.textureforge.ai.core.domain.repository.ConnectivityObserver
import com.textureforge.ai.core.domain.usecase.ObserveHomeDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** MVI state for Section 7.2. Loading/Error/Empty/Content are all real, distinct states — never a blank screen (Law #4). */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Content(
        val recentAnalyses: List<AnalysisResult>,
        val projects: List<Project>,
        val dailyTip: KnowledgeEntry?,
        val isOffline: Boolean
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeHomeDashboard: ObserveHomeDashboardUseCase,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        observeHomeDashboard(),
        connectivityObserver.isOnline(),
        errorFlow
    ) { dashboard, isOnline, error ->
        when {
            error != null -> HomeUiState.Error(error)
            else -> HomeUiState.Content(
                recentAnalyses = dashboard.recentAnalyses,
                projects = dashboard.projects,
                dailyTip = dashboard.dailyTip,
                isOffline = !isOnline
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    val activeProject: StateFlow<Project?> = uiState.map { state ->
        (state as? HomeUiState.Content)?.projects?.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun retry() {
        errorFlow.value = null
    }
}
