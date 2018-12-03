package fr.socket.florian.dondesang.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class User(
    val json: String,
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
    val plasmaDonationsHistory: Array<Donation>,
    val accordADSB: Boolean
) : Parcelable {

    fun lastDonationDate(): Date? {
        val array = bloodDonationsHistory + plateletsDonationsHistory + plasmaDonationsHistory
        if (array.isEmpty()) {
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

    fun toJSON(
        homeType: String,
        address: String,
        building: String,
        placeOf: String,
        zipCode: String,
        city: String,
        weight: Double,
        height: Int,
        personalNumber: String,
        mobileNumber: String,
        professionalNumber: String,
        email: String,
        accordADSB: Boolean
    ): String {
        val json = JSONObject(this.json)
        json.put("rue", address)
        json.put("etage", homeType)
        json.put("immeuble", building)
        json.put("bp", placeOf)
        json.put("cp", zipCode)
        json.put("ville", city)
        json.put("poids", weight)
        json.put("taille", height)
        json.put("telFix", personalNumber)
        json.put("telMob", mobileNumber)
        json.put("telPro", professionalNumber)
        json.put("email", email)
        json.put("transmissionADSB", accordADSB)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)
        if (accordADSB) json.put("dateAccordADSB", dateFormat.format(Date()))
        else json.put("dateRetractationADSB", dateFormat.format(Date()))
        return json.toString()
    }

    companion object {
        fun parse(json: JSONObject): User {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)
            val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)

            val nbBloodDonations = json.getInt("nbDonSang")
            val nbPlateletsDonations = json.getInt("nbDonPlaquette")
            val nbPlasmaDonations = json.getInt("nbDonPlasma")

            return User(
                json = json.toString(),
                id = loadString(json, "identifiantMDD"),

                name = loadString(json, "nom"),
                marriedName = loadString(json, "nomMarital"),
                firstName = loadString(json, "prenom"),

                birthDate = displayFormat.format(dateFormat.parse(loadString(json, "dateNaissance"))),
                birthplace = loadString(json, "lieuNaissance"),

                address = loadString(json, "rue"),
                homeType = loadString(json, "etage"),
                building = loadString(json, "immeuble"),
                placeOf = loadString(json, "bp"),
                zipCode = loadString(json, "cp"),
                city = loadString(json, "ville"),

                weight = json.getDouble("poids"),
                height = json.getInt("taille"),

                personalNumber = loadString(json, "telFix"),
                mobileNumber = loadString(json, "telMob"),
                professionalNumber = loadString(json, "telPro"),
                email = loadString(json, "email"),

                bloodType = loadString(json, "groupeSanguinLabel"),
                bloodTypeMessage = loadString(json, "groupeSanguinMessage"),

                nbBloodDonations = nbBloodDonations,
                nbPlateletsDonations = nbPlateletsDonations,
                nbPlasmaDonations = nbPlasmaDonations,

                nextBloodDonationDate = dateFormat.parse(loadString(json, "dateEligSang")),
                nextPlateletsDonationDate = dateFormat.parse(loadString(json, "dateEligPlaquette")),
                nextPlasmaDonationDate = dateFormat.parse(loadString(json, "dateEligPlasma")),

                bloodDonationsHistory = if (nbBloodDonations > 0) parseArray(
                    dateFormat,
                    json.getJSONArray("donsSang")
                ) else emptyArray(),

                plateletsDonationsHistory = if (nbPlateletsDonations > 0) parseArray(
                    dateFormat,
                    json.getJSONArray("donsPlaquette")
                ) else emptyArray(),

                plasmaDonationsHistory = if (nbPlasmaDonations > 0) parseArray(
                    dateFormat,
                    json.getJSONArray("donsPlasma")
                ) else emptyArray(),
                accordADSB = json.getBoolean("transmissionADSB")
            )
        }

        private fun parseArray(dateFormat: SimpleDateFormat, json: JSONArray): Array<Donation> {
            return Array(json.length()) {
                Donation(
                    json.getJSONObject(it).getString("villeDon"),
                    dateFormat.parse(json.getJSONObject(it).getString("dateDon"))
                )
            }
        }

        private fun loadString(json: JSONObject, key: String): String {
            return if (json.isNull(key)) "" else json.getString(key)
        }
    }
}
