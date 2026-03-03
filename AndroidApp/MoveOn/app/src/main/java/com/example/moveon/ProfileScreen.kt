package com.example.moveon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moveon.ui.theme.MGreen


@Composable
fun ProfileScreen(navController : NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(city = "Saint-Petersburg")

        Box(
            //modifier = Modifier.weight(0.6f)
        ) {
            MakeProfile(ProfileData(
                R.drawable.img,
                "Krosh",
                "01.01.2000",
                "Romashkovaya dolina",
                4.6,
                "In search of a Yozhik bla bla lba that's a description lalala there are some stats below")
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp).padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly)
        {
            Icon(
                imageVector = Icons.Filled.Accessibility,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
            Icon(
                imageVector = Icons.Filled.AddReaction,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
            Icon(
                imageVector = Icons.Filled.Build,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        Row(modifier = Modifier.weight(1f).padding(start = 12.dp, top = 24.dp).clickable{})
        {
            Text(text = "My Events",
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontSize = 22.sp
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "My Events",
                tint = Color.Black,
                modifier = Modifier.size(25.dp)
            )
        }

        BottomBar(navController)
    }
}