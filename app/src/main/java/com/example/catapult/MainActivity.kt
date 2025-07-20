package com.example.catapult

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.catapult.core.compose.theme.CatapultTheme
import com.example.catapult.navigation.Navigation
import com.example.catapult.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !splashViewModel.bootCompleted.value
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatapultTheme {
                val hasUserAccount = splashViewModel.hasUserAccount.collectAsState()

                if (hasUserAccount.value)
                    Navigation(startDestination = "list")
                else
                    Navigation(startDestination = "register")
            }
        }
    }
}


