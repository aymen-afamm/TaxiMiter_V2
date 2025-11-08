package com.example.moltaxi.utils


import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "TaxiMeterPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_BASE_FARE = "base_fare"
        private const val KEY_FARE_PER_KM = "fare_per_km"
        private const val KEY_FARE_PER_MINUTE = "fare_per_minute"
        private const val KEY_DRIVER_NAME = "driver_name"
        private const val KEY_TOTAL_TRIPS = "total_trips"
        private const val KEY_TOTAL_EARNINGS = "total_earnings"
    }

    fun saveBaseFare(fare: Double) {
        prefs.edit().putFloat(KEY_BASE_FARE, fare.toFloat()).apply()
    }

    fun getBaseFare(): Double {
        return prefs.getFloat(KEY_BASE_FARE, 2.5f).toDouble()
    }

    fun saveFarePerKm(fare: Double) {
        prefs.edit().putFloat(KEY_FARE_PER_KM, fare.toFloat()).apply()
    }

    fun getFarePerKm(): Double {
        return prefs.getFloat(KEY_FARE_PER_KM, 1.5f).toDouble()
    }

    fun saveFarePerMinute(fare: Double) {
        prefs.edit().putFloat(KEY_FARE_PER_MINUTE, fare.toFloat()).apply()
    }

    fun getFarePerMinute(): Double {
        return prefs.getFloat(KEY_FARE_PER_MINUTE, 0.5f).toDouble()
    }

    fun saveDriverName(name: String) {
        prefs.edit().putString(KEY_DRIVER_NAME, name).apply()
    }

    fun getDriverName(): String {
        return prefs.getString(KEY_DRIVER_NAME, "") ?: ""
    }

    fun incrementTotalTrips() {
        val current = getTotalTrips()
        prefs.edit().putInt(KEY_TOTAL_TRIPS, current + 1).apply()
    }

    fun getTotalTrips(): Int {
        return prefs.getInt(KEY_TOTAL_TRIPS, 0)
    }

    fun addToTotalEarnings(amount: Double) {
        val current = getTotalEarnings()
        prefs.edit().putFloat(KEY_TOTAL_EARNINGS, (current + amount).toFloat()).apply()
    }

    fun getTotalEarnings(): Double {
        return prefs.getFloat(KEY_TOTAL_EARNINGS, 0f).toDouble()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
