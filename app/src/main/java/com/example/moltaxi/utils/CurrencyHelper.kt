package com.example.moltaxi.utils


import java.text.NumberFormat
import java.util.*

class CurrencyHelper {

    companion object {
        fun formatMoroccanDirham(amount: Double): String {
            return String.format("%.2f DH", amount)
        }

        fun formatCurrency(amount: Double, currencyCode: String = "MAD"): String {
            val format = NumberFormat.getCurrencyInstance(Locale("ar", "MA"))
            format.currency = Currency.getInstance(currencyCode)
            return format.format(amount)
        }

        fun parseCurrency(formattedAmount: String): Double? {
            return try {
                formattedAmount.replace("[^0-9.]".toRegex(), "").toDouble()
            } catch (e: Exception) {
                null
            }
        }
    }
}