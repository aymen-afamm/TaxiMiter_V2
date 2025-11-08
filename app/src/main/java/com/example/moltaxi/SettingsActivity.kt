package com.example.moltaxi


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moltaxi.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefsHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefsHelper = PreferencesHelper(this)

        loadSettings()
        setupClickListeners()
    }

    private fun loadSettings() {
        etBaseFare.setText(prefsHelper.getBaseFare().toString())
        etFarePerKm.setText(prefsHelper.getFarePerKm().toString())
        etFarePerMinute.setText(prefsHelper.getFarePerMinute().toString())
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        btnResetDefaults.setOnClickListener {
            resetToDefaults()
        }
    }

    private fun saveSettings() {
        try {
            val baseFare = etBaseFare.text.toString().toDoubleOrNull()
            val farePerKm = etFarePerKm.text.toString().toDoubleOrNull()
            val farePerMinute = etFarePerMinute.text.toString().toDoubleOrNull()

            if (baseFare == null || farePerKm == null || farePerMinute == null) {
                Toast.makeText(this, "Veuillez entrer des valeurs valides", Toast.LENGTH_SHORT).show()
                return
            }

            if (baseFare < 0 || farePerKm < 0 || farePerMinute < 0) {
                Toast.makeText(this, "Les valeurs ne peuvent pas être négatives", Toast.LENGTH_SHORT).show()
                return
            }

            prefsHelper.saveBaseFare(baseFare)
            prefsHelper.saveFarePerKm(farePerKm)
            prefsHelper.saveFarePerMinute(farePerMinute)

            Toast.makeText(this, "Paramètres sauvegardés", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetToDefaults() {
        etBaseFare.setText("2.5")
        etFarePerKm.setText("1.5")
        etFarePerMinute.setText("0.5")
        Toast.makeText(this, "Valeurs par défaut restaurées", Toast.LENGTH_SHORT).show()
    }
}
