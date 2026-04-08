package com.example.moveon.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.ui.profile.BirthDatePicker
import com.example.moveon.ui.theme.MGreen
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant


//@Preview(name = "Sign Up Screen")
//@Composable
//fun SignUpScreenPreview() {
//    SignUpScreen(navController = rememberNavController())
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<LocalDate?>(null) }
    var gender by remember { mutableStateOf("") }
    // var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat_password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Welcome to MoveOn!",
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.SansSerif,
            fontSize = 35.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("name") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("surname") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BirthDatePicker(
            selectedDate = birth,
            onDateSelected = { birth = it },
            width = 0.7f
        )

        Spacer(modifier = Modifier.height(16.dp))

        PickGender(
            selectedGender = gender,
            onGenderSelected = { gender = it }
        )

        /* Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("description") },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) */

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("password") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = repeat_password,
            onValueChange = { repeat_password = it },
            label = { Text("repeat password") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val scope = rememberCoroutineScope()
        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {

                val isValid = name.isNotBlank() &&
                        surname.isNotBlank() &&
                        birth != null &&
                        gender.isNotBlank() &&
                        email.isNotBlank() &&
                        password.isNotBlank() &&
                        repeat_password == password

                if (!isValid) return@Button

                val request = RegisterRequest(
                    userName = name,
                    userSurname = surname,
                    dateOfBirth = birth!!,
                    email = email,
                    password = password,
                    gender = gender
                )

                scope.launch {
                    val response = Handlers.entryHandler.register(request)

                    if (response.success) {
                        navController.navigate("main")
                    } else {
                        println(response.errorMessage)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 25.sp, text = "Sign Up")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickGender(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val genders = listOf("Male", "Female")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {

        TextField(
            value = selectedGender,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gender") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            },
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

