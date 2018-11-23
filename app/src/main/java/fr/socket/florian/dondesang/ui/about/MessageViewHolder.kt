package fr.socket.florian.dondesang.ui.about

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R

class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.message)

    fun update(@StringRes stringRes: Int) {
        textView.setText(stringRes)
    }
}