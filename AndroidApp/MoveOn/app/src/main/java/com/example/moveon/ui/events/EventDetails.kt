package com.example.moveon.ui.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveon.R
import com.example.moveon.client.jsonClasses.Person
import com.example.moveon.ui.common.MoveOnTopBar
import com.example.moveon.ui.profile.DrawStars
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.viewModel.EventDetailsViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun EventDetails(navController : NavController,
                 eventId: Int,
                 viewModel: EventDetailsViewModel = viewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(viewModel.joinSuccess) {
        if (viewModel.joinSuccess) {
            navController.navigate("main") {
                popUpTo("eventDetails/$eventId") { inclusive = true }
            }
        }
    }

    when {
        viewModel.isLoadingEvent -> Text("Loading")
        viewModel.error != null -> Text("${viewModel.error}")
        else -> {
            val data = viewModel.event ?: run {
                navController.popBackStack()
                return
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState(),)
                ) {
                    MoveOnTopBar(navController, "main")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.title!!,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(8.dp)
                    )


                    val localDateTime = data.dateTime!!.toLocalDateTime(TimeZone.currentSystemDefault())
                    val formattedDate = remember(localDateTime) {
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(
                            localDateTime.toJavaLocalDateTime()
                        )
                    }
                    Text(
                        text = formattedDate,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp),
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.sportType!!,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp).padding(8.dp))

                    Text(text = data.description ?: "", fontSize = 20.sp, modifier = Modifier.padding(8.dp))

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(text = "Организатор:", fontSize = 20.sp, modifier = Modifier.padding(8.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    Participant(
                        data.participants!![0],
                        onClick = { navController.navigate("profile/${data.participants[0].id}") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var expanded by remember { mutableStateOf(true) }

                    Row(modifier = Modifier.clickable {expanded = !expanded}.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Участники: ${data.currentAmountOfPeople}/${data.maxAmountOfPeople}", fontSize = 20.sp)

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            data.participants.drop(1).forEach { participant ->
                                Participant(
                                    participant = participant,
                                    onClick = { navController.navigate("profile/${participant.id}") }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                viewModel.error?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (data.isUserParticipant != true) {
                    Button(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
                        onClick = { viewModel.joinEvent(eventId) },
                        enabled = !viewModel.isJoining,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MGreen
                        )
                    ) {
                        Text(
                            fontSize = 25.sp,
                            text = if (viewModel.isJoining) "Joining..." else "Join"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Participant(participant: Person, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp).clickable{ onClick() }) {
        Image(painter = painterResource(/*id = data.photoId ?: R.drawable.img*/R.drawable.img),
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(5.dp).size(50.dp).clip(CircleShape))

        Column(modifier = Modifier.padding(start = 10.dp, top = 8.dp)) {
            Text(text = participant.name + " " + participant.surname,
                fontSize = 18.sp,
                modifier = Modifier.padding(2.dp))

            DrawStars(participant.rating ?: 0.0)
        }
    }
}