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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveon.ui.theme.MGreen



@Preview(name = "Sign In Screen")
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}


@Composable
fun SignInScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = MGreen
            )
        ) {
            Text(fontSize = 20.sp, text = "Sign in")
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Do not have an account?", fontSize = 20.sp, fontStyle = FontStyle.Italic)

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = MGreen
            )
        ) {
            Text(fontSize = 15.sp, text = "Sign up")
        }
    }
}


