package fr.socket.florian.dondesang.ui.notification

import android.content.SharedPreferences
import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.preference_row.view.*

class PreferenceViewHolder(private val preferences: SharedPreferences, private val view: View) :
    RecyclerView.ViewHolder(view), CompoundButton.OnCheckedChangeListener {
    var preference: Preference = Preference(0, 0, 0)
        set(value) {
            field = value
            view.title.setText(preference.titleRes)
            view.description.setText(preference.descriptionRes)
            view.switch_button.isChecked = preferences.getBoolean(view.context.getString(value.keyRes), false)
            view.switch_button.setOnCheckedChangeListener(this)
            view.setOnClickListener { view.switch_button.toggle() }
        }

    override fun onCheckedChanged(cb: CompoundButton?, isChecked: Boolean) {
        preferences
            .edit()
            .putBoolean(view.context.getString(preference.keyRes), isChecked)
            .apply()
    }
}