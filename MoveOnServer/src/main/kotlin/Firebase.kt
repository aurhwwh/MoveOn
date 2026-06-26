package MoveOn

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification

var firebaseEnabled = false

fun initFirebase() {
    try {
        val serviceAccount =
            Thread.currentThread().contextClassLoader
                .getResourceAsStream("moveon-1eb54-firebase-adminsdk-fbsvc-f382e4188e.json")

        if (serviceAccount == null) {
            firebaseEnabled = false
            return
        }

        val options = FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(serviceAccount)
            )
            .build()

        FirebaseApp.initializeApp(options)

        firebaseEnabled = true

    } catch (e: Exception) {
        firebaseEnabled = false
    }
}

fun sendPushNotification(deviceToken: String, title: String, body: String) {
    if (!firebaseEnabled) return
    if (deviceToken.isBlank()) return
    try {
        val message = Message.builder()
            .setToken(deviceToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .build()
        val response = FirebaseMessaging.getInstance().send(message)
        println("FCM sent: $response")
    } catch (e: Exception) {
        System.err.println("FCM error: ${e.message}")
    }
}