package com.example.moveon.ui.profile

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moveon.ui.common.BottomBar
import com.example.moveon.ui.common.CityTopBar
import com.example.moveon.ui.events.MakeEvent
import com.example.moveon.ui.theme.DLightGreen
import com.example.moveon.ui.theme.LightGreen
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.viewModel.CityViewModel
import com.example.moveon.viewModel.EventOwnerFilter
import com.example.moveon.viewModel.EventTimeFilter
import com.example.moveon.viewModel.ProfileViewModel


@Composable
fun MyProfileScreen(navController : NavController,
                    cityViewModel: CityViewModel,
                    viewModel: ProfileViewModel) {

    LaunchedEffect(Unit) {
        viewModel.loadMyProfile()
        viewModel.updateTimeFilter(EventTimeFilter.UPCOMING)
    }

    Scaffold(
        topBar = { CityTopBar(cityViewModel) },
        bottomBar = { BottomBar(navController) },
        //containerColor = LightGreen
    ) { padding ->

        Column( modifier = Modifier
            .fillMaxSize()
            .padding(padding) ) {
            when {
                viewModel.isProfileLoading -> Text("Загрузка")
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
                                .padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = "События",
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(Modifier.size(5.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.size(4.dp))

                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = viewModel.selectedTimeFilter == EventTimeFilter.UPCOMING,
                    onClick = {
                        viewModel.updateTimeFilter(EventTimeFilter.UPCOMING)
                    },
                    label = {Text(text = "Будущие", fontSize = 18.sp)},
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DLightGreen,
                        containerColor = Color.White
                    )
                )

                Spacer(Modifier.size(4.dp))

                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = viewModel.selectedTimeFilter == EventTimeFilter.PAST,
                    onClick = {
                        viewModel.updateTimeFilter(EventTimeFilter.PAST)
                    },
                    label = {Text(text = "Прошедшие", fontSize = 18.sp)},
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DLightGreen,
                        containerColor = Color.White
                    )
                )

                Spacer(Modifier.size(4.dp))
            }

            Row (verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.selectedOwnerFilter == EventOwnerFilter.ME,

                    onCheckedChange = { checked ->
                        viewModel.updateOwnerFilter(
                            if (checked) EventOwnerFilter.ME
                            else EventOwnerFilter.ANYONE
                        )
                    }
                )

                Text(text = "Созданные мной", fontSize = 17.sp)
            }

            when {
                viewModel.isEventsLoading -> {
                    Text(
                        text = "Загрузка...",
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