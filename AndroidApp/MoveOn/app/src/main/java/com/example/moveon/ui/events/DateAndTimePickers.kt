package com.example.moveon.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EventDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    isError: Boolean = false
) {
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
            isError = isError,

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
                selectedDate?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds() ?:
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
    onTimeSelected: (Int, Int) -> Unit,
    isError: Boolean = false
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
            modifier = Modifier.fillMaxWidth(),
            isError = isError
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
                    } else   {
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