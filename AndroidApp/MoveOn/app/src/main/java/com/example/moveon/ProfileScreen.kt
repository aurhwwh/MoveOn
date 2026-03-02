package com.example.moveon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun ProfileScreen(navController : NavController) {
    Column() {
        TopBar(city = "Saint-Petersburg")

        Box(
            modifier = Modifier.weight(1f)
        ) {
            Text("Profile")
        }

        BottomBar(navController)
    }
}