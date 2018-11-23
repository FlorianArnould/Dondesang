package fr.socket.florian.dondesang.loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import fr.socket.florian.dondesang.R
import fr.socket.florian.dondesang.loader.web.WebConnection
import fr.socket.florian.dondesang.loader.web.WebConnectionException
import fr.socket.florian.dondesang.model.Location
import fr.socket.florian.dondesang.model.Question
import fr.socket.florian.dondesang.model.User
import org.json.JSONException
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException


class Loader {
    private val connection: WebConnection = WebConnection()

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
                connection.setCookie(cookie[0], cookie[1])
                connection.setCookie(ASPNET[0], ASPNET[1])
                connection.setCookie(ADAuthCookie[0], ADAuthCookie[1])
                return true
            }
        } catch (e: IOException) {
            Log.e("login", e.message, e)
        }
        return false
    }

    fun getQuestion(id: String, callback: (Question?) -> Unit) {
        AsyncQuestion(id, this, callback).execute()
    }

    private fun getQuestion(id: String): Question {
        val response = Jsoup.connect("https://dondesang.efs.sante.fr/test-aptitude-au-don")
            .data("question", id)
            .method(Connection.Method.GET)
            .followRedirects(false)
            .execute()
        val questionElement = response.parse().getElementsByClass("Question")[0]
        val errorElement = questionElement.child(3)
        return Question(
            questionElement.child(1).text(),
            errorElement.child(1).text(),
            questionElement.getElementsByTag("img")[0].attr("src"),
            questionElement.getElementsByAttributeValue("name", "next_question_id")[0].attr("value")
        )
    }

    fun downloadImage(relativeUrl: String, callback: (Bitmap?) -> Unit) {
        DownloadImageTask("https://dondesang.efs.sante.fr$relativeUrl", callback).execute()
    }

    fun getLocations(callback: (List<Location>?) -> Unit) {
        AsyncLocations(callback).execute()
    }

    fun loadUser(): User? {
        try {
            val document = connection.get("https://donneurs.efs.sante.fr/Profil#/home").document
            val tokenElement = document.getElementById("profilUserId")
            val userIdElement = document.getElementById("profilUserCode")
            if (tokenElement == null || userIdElement == null) {
                return null
            }
            val userId = userIdElement.attr("value")
            val token = tokenElement.attr("value")
            val params = "{userId: \"$userId\", token: \"$token\"}"
            val json = connection.post(
                "https://donneurs.efs.sante.fr/api/services/webdonneur/donneurService/GetDonneurDetails",
                params
            ).json
            return User.parse(json)
        } catch (e: WebConnectionException) {
            Log.e("loadUser", "Cannot request the user details : " + e.message, e)
        }
        return null
    }

    fun asyncLoadUser(callback: (User?) -> Unit) {
        AsyncLoadUser(this, callback).execute()
    }

    private class AsyncLoadUser internal constructor(
        private val _loader: Loader,
        private val callback: (User?) -> Unit
    ) : AsyncTask<Void, Void, User?>() {

        override fun doInBackground(vararg voids: Void): User? {
            return _loader.loadUser()
        }

        override fun onPostExecute(user: User?) {
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

    private class AsyncQuestion(
        private val id: String,
        private val loader: Loader,
        private val callback: (Question?) -> Unit
    ) : AsyncTask<Void, Void, Question?>() {
        override fun doInBackground(vararg p0: Void?): Question? {
            return loader.getQuestion(id)
        }

        override fun onPostExecute(result: Question?) {
            super.onPostExecute(result)
            callback(result)
        }
    }

    private class DownloadImageTask(val url: String, val callback: (Bitmap?) -> Unit) :
        AsyncTask<Void, Void, Bitmap?>() {
        override fun doInBackground(vararg p0: Void): Bitmap? {
            var mIcon11: Bitmap? = null
            try {
                Log.d("DownloadImage", url)
                val input = java.net.URL(url).openStream()
                mIcon11 = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.e("DownloadImage", e.message, e)
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            callback(result)
            Log.d("DownloadImage", "callback called " + (result == null))
        }
    }

    private class AsyncLocations(val callback: (List<Location>?) -> Unit) : AsyncTask<Void, Void, List<Location>?>() {
        override fun doInBackground(vararg p0: Void?): List<Location>? {
            val doc = Jsoup.connect("https://dondesang.efs.sante.fr/trouver-une-collecte")
                .method(Connection.Method.GET)
                .timeout(0)
                .execute()
                .parse()
            val children = doc.getElementsByClass("collectes-list")[0].children()
            return List(children.size) { parseLocation(children[it]) }
        }

        private fun parseLocation(element: Element): Location {
            val latLong = element.getElementsByClass("mail")[0].child(0).attr("href").split("?")[1].split("&")
            return Location(
                name = element.getElementsByClass("title")[0].text(),
                isFixed = element.getElementsByClass("picto")[0].attr("src").contains("fixe"),
                address = element.getElementsByClass("address")[0].text(),
                phone = try {
                    element.getElementsByAttributeValue("itemprop", "telephone")[0].child(0).text()
                } catch (e: java.lang.Exception) {
                    null
                },
                canBlood = element.getElementsByClass("Sang").size == 1,
                canPlasma = element.getElementsByClass("Plasma").size == 1,
                canPlatelet = element.getElementsByClass("Plaquettes").size == 1,
                info = element.getElementsByClass("infos-text")[0].child(0).html().replace("<br>", "").trim().replace("&amp;", "&"),
                lat = latLong[0].split("=")[1].toFloat(),
                long = latLong[1].split("=")[1].toFloat()
            )
        }

        override fun onPostExecute(result: List<Location>?) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}
