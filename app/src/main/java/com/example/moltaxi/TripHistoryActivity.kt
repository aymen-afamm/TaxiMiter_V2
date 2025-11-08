package com.example.moltaxi


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moltaxi.adapters.TripHistoryAdapter
import com.example.moltaxi.models.TripHistory
import kotlinx.android.synthetic.main.activity_trip_history.*

class TripHistoryActivity : AppCompatActivity() {

    private lateinit var adapter: TripHistoryAdapter
    private val trips = mutableListOf<TripHistory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_history)

        setupRecyclerView()
        loadTripHistory()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = TripHistoryAdapter(trips)
        rvTripHistory.layoutManager = LinearLayoutManager(this)
        rvTripHistory.adapter = adapter
    }

    private fun loadTripHistory() {
        // Load from SharedPreferences or Database
        // For demo, adding sample data
        trips.add(TripHistory(
            id = 1,
            date = System.currentTimeMillis(),
            distance = 12.5f,
            duration = 25,
            fare = 35.5
        ))
        trips.add(TripHistory(
            id = 2,
            date = System.currentTimeMillis() - 86400000,
            distance = 8.3f,
            duration = 18,
            fare = 24.5
        ))
        adapter.notifyDataSetChanged()

        updateStatistics()
    }

    private fun updateStatistics() {
        val totalTrips = trips.size
        val totalDistance = trips.sumOf { it.distance.toDouble() }
        val totalEarnings = trips.sumOf { it.fare }

        tvTotalTrips.text = totalTrips.toString()
        tvTotalDistance.text = String.format("%.1f km", totalDistance)
        tvTotalEarnings.text = String.format("%.2f DH", totalEarnings)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnClearHistory.setOnClickListener {
            trips.clear()
            adapter.notifyDataSetChanged()
            updateStatistics()
        }
    }
}
