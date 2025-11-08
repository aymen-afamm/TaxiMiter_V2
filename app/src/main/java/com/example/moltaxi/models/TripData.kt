package com.example.moltaxi.models

data class TripData(
    var distanceKm: Float = 0f,
    var timeMinutes: Int = 0,
    var fare: Double = 0.0,
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var isActive: Boolean = false
) {
    fun getTripDuration(): Long {
        return if (endTime > startTime) {
            endTime - startTime
        } else {
            System.currentTimeMillis() - startTime
        }
    }

    fun reset() {
        distanceKm = 0f
        timeMinutes = 0
        fare = 0.0
        startTime = 0L
        endTime = 0L
        isActive = false
    }

    fun getTripSummary(): String {
        return """
            Distance: ${String.format("%.2f", distanceKm)} km
            Temps: $timeMinutes minutes
            Tarif: ${String.format("%.2f", fare)} DH
        """.trimIndent()
    }
}
