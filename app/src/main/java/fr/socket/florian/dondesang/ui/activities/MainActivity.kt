package fr.socket.florian.dondesang.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import fr.socket.florian.dondesang.ui.about.AboutFragment
import fr.socket.florian.dondesang.ui.abstracts.TitledFragment
import fr.socket.florian.dondesang.ui.donation.DonationsFragment
import fr.socket.florian.dondesang.ui.donation.LoadingFragment
import fr.socket.florian.dondesang.ui.information.PersonalInfoFragment
import fr.socket.florian.dondesang.ui.location.LocationFragment
import fr.socket.florian.dondesang.ui.notification.NotificationsFragment
import fr.socket.florian.dondesang.ui.quiz.QuizFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val loader: Loader = Loader()
    var user: User? = null
        set(value) {
            field = value
            onUserLoaded(user)
        }

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

        nav_view.getHeaderView(0).login_button.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java), LOGIN_ACTIVITY_RESULT)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        nav_view.menu.findItem(R.id.donner_part).isVisible = false

        val sharedPreferences = getSharedPreferences(getString(R.string.login_info), Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(getString(R.string.isAuthenticated), false)) {
            inflateFragment(LoadingFragment())
            firstLoadUser()
            nav_view.getHeaderView(0).shimmer_layout.visibility = View.VISIBLE
            nav_view.getHeaderView(0).login_button.visibility = View.GONE
        } else {
            nav_view.setCheckedItem(R.id.nav_questions)
            inflateFragment(QuizFragment.newInstance())
        }
    }

    private fun firstLoadUser() {
        loader.initialize(this) { loader ->
            if (loader == null) {
                Toast.makeText(
                    this@MainActivity,
                    "An error occurred during authentication check",
                    Toast.LENGTH_LONG
                ).show()
                startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java), LOGIN_ACTIVITY_RESULT)
            } else {
                loader.loadUser {
                    this.user = it
                    if (it != null) {
                        nav_view.menu.findItem(R.id.donner_part).isVisible = true
                        if (supportFragmentManager.findFragmentById(R.id.fragmentContent) is LoadingFragment) {
                            inflateFragment(DonationsFragment.newInstance(it))
                            nav_view.setCheckedItem(R.id.nav_dons)
                        }
                        nav_view.getHeaderView(0).shimmer_layout.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOGIN_ACTIVITY_RESULT && resultCode == LoginActivity.SUCCESS) {
            nav_view.getHeaderView(0).login_button.visibility = View.GONE
            nav_view.getHeaderView(0).shimmer_layout.visibility = View.VISIBLE
            firstLoadUser()
        }
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
            nav_view.getHeaderView(0).blood_type.text = user.bloodType
            val nameString = "${user.firstName} ${user.name}"
            nav_view.getHeaderView(0).name.text = nameString
            nav_view.getHeaderView(0).blood_type_message.text = user.bloodTypeMessage
        } else {
            Snackbar.make(fragmentContent, "Le chargement a échoué", Snackbar.LENGTH_INDEFINITE)
                .setAction("Réessayer") {
                    loader.initialize(this) { loader ->
                        loader?.loadUser { user ->
                            this.user = user
                            if (user != null) inflateFragment(DonationsFragment.newInstance(user))
                        }
                    }
                }.show()
        }
    }

    companion object {
        private const val LOGIN_ACTIVITY_RESULT = 1
    }
}