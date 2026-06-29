package com.example.moveon.ui.profile

import android.text.Layout
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveon.R
import com.example.moveon.data.TokenStorage
import com.example.moveon.ui.common.MoveOnTopBar
import com.example.moveon.ui.theme.DLightGreen
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.ui.theme.moveOnTextFieldColor
import com.example.moveon.utils.AvatarUtils
import com.example.moveon.utils.UserAvatar
import com.example.moveon.viewModel.ProfileViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile = viewModel.profile ?: return

    var name by remember(profile) { mutableStateOf(profile.name ?: "") }
    var surname by remember(profile) { mutableStateOf(profile.surname ?: "") }
    var birth by remember(profile) { mutableStateOf(profile.birth) }
    var description by remember(profile) { mutableStateOf(profile.description ?: "") }
    var selectedPhotoId by remember(profile) { mutableStateOf(profile.photoId) }

    var isNameError by remember { mutableStateOf(false) }
    var isSurnameError by remember { mutableStateOf(false) }
    var isBirthError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(viewModel.editSuccess) {
        if (viewModel.editSuccess) {
            viewModel.clearEditState()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                }
            )
        }
    ) {
        MoveOnTopBar(navController, "profile")

        UserAvatar(
            photoId = selectedPhotoId,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            colors = moveOnTextFieldColor(),
            modifier = Modifier.fillMaxWidth(),
            isError = isNameError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Фамилия") },
            colors = moveOnTextFieldColor(),
            modifier = Modifier.fillMaxWidth(),
            isError = isSurnameError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        BirthDatePicker(
            selectedDate = birth,
            onDateSelected = {
                birth = it
                isBirthError = false
            },
            width = 1f,
            isError = isBirthError
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("О себе") },
            colors = moveOnTextFieldColor(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Выберите аватарку",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AvatarUtils.avatarIds) { id ->
                val isSelected = selectedPhotoId == id
                UserAvatar(
                    photoId = id,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable { selectedPhotoId = id }
                        .let { mod ->
                            if (isSelected) {
                                mod.background(
                                    color = MGreen,
                                    shape = CircleShape
                                )
                            } else mod
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                if (birth == null) return@Button
                viewModel.editProfile(
                    name = name,
                    surname = surname,
                    birth = birth!!,
                    description = description.trim(),
                    photoId = selectedPhotoId
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MGreen
            )
        ) {
            Text(fontSize = 20.sp, text = "Сохранить")
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            modifier = Modifier.align(Alignment.End).padding(8.dp),
            enabled = !viewModel.isEditing,
            onClick = {
                TokenStorage.clear()
                navController.navigate("login") {
                    popUpTo(0) {
                        inclusive = true
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text(fontSize = 20.sp, text = "Выйти")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun BirthDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    width: Float,
    isError: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    val formatter = remember { java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = selectedDate?.toJavaLocalDate()?.format(formatter) ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Дата рождения") },
            colors = moveOnTextFieldColor(),
            modifier = Modifier.fillMaxWidth(width),
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            }
        )
    }

    val today = Instant.fromEpochMilliseconds(System.currentTimeMillis())
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    val minDate = today.minus(100, DateTimeUnit.YEAR)
    val maxDate = today.minus(16, DateTimeUnit.YEAR)

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = maxDate
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds(),
            selectableDates = object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean {
                    return year in minDate.year..maxDate.year
                }
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant
                        .fromEpochMilliseconds(utcTimeMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                    return date in minDate..maxDate
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
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        onDateSelected(date)
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MGreen,

                    disabledSelectedYearContainerColor = DLightGreen,
                    disabledSelectedDayContainerColor = DLightGreen,

                    todayContentColor = MGreen,
                    todayDateBorderColor = MGreen,

                    dayInSelectionRangeContainerColor = MGreen,

                    dateTextFieldColors = moveOnTextFieldColor(),

                    currentYearContentColor = MGreen,
                    selectedYearContainerColor = MGreen
                )
            )
        }
    }
}