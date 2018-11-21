package fr.socket.florian.dondesang.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import fr.socket.florian.dondesang.R

class NotificationsFragment : TitledFragment() {

    override val title
        get() = getString(R.string.notifications)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        val context = context
        if (context != null) {
            val preferences =
                context.getSharedPreferences(getString(R.string.notification_preferences), Context.MODE_PRIVATE)
            setPreference(
                view.findViewById(R.id.next_blood_donation),
                R.string.next_blood_donation_notification,
                preferences
            )
            setPreference(
                view.findViewById(R.id.next_platelet_donation),
                R.string.next_platelet_donation_notification,
                preferences
            )
            setPreference(
                view.findViewById(R.id.next_plasma_donation),
                R.string.next_plasma_donation_notification,
                preferences
            )
            setPreference(
                view.findViewById(R.id.after_donation_congrats),
                R.string.after_donation_congrats_notification,
                preferences
            )
        }
        return view
    }

    private fun setPreference(button: Switch, @StringRes stringRes: Int, preferences: SharedPreferences) {
        button.isChecked = preferences.getBoolean(getString(stringRes), true)
        button.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean(getString(stringRes), isChecked).apply()
        }
    }

    companion object {

        fun newInstance(): NotificationsFragment {
            return NotificationsFragment()
        }
    }
}
