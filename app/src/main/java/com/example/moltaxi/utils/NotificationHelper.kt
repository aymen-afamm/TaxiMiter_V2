package com.example.moltaxi.utils


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moltaxi.R


class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "taxi_meter_channel"
        private const val CHANNEL_NAME = "Taxi Meter Notifications"
        private const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Notifications pour les courses de taxi"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTripEndNotification(fare: Double, distanceKm: Float, timeMinutes: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_taxi)
            .setContentTitle("Course terminée")
            .setContentText("Tarif total: ${String.format("%.2f", fare)} DH")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Course terminée avec succès!\n\n" +
                                "💰 Tarif total: ${String.format("%.2f", fare)} DH\n" +
                                "📍 Distance: ${String.format("%.2f", distanceKm)} km\n" +
                                "⏱️ Temps: $timeMinutes minutes"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(context.getColor(R.color.taxi_yellow))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    fun sendTripStartNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_taxi)
            .setContentTitle("Course en cours")
            .setContentText("Le compteur est actif")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}