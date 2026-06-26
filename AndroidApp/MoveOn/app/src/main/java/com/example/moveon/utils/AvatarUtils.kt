package com.example.moveon.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.moveon.R

object AvatarUtils {
    fun getAvatarResId(photoId: Int?): Int {
        return when (photoId) {
            1 -> R.drawable.default_avatar
            2 -> R.drawable.avatar_2
            3 -> R.drawable.avatar_3
            4 -> R.drawable.avatar_4
            5 -> R.drawable.avatar_5
            6 -> R.drawable.avatar_6
            7 -> R.drawable.avatar_7
            8 -> R.drawable.avatar_8
            9 -> R.drawable.avatar_9
            10 -> R.drawable.avatar_10
            else -> R.drawable.default_avatar
        }
    }

    val avatarIds = (1..10).toList()
}

@Composable
fun UserAvatar(
    photoId: Int?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val resId = AvatarUtils.getAvatarResId(photoId)
    androidx.compose.foundation.Image(
        painter = painterResource(resId),
        contentDescription = "Avatar",
        modifier = modifier.clip(androidx.compose.foundation.shape.CircleShape),
        contentScale = contentScale
    )
}