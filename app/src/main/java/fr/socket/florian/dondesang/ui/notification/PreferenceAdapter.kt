package fr.socket.florian.dondesang.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R

class PreferenceAdapter(context: Context, private val preferences: Array<Preference>) :
    RecyclerView.Adapter<PreferenceViewHolder>() {
    private val sharedPreferences =
        context.getSharedPreferences(context.getString(R.string.notification_preferences), Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        return PreferenceViewHolder(
            sharedPreferences,
            LayoutInflater.from(parent.context).inflate(R.layout.preference_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return preferences.size
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.preference = preferences[position]
    }

}