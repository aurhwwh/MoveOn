package com.example.moveon.ui.events


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveon.client.handlers.Place
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.CreateEventWithRouteRequest
import com.example.moveon.client.jsonClasses.Point
import com.example.moveon.ui.common.MoveOnTopBar
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.viewModel.EventsViewModel
import com.example.moveon.viewModel.GeocodingViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
fun CreateEvent(navController : NavController,
                lat: Double? = null,
                lon: Double? = null,
                route: List<Point>? = null,
                viewModel: EventsViewModel = viewModel(),
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var sportType by remember { mutableStateOf("") }
    var maxAmountInput by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var date by remember { mutableStateOf<LocalDate?>(null) }
    var hours by remember { mutableStateOf<Int?>(null) }
    var mins by remember { mutableStateOf<Int?>(null) }

    val geoViewModel: GeocodingViewModel = viewModel()
    val suggestions = geoViewModel.suggestion
    val reversePlace = geoViewModel.reverseResult
    var locationQuery by remember { mutableStateOf("") }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    var isNameError by remember { mutableStateOf(false) }
    var isSportTypeError by remember { mutableStateOf(false) }
    var isDateError by remember { mutableStateOf(false) }
    var isTimeError by remember { mutableStateOf(false) }
    var isMaxPeopleError by remember { mutableStateOf(false) }
    var isPlaceError by remember { mutableStateOf(false) }


    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            geoViewModel.reverseGeocode(lat, lon)
        }
    }

    LaunchedEffect(reversePlace) {
        reversePlace?.let {
            selectedPlace = it
            locationQuery = it.name
        }
    }

    LaunchedEffect(viewModel.createSuccess) {
        if (viewModel.createSuccess) {
            navController.navigate("main")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewModel.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
            var label = "Create Event with route"
            if(route.isNullOrEmpty()){
                label = "Create Event at point"
            }
            if(route.isNullOrEmpty() && lat == null && lon == null) {
                MoveOnTopBar(navController, "main")
                Spacer(modifier = Modifier.height(16.dp))
                label = "Create Event"
            }


            Text(
                text = label,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp,
            )


            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameError = false
                },
                isError = isNameError,
                label = { Text("Event name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down)
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            SportPicker(
                selectedSport = sportType,
                onSportSelected = {
                    sportType = it
                    isSportTypeError = false
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    EventDatePicker(
                        selectedDate = date,
                        onDateSelected = {
                            date = it
                            isDateError = false
                        },
                        isError = isDateError
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
                            isTimeError = false
                        },
                        isError = isTimeError
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = maxAmountInput,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        maxAmountInput = input
                        isMaxPeopleError = false
                    }
                },
                isError = isMaxPeopleError,
                label = { Text("Amount of people (2–20)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down)
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LocationSearchField(
                query = locationQuery,
                onQueryChange = {
                    locationQuery = it
                    selectedPlace = null
                    geoViewModel.onQueryChanged(it)
                },
                suggestions = suggestions,
                onPlaceSelected = { place ->
                    selectedPlace = place
                    locationQuery = place.name
                },
                isError = isPlaceError,
                readOnly = lat != null && lon != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            enabled = !viewModel.isCreating,
            onClick = {
                val maxPeople = maxAmountInput.toIntOrNull()?.coerceIn(2, 20)

                val dateTime = if (date != null && hours != null && mins != null) {
                    date!!.atTime(hours!!, mins!!).toInstant(TimeZone.currentSystemDefault())
                } else null

                isNameError = name.isBlank()
                isSportTypeError = sportType.isBlank()
                isDateError = date == null
                isTimeError = hours == null || mins == null
                isMaxPeopleError = maxPeople == null
                isPlaceError = selectedPlace == null

                if (
                    isNameError ||
                    isSportTypeError ||
                    isDateError ||
                    isTimeError ||
                    isMaxPeopleError ||
                    isPlaceError
                ) {
                    return@Button
                }

                if (route.isNullOrEmpty()) {

                    val request = CreateEventRequest(
                        title = name,
                        description = description,
                        dateTime = dateTime!!,
                        maxAmountOfPeople = maxPeople!!,
                        sportType = sportType,
                        city = selectedPlace!!.city,
                        place = selectedPlace!!.name,
                        lat = selectedPlace!!.lat,
                        lon = selectedPlace!!.lon
                    )

                    viewModel.createEvent(request)
                }
                else{
                    val request = CreateEventWithRouteRequest(
                        title = name,
                        description = description,
                        dateTime = dateTime!!,
                        maxAmountOfPeople = maxPeople!!,
                        sportType = sportType,
                        city = selectedPlace!!.city,
                        place = selectedPlace!!.name,
                        lat = selectedPlace!!.lat,
                        lon = selectedPlace!!.lon,
                        route = route
                    )

                    viewModel.createEventWithRoute(request)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 25.sp, text = if (viewModel.isCreating) "Creating..." else "Create")
        }
    }
}



@Composable
fun LocationSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<Place>,
    onPlaceSelected: (Place) -> Unit,
    isError: Boolean = false,
    readOnly: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldWidth by remember { mutableStateOf(0) }

    Box {

        TextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = it.isNotBlank()
            },
            label = { Text("Location") },
            isError = isError,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    textFieldWidth = it.size.width
                },
            readOnly = readOnly
        )

        DropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = {
                expanded = false
            },
            properties = PopupProperties(
                focusable = false
            ),
            modifier = Modifier.width(
                with(LocalDensity.current) {
                    textFieldWidth.toDp()
                }
            )
        ) {

            suggestions.forEachIndexed { index, place ->

                DropdownMenuItem(
                    text = {
                        Column {
                            Text(place.name)

                            Text(
                                text = place.city,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    },
                    onClick = {
                        onPlaceSelected(place)
                        expanded = false
                    }
                )

                if (index != suggestions.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportPicker(
    selectedSport: String,
    onSportSelected: (String) -> Unit
) {
    val sports = listOf(
        "Футбол",
        "Баскетбол",
        "Хоккей",
        "Теннис",
        "Воллейбол",
        "Бадбинтон",
        "Бег",
        "Велоспорт",
        "Ролики",
        "Коньки",
        "Лыжи",
        "Шашки",
        "Шахматы",
        "Прес качат",
        "Другое"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            value = selectedSport,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Sport type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sports.forEach { sport ->
                DropdownMenuItem(
                    text = { Text(sport, fontSize = 16.sp) },
                    onClick = {
                        onSportSelected(sport)
                        expanded = false
                    }
                )
            }
        }
    }
}