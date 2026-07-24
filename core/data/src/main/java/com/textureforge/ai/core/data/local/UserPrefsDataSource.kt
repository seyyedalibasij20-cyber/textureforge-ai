package com.textureforge.ai.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.textureforge.ai.core.domain.model.DomainFailure
import com.textureforge.ai.core.domain.model.DomainResult
import com.textureforge.ai.core.domain.model.UserPrefs
import com.textureforge.ai.core.domain.repository.UserPrefsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "textureforge_prefs")

private object PrefsKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val IS_GUEST = booleanPreferencesKey("is_guest_mode")
    val AMBIENT_MOTION = booleanPreferencesKey("ambient_motion_enabled")
    val REDUCE_MOTION = booleanPreferencesKey("reduce_motion_enabled")
    val BLUR_INTENSITY = floatPreferencesKey("glass_blur_intensity")
    val LITE_MOTION_AUTO = booleanPreferencesKey("lite_motion_auto")
}

@Singleton
class UserPrefsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPrefsRepository {

    override fun observePrefs(): Flow<UserPrefs> = context.dataStore.data.map { prefs ->
        UserPrefs(
            userId = prefs[PrefsKeys.USER_ID],
            isGuestMode = prefs[PrefsKeys.IS_GUEST] ?: true,
            ambientMotionEnabled = prefs[PrefsKeys.AMBIENT_MOTION] ?: true,
            reduceMotionEnabled = prefs[PrefsKeys.REDUCE_MOTION] ?: false,
            glassBlurIntensity = prefs[PrefsKeys.BLUR_INTENSITY] ?: 1.0f,
            liteMotionModeAutoEnabled = prefs[PrefsKeys.LITE_MOTION_AUTO] ?: true
        )
    }

    override suspend fun update(transform: (UserPrefs) -> UserPrefs): DomainResult<Unit> = runCatching {
        val current = observePrefs().first()
        val next = transform(current)
        context.dataStore.edit { prefs ->
            next.userId?.let { prefs[PrefsKeys.USER_ID] = it }
            prefs[PrefsKeys.IS_GUEST] = next.isGuestMode
            prefs[PrefsKeys.AMBIENT_MOTION] = next.ambientMotionEnabled
            prefs[PrefsKeys.REDUCE_MOTION] = next.reduceMotionEnabled
            prefs[PrefsKeys.BLUR_INTENSITY] = next.glassBlurIntensity
            prefs[PrefsKeys.LITE_MOTION_AUTO] = next.liteMotionModeAutoEnabled
        }
    }.fold({ DomainResult.Success(Unit) }, { DomainResult.Error(DomainFailure.Storage) })
}
