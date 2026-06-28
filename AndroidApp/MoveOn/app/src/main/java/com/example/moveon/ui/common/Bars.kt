package com.example.moveon.ui.common

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.moveon.viewModel.CityViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moveon.ui.theme.DLightGreen

@Composable
fun CityTopBar(viewModel: CityViewModel) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
        .background(MGreen)
        .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Move") }
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("On") }
                },
                color = Color.White,
                fontSize = 30.sp,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-0.3).sp
            )

            SelectCity(
                selectedCity = viewModel.selectedCity.collectAsState().value,
                onSelectedCity = { viewModel.updateCity(it) }
            )
        }
    }
}


@Composable
fun BottomBar(
    navController: NavController
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DLightGreen,
        tonalElevation = 0.dp,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row( modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomBarItem(
                icon = Icons.Outlined.Home,
                label = "Главная",
                isActive = currentRoute == "main",
                onClick = {
                    navController.navigate("main") {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )

            BottomBarItem(
                icon = Icons.Outlined.Map,
                label = "Карта",
                isActive = currentRoute == "map",
                onClick = {
                    navController.navigate("map") {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MGreen)
                    .clickable { navController.navigate("addEvent") }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Создать событие",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Создать",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            BottomBarItem(
                icon = Icons.Outlined.AccountCircle,
                label = "Профиль",
                isActive = currentRoute == "profile",
                onClick = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) Color(0xFFE1F5EE) else Color.Transparent
    val contentColor = if (isActive) Color(0xFF06AC9F) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            color = contentColor
        )
    }
}



@Composable
fun MoveOnTopBar(navController : NavController, prevScreen : String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
        .background(MGreen)
        .windowInsetsPadding(WindowInsets.statusBars)
        .padding(vertical = 4.dp)
        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {navController.popBackStack()}) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Move") }
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("On") }
            },
            color = Color.White,
            fontSize = 28.sp,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-0.3).sp
        )

        Box(modifier = Modifier.size(48.dp))
    }
}