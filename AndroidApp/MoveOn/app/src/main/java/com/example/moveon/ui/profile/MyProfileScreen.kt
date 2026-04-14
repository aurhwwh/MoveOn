package com.example.moveon.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveon.ui.common.BottomBar
import com.example.moveon.ui.common.CityTopBar
import com.example.moveon.viewModel.CityViewModel
import com.example.moveon.viewModel.ProfileViewModel


@Composable
fun MyProfileScreen(navController : NavController,
                    cityViewModel: CityViewModel,
                    viewModel: ProfileViewModel = viewModel()) {

    LaunchedEffect("profile") {
        viewModel.loadMyProfile()
    }

    Scaffold(
        topBar = { CityTopBar(cityViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { padding ->

        Column( modifier = Modifier.fillMaxSize().padding(padding) ) {
            when {
                viewModel.isLoading -> Text("Loading")
                viewModel.error != null -> Text(text = viewModel.error ?: "Unknown error")

                viewModel.profile != null -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Box(modifier = Modifier.fillMaxWidth()) {
                            MakeProfile(viewModel.profile!!)

                            IconButton(onClick = { navController.navigate("editProfile") },
                                modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    tint = Color.Black,
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp).padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly)
                        {
                            Icon(
                                imageVector = Icons.Filled.Accessibility,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.AddReaction,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.Build,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Row(modifier = Modifier.weight(1f).padding(start = 12.dp, top = 24.dp).clickable{})
                        {
                            Text(text = "My Events",
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 22.sp
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "My Events",
                                tint = Color.Black,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}