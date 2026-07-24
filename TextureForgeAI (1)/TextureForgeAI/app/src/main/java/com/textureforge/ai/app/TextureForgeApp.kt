package com.textureforge.ai.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Composition root entry point (Section 6.1). Hilt generates the full DI
 * graph from here; no manual service locators anywhere in the app.
 */
@HiltAndroidApp
class TextureForgeApp : Application()
