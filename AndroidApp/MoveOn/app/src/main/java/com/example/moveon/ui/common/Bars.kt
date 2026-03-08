package com.example.moveon.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
fun TopBar(
    city: String
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MGreen)
        .padding(WindowInsets.statusBars.asPaddingValues())
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

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {})
            {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "City",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
                Text(text = "Saint-Petersburg",
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 22.sp
                    //modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
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