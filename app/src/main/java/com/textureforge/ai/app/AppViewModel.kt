package com.textureforge.ai.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textureforge.ai.core.designsystem.theme.MotionPreferences
import com.textureforge.ai.core.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Owns the single source of truth for [MotionPreferences] so every screen's
 * ambient background and glass blur strength stay in sync with Settings
 * (7.9) without each feature module depending on :core:data directly.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    val motionPreferences: StateFlow<MotionPreferences> = userPrefsRepository.observePrefs()
        .map { prefs ->
            MotionPreferences(
                ambientMotionEnabled = prefs.ambientMotionEnabled,
                reduceMotionEnabled = prefs.reduceMotionEnabled,
                glassBlurStrength = prefs.glassBlurIntensity,
                liteMotionMode = prefs.liteMotionModeAutoEnabled
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MotionPreferences())

    /** Null while prefs are still loading from DataStore — callers must not decide a start destination until this is non-null (avoids a flash to Onboarding for returning users). */
    val isGuestOrSignedIn: StateFlow<Boolean?> = userPrefsRepository.observePrefs()
        .map { it.userId != null || it.isGuestMode }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
