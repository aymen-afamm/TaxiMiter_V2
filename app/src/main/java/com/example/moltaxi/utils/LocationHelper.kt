package com.example.moltaxi.utils


import android.location.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LocationHelper {

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360

        return bearing.toFloat()
    }

    fun isValidLocation(location: Location?): Boolean {
        return location != null &&
                location.latitude != 0.0 &&
                location.longitude != 0.0
    }

    fun formatCoordinates(lat: Double, lon: Double): String {
        return String.format("%.6f, %.6f", lat, lon)
    }

    fun getSpeedInKmh(location: Location): Float {
        return if (location.hasSpeed()) {
            location.speed * 3.6f // Convert m/s to km/h
        } else {
            0f
        }
    }
}