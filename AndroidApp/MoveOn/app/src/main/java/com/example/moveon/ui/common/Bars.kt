package com.example.moveon.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moveon.ui.theme.MGreen

@Composable
fun CityTopBar(
    city: String
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MGreen)
        .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MoveOn",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp,
            )

            SelectCity()
        }
    }
}


@Composable
fun BottomBar(
    navController: NavController
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MGreen)
        .padding(WindowInsets.navigationBars.asPaddingValues())
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.navigate("main") }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Main",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            }

            IconButton(onClick = { navController.navigate("map") }) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Map",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            }

            IconButton(onClick = { navController.navigate("messages") }) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Messages",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            }

            IconButton(onClick = { navController.navigate("notifications") }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            }

            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp).padding(top = 10.dp)
                )
            }
        }
    }
}


@Composable
fun MoveOnTopBar(navController : NavController, prevScreen : String) {
    Row(modifier = Modifier.fillMaxWidth().background(MGreen).windowInsetsPadding(WindowInsets.statusBars),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {navController.navigate(prevScreen) }) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }

        Text(
            text = "MoveOn",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.SansSerif,
            fontSize = 30.sp,
        )
    }
}