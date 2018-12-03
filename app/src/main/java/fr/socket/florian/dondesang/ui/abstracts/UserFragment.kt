package fr.socket.florian.dondesang.ui.abstracts

import android.os.Bundle
import fr.socket.florian.dondesang.model.User

abstract class UserFragment : TitledFragment() {

    protected var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getParcelable(USER_ARG)
    }

    companion object {
        private const val USER_ARG = "user_arg"
        fun setUser(instance: UserFragment, user: User) {
            val bundle = Bundle()
            bundle.putParcelable(USER_ARG, user)
            instance.arguments = bundle
        }
    }
}