package fr.socket.florian.dondesang.ui.activities

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = getColor(R.color.colorPrimaryDark)
        button.setOnClickListener(this)
    }

    private fun displayButtonToProgress(runnable: Runnable) {
        button.isEnabled = false
        buttonText.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            buttonProgress.animate().alpha(1f).duration = 500
            runnable.run()
        }, 500)
    }

    private fun animateProgressToSuccess(runnable: Runnable) {
        buttonProgress.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            val transitionDrawable = TransitionDrawable(
                arrayOf(
                    getDrawable(R.drawable.login_button_normal),
                    getDrawable(R.drawable.login_button_success)
                )
            )
            button.background = transitionDrawable
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(500)
            resultButtonIcon.setImageResource(R.drawable.ic_check)
            resultButtonIcon.animate().alpha(1f).duration = 500
            Handler().postDelayed({ runnable.run() }, 500)
        }, 500)
    }

    private fun animateProgressToError() {
        buttonProgress.animate().alpha(0f).duration = 500
        Handler().postDelayed({
            val transitionDrawable = TransitionDrawable(
                arrayOf(
                    getDrawable(R.drawable.login_button_normal),
                    getDrawable(R.drawable.login_button_error)
                )
            )
            button.background = transitionDrawable
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(500)
            resultButtonIcon.setImageResource(R.drawable.ic_close)
            resultButtonIcon.animate().alpha(1f).duration = 500
            Handler().postDelayed({
                resultButtonIcon.animate().alpha(0f).duration = 500
                transitionDrawable.reverseTransition(500)
                Handler().postDelayed({
                    buttonText.animate().alpha(1f).duration = 500
                    button.isEnabled = true
                }, 500)
            }, 1000)
        }, 500)
    }

    private fun onSuccess() {
        animateProgressToSuccess(Runnable {
            finish()
        })
    }

    private fun onFailed() {
        animateProgressToError()
    }

    override fun onClick(view: View?) {
        displayButtonToProgress(Runnable {
            val usernameString = editUsername.text.toString()
            val passwordString = editPassword.text.toString()
            if (usernameString.isEmpty() || passwordString.isEmpty()) {
                onFailed()
                return@Runnable
            }
            Loader().login(usernameString, passwordString) {
                if (it) {
                    getSharedPreferences(getString(R.string.login_info), Context.MODE_PRIVATE)
                        .edit()
                        .putString(getString(R.string.username), usernameString)
                        .putString(getString(R.string.password), passwordString)
                        .putBoolean(getString(R.string.isAuthenticated), true)
                        .apply()
                    onSuccess()
                } else onFailed()
            }
        })
    }
}
