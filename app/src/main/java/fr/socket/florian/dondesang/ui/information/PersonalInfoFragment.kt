package fr.socket.florian.dondesang.ui.information

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader
import fr.socket.florian.dondesang.model.User
import fr.socket.florian.dondesang.ui.abstracts.UserFragment
import fr.socket.florian.dondesang.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_personal_info.*
import org.json.JSONObject

class PersonalInfoFragment : UserFragment() {
    override val title
        get() = getString(R.string.mes_infos_personnelles)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (user != null) inflateUser(user!!)
        save_button.setOnClickListener {
            Loader().initialize(context!!) { loader ->
                if (loader == null) save_button.error()
                else {
                    val jsonUser = user!!.toJSON(
                        homeType = input_home_type.text.toString(),
                        address = input_address.text.toString(),
                        building = input_building.text.toString(),
                        placeOf = input_place_of.text.toString(),
                        zipCode = input_zip_code.text.toString(),
                        city = input_city.text.toString(),
                        weight = input_weight.text.toString().toDouble(),
                        height = input_height.text.toString().toInt(),
                        personalNumber = input_personal_number.text.toString(),
                        mobileNumber = input_mobile_number.text.toString(),
                        professionalNumber = input_professional_number.text.toString(),
                        email = input_email.text.toString(),
                        accordADSB = checkbox.isChecked
                    )
                    loader.putUser(jsonUser) { success ->
                        if (success) {
                            save_button.success(Runnable {
                                user = User.parse(JSONObject(jsonUser))
                                (activity as MainActivity).user = user
                                Handler().postDelayed({ save_button.reinitialize() }, 1000)
                            })
                        } else save_button.error()
                    }
                }
            }
        }
    }

    private fun inflateUser(user: User) {
        input_donation_id.setText(user.id)
        input_current_name.setText(user.marriedName)
        input_birth_name.setText(user.name)
        input_first_name.setText(user.firstName)
        input_birth_date.setText(user.birthDate)
        input_birth_place.setText(user.birthplace)
        input_address.setText(user.address)
        input_home_type.setText(user.homeType)
        input_building.setText(user.building)
        input_place_of.setText(user.placeOf)
        input_zip_code.setText(user.zipCode)
        input_city.setText(user.city)
        input_weight.setText(user.weight.toString())
        input_height.setText(user.height.toString())
        input_personal_number.setText(user.personalNumber)
        input_mobile_number.setText(user.mobileNumber)
        input_professional_number.setText(user.professionalNumber)
        input_email.setText(user.email)
        checkbox.isChecked = user.accordADSB
    }

    companion object {
        fun newInstance(user: User): PersonalInfoFragment {
            val fragment = PersonalInfoFragment()
            setUser(fragment, user)
            return fragment
        }
    }
}
