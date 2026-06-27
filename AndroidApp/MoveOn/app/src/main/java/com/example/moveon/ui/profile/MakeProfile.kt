package com.example.moveon.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveon.R
import com.example.moveon.data.ProfileData
import com.example.moveon.ui.theme.DLightGreen
import com.example.moveon.ui.theme.LightGreen
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.utils.UserAvatar   // добавлен импорт
import kotlinx.datetime.toJavaLocalDate

@Composable
fun MakeProfile(data: ProfileData) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        shape = RoundedCornerShape(16.dp),

    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(
                    photoId = data.photoId,
                    modifier = Modifier.padding(5.dp).size(100.dp).clip(CircleShape)
                )

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = data.name + " " + data.surname,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )

                    val birth = data.birth.toJavaLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    Text(text = birth,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(2.dp)
                    )

                    Spacer(Modifier.size(4.dp))

                    DrawStars(data.rating)
                }
            }

            if (data.description.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MGreen.copy(alpha = 0.3f)
                )
                Text(
                    text = data.description,
                    fontSize = 18.sp,
                    color = Color(0xFF333333)
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
                        tint = Color(0xFFFFC107)
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