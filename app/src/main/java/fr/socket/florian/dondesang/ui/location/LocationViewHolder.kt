package fr.socket.florian.dondesang.ui.location

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.model.Location
import kotlin.math.roundToInt


class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val icon: ImageView = view.findViewById(R.id.icon)
    private val name: TextView = view.findViewById(R.id.name)
    private val address: TextView = view.findViewById(R.id.address)
    private val phoneButton: ImageButton = view.findViewById(R.id.phone_button)
    private val information: TextView = view.findViewById(R.id.info)
    private val distance: TextView = view.findViewById(R.id.distance)
    private val bloodIcon: View = view.findViewById(R.id.blood_layout)
    private val plasmaIcon: View = view.findViewById(R.id.plasma_layout)
    private val plateletsIcon: View = view.findViewById(R.id.platelets_layout)

    fun update(location: Location) {
        name.text = location.name
        icon.setImageResource(if (location.isFixed) R.drawable.ic_business else R.drawable.ic_bus)
        address.text = location.address
        information.text = location.info
        if(location.distance != null) {
            val distanceString = "${location.distance?.roundToInt()} km"
            distance.text = distanceString
        } else {
            distance.visibility = View.GONE
        }
        phoneButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${location.phone}")
            it.context.startActivity(intent)
        }
        if (!location.canBlood) bloodIcon.visibility = View.GONE
        if (!location.canPlasma) plasmaIcon.visibility = View.GONE
        if (!location.canPlatelet) plateletsIcon.visibility = View.GONE

        view.setOnClickListener {
            val uri = Uri.parse("geo:${location.lat},${location.long}?q=${Uri.encode(location.address)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            it.context.startActivity(intent)
        }
    }
}