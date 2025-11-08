package com.example.moltaxi

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.moltaxi.utils.LocationHelper
import com.example.moltaxi.utils.NotificationHelper
import com.example.moltaxi.utils.TarifCalculator
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_meter.*
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.roundToInt

class MeterActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    // Location variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null

    // Trip tracking variables
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var totalDistance: Float = 0f
    private var currentFare: Double = 0.0

    // Handlers and Runnables for timer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

    // Tarif parameters
    private val tarifCalculator = TarifCalculator(
        baseFare = 2.5,
        farePerKm = 1.5,
        farePerMinute = 0.5
    )

    // Helpers
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var locationHelper: LocationHelper

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val UPDATE_INTERVAL = 3000L // 3 seconds
        private const val FASTEST_INTERVAL = 1000L // 1 second
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meter)

        initializeComponents()
        setupClickListeners()
        checkLocationPermissions()
        setupAnimations()
    }

    private fun initializeComponents() {
        // Initialize helpers
        notificationHelper = NotificationHelper(this)
        locationHelper = LocationHelper()

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize displays
        segmentTotal.setValue("0.00")
        segmentTime.setValue("00:00")
        segmentDistance.setValue("0.00")

        // Timer runnable
        timerRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    elapsedTime = System.currentTimeMillis() - startTime
                    updateTimeDisplay()
                    updateFare()
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }

        // Setup location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnStart.setOnClickListener {
            if (!isRunning) {
                startTrip()
            }
        }

        btnReset.setOnClickListener {
            resetTrip()
        }

        btnStop.setOnClickListener {
            stopTrip()
        }

        // Bottom navigation
        navHome.setOnClickListener {
            finish()
        }

        navMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupAnimations() {
        // Pulse animation for taxi logo
        val scaleX = ObjectAnimator.ofFloat(taxiAnimation, "scaleX", 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(taxiAnimation, "scaleY", 1f, 1.1f, 1f)
        scaleX.duration = 2000
        scaleY.duration = 2000
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()
        scaleX.start()
        scaleY.start()

        // Fade in animation for card
        cardDisplay.alpha = 0f
        cardDisplay.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()
    }

    private fun checkLocationPermissions() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Permissions granted
            initializeLocation()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Cette application nécessite l'accès à votre position pour fonctionner.",
                LOCATION_PERMISSION_REQUEST_CODE,
                *perms
            )
        }
    }

    private fun initializeLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    lastLocation = it
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erreur d'accès à la localisation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTrip() {
        if (!isRunning) {
            isRunning = true
            startTime = System.currentTimeMillis()
            totalDistance = 0f
            currentFare = tarifCalculator.baseFare

            // Update UI
            btnStart.visibility = View.GONE
            btnStop.visibility = View.VISIBLE
            segmentTotal.setSegmentColor(ContextCompat.getColor(this, R.color.green))

            // Start timer
            handler.post(timerRunnable)

            // Start location updates
            startLocationUpdates()

            // Animate start
            animateButton(btnStart)

            Toast.makeText(this, "Course démarrée", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopTrip() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(timerRunnable)
            stopLocationUpdates()

            // Update UI
            btnStart.visibility = View.VISIBLE
            btnStop.visibility = View.GONE
            segmentTotal.setSegmentColor(ContextCompat.getColor(this, R.color.segment_red))

            // Send notification
            val minutes = (elapsedTime / 60000).toInt()
            notificationHelper.sendTripEndNotification(
                currentFare,
                totalDistance,
                minutes
            )

            Toast.makeText(this, "Course terminée - ${String.format("%.2f", currentFare)} DH", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetTrip() {
        stopTrip()

        // Reset all values
        elapsedTime = 0
        totalDistance = 0f
        currentFare = 0.0

        // Update displays with animation
        segmentTotal.setValue("0.00")
        segmentTime.setValue("00:00")
        segmentDistance.setValue("0.00")

        // Animate reset
        cardDisplay.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(150)
            .withEndAction {
                cardDisplay.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.create().apply {
                interval = UPDATE_INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erreur lors du suivi de la position", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun onLocationUpdate(location: Location) {
        if (isRunning && lastLocation != null) {
            val distance = lastLocation!!.distanceTo(location)

            // Only count significant movements (> 5 meters)
            if (distance > 5) {
                totalDistance += distance / 1000f // Convert to km
                updateDistanceDisplay()
                updateFare()
            }
        }
        lastLocation = location
    }

    private fun updateTimeDisplay() {
        val minutes = (elapsedTime / 60000).toInt()
        val seconds = ((elapsedTime % 60000) / 1000).toInt()
        segmentTime.setValue(String.format("%02d:%02d", minutes, seconds))
    }

    private fun updateDistanceDisplay() {
        segmentDistance.setValue(String.format("%.2f", totalDistance))
    }

    private fun updateFare() {
        val minutes = (elapsedTime / 60000.0)
        currentFare = tarifCalculator.calculateFare(totalDistance.toDouble(), minutes)
        segmentTotal.setValue(String.format("%.2f", currentFare))
    }

    private fun animateButton(button: View) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initializeLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(
            this,
            "L'application nécessite l'accès à la localisation pour fonctionner",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        handler.removeCallbacks(timerRunnable)
    }
}