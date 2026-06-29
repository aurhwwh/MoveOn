package com.example.moveon.ui.entry

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.LoginRequest
import com.example.moveon.data.TokenStorage.saveTokens
import com.example.moveon.ui.theme.MGreen
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Icon
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.example.moveon.client.jsonClasses.StoreFcmTokenRequest
import com.example.moveon.ui.theme.moveOnTextFieldColor
import com.google.firebase.messaging.FirebaseMessaging


private fun isValidEmail(email: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    return regex.matches(email)
}

private fun isValidPassword(password: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9]+$")
    return password.isNotBlank() && regex.matches(password)
}

@Preview(name = "Sign In Screen")
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}

@Composable
fun SignInScreen(navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun validateAndLogin() {
        val emailValid = isValidEmail(email)
        val passwordValid = isValidPassword(password)

        isEmailError = !emailValid
        isPasswordError = !passwordValid

        if (emailValid && passwordValid) {
            val request = LoginRequest(email = email, password = password)
            scope.launch {
                val response = Handlers.entryHandler.login(request)
                if (response.success && !response.accessToken.isNullOrBlank() && !response.refreshToken.isNullOrBlank()) {
                    saveTokens(response.accessToken, response.refreshToken)

                    val prefs = context.getSharedPreferences("fcm", Context.MODE_PRIVATE)
                    val token = prefs.getString("token", null)
                    if (!token.isNullOrEmpty()) {
                        val fcmRequest = StoreFcmTokenRequest(token)
                        scope.launch {
                            val res = Handlers.entryHandler.storeFcmToken(
                                StoreFcmTokenRequest(token)
                            )
                            if (!res.success){
                                println("FCM_TOKEN "+res.errorMessage)
                            }
                        }

                    }
                    navController.navigate("main") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
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
            .windowInsetsPadding(WindowInsets.statusBars)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Добро пожаловать!",
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.SansSerif,
            fontSize = 35.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                isEmailError = false
            },
            label = { Text("Электронная почта") },
            colors = moveOnTextFieldColor(),
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
            },
            label = { Text("Пароль") },
            colors = moveOnTextFieldColor(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                    )
                }
            },
            isError = isPasswordError,
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
            onClick = { validateAndLogin() },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 20.sp, text = "Войти")
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Ещё нет аккаунта?", fontSize = 20.sp, fontStyle = FontStyle.Italic)

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { navController.navigate("register") },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 15.sp, text = "Зарегистрироваться")
        }
    }
}