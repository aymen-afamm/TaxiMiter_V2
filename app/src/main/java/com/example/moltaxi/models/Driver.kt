package com.example.moltaxi.models


data class Driver(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val licenseType: String,
    val licenseNumber: String,
    val phoneNumber: String,
    val vehicleModel: String,
    val vehiclePlate: String,
    val photoUrl: String? = null
) {
    fun getFullName(): String = "$firstName $lastName".trim()

    fun getQRCodeData(): String {
        return """
            Chauffeur: ${getFullName()}
            Âge: $age ans
            Type de Permis: $licenseType
            N° Permis: $licenseNumber
            Téléphone: $phoneNumber
            Véhicule: $vehicleModel
            Plaque: $vehiclePlate
        """.trimIndent()
    }
}