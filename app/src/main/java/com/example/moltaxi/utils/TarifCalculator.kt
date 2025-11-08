package com.example.moltaxi.utils


class TarifCalculator(
    val baseFare: Double = 2.5,
    val farePerKm: Double = 1.5,
    val farePerMinute: Double = 0.5
) {

    fun calculateFare(distanceKm: Double, timeMinutes: Double): Double {
        val distanceFare = distanceKm * farePerKm
        val timeFare = timeMinutes * farePerMinute
        return baseFare + distanceFare + timeFare
    }

    fun calculateDistanceFare(distanceKm: Double): Double {
        return distanceKm * farePerKm
    }

    fun calculateTimeFare(timeMinutes: Double): Double {
        return timeMinutes * farePerMinute
    }

    fun getFareBreakdown(distanceKm: Double, timeMinutes: Double): FareBreakdown {
        return FareBreakdown(
            baseFare = baseFare,
            distanceFare = calculateDistanceFare(distanceKm),
            timeFare = calculateTimeFare(timeMinutes),
            totalFare = calculateFare(distanceKm, timeMinutes)
        )
    }
}

data class FareBreakdown(
    val baseFare: Double,
    val distanceFare: Double,
    val timeFare: Double,
    val totalFare: Double
)