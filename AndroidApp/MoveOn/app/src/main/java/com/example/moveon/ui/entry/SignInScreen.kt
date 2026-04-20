package com.example.moveon.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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
            value = email,
            onValueChange = {
                email = it
                isEmailError = false
            },
            label = { Text("email") },
            isError = isEmailError,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordError = false
            },
            label = { Text("password") },
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
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { validateAndLogin() },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 20.sp, text = "Sign in")
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Do not have an account?", fontSize = 20.sp, fontStyle = FontStyle.Italic)

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { navController.navigate("register") },
            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
        ) {
            Text(fontSize = 15.sp, text = "Sign up")
        }
    }
}