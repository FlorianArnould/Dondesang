package fr.socket.florian.dondesang.ui.about

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R

internal class RepositoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.title)
    private val description: TextView = view.findViewById(R.id.description)
    // TODO implement OnClickListener in a parent
    fun update(repository: Repository) {
        title.text = repository.title
        description.text = repository.description
        view.setOnClickListener { view ->
            view.context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(repository.link)
                )
            )
        }
    }
}
