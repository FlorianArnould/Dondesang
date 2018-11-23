package fr.socket.florian.dondesang.ui.activities

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.HistoryManager
import fr.socket.florian.dondesang.loader.Loader
import fr.socket.florian.dondesang.model.User
import fr.socket.florian.dondesang.service.Utils
import fr.socket.florian.dondesang.ui.about.AboutFragment
import fr.socket.florian.dondesang.ui.donation.DonationsFragment
import fr.socket.florian.dondesang.ui.donation.LoadingFragment
import fr.socket.florian.dondesang.ui.abstracts.*
import fr.socket.florian.dondesang.ui.information.PersonalInfoFragment
import fr.socket.florian.dondesang.ui.location.LocationFragment
import fr.socket.florian.dondesang.ui.notification.NotificationsFragment
import fr.socket.florian.dondesang.ui.quiz.QuizFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val loader: Loader = Loader()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_dons)

        inflateFragment(LoadingFragment())

        user = intent?.extras?.getParcelable(USER_ARG)
        if (user == null) {
            loader.initialize(this) { loader ->
                if (loader == null) {
                    Toast.makeText(
                        this@MainActivity,
                        "An error occurred during authentication check",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java), LOGIN_ACTIVITY_RESULT)
                } else {
                    loadData()
                }
            }
        } else {
            onUserLoaded(user)
        }
        if (!Utils.isAlarmSet(this)) {
            Utils.scheduleAlarm(this)
            Log.d("SignalAlarm", "alarm not set")
        } else {
            Log.d("SignalAlarm", "alarm set")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOGIN_ACTIVITY_RESULT) {
            loader.initialize(this) { loadData() }
        }
    }

    private fun loadData() {
        loader.asyncLoadUser(this::onUserLoaded)
    }

    private fun inflateFragment(fragment: TitledFragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContent, fragment)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_questions -> inflateFragment(QuizFragment.newInstance())
            R.id.nav_find -> inflateFragment(LocationFragment.newInstance())
            R.id.nav_dons -> inflateFragment(DonationsFragment.newInstance(user!!))
            R.id.nav_dossier -> inflateFragment(PersonalInfoFragment.newInstance(user!!))
            R.id.nav_notifications -> inflateFragment(NotificationsFragment.newInstance())
            R.id.nav_about -> inflateFragment(AboutFragment.newInstance())
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun onUserLoaded(user: User?) {
        if (user != null) {
            HistoryManager.saveAndCheck(this, user)
            this.user = user
            inflateFragment(DonationsFragment.newInstance(user))
            nav_view.getHeaderView(0).findViewById<TextView>(R.id.blood_type).text = user.bloodType
            val nameString = "${user.firstName} ${user.name}"
            nav_view.getHeaderView(0).findViewById<TextView>(R.id.name).text = nameString
            nav_view.getHeaderView(0).findViewById<TextView>(R.id.blood_type_message).text = user.bloodTypeMessage
        } else {
            Snackbar.make(fragmentContent, "Le chargement a échoué", Snackbar.LENGTH_INDEFINITE)
                .setAction("Réessayer") {
                    loader.asyncLoadUser(this::onUserLoaded)
                }.show()
        }
    }

    companion object {
        private const val LOGIN_ACTIVITY_RESULT = 1
        const val USER_ARG = "user_arg"
    }
}
