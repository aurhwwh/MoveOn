package com.example.moveon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moveon.ui.entry.SignInScreen
import com.example.moveon.ui.entry.SignUpScreen
import com.example.moveon.ui.events.CreateEvent
import com.example.moveon.ui.events.EventDetails
import com.example.moveon.ui.events.MainScreen
import com.example.moveon.ui.map.MapScreen
import com.example.moveon.ui.profile.EditProfileScreen
import com.example.moveon.ui.profile.MyProfileScreen
import com.example.moveon.ui.profile.UserProfileScreen
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.viewModel.CityViewModel
import com.example.moveon.viewModel.ProfileViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }

            val channel = NotificationChannel(
                "default",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                Log.d("FCM_TOKEN", it.result ?: "NULL")
            }

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
            val cityViewModel: CityViewModel = viewModel()
            val profileViewModel: ProfileViewModel = viewModel()

            NavHost(
                navController = navController,
                startDestination = if (
                    com.example.moveon.data.TokenStorage.getAccess().isNullOrEmpty()
                ) {
                    "login"
                } else {
                    "main"
                }
            ) {
                composable("main") {
                    MainScreen(navController, cityViewModel)
                }
                composable("profile") {
                    MyProfileScreen(navController, cityViewModel, profileViewModel)
                }
                composable("editProfile") {
                    EditProfileScreen(navController, profileViewModel)
                }
                composable("addEvent?lat={lat}&lon={lon}",
                    arguments = listOf(
                        navArgument("lat") {
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("lon") {
                            nullable = true
                            defaultValue = null
                        }
                    )) { backStackEntry ->

                    CreateEvent(
                        navController = navController,
                        lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull(),
                        lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
                    )
                }
                composable("eventDetails/{eventId}") { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId")!!.toInt()
                    EventDetails(navController, eventId = eventId)
                }
                composable("register") {
                    SignUpScreen(navController)
                }
                composable("login"){
                    SignInScreen(navController)
                }
                composable("profile/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")!!.toInt()
                    UserProfileScreen(navController, cityViewModel, userId = userId)
                }
                composable("map") {
                    MapScreen(navController)
                }
                composable("map/{eventId}") { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId")!!.toInt()
                    MapScreen(navController, eventId = eventId)
                }
            }
        }
    }
}