package fr.socket.florian.dondesang.ui.about

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.socket.florian.dondesang.R

class AboutAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageRes = R.string.about_message
    private val headers = listOf(R.string.repository, R.string.icons, R.string.websites)
    private val repositories = listOf(Repository("Dondesang", "Application Android regroupant les services de l'EFS", "https://github.com/FlorianArnould/Dondesang"))
    private val icons = listOf(
        Icon(R.drawable.ic_droplet, "Icon made by Karen Tyler from The Noun Project", "https://thenounproject.com/search/?q=droplet&i=341207"),
        Icon(R.drawable.ic_github, "Icon made by Smashicons from www.flaticon.com", "https://smashicons.com/"))
    private val websites = listOf(
        Website("Etablissement français du sang (EFS)", "Site officiel de Etablissement français du sang (EFS) regroupant énormement d'informations", "https://dondesang.efs.sante.fr/"),
        Website("Espace donneur de l'EFS", "L'espace de l'Etablissement français du sang (EFS) dédié aux donneurs de sang", "https://donneurs.efs.sante.fr/"))

    private var headerCounter: Int = 0
    private var repositoriesCounter: Int = 0
    private var iconsCounter: Int = 0
    private var websitesCounter: Int = 0

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            headerCounter = 0
            repositoriesCounter = 0
            iconsCounter = 0
            websitesCounter = 0
        }
        return if(position == 0) MESSAGE_VIEW_TYPE
        else if (position == 1 || position == repositories.size + 2 || position == repositories.size + icons.size + 3) HEADER_VIEW_TYPE
        else if (position < repositories.size + 2) REPOSITORY_VIEW_TYPE
        else if (position < icons.size + repositories.size + 3) ICON_VIEW_TYPE
        else WEBSITE_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return headers.size + repositories.size + icons.size + websites.size + 1
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        Log.d(position.toString(), getItemViewType(position).toString())
        when (getItemViewType(position)) {
            MESSAGE_VIEW_TYPE -> (viewHolder as MessageViewHolder).update(messageRes)
            ICON_VIEW_TYPE -> (viewHolder as IconViewHolder).update(icons[iconsCounter++])
            HEADER_VIEW_TYPE -> (viewHolder as HeaderViewHolder).update(headers[headerCounter++])
            REPOSITORY_VIEW_TYPE -> (viewHolder as RepositoryViewHolder).update(repositories[repositoriesCounter++])
            WEBSITE_VIEW_TYPE -> (viewHolder as WebsiteViewHolder).update(websites[websitesCounter++])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            MESSAGE_VIEW_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.message_row, parent, false)
                return MessageViewHolder(view)
            }
            HEADER_VIEW_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.header_row, parent, false)
                return HeaderViewHolder(view)
            }
            ICON_VIEW_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.icon_row, parent, false)
                return IconViewHolder(view)
            }
            REPOSITORY_VIEW_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.repository_row, parent, false)
                return RepositoryViewHolder(view)
            }
            WEBSITE_VIEW_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.website_row, parent, false)
                return WebsiteViewHolder(view)
            }
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.icon_row, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    companion object {
        private const val MESSAGE_VIEW_TYPE = 0
        private const val ICON_VIEW_TYPE = 1
        private const val HEADER_VIEW_TYPE = 2
        private const val REPOSITORY_VIEW_TYPE = 3
        private const val WEBSITE_VIEW_TYPE = 4
    }
}