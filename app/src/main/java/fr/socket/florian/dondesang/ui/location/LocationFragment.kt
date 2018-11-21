package fr.socket.florian.dondesang.ui.location

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader
import fr.socket.florian.dondesang.model.setDistance
import fr.socket.florian.dondesang.model.sortByDistance
import fr.socket.florian.dondesang.ui.fragments.TitledFragment
import kotlinx.android.synthetic.main.recycler_view.*


class LocationFragment : TitledFragment(), LocationListener {

    private val adapter = LocationAdapter(emptyList())

    override val title: String
        get() = getString(R.string.find_location)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter

        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_GPS_PERMISSION)
        } else {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)
        } else {
            loadLocations()
        }
    }

    private fun loadLocations(lat: Float? = null, long: Float? = null) {
        Log.d("loadLocation", "loading locations ...")
        Loader().getLocations { locations ->
            if (locations != null) {
                progress.animate().alpha(0f).duration = 300
                Handler().postDelayed({ progress.visibility = View.GONE }, 300)

                if (lat != null && long != null) {
                    locations.forEach { setDistance(it, lat, long) }
                    adapter.locations = sortByDistance(locations)
                } else {
                    adapter.locations = locations
                }
                recyclerView.scheduleLayoutAnimation()
            }
        }
    }

    override fun onLocationChanged(l: Location?) {
        loadLocations(l?.latitude?.toFloat(), l?.longitude?.toFloat())
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}

    companion object {
        private const val REQUEST_GPS_PERMISSION = 0

        fun newInstance(): LocationFragment {
            return LocationFragment()
        }
    }
}