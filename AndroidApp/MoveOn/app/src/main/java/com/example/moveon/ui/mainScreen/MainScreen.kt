package com.example.moveon.ui.mainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moveon.R
import com.example.moveon.data.EventData
import com.example.moveon.ui.common.BottomBar
import com.example.moveon.ui.common.TopBar

@Composable
fun MainScreen(navController : NavController) {
    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(city = "Saint-Petersburg")

        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.padding(PaddingValues(8.dp))) {
                Row(modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {})
                    {
                        Text(
                            text = "Events ",
                            color = Color.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 28.sp,
                        )
                        Icon(
                            imageVector = Icons.Filled.FilterAlt,
                            contentDescription = "City",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {})
                    {
                        Text(text = "Search ",
                            color = Color.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 28.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "City",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(listOf(
                    EventData(R.drawable.img, "Football", "Let's play", "14.03 14:03", "4/10"),
                    EventData(R.drawable.img, "Football", "Let's play", "14.03 14:03", "4/10")
                )) {
                        _, data ->
                    MakeEvent(data = data)
                }
            }

            BottomBar(navController)
        }
    }
}