package fr.socket.florian.dondesang.loader

import android.content.Context
import android.os.AsyncTask
import android.util.Log

import org.json.JSONException
import org.jsoup.Connection
import org.jsoup.Jsoup

import java.io.IOException

import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.web.WebConnection
import fr.socket.florian.dondesang.loader.web.WebConnectionException
import fr.socket.florian.dondesang.model.User
import fr.socket.florian.dondesang.model.UserException

class Loader {
    private val _connection: WebConnection = WebConnection()

    fun initialize(context: Context, callback: (Loader?) -> Unit) {
        val preferences = context.getSharedPreferences(context.getString(R.string.login_info), Context.MODE_PRIVATE)
        val username = preferences.getString(context.getString(R.string.username), null)
        val password = preferences.getString(context.getString(R.string.password), null)
        if (username == null || password == null) {
            callback(null)
            return
        }
        login(username, password) {
                if (it) callback(this@Loader)
                else callback(null)
        }
    }

    val isUserAuthenticated: Boolean
        get() {
            try {
                val json = _connection.post(
                    "https://donneurs.efs.sante.fr/api/services/webdonneur/donneurService/IsUserAuthenticated",
                    "JSON"
                ).json
                return json.getBoolean("result")
            } catch (e: WebConnectionException) {
                Log.e("isUserAuthenticated", "Cannot request if the user is authenticated : " + e.message, e)
            } catch (e: JSONException) {
                Log.e("isUserAuthenticated", "Cannot parse the answer : " + e.message, e)
            }
            return false
        }

    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        AsyncLogin(username, password, this, callback).execute()
    }

    private fun login(username: String, password: String): Boolean {
        try {
            var response: Connection.Response = Jsoup.connect("https://donneurs.efs.sante.fr/Home/Login")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute()
            val cookie =
                response.header("Set-Cookie").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2].split(
                    ";".toRegex()
                ).dropLastWhile { it.isEmpty() }.toTypedArray()[0].substring(1).split("=".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
            val requestVerificationToken =
                response.parse().getElementsByAttributeValue("name", "__RequestVerificationToken").attr("value")
            response = Jsoup.connect("https://donneurs.efs.sante.fr/Home/Login")
                .method(Connection.Method.POST)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .followRedirects(false)
                .cookie(cookie[0], cookie[1])
                .requestBody("__RequestVerificationToken=$requestVerificationToken&UserName=$username&Password=$password&RememberMe=true")
                .execute()
            if (response.statusCode() == 302) {
                Log.d("login", response.header("Set-Cookie"))
                val cookies =
                    response.header("Set-Cookie").split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val ASPNET =
                    cookies[0].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split("=".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                val ADAuthCookie =
                    cookies[4].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split("=".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                _connection.setCookie(cookie[0], cookie[1])
                _connection.setCookie(ASPNET[0], ASPNET[1])
                _connection.setCookie(ADAuthCookie[0], ADAuthCookie[1])
                return true
            }
        } catch (e: IOException) {
            Log.e("login", e.message, e)
        }
        return false
    }

    fun loadUser(): User? {
        try {
            val document = _connection.get("https://donneurs.efs.sante.fr/Profil#/home").document
            val tokenElement = document.getElementById("profilUserId")
            val userIdElement = document.getElementById("profilUserCode")
            if (tokenElement == null || userIdElement == null) {
                return null
            }
            val userId = userIdElement.attr("value")
            val token = tokenElement.attr("value")
            val params = "{userId: \"$userId\", token: \"$token\"}"
            val json = _connection.post(
                "https://donneurs.efs.sante.fr/api/services/webdonneur/donneurService/GetDonneurDetails",
                params
            ).json
            return User(json)
        } catch (e: WebConnectionException) {
            Log.e("loadUser", "Cannot request the user details : " + e.message, e)
        } catch (e: UserException) {
            Log.e("loadUser", "Cannot create an user instance : " + e.message, e)
        }
        return null
    }

    fun asyncIsUserAuthenticated(callback: (Boolean) -> Unit) {
        AsyncIsUserAuthenticated(this, callback).execute()
    }

    fun asyncLoadUser(callback: (User?) -> Unit) {
        AsyncLoadUser(this, callback).execute()
    }

    private class AsyncIsUserAuthenticated(private val _loader: Loader, private val callback: (Boolean) -> Unit) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg voids: Void): Boolean {
            return _loader.isUserAuthenticated
        }

        override fun onPostExecute(result: Boolean) {
            callback(result)
        }
    }

    private class AsyncLoadUser internal constructor(
        private val _loader: Loader,
        private val callback: (User?) -> Unit
    ) : AsyncTask<Void, Void, User>() {

        override fun doInBackground(vararg voids: Void): User? {
            return _loader.loadUser()
        }

        override fun onPostExecute(user: User) {
            callback(user)
        }
    }

    private class AsyncLogin(
        private val _username: String,
        private val _password: String,
        private val _loader: Loader,
        private val _callback: (Boolean) -> Unit
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg voids: Void): Boolean {
            return _loader.login(_username, _password)
        }

        override fun onPostExecute(success: Boolean) {
            super.onPostExecute(success)
            _callback(success)
        }
    }
}
