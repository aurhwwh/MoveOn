package com.example.moveon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.ui.mainScreen.MainScreen
import com.example.moveon.ui.profile.EditProfileScreen
import com.example.moveon.ui.profile.ProfileScreen
import com.example.moveon.ui.theme.MGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(
                scrim = MGreen.toArgb(),
                //darkScrim = MGreen.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.Companion.light(
                scrim = MGreen.toArgb(),
                darkScrim = MGreen.toArgb()
            )
        )

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") {
                    MainScreen(navController)
                }
                composable("profile") {
                    ProfileScreen(navController, Handlers.profileHandler)
                }
                composable("editProfile") {
                    EditProfileScreen(navController)
                }
            }
        }
    }
}