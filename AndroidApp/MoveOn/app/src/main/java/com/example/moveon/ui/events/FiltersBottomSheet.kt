package com.example.moveon.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListRequest
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    currentFilters: ViewFilteredEventsListRequest,
    onDismiss: () -> Unit,
    onApply: (
        sportType: String?,
        maxPeople: Int?,
        creatorRating: Double?,
        nextDays: Int?
    ) -> Unit,
    onClear: () -> Unit
) {

    var selectedSport by remember {
        mutableStateOf(currentFilters.sportType)
    }

    var maxPeople by remember {
        mutableFloatStateOf(
            currentFilters.maxAmountOfPeople?.toFloat() ?: 20f
        )
    }

    var rating by remember {
        mutableFloatStateOf(
            currentFilters.creatorRating?.toFloat() ?: 0f
        )
    }

    var selectedDays by remember {
        mutableStateOf(currentFilters.nextDays)
    }


    val daysOptions = listOf(
        null to "Любое время",
        1 to "Сегодня",
        3 to "3 дня",
        7 to "Неделя",
        14 to "2 недели",
        30 to "Месяц",
        60 to "2 месяца"
    )


    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Фильтры",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )


            Text(
                "Вид спорта",
                fontSize = 18.sp
            )

            Spacer(Modifier.height(8.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    SportPicker(
                        selectedSport = selectedSport ?: "",
                        onSportSelected = {
                            selectedSport = it
                        }
                    )
                }


                if (selectedSport != null) {
                    IconButton(
                        onClick = {
                            selectedSport = null
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Очистить"
                        )
                    }
                }
            }



            Spacer(Modifier.height(24.dp))


            Text(
                "Максимум участников: ${maxPeople.toInt()}",
                fontSize = 18.sp
            )

            Slider(
                value = maxPeople,
                onValueChange = {
                    maxPeople = it
                },
                valueRange = 2f..20f,
                steps = 18
            )

            Spacer(Modifier.height(24.dp))


            Text(
                "Минимальный рейтинг организатора",
                fontSize = 18.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    if (rating == 0f)
                        "Любой"
                    else
                        String.format(Locale.US,"%.1f", rating),
                    fontSize = 18.sp
                )

                if (rating > 0f) {
                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Filled.Star,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }

            Slider(
                value = rating,
                onValueChange = {
                    rating = (it * 2).roundToInt() / 2f
                },
                valueRange = 0f..5f,
                steps = 9
            )

            Spacer(Modifier.height(24.dp))


            Text(
                "Когда",
                fontSize = 18.sp
            )

            Spacer(Modifier.height(8.dp))

            val daysOptions = listOf(
                1 to "Сегодня",
                3 to "3 дня",
                7 to "Неделя",
                14 to "2 недели",
                30 to "Месяц",
                60 to "2 месяца"
            )


            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                daysOptions.chunked(3).forEach { rowOptions ->

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        rowOptions.forEach { (days, title) ->
                            FilterChip(
                                selected = selectedDays == days,
                                onClick = {
                                    selectedDays =
                                        if (selectedDays == days)
                                            null
                                        else
                                            days
                                },
                                label = {
                                    Text(
                                        title,
                                        fontSize = 15.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(
                    onClick = onClear
                ) {
                    Text(
                        "Сбросить",
                        fontSize = 17.sp
                    )
                }

                Button(
                    onClick = {
                        onApply(
                            selectedSport,
                            maxPeople.toInt().let {
                                if (it == 20) null else it
                            },
                            if (rating == 0f)
                                null
                            else
                                rating.toDouble(),
                            selectedDays
                        )
                    }
                ) {
                    Text(
                        "Применить",
                        fontSize = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}