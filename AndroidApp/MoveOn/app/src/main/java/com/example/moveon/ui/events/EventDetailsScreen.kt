package com.example.moveon.ui.events

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveon.client.jsonClasses.Person
import com.example.moveon.ui.common.MoveOnTopBar
import com.example.moveon.ui.profile.DrawStars
import com.example.moveon.ui.theme.MGreen
import com.example.moveon.utils.UserAvatar
import com.example.moveon.viewModel.EventDetailsViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import androidx.core.net.toUri
import com.example.moveon.ui.theme.DLightGreen
import kotlin.time.Clock

@OptIn(ExperimentalTime::class)
@Composable
fun EventDetails(
    navController: NavController,
    eventId: Int,
    viewModel: EventDetailsViewModel = viewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    when {
        viewModel.isLoadingEvent -> Text("Загрузка")
        viewModel.error != null -> Text("${viewModel.error}")
        else -> {
            val data = viewModel.event ?: run {
                navController.popBackStack()
                return
            }
            val context = LocalContext.current

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                    MoveOnTopBar(navController, previousRoute ?: "main")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            color = DLightGreen
                        ) {
                            Text(
                                text = data.sportType!!,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0F6E56),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = data.title!!,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 28.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    val localDateTime =
                        data.dateTime!!.toLocalDateTime(TimeZone.currentSystemDefault())

                    val date = remember(localDateTime) {
                        java.time.format.DateTimeFormatter.ofPattern("d MMMM")
                            .format(localDateTime.toJavaLocalDateTime())
                    }

                    val time = remember(localDateTime) {
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                            .format(localDateTime.toJavaLocalDateTime())
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                        MetaItem(icon = Icons.Default.CalendarToday, text = date)
                        MetaItem(icon = Icons.Default.AccessTime, text = time)
                        MetaItem(
                            icon = Icons.Default.Group,
                            text = "${data.currentAmountOfPeople}/${data.maxAmountOfPeople}"
                        )
                    }

                    HorizontalDivider(color = Color(0xFF0F6E56))

                    SectionBlock(label = "Описание") {
                        Text(
                            text = data.description ?: "",
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFF555555)
                        )
                    }

                    SectionBlock(label = "Место") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = MGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = data.place ?: "",
                                modifier = Modifier.weight(1f),
                                fontSize = 18.sp,
                                color = Color(0xFF555555)
                            )
                            OutlinedButton(
                                onClick = {
                                    val uri = "geo:${data.lat},${data.lon}?q=${data.lat},${data.lon}".toUri()
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                },
                                shape = CircleShape,
                                border = BorderStroke(1.5.dp, MGreen),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MGreen),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Открыть в...", fontSize = 14.sp)
                            }
                        }

                        if (!data.route.isNullOrEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { navController.navigate("map/$eventId") },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DLightGreen,
                                    contentColor = Color(0xFF0F6E56)
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Открыть маршрут на карте", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFF0F6E56))
                        Spacer(Modifier.height(10.dp))

                        SectionBlock(label = "Организатор") {
                            data.participants?.firstOrNull()?.let { organizer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { navController.navigate("profile/${organizer.id}") }
                                        .background(Color(0xFFF7FFF8)),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    UserAvatar(
                                        photoId = organizer.photoId,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${organizer.name} ${organizer.surname}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        DrawStars(organizer.rating ?: 0.0)
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = Color.LightGray
                                    )
                                }
                            }
                        }

                        var expanded by remember { mutableStateOf(true) }
                        SectionBlock(label = "Участники") {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable{ expanded = !expanded},
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Group, contentDescription = null, tint = MGreen, modifier = Modifier.size(17.dp))

                                    Text(
                                        text = "${data.currentAmountOfPeople}/${data.maxAmountOfPeople}",
                                        fontSize = 15.sp
                                    )
                                }
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            val progress = (data.currentAmountOfPeople?.toFloat() ?: 0f) / (data.maxAmountOfPeople?.toFloat() ?: 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MGreen,
                                trackColor = DLightGreen
                            )

                            AnimatedVisibility(
                                visible = expanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    data.participants?.drop(0)?.forEach { participant ->
                                        Participant(
                                            participant = participant,
                                            onClick = { navController.navigate("profile/${participant.id}") }
                                        )
                                    }
                                }
                            }
                        }

                        if (data.isUserParticipant == true) {
                            SectionBlock(label = "Чат") {
                                if (viewModel.isLoadingMessages) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    ) {
                                        items(viewModel.messages) { message ->
                                            MessageItem(message)
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                var text by remember { mutableStateOf("") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = text,
                                        onValueChange = { text = it },
                                        modifier = Modifier.weight(1f),
                                        placeholder = { Text("Написать сообщение...") },
                                        enabled = !viewModel.isSendingMessage
                                    )
                                    IconButton(
                                        onClick = {
                                            val cleanedText = text.trim()
                                            if (cleanedText.isNotEmpty()) {
                                                viewModel.sendMessage(eventId, text)
                                                text = ""
                                            }
                                        },
                                        enabled = !viewModel.isSendingMessage
                                    ) {
                                        if (viewModel.isSendingMessage) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        } else {
                                            Icon(Icons.Default.Send, contentDescription = "Отправить", tint = MGreen)
                                        }
                                    }
                                }
                            }
                        }
                        //Spacer(Modifier.height(80.dp))
                    }


                    if (data.isUserParticipant != true) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(18.dp),
                            onClick = { viewModel.joinEvent(eventId) },
                            enabled = !viewModel.isJoining,
                            colors = ButtonDefaults.buttonColors(containerColor = MGreen)
                        ) {
                            Text(
                                fontSize = 25.sp,
                                text = if (viewModel.isJoining) "Присоединение..." else "Присоединиться"
                            )
                        }
                    }

                    if(data.dateTime <= Clock.System.now()
                        && data.isUserParticipant == true
                        && !data.isEventRatedByUser
                        && !viewModel.isRating
                    ) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(18.dp),
                            onClick = { viewModel.openRating() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MGreen
                            )
                        ) {
                            Text(
                                fontSize = 25.sp,
                                text = "Оценить участников"
                            )
                        }
                    }

                    if (viewModel.showRating && !viewModel.isRating) {
                        Dialog(onDismissRequest = { viewModel.closeRating() }) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                tonalElevation = 8.dp
                            ) {
                                RatingSheet(
                                    participants = data.participants ?: emptyList(),
                                    currentUserId = data.userId!!,
                                    isRating = viewModel.isRating,
                                    onSubmit = {ratings ->
                                        viewModel.rateEvent(
                                            eventId = eventId,
                                            ratings = ratings
                                        )
                                    }
                                )
                            }
                        }
                    }
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
}

@Composable
fun Participant(participant: Person, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp).clickable { onClick() }) {
        UserAvatar(
            photoId = participant.photoId,
            modifier = Modifier
                .padding(5.dp)
                .size(50.dp)
                .clip(CircleShape)
        )

        Column(modifier = Modifier.padding(start = 10.dp, top = 8.dp)) {
            Text(
                text = participant.name + " " + participant.surname,
                fontSize = 18.sp,
                modifier = Modifier.padding(2.dp)
            )

            DrawStars(participant.rating ?: 0.0)
        }
    }
}

@Composable
fun MessageItem(message: com.example.moveon.client.jsonClasses.EventMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row {
            Text(
                text = "${message.userName ?: "User"} ${message.userSurname ?: ""}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = " • ${message.createdAt.take(16)}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            text = message.message,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
fun MetaItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MGreen, modifier = Modifier.size(18.dp))
        Text(text, fontSize = 17.sp, color = Color.Gray)
    }
}


@Composable
fun SectionBlock(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.06.em,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        content()
    }
    HorizontalDivider(color = Color(0xFF0F6E56))
}