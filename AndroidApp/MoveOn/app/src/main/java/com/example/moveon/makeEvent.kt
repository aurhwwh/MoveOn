package com.example.moveon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MakeEvent(data: EventData) {
    Card(modifier = Modifier.fillMaxWidth().padding(4.dp).padding(top = 15.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box() {
            Row() {
                Image(painter = painterResource(id = data.imageId),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(5.dp).size(68.dp).clip(CircleShape))

                Column(modifier = Modifier.padding(start = 10.dp).padding(top = 8.dp)) {
                    Text(text = data.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = data.description, fontSize = 15.sp)
                }
            }

        }
    }
}