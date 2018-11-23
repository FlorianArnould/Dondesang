package fr.socket.florian.dondesang.ui.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.model.User
import fr.socket.florian.dondesang.ui.abstracts.UserFragment
import java.util.*

class DonationsFragment : UserFragment() {
    override val title
        get() = getString(R.string.mes_dons)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_donations_2, container, false)
        if (user != null) {
            val lastDonationDate = user!!.lastDonationDate()
            fillCard(
                view.findViewById(R.id.blood_card),
                R.string.blood_donation,
                R.string.blood_donation_message,
                R.color.blood,
                user!!.nbBloodDonations,
                user!!.nextBloodDonationDate,
                lastDonationDate ?: Date(0)
            )

            fillCard(
                view.findViewById(R.id.plasma_card),
                R.string.plasma_donation,
                R.string.plasma_donation_message,
                R.color.plasma,
                user!!.nbPlasmaDonations,
                user!!.nextPlasmaDonationDate,
                lastDonationDate ?: Date(0)
            )

            fillCard(
                view.findViewById(R.id.platelets_card),
                R.string.platelets_donation,
                R.string.platelets_donation_message,
                R.color.platelets,
                user!!.nbPlateletsDonations,
                user!!.nextPlateletsDonationDate,
                lastDonationDate ?: Date(0)
            )
        }
        return view
    }

    private fun fillCard(
        view: View,
        titleRes: Int,
        messageRes: Int,
        colorRes: Int,
        number: Int,
        nextDonationDate: Date,
        lastDonationDate: Date
    ) {
        val imageView = view.findViewById<ImageView>(R.id.image)
        imageView.setImageResource(R.drawable.ic_droplet)
        imageView.setColorFilter(context!!.getColor(colorRes))

        val title = view.findViewById<TextView>(R.id.title)
        title.setText(titleRes)

        val message = view.findViewById<TextView>(R.id.message)
        message.setText(messageRes)

        val numberView = view.findViewById<TextView>(R.id.donation_number)
        numberView.text = number.toString()

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val day = 24 * 60 * 60 * 1000
        val progress = ((Calendar.getInstance().time.time - lastDonationDate.time) / day).toInt()
        val max = ((nextDonationDate.time - lastDonationDate.time) / day).toInt()

        progressBar.progress = progress
        progressBar.max = max

        val textView = view.findViewById<TextView>(R.id.remaining_time_message)
        val remaining = max - progress
        if (remaining > 1) {
            textView.text = "Encore $remaining jours avant le prochain don"
        } else if (remaining == 1) {
            textView.text = "Un dernier jour avant le prochain don"
        } else {
            textView.text = "Vous pouvez retourner faire un don"
        }
    }

    companion object {
        fun newInstance(user: User): DonationsFragment {
            val fragment = DonationsFragment()
            setUser(fragment, user)
            return fragment
        }
    }
}
