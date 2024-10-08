package com.anthonyla.paperize.feature.wallpaper.presentation.settings_screen.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anthonyla.paperize.R
import com.anthonyla.paperize.data.Contact
import com.anthonyla.paperize.feature.wallpaper.presentation.settings_screen.SettingsState
import kotlinx.coroutines.flow.StateFlow

/**
 * Scrollable settings screen to wrap all settings components
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScrollableSettings(
    settingsState: StateFlow<SettingsState>,
    largeTopAppBarHeightPx: Dp,
    scroll: ScrollState,
    onDynamicThemingClick: (Boolean) -> Unit,
    onDarkModeClick: (Boolean?) -> Unit,
    onAmoledClick: (Boolean) -> Unit,
    onAnimateClick: (Boolean) -> Unit,
    onPrivacyClick: () -> Unit,
    onLicenseClick: () -> Unit,
    onResetClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val state = settingsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var toContact by rememberSaveable { mutableStateOf(false) }
    if (toContact) {
        Contact(context)
        toContact = false
    }

    val translateLink = "https://crowdin.com/project/paperize/invite?h=d8d7a7513d2beb0c96ba9b2a5f85473e2084922"
    val githubLink = "https://github.com/Anthonyy232/Paperize"
    val fdroidLink = "https://f-droid.org/en/packages/com.anthonyla.paperize/"
    val izzyOnDroidLink = "https://apt.izzysoft.de/fdroid/index/apk/com.anthonyla.paperize"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.home_screen)
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(Modifier.height(largeTopAppBarHeightPx))
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                ListSectionTitle(stringResource(R.string.appearance))
                Spacer(modifier = Modifier.height(16.dp))
                DarkModeListItem(
                    darkMode = state.value.darkMode,
                    onDarkModeClick = { onDarkModeClick(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (state.value.darkMode == null || state.value.darkMode == true) {
                    AmoledListItem(
                        amoledMode = state.value.amoledTheme,
                        onAmoledClick = { onAmoledClick(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                DynamicThemingListItem(
                    dynamicTheming = state.value.dynamicTheming,
                    onDynamicThemingClick = { onDynamicThemingClick(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AnimationListItem(
                    animate = state.value.animate,
                    onAnimateClick = { onAnimateClick(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ListSectionTitle(stringResource(R.string.about))
                Spacer(modifier = Modifier.height(16.dp))
                TranslateListItem(
                    onClick = {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(translateLink)
                        context.startActivity(openURL)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrivacyPolicyListItem (onPrivacyPolicyClick = onPrivacyClick)
                Spacer(modifier = Modifier.height(16.dp))
                LicenseListItem(onLicenseClick = onLicenseClick)
                Spacer(modifier = Modifier.height(16.dp))
                ContactListItem(onContactClick = { toContact = true })
                Spacer(modifier = Modifier.height(16.dp))
                PaperizeListItem(
                    onGitHubClick = {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(githubLink)
                        context.startActivity(openURL)
                    },
                    onFdroidClick = {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(fdroidLink)
                        context.startActivity(openURL)
                    },
                    onIzzyOnDroidClick = {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(izzyOnDroidLink)
                        context.startActivity(openURL)
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                ResetListItem(onResetClick = onResetClick)
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
    )
}