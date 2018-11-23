package fr.socket.florian.dondesang.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.Loader

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        window.statusBarColor = getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = getColor(R.color.colorPrimaryDark)
        val sharedPreferences = getSharedPreferences(getString(R.string.login_info), Context.MODE_PRIVATE)
        val intent = Intent(this, MainActivity::class.java)
        if (sharedPreferences.getBoolean(getString(R.string.isAuthenticated), false)) {
            Loader().initialize(this) { loader ->
                if (loader != null) {
                    loader.asyncLoadUser { user ->
                        intent.putExtra(MainActivity.USER_ARG, user)
                        startApp(intent)
                    }
                } else startApp(intent)
            }
        } else startApp(intent)
    }

    private fun startApp(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}
