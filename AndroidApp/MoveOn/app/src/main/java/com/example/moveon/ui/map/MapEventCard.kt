package com.example.moveon.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moveon.client.jsonClasses.EventsMarker
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
fun MapEventCard(
    navController : NavController,
    event : EventsMarker,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = event.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = event.sportType,
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val localDateTime = event.dateTime.toLocalDateTime(TimeZone.currentSystemDefault())
                val formattedDate = remember(localDateTime) {
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(
                        localDateTime.toJavaLocalDateTime()
                    )
                }
                Text(text = formattedDate, fontSize = 15.sp)
                Text(text = "${event.currentAmountOfPeople}/${event.maxAmountOfPeople}", fontSize = 15.sp)
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row {

                Button(
                    onClick = {
                        navController.navigate("eventDetails/${event.eventId}")
                    }
                ) {
                    Text("Info")
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text("Close")
                }
            }
        }
    }
}