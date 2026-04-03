package com.example.moveon.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveon.data.ProfileData
import java.time.format.DateTimeFormatter


@Composable
fun MakeProfile(data: ProfileData) {
    Box(modifier = Modifier.padding(8.dp)) {
        Column() {
            Row() {
                Image(painter = painterResource(id = data.photoId),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(5.dp).size(100.dp).clip(CircleShape))

                Column(modifier = Modifier.padding(start = 10.dp, top = 8.dp)) {
                    Text(text = data.name + " " + data.surname,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(2.dp))

                    val birth = data.birth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    Text(text = birth,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(2.dp))
                    Text(text = data.city,
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(2.dp)
                    )
                    DrawStars(data.rating)
                }
            }
            Box() {
                Text(text = data.description,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@Composable
fun DrawStars(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val full = rating.toInt()
        val progress = (rating - full).coerceIn(0.0, 1.0)

        for (i in 1 .. 5) {
            Box() {
                if (i <= full) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.Yellow,
                    )
                }

                if (i == full + 1 && progress != 0.0) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.clip(
                            GenericShape { size, _ ->
                                addRect(
                                    Rect(
                                        0f,
                                        0f,
                                        size.width * progress.toFloat(),
                                        size.height
                                    )
                                )
                            }
                        )
                    )
                }

                if (i > full + 1 || (i == full + 1 && progress == 0.0)) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}