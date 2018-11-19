package fr.socket.florian.dondesang.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray

import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class User(
    val id: String,
    val name: String,
    val marriedName: String,
    val firstName: String,
    val birthDate: String,
    val birthplace: String,
    val address: String,
    val homeType: String,
    val building: String,
    val placeOf: String,
    val zipCode: String,
    val city: String,
    val weight: Double,
    val height: Int,
    val personalNumber: String,
    val mobileNumber: String,
    val professionalNumber: String,
    val email: String,
    val bloodType: String,
    val bloodTypeMessage: String,
    val nbBloodDonations: Int,
    val nbPlateletsDonations: Int,
    val nbPlasmaDonations: Int,
    val nextBloodDonationDate: Date,
    val nextPlateletsDonationDate: Date,
    val nextPlasmaDonationDate: Date,
    val bloodDonationsHistory: Array<Donation>,
    val plateletsDonationsHistory: Array<Donation>,
    val plasmaDonationsHistory: Array<Donation>
    ) : Parcelable {

    fun lastDonationDate(): Date? {
        val array = bloodDonationsHistory + plateletsDonationsHistory + plasmaDonationsHistory
        if(array.isEmpty()){
            return null
        }
        var last = array[0].date
        for (donation in array) {
            if (last < donation.date) {
                last = donation.date
            }
        }
        return last
    }

    companion object {
        fun parse(json: JSONObject): User {

            val donner = json.getJSONObject("result").getJSONObject("content").getJSONObject("value").getJSONObject("donneur")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)

            val nbBloodDonations = donner.getInt("nbDonSang")
            val nbPlateletsDonations = donner.getInt("nbDonPlaquette")
            val nbPlasmaDonations = donner.getInt("nbDonPlasma")

            return User (
                id = loadString(donner, "identifiantMDD"),

                name = loadString(donner, "nom"),
                marriedName = loadString(donner, "nomMarital"),
                firstName = loadString(donner, "prenom"),

                birthDate = loadString(donner, "dateNaissance"),
                birthplace = loadString(donner, "lieuNaissance"),

                address = loadString(donner, "rue"),
                homeType = loadString(donner, "etage"),
                building = loadString(donner, "immeuble"),
                placeOf = loadString(donner, "bp"),
                zipCode = loadString(donner, "cp"),
                city = loadString(donner, "ville"),

                weight = donner.getDouble("poids"),
                height = donner.getInt("taille"),

                personalNumber = loadString(donner, "telFix"),
                mobileNumber = loadString(donner, "telMob"),
                professionalNumber = loadString(donner, "telPro"),
                email = loadString(donner, "email"),

                bloodType = loadString(donner, "groupeSanguinLabel"),
                bloodTypeMessage = loadString(donner, "groupeSanguinMessage"),

                nbBloodDonations = nbBloodDonations,
                nbPlateletsDonations = nbPlateletsDonations,
                nbPlasmaDonations = nbPlasmaDonations,

                nextBloodDonationDate = dateFormat.parse(loadString(donner, "dateEligSang")),
                nextPlateletsDonationDate = dateFormat.parse(loadString(donner, "dateEligPlaquette")),
                nextPlasmaDonationDate = dateFormat.parse(loadString(donner, "dateEligPlasma")),

                bloodDonationsHistory = if (nbBloodDonations > 0) parseArray(dateFormat, donner.getJSONArray("donsSang")) else emptyArray(),

                plateletsDonationsHistory = if (nbPlateletsDonations > 0) parseArray(dateFormat, donner.getJSONArray("donsPlaquette")) else emptyArray(),

                plasmaDonationsHistory = if (nbPlasmaDonations > 0) parseArray(dateFormat, donner.getJSONArray("donsPlasma")) else emptyArray()
            )
        }

        private fun parseArray(dateFormat: SimpleDateFormat, json: JSONArray): Array<Donation> {
            return Array(json.length()) {
                Donation(json.getJSONObject(it).getString("villeDon"), dateFormat.parse(json.getJSONObject(it).getString("dateDon")))
            }
        }

        private fun loadString(json: JSONObject, key: String): String {
            return if (json.isNull(key)) "" else json.getString(key)
        }
    }
}
