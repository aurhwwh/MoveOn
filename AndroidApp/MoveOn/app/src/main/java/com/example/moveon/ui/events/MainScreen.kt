package com.example.moveon.ui.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems

import com.example.moveon.ui.common.BottomBar
import com.example.moveon.ui.common.CityTopBar
import com.example.moveon.ui.theme.DLightGreen
import com.example.moveon.ui.theme.LightGreen
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.viewModel.CityViewModel
import com.example.moveon.viewModel.EventsViewModel


@Composable
fun MainScreen(navController : NavController, cityViewModel: CityViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {

        val eventsViewModel: EventsViewModel = viewModel()

        LaunchedEffect(cityViewModel) {
            cityViewModel.selectedCity.collect { city ->
                eventsViewModel.setCity(city)
            }
        }

        var showFilters by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { CityTopBar(cityViewModel) },
            bottomBar = { BottomBar(navController) },
            containerColor = LightGreen
        ) { padding ->
            Column(modifier = Modifier.weight(1f).padding(padding)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "События ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.2).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp))
                    {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DLightGreen,
                            border = BorderStroke(0.5.dp, MGreen.copy(0.6f)),
                            modifier = Modifier.size(34.dp).clickable { showFilters = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Фильтры",
                                    tint = Color(0xFF0F6E56),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                val events = eventsViewModel.eventsFlow.collectAsLazyPagingItems()

                when (val state = events.loadState.refresh) {
                    is LoadState.Loading -> {Text("Загрузка")}

                    is LoadState.Error -> {
                        Column {
                            Text("${state.error.message}")

                            Text("Retry", modifier = Modifier.clickable {events.retry()})
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(count = events.itemCount) { index ->
                                events[index]?.let {
                                    MakeEvent(
                                        data = it,
                                        onClick = { navController.navigate("eventDetails/${it.eventId}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showFilters) {
                FiltersBottomSheet(
                    currentFilters = eventsViewModel.filters,
                    onDismiss = {
                        showFilters = false
                    },
                    onApply = { sport, maxPeople, rating, nextDays ->
                        eventsViewModel.updateFilters(
                            sportType = sport,
                            maxAmountOfPeople = maxPeople,
                            creatorRating = rating,
                            nextDays = nextDays
                        )
                        showFilters = false
                    },
                    onClear = {
                        eventsViewModel.clearFilters()
                        showFilters = false
                    }
                )
            }
        }
    }
}