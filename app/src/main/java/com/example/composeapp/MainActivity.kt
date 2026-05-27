package com.example.composeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.composeapp.viewmodel.AuthViewModel
import com.example.composeapp.ui.screens.auth.LoginScreen
import com.example.composeapp.ui.screens.auth.RegistrationScreen
import com.example.composeapp.ui.screens.home.HomeScreen
import com.example.composeapp.ui.screens.features.HunterRegistrationScreen
import com.example.composeapp.ui.screens.features.AddCatchScreen
import com.example.composeapp.ui.screens.features.SalesScreen
import com.example.composeapp.ui.screens.features.ExpenseScreen
import com.example.composeapp.ui.screens.home.SettingsScreen
import com.example.composeapp.viewmodel.AuthState
import com.dilip.composeapp.ui.theme.ComposeAppTheme

class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        // Ensure app opens in Hindi by default if no preference is set
        if (androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().isEmpty) {
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                androidx.core.os.LocaleListCompat.forLanguageTags("hi")
            )
        }
        super.onCreate(savedInstanceState)
        
        // Keep the system splash screen on screen until the session check is finished
        splashScreen.setKeepOnScreenCondition {
            authViewModel.authState.value is AuthState.Checking
        }

        setContent {
            ComposeAppTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                
                // Once we are out of Checking state, show the appropriate screen
                if (authState !is AuthState.Checking) {
                    val startDestination = if (authState is AuthState.Success) "home" else "login"
                    
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") { LoginScreen(navController, authViewModel) }
                        composable("registration") { RegistrationScreen(navController, authViewModel) }
                        composable("home") { HomeScreen(navController, authViewModel) }
                        composable("hunter_reg") { HunterRegistrationScreen(navController) }
                        composable("add_catch") { AddCatchScreen(navController) }
                        composable("sales") { SalesScreen(navController) }
                        composable("expense") { ExpenseScreen(navController) }
                        composable("settings") { SettingsScreen(navController) }
                    }
                }
            }
        }
    }
}
