package com.example.moltaxi


import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.moltaxi.models.Driver
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var driver: Driver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        driver = Driver(
            firstName = "AYMEN",
            lastName = "",
            age = 35,
            licenseType = "Permis A",
            licenseNumber = "ABC123456",
            phoneNumber = "+212 6XX XXX XXX",
            vehicleModel = "Mercedes-Benz",
            vehiclePlate = "12345-أ-67"
        )

        setupUI()
        setupAnimations()
        generateQRCode()
    }

    private fun setupUI() {
        tvDriverName.text = driver.getFullName()
        tvDriverAge.text = "${driver.age} ans"
        tvLicenseType.text = driver.licenseType
        tvLicenseNumber.text = "N° ${driver.licenseNumber}"
        tvPhoneNumber.text = driver.phoneNumber
        tvVehicleInfo.text = "${driver.vehicleModel} - ${driver.vehiclePlate}"

        btnBack.setOnClickListener {
            finish()
        }

        btnModifier.setOnClickListener {
            // TODO: Implement profile editing
        }
    }

    private fun setupAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        cardProfile.startAnimation(fadeIn)

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideUp.startOffset = 200
        cardQRCode.startAnimation(slideUp)

        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
        imgProfile.startAnimation(pulse)
    }

    private fun generateQRCode() {
        try {
            val qrCodeData = driver.getQRCodeData()
            val size = 512
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(
                qrCodeData,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }

            imgQRCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}