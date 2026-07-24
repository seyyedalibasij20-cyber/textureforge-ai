package com.textureforge.ai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textureforge.ai.app.navigation.TfAppScaffold
import com.textureforge.ai.app.navigation.TfDestination
import com.textureforge.ai.core.designsystem.theme.TextureForgeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host (Navigation Compose owns all screen transitions).
 * Edge-to-edge with proper WindowInsets handling per Law #8; WindowSizeClass
 * computed here once and threaded down so bottom-nav vs nav-rail switching
 * (4.7) is driven by real window metrics, not a guessed breakpoint.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val motionPreferences by appViewModel.motionPreferences.collectAsStateWithLifecycle()
            val isSignedInOrGuest by appViewModel.isGuestOrSignedIn.collectAsStateWithLifecycle()

            TextureForgeTheme(motionPreferences = motionPreferences) {
                // isSignedInOrGuest is null only for the first frame or two while
                // DataStore prefs are loading; the theme/background is already
                // visible underneath so this never reads as a blank screen.
                isSignedInOrGuest?.let { signedInOrGuest ->
                    TfAppScaffold(
                        startDestination = if (signedInOrGuest) TfDestination.Home else TfDestination.Onboarding,
                        widthSizeClass = windowSizeClass.widthSizeClass
                    )
                }
            }
        }
    }
}
