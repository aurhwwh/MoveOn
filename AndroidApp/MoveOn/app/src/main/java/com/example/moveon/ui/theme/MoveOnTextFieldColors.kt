package com.example.moveon.ui.theme

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable


@Composable
fun moveOnTextFieldColor() = TextFieldDefaults.colors(
    focusedIndicatorColor = MLabelColor.copy(alpha = 0.6f),
    unfocusedIndicatorColor = MOutlineColor,

    focusedLabelColor = MLabelColor,

    cursorColor = MLabelColor.copy(0.6f),
)