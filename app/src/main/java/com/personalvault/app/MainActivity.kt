package com.personalvault.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.personalvault.app.ui.AppNavGraph
import com.personalvault.app.ui.lock.BiometricGate
import com.personalvault.app.ui.theme.PersonalVaultTheme

// FragmentActivity is required by androidx.biometric.BiometricPrompt.
// It still supports Jetpack Compose via setContent { } because
// FragmentActivity extends androidx.activity.ComponentActivity.
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalVaultTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
private fun AppRoot() {
    var unlocked by remember { mutableStateOf(false) }
    if (!unlocked) {
        BiometricGate(onUnlocked = { unlocked = true })
    } else {
        AppNavGraph()
    }
}
