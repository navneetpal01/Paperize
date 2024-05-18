package com.anthonyla.paperize.feature.wallpaper.presentation

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.anthonyla.paperize.core.SettingsConstants
import com.anthonyla.paperize.data.settings.SettingsDataStore
import com.anthonyla.paperize.feature.wallpaper.presentation.settings_screen.SettingsViewModel
import com.anthonyla.paperize.feature.wallpaper.presentation.themes.PaperizeTheme
import com.anthonyla.paperize.feature.wallpaper.presentation.wallpaper_screen.WallpaperScreenViewModel
import com.anthonyla.paperize.feature.wallpaper.wallpaper_service.WallpaperService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val wallpaperScreenViewModel: WallpaperScreenViewModel by viewModels()
    private val context = this
    @Inject lateinit var settingsDataStoreImpl: SettingsDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SET_WALLPAPER) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.SET_WALLPAPER), 0)
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                val fadeOut = ObjectAnimator.ofFloat(splashScreenViewProvider.view, View.ALPHA, 1f, 0f)
                fadeOut.interpolator = AccelerateInterpolator()
                fadeOut.duration = 300L
                fadeOut.doOnEnd { splashScreenViewProvider.remove() }
                fadeOut.start()
            }
        }
        splashScreen.setKeepOnScreenCondition { settingsViewModel.setKeepOnScreenCondition }

        setContent {
            val settingsState = settingsViewModel.state.collectAsStateWithLifecycle()
            val selectedState = wallpaperScreenViewModel.state.collectAsStateWithLifecycle()
            val isFirstLaunch = runBlocking { settingsDataStoreImpl.getBoolean(SettingsConstants.FIRST_LAUNCH) } ?: true
            if (isFirstLaunch) {
                val contentResolver = context.contentResolver
                val persistedUris = contentResolver.persistedUriPermissions
                for (permission in persistedUris) {
                    contentResolver.releasePersistableUriPermission(permission.uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                lifecycleScope.launch {
                    val serviceIntent = Intent(this@MainActivity, WallpaperService::class.java)
                    val isAlreadyRunning = withContext(Dispatchers.IO) {5
                        val activityManager = getSystemService(ACTIVITY_SERVICE) as? ActivityManager
                        activityManager?.getRunningServices(Integer.MAX_VALUE)?.any {
                            it.service == serviceIntent.component
                        } ?: false
                    }
                    if (selectedState.value.selectedAlbum != null && settingsState.value.enableChanger) {
                        if (!isAlreadyRunning) {
                            val intent = Intent(context, WallpaperService::class.java).apply {
                                action = WallpaperService.Actions.START.toString()
                                putExtra("timeInMinutes", settingsState.value.interval)
                            }
                            context.startForegroundService(intent)
                        }
                    } else {
                        if (isAlreadyRunning) {
                            Intent(context, WallpaperService::class.java).also {
                                it.action = WallpaperService.Actions.STOP.toString()
                                context.startForegroundService(it)
                            }
                        }
                    }
                }
            }

            PaperizeTheme(settingsState) {
                Surface(tonalElevation = 5.dp) {
                    PaperizeApp(isFirstLaunch)
                }
            }

        }
    }
}
