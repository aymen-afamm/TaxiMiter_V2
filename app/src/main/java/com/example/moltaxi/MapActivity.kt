package com.example.moltaxi


import android.Manifest
import android.animation.ValueAnimator
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*
import pub.devrel.easypermissions.EasyPermissions

class MapActivity : AppCompatActivity(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentMarker: Marker? = null
    private var pathPolyline: Polyline? = null
    private val pathPoints = mutableListOf<LatLng>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
        private const val DEFAULT_ZOOM = 17f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        setupMap()
        setupClickListeners()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateMapLocation(location)
                }
            }
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        fabMyLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Map styling
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
        }

        // Set map style (dark mode)
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Toast.makeText(this, "Style parsing failed.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (EasyPermissions.hasPermissions(this, *perms)) {
            enableMyLocation()
            startLocationUpdates()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "L'accès à la localisation est nécessaire pour afficher votre position.",
                LOCATION_PERMISSION_REQUEST_CODE,
                *perms
            )
        }
    }

    private fun enableMyLocation() {
        try {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erreur d'accès à la localisation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)
                    )
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erreur lors de la récupération de la position", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.create().apply {
                interval = 3000L
                fastestInterval = 1000L
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erreur lors du suivi de la position", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMapLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)

        // Add to path
        pathPoints.add(latLng)

        // Update or create marker
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Ma position")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi_marker))
            )
        } else {
            animateMarker(currentMarker!!, latLng)
        }

        // Update polyline
        pathPolyline?.remove()
        pathPolyline = mMap.addPolyline(
            PolylineOptions()
                .addAll(pathPoints)
                .width(10f)
                .color(ContextCompat.getColor(this, R.color.taxi_yellow))
                .geodesic(true)
        )

        // Update distance display
        if (pathPoints.size > 1) {
            val totalDistance = calculateTotalDistance()
            tvDistance.text = String.format("%.2f km", totalDistance)
        }
    }

    private fun animateMarker(marker: Marker, toPosition: LatLng) {
        val startPosition = marker.position
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000

        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            val lat = startPosition.latitude + (toPosition.latitude - startPosition.latitude) * fraction
            val lng = startPosition.longitude + (toPosition.longitude - startPosition.longitude) * fraction
            marker.position = LatLng(lat, lng)
        }

        valueAnimator.start()
    }

    private fun calculateTotalDistance(): Float {
        var distance = 0f
        for (i in 0 until pathPoints.size - 1) {
            val results = FloatArray(1)
            Location.distanceBetween(
                pathPoints[i].latitude,
                pathPoints[i].longitude,
                pathPoints[i + 1].latitude,
                pathPoints[i + 1].longitude,
                results
            )
            distance += results[0]
        }
        return distance / 1000f // Convert to km
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
        enableMyLocation()
        startLocationUpdates()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
