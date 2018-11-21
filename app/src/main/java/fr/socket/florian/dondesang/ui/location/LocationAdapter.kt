package fr.socket.florian.dondesang.ui.location

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.model.Location

class LocationAdapter(locations: List<Location> = emptyList()) : RecyclerView.Adapter<LocationViewHolder>() {

    var locations: List<Location> = locations
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("coucou", "coucou")
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): LocationViewHolder {
        Log.d("onCreateViewHolder", "coucou")
        return LocationViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.card_location,
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(viewHolder: LocationViewHolder, position: Int) {
        viewHolder.update(locations[position])
        Log.d("onBind", "coucou")
    }

}