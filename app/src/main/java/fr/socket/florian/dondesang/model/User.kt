package fr.socket.florian.dondesang.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray

import org.json.JSONException
import org.json.JSONObject

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

@Parcelize
class User(
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

    val lastDonationDate: Date?
        get() {
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

            val donnor = json.getJSONObject("result").getJSONObject("content").getJSONObject("value").getJSONObject("donneur")

            val dfsource = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)

            val nbBloodDonations = donnor.getInt("nbDonSang")
            val nbPlateletsDonations = donnor.getInt("nbDonPlaquette")
            val nbPlasmaDonations = donnor.getInt("nbDonPlasma")

            return User (
                id = loadString(donnor, "identifiantMDD"),

                name = loadString(donnor, "nom"),
                marriedName = loadString(donnor, "nomMarital"),
                firstName = loadString(donnor, "prenom"),

                birthDate = loadString(donnor, "dateNaissance"),
                birthplace = loadString(donnor, "lieuNaissance"),

                address = loadString(donnor, "rue"),
                homeType = loadString(donnor, "etage"),
                building = loadString(donnor, "immeuble"),
                placeOf = loadString(donnor, "bp"),
                zipCode = loadString(donnor, "cp"),
                city = loadString(donnor, "ville"),

                weight = donnor.getDouble("poids"),
                height = donnor.getInt("taille"),

                personalNumber = loadString(donnor, "telFix"),
                mobileNumber = loadString(donnor, "telMob"),
                professionalNumber = loadString(donnor, "telPro"),
                email = loadString(donnor, "email"),

                bloodType = loadString(donnor, "groupeSanguinLabel"),
                bloodTypeMessage = loadString(donnor, "groupeSanguinMessage"),

                nbBloodDonations = nbBloodDonations,
                nbPlateletsDonations = nbPlateletsDonations,
                nbPlasmaDonations = nbPlasmaDonations,

                nextBloodDonationDate = dfsource.parse(loadString(donnor, "dateEligSang")),
                nextPlateletsDonationDate = dfsource.parse(loadString(donnor, "dateEligPlaquette")),
                nextPlasmaDonationDate = dfsource.parse(loadString(donnor, "dateEligPlasma")),

                bloodDonationsHistory = if (nbBloodDonations > 0) parseArray(dfsource, donnor.getJSONArray("donsSang")) else emptyArray(),

                plateletsDonationsHistory = if (nbPlateletsDonations > 0) parseArray(dfsource, donnor.getJSONArray("donsPlaquette")) else emptyArray(),

                plasmaDonationsHistory = if (nbPlasmaDonations > 0) parseArray(dfsource, donnor.getJSONArray("donsPlasma")) else emptyArray()
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
