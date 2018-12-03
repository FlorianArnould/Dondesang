package fr.socket.florian.dondesang.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.ui.abstracts.TitledFragment
import kotlinx.android.synthetic.main.recycler_view.*

class NotificationsFragment : TitledFragment() {

    override val title
        get() = getString(R.string.notifications)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = PreferenceAdapter(
            recyclerView.context, arrayOf(
                Preference(
                    R.string.next_blood_donation,
                    R.string.next_blood_donation_description,
                    R.string.next_blood_donation_notification
                ),
                Preference(
                    R.string.next_plasma_donation,
                    R.string.next_plasma_donation_description,
                    R.string.next_plasma_donation_notification
                ),
                Preference(
                    R.string.next_platelet_donation,
                    R.string.platelets_donation_description,
                    R.string.next_platelet_donation_notification
                ),
                Preference(
                    R.string.after_donation_congrats,
                    R.string.after_donation_congrats_description,
                    R.string.after_donation_congrats_notification
                )
            )
        )
    }

    companion object {

        fun newInstance(): NotificationsFragment {
            return NotificationsFragment()
        }
    }
}
