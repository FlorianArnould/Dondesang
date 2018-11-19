package fr.socket.florian.dondesang.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.model.User

class PersonalInfoFragment : UserFragment() {
    override val title
            get() = getString(R.string.mes_infos_personnelles)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal_info, container, false)
        if (user != null) {
            setText(view, R.id.input_donation_id, user!!.id)
            setText(view, R.id.input_current_name, user!!.marriedName)
            setText(view, R.id.input_birth_name, user!!.name)
            setText(view, R.id.input_first_name, user!!.firstName)
            setText(view, R.id.input_birth_date, user!!.birthDate)
            setText(view, R.id.input_birth_place, user!!.birthplace)
            setText(view, R.id.input_address, user!!.address)
            setText(view, R.id.input_home_type, user!!.homeType)
            setText(view, R.id.input_building, user!!.building)
            setText(view, R.id.input_place_of, user!!.placeOf)
            setText(view, R.id.input_zip_code, user!!.zipCode)
            setText(view, R.id.input_city, user!!.city)
            setText(view, R.id.input_weight, user!!.weight.toString())
            setText(view, R.id.input_height, user!!.height.toString())
            setText(view, R.id.input_personal_number, user!!.personalNumber)
            setText(view, R.id.input_mobile_number, user!!.mobileNumber)
            setText(view, R.id.input_professional_number, user!!.professionalNumber)
            setText(view, R.id.input_email, user!!.email)
        }
        return view
    }

    private fun setText(view: View, id: Int, text: String) {
        val edit = view.findViewById<EditText>(id)
        edit.setText(text)
    }

    companion object {
        fun newInstance(user: User): PersonalInfoFragment {
            val fragment = PersonalInfoFragment()
            setUser(fragment, user)
            return fragment
        }
    }
}
