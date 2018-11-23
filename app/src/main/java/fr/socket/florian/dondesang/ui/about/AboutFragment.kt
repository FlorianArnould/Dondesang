package fr.socket.florian.dondesang.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.ui.abstracts.TitledFragment
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : TitledFragment() {
    override val title: String
        get() = getString(R.string.about)

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = AboutAdapter()
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}