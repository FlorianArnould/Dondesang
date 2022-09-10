package fr.socket.florian.dondesang.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.service.NotificationWorker

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        NotificationWorker.schedule()
        window.statusBarColor = getColor(R.color.colorPrimary)
        window.navigationBarColor = getColor(R.color.colorPrimary)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 500)
    }
}
