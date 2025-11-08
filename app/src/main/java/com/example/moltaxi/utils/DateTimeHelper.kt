package com.example.moltaxi.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeHelper {

    companion object {
        fun formatTime(milliseconds: Long): String {
            val seconds = (milliseconds / 1000).toInt()
            val minutes = seconds / 60
            val secs = seconds % 60
            return String.format("%02d:%02d", minutes, secs)
        }

        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        fun formatDateOnly(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        fun formatTimeOnly(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        fun getCurrentTimestamp(): Long {
            return System.currentTimeMillis()
        }
    }
}