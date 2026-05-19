package com.example.moveon.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.LoginRequest
import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.data.TokenStorage.saveTokens
import com.example.moveon.ui.profile.BirthDatePicker
import com.example.moveon.ui.theme.MGreen
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.math.log


private fun isValidName(name: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9]+$")
    return name.isNotBlank() && regex.matches(name)
}

private fun isValidEmail(email: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    return regex.matches(email)
}

private fun isValidPassword(password: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9]+$")
    return password.isNotBlank() && regex.matches(password)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<LocalDate?>(null) }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    var isNameError by remember { mutableStateOf(false) }
    var isSurnameError by remember { mutableStateOf(false) }
    var isBirthError by remember { mutableStateOf(false) }
    var isGenderError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isRepeatPasswordError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun validateAndRegister() {
        val nameValid = isValidName(name)
        val surnameValid = isValidName(surname)
        val birthValid = birth != null
        val genderValid = gender.isNotBlank()
        val emailValid = isValidEmail(email)
        val passwordValid = isValidPassword(password)
        val repeatValid = password == repeatPassword && passwordValid

        isNameError = !nameValid
        isSurnameError = !surnameValid
        isBirthError = !birthValid
        isGenderError = !genderValid
        isEmailError = !emailValid
        isPasswordError = !passwordValid
        isRepeatPasswordError = !repeatValid

        if (nameValid && surnameValid && birthValid && genderValid && emailValid && passwordValid && repeatValid) {
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
                    val loginRequest = LoginRequest(request.email, request.password)
                    val loginResponse = Handlers.entryHandler.login(loginRequest);
                    if (loginResponse.success && !loginResponse.accessToken.isNullOrBlank() && !loginResponse.refreshToken.isNullOrBlank()) {
                        saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                        navController.navigate("main") {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                    else{
                        println(loginResponse.errorMessage)
                    }
                } else {
                    println(response.errorMessage)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
            onValueChange = {
                name = it
                isNameError = false
            },
            label = { Text("name") },
            isError = isNameError,
            modifier = Modifier.fillMaxWidth(0.7f),
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
            onValueChange = {
                surname = it
                isSurnameError = false
            },
            label = { Text("surname") },
            isError = isSurnameError,
            modifier = Modifier.fillMaxWidth(0.7f),
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
            width = 0.7f,
            isError = isBirthError
        )

        Spacer(modifier = Modifier.height(16.dp))

        PickGender(
            selectedGender = gender,
            onGenderSelected = {
                gender = it
                isGenderError = false
            },
            isError = isGenderError
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                isEmailError = false
            },
            label = { Text("email") },
            isError = isEmailError,
            modifier = Modifier.fillMaxWidth(0.7f),
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
            value = password,
            onValueChange = {
                password = it
                isPasswordError = false
                isRepeatPasswordError = false
            },
            label = { Text("password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            isError = isPasswordError,
            modifier = Modifier.fillMaxWidth(0.7f),
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
            value = repeatPassword,
            onValueChange = {
                repeatPassword = it
                isRepeatPasswordError = false
            },
            label = { Text("repeat password") },
            visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                    Icon(
                        imageVector = if (repeatPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            isError = isRepeatPasswordError,
            modifier = Modifier.fillMaxWidth(0.7f),
            singleLine = true,
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

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { validateAndRegister() },
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
    onGenderSelected: (String) -> Unit,
    isError: Boolean = false
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
            isError = isError,
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