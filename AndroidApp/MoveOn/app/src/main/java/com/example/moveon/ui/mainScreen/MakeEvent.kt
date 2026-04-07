package com.example.moveon.ui.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveon.R
import com.example.moveon.client.jsonClasses.EventData
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
fun MakeEvent(data: EventData, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(4.dp).padding(top = 15.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(modifier = Modifier.clickable{ onClick() }) {
            Row() {
                Image(painter = painterResource(/*id = data.photoId ?: R.drawable.img*/R.drawable.img),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(5.dp).size(68.dp).clip(CircleShape))

                Column(modifier = Modifier.padding(start = 10.dp).padding(top = 8.dp)) {
                    Text(text = data.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text(text = data.sportType, fontStyle = FontStyle.Italic, fontSize = 20.sp)
                    Text(text = data.description, fontSize = 18.sp)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val localDateTime = data.dateTime.toLocalDateTime(TimeZone.currentSystemDefault())
                        val formattedDate = remember(localDateTime) {
                            java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(
                                localDateTime.toJavaLocalDateTime()
                            )
                        }
                        Text(text = formattedDate, fontSize = 15.sp)
                        Text(text = "${data.currentAmountOfPeople}/${data.maxAmountOfPeople}", fontSize = 15.sp)
                    }
                }
            }

        }
    }
}