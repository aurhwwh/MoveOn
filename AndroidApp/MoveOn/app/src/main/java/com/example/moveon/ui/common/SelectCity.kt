package com.example.moveon.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectCity() {
    val cities = listOf("Saint-Petersburg", "Moscow")

    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember {mutableStateOf(cities[0])}

    Box {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {expanded = true})
        {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "City",
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
            Text(text = selectedCity,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 22.sp
                //modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )

            DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}) {
                cities.forEach { city -> DropdownMenuItem(
                    text = {Text(text = city,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 22.sp) },
                    onClick = {
                        selectedCity = city
                        expanded = false}
                    )
                }
            }
        }
    }
}