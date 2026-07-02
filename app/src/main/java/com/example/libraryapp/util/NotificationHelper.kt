package com.example.libraryapp.util

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(val context: Context) {
    private val CHANNEL_ID = "library_notifications"
    private val NOTIFICATION_ID = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Library Updates"
            val descriptionText = "Ειδοποιήσεις για νέες καταχωρήσεις"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendBookAddedNotification(bookTitle: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info) // Μπορείς να βάλεις το δικό σου εικονίδιο
            .setContentTitle("Επιτυχής Καταχώρηση")
            .setContentText("Το βιβλίο '$bookTitle' προστέθηκε στη βιβλιοθήκη σας.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // Προσοχή: Για Android 13+ χρειάζεται άδεια POST_NOTIFICATIONS
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}