package fr.socket.florian.dondesang.ui.about

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R

internal class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = view.findViewById(R.id.title)

    fun update(@StringRes stringRes: Int) {
        title.setText(stringRes)
    }
}
