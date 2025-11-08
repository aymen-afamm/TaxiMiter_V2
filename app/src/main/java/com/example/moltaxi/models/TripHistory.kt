package com.example.moltaxi.models


data class TripHistory(
    val id: Long,
    val date: Long,
    val distance: Float,
    val duration: Int,
    val fare: Double
) {
    fun getFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(date))
    }

    fun getFormattedDistance(): String {
        return String.format("%.2f km", distance)
    }

    fun getFormattedDuration(): String {
        return "$duration min"
    }

    fun getFormattedFare(): String {
        return String.format("%.2f DH", fare)
    }
}