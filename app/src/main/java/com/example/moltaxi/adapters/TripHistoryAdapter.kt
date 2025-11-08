package com.example.moltaxi.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moltaxi.R
import com.example.moltaxi.models.TripHistory
import kotlinx.android.synthetic.main.item_trip_history.view.*

class TripHistoryAdapter(
    private val trips: List<TripHistory>
) : RecyclerView.Adapter<TripHistoryAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip_history, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount() = trips.size

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(trip: TripHistory) {
            itemView.tvTripDate.text = trip.getFormattedDate()
            itemView.tvTripDistance.text = trip.getFormattedDistance()
            itemView.tvTripDuration.text = trip.getFormattedDuration()
            itemView.tvTripFare.text = trip.getFormattedFare()
        }
    }
}