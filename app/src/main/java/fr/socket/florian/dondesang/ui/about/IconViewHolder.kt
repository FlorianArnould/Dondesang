package fr.socket.florian.dondesang.ui.about

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R

internal class IconViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val icon: ImageView = view.findViewById(R.id.icon)
    private val message: TextView = view.findViewById(R.id.message)
    // TODO implement OnClickListener in a parent

    fun update(iconObject: Icon) {
        icon.setImageResource(iconObject.drawableRes)
        message.text = iconObject.message
        view.setOnClickListener { view -> view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(iconObject.link))) }
    }
}
