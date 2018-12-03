package fr.socket.florian.dondesang.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = getColor(R.color.colorPrimary)
        window.navigationBarColor = getColor(R.color.colorPrimary)
        progressButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val usernameString = editUsername.text.toString()
        val passwordString = editPassword.text.toString()
        if (usernameString.isEmpty() || passwordString.isEmpty()) {
            progressButton.error()
            return
        }
        Loader().login(usernameString, passwordString) {
            if (it) {
                getSharedPreferences(getString(R.string.login_info), Context.MODE_PRIVATE)
                    .edit()
                    .putString(getString(R.string.username), usernameString)
                    .putString(getString(R.string.password), passwordString)
                    .putBoolean(getString(R.string.isAuthenticated), true)
                    .apply()
                progressButton.success(Runnable {
                    setResult(SUCCESS)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                })
            } else progressButton.error()
        }
    }

    companion object {
        const val SUCCESS = 1
    }
}
