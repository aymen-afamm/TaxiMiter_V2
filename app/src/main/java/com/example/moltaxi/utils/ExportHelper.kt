package com.example.moltaxi.utils


import android.content.Context
import android.os.Environment
import com.example.moltaxi.models.TripHistory
import java.io.File
import java.io.FileWriter

class ExportHelper(private val context: Context) {

    fun exportToCSV(trips: List<TripHistory>): File? {
        try {
            val fileName = "taxi_trips_${System.currentTimeMillis()}.csv"
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                fileName
            )

            FileWriter(file).use { writer ->
                // Header
                writer.append("Date,Distance (km),Duration (min),Fare (DH)\n")

                // Data
                trips.forEach { trip ->
                    writer.append("${trip.getFormattedDate()},")
                    writer.append("${trip.distance},")
                    writer.append("${trip.duration},")
                    writer.append("${trip.fare}\n")
                }
            }

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun shareTripReceipt(trip: TripHistory): String {
        return """
            🚖 REÇU DE COURSE TAXI
            ━━━━━━━━━━━━━━━━━━━━━━
            
            Date: ${trip.getFormattedDate()}
            
            Distance parcourue: ${trip.getFormattedDistance()}
            Durée du trajet: ${trip.getFormattedDuration()}
            
            ━━━━━━━━━━━━━━━━━━━━━━
            TOTAL À PAYER: ${trip.getFormattedFare()}
            ━━━━━━━━━━━━━━━━━━━━━━
            
            Merci pour votre confiance! 🙏
        """.trimIndent()
    }
}
