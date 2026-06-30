package com.example.moveon.ui.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveon.R
import com.example.moveon.client.jsonClasses.Person
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.utils.UserAvatar


@Composable
fun RatingStars(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
) {
    Row {
        for (star in 1..5) {

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (star <= rating)
                    Color(0xFFFFC107)
                else
                    Color.Gray,
                modifier = Modifier.size(32.dp).clickable { onRatingSelected(star) }
            )
        }
    }
}


@Composable
fun RatingSheet(
    participants: List<Person>,
    currentUserId: Int,
    isRating: Boolean,
    onSubmit: (Map<Int, Int>) -> Unit
) {
    val ratings = remember{ mutableStateMapOf<Int, Int>() }
    val participantsToRate = remember(participants, currentUserId) {
        participants.filter { it.id != currentUserId }
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {

        Text(
            text = "Оценить участников",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {

            items (items = participantsToRate, key = {it.id}) { participant ->
                val rating = ratings[participant.id] ?: 0

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).heightIn(min = 80.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    UserAvatar(
                        photoId = participant.photoId,
                        modifier = Modifier.size(50.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f))
                    {
                        Text(text = "${participant.name} ${participant.surname}", fontSize = 17.sp)

                        RatingStars(
                            rating = rating,
                            onRatingSelected = {selectedRating ->
                                ratings[participant.id] =
                                    if (rating == selectedRating) {0}
                                    else {selectedRating}
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MGreen),
            onClick = { onSubmit(ratings) },
            enabled = ratings.values.any{ it > 0} && !isRating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isRating) "Отправка..."
                else "Отправить"
            )
        }
    }
}