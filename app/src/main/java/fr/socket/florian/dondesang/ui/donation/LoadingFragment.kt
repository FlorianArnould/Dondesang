package fr.socket.florian.dondesang.ui.donation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.ui.abstracts.TitledFragment


/**
 * A simple [Fragment] subclass.
 */
class LoadingFragment : TitledFragment() {

    override val title: String
        get() = getString(R.string.app_name)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }
}
