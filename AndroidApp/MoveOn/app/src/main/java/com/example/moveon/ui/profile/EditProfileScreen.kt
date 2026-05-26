package com.example.moveon.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moveon.R
import com.example.moveon.client.api.AvatarApi
import com.example.moveon.data.TokenStorage
import com.example.moveon.ui.common.MoveOnTopBar
import com.example.moveon.ui.theme.MGreen
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.io.File
import java.io.InputStream
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<LocalDate?>(null) }
    var city by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Avatar related
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val userId = TokenStorage.getUserIdFromToken() ?: 0
    val baseUrl = "http://10.0.2.2:8080"
    val currentAvatarUrl = "$baseUrl/avatars/$userId.jpg"

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uploadError = null
    }

    fun uploadAvatar(uri: Uri) {
        scope.launch {
            isUploading = true
            uploadError = null
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val success = AvatarApi.uploadAvatar(tempFile)
                if (success) {
                    selectedImageUri = null
                    Toast.makeText(context, "Avatar updated", Toast.LENGTH_SHORT).show()
                } else {
                    uploadError = "Upload failed"
                }
            } catch (e: Exception) {
                uploadError = e.message ?: "Unknown error"
            } finally {
                isUploading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MoveOnTopBar(navController, "profile")

        // Avatar with click handling
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
                .size(100.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = currentAvatarUrl,
                    contentDescription = "Current avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.img),
                    placeholder = painterResource(R.drawable.img)
                )
            }
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.White)
            }
        }

        Text(
            text = "Tap to change photo",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (uploadError != null) {
            Text(
                text = uploadError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        BirthDatePicker(
            selectedDate = birth,
            onDateSelected = { birth = it },
            width = 1f
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
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

        if (selectedImageUri != null && !isUploading) {
            Button(
                onClick = { uploadAvatar(selectedImageUri!!) },
                colors = ButtonDefaults.buttonColors(containerColor = MGreen),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Upload new avatar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { /* Save profile data */ },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 20.sp, text = "Save")
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            modifier = Modifier.align(Alignment.End).padding(8.dp),
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
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(fontSize = 20.sp, text = "Logout")
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
            label = { Text("Date of birth") },
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
            DatePicker(state = datePickerState)
        }
    }
}