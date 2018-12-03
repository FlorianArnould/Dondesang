package fr.socket.florian.dondesang.model

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

data class Location(
    val name: String,
    val isFixed: Boolean,
    val address: String,
    val phone: String?,
    val canBlood: Boolean,
    val canPlasma: Boolean,
    val canPlatelet: Boolean,
    val info: String,
    val lat: Float,
    val long: Float,
    var distance: Float? = null
)

fun sortByDistance(locations: List<Location>): List<Location> {
    return locations.sortedBy(Location::distance)
}

fun setDistance(location: Location, lat: Float, long: Float) {
    location.distance = distance(lat, long, location.lat, location.long)
}

private fun distance(lat1: Float, long1: Float, lat2: Float, long2: Float): Float {

    val latitude1 = lat1 * Math.PI / 180
    val latitude2 = lat2 * Math.PI / 180

    val longitude1 = long1 * Math.PI / 180
    val longitude2 = long2 * Math.PI / 180

    val r = 6371f

    return (r * acos(cos(latitude1) * cos(latitude2) * cos(longitude2 - longitude1) + sin(latitude1) * sin(latitude2))).toFloat()
}
