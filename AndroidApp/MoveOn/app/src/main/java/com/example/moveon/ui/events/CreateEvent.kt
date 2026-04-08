package com.example.moveon.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.ui.common.MoveOnTopBar
import com.example.moveon.ui.theme.MGreen
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
@Composable
fun AddEvent(navController : NavController) {
    var name by remember { mutableStateOf("") }
    var sportType by remember { mutableStateOf("") }
    var maxAmountInput by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var date by remember { mutableStateOf<LocalDate?>(null) }
    var hours by remember { mutableStateOf<Int?>(null) }
    var mins by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoveOnTopBar(navController, "main")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Event",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp,
            )


            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Event name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = sportType,
                onValueChange = { sportType = it },
                label = { Text("Sport type") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    EventDatePicker(
                        selectedDate = date,
                        onDateSelected = { date = it }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    TimePickerField(
                        selectedDate = date,
                        hour = hours,
                        minute = mins,
                        onTimeSelected = { h, m ->
                            hours = h
                            mins = m
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = maxAmountInput,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        maxAmountInput = input
                    }
                },
                label = { Text("Amount of people (2–20)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            onClick = {
                val maxPeople = maxAmountInput.toIntOrNull()?.coerceIn(2, 20)

                val dateTime = if (date != null && hours != null && mins != null) {
                    date!!.atTime(hours!!, mins!!).toInstant(TimeZone.currentSystemDefault())
                } else null

                val isValid = name.isNotBlank() && sportType.isNotBlank() && maxPeople != null && dateTime != null

                if (!isValid) return@Button

                val request = CreateEventRequest(
                    title = name,
                    description = description,
                    time = dateTime,
                    maxAmountOfPeople = maxPeople,
                    sportType = sportType
                )

                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    try {
                        val response = Handlers.eventsHandler.createEvent(request)

                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            navController.navigate("main")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 25.sp, text = "Create")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EventDatePicker(selectedDate: LocalDate?, onDateSelected: (LocalDate) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val formatter = remember { java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        TextField(
            value = selectedDate?.toJavaLocalDate()?.format(formatter) ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Event date") },
            modifier = Modifier.fillMaxWidth(),

            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            }
        )
    }

    val today = Instant.fromEpochMilliseconds(System.currentTimeMillis()).toLocalDateTime(TimeZone.currentSystemDefault()).date
    val maxDate = today.plus(2, DateTimeUnit.MONTH)

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis =
                today.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),

            selectableDates = object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean {
                    return year in today.year..maxDate.year
                }

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant
                        .fromEpochMilliseconds(utcTimeMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date

                    return date in today..maxDate
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val date = Instant
                            .fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date

                        onDateSelected(date)
                    }
                    showDialog = false
                }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TimePickerField(
    selectedDate: LocalDate?,
    hour: Int?,
    minute: Int?,
    onTimeSelected: (Int, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val now = remember {
        Instant.fromEpochMilliseconds(System.currentTimeMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        TextField(
            value = if (hour != null && minute != null) {
                "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
            } else "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDialog) {
        val timeState = rememberTimePickerState()

        val isToday = (selectedDate == now.date)

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    val h = timeState.hour
                    val m = timeState.minute

                    val isValid = if (isToday) {
                        (h > now.hour + 1) || (h == now.hour + 1 && m >= now.minute)
                    } else {
                        true
                    }

                    if (isValid) {
                        onTimeSelected(h, m)
                        showDialog = false
                    }
                }) {
                    Text("OK")
                }
            },
            text = {
                TimePicker(state = timeState)
            }
        )
    }
}