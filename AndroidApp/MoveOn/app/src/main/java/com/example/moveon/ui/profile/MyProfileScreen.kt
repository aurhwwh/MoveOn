package com.example.moveon.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.moveon.ui.events.MakeEvent
import com.example.moveon.viewModel.CityViewModel
import com.example.moveon.viewModel.ProfileViewModel


@Composable
fun MyProfileScreen(navController : NavController,
                    cityViewModel: CityViewModel,
                    viewModel: ProfileViewModel = viewModel()) {

    LaunchedEffect("profile") {
        viewModel.loadMyProfile()
        viewModel.loadMyEvents()
    }

    Scaffold(
        topBar = { CityTopBar(cityViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { padding ->

        Column( modifier = Modifier
            .fillMaxSize()
            .padding(padding) ) {
            when {
                viewModel.isProfileLoading -> Text("Loading profile")
                viewModel.profileError != null -> Text(
                    text = viewModel.profileError ?: "Unknown error"
                )

                viewModel.profile != null -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MakeProfile(viewModel.profile!!)

                        IconButton(
                            onClick = { navController.navigate("editProfile") },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                    }

                    ProfileBottomIcons()
                }
            }

            Spacer(modifier = Modifier.size(30.dp))

            Text(
                text = "Events",
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontSize = 25.sp
            )

            var expanded by remember { mutableStateOf(false) }
            val types = listOf("All", "Created by me")

            Box()   {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true })
                {
                    Text(
                        text = viewModel.selectedEventsType,
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 24.sp
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expanded = true }
                    )
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text = type, fontSize = 20.sp) },
                            onClick = {
                                viewModel.updateSelectedEventsType(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            when {
                viewModel.isEventsLoading -> {
                    Text(
                        text = "Loading events...",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                viewModel.eventsError != null -> {
                    Text(
                        text = viewModel.eventsError ?: "Unknown error",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = viewModel.filteredEvents,
                            key = { it.eventId }
                        ) { event ->
                            MakeEvent(
                                data = event,
                                onClick = {
                                    navController.navigate("eventDetails/${event.eventId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}