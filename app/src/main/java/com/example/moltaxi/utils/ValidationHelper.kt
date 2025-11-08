package com.example.moltaxi.utils


import android.util.Patterns

class ValidationHelper {

    companion object {
        fun isValidPhoneNumber(phone: String): Boolean {
            return phone.matches(Regex("^\\+?[0-9]{10,15}$"))
        }

        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidLicenseNumber(license: String): Boolean {
            return license.length >= 6 && license.matches(Regex("^[A-Z0-9]+$"))
        }

        fun isValidAmount(amount: String): Boolean {
            return try {
                val value = amount.toDouble()
                value >= 0
            } catch (e: Exception) {
                false
            }
        }
    }
}