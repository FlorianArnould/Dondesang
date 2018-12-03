package fr.socket.florian.dondesang.loader.web

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class WebConnection {
    private val cookies: MutableMap<String, String>

    init {
        cookies = HashMap()
    }

    private fun setHeaders(conn: HttpsURLConnection) {
        conn.setRequestProperty("Host", "donneurs.efs.sante.fr")
        conn.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21"
        )
        conn.setRequestProperty("Accept", "application/json, text/plain, */*")
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
        conn.setRequestProperty("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
        conn.setRequestProperty("Origin", "https://donneurs.efs.sante.fr")
        conn.setRequestProperty("Referer", "https://donneurs.efs.sante.fr/Profil")
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
    }

    private fun setCookies(conn: HttpsURLConnection) {
        val builder = StringBuilder()
        for ((key, value) in cookies) {
            builder.append(key)
            builder.append("=")
            builder.append(value)
            builder.append("; ")
        }
        conn.setRequestProperty("Cookie", builder.toString())
    }

    fun setCookie(name: String, value: String) {
        cookies[name] = value
    }

    private fun interpretSetCookie(conn: HttpsURLConnection) {
        val cookies = conn.headerFields["Set-Cookie"]
        if (cookies != null) {
            for (cookie in cookies) {
                val baseCookie =
                    cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split("=".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                if (baseCookie.size > 1) {
                    this.cookies[baseCookie[0]] = baseCookie[1]
                }
            }
        }
    }

    @Throws(WebConnectionException::class)
    operator fun get(url: String): WebGetResult {
        var conn: HttpsURLConnection? = null
        try {
            conn = URL(url).openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            setHeaders(conn)
            setCookies(conn)
            conn.doInput = true
            conn.doOutput = false
            Log.i("GET", conn.responseCode.toString() + " : " + url)
            interpretSetCookie(conn)
            val result = StringBuilder()
            BufferedReader(InputStreamReader(conn.inputStream)).forEachLine { line -> result.append(line) }
            return WebGetResult(Jsoup.parse(result.toString()), conn.date)
        } catch (e: IOException) {
            throw WebConnectionException("An error occurred when loading " + url + " : " + e.message, e)
        } finally {
            conn?.errorStream?.close()
            conn?.disconnect()
        }

    }

    @Throws(WebConnectionException::class)
    fun post(url: String, parameters: String): WebPostResult {
        var conn: HttpsURLConnection? = null
        try {
            conn = URL(url).openConnection() as HttpsURLConnection
            setHeaders(conn)
            setCookies(conn)
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true
            conn.outputStream.use { out ->
                out.write(parameters.toByteArray())
                out.flush()
            }
            conn.connect()
            Log.i("POST", conn.responseCode.toString() + " : " + url)
            interpretSetCookie(conn)
            val result = StringBuilder()
            BufferedReader(InputStreamReader(conn.inputStream)).forEachLine { line -> result.append(line) }
            return WebPostResult(JSONObject(result.toString()), conn.date)
        } catch (e: IOException) {
            throw WebConnectionException(
                "An error occurred when requesting " + url + " with " + parameters + " : " + e.message,
                e
            )
        } catch (e: JSONException) {
            throw WebConnectionException(
                "Cannot parse the response when requesting " + url + " with " + parameters + " : " + e.message,
                e
            )
        } finally {
            conn?.errorStream?.close()
            conn?.disconnect()
        }

    }
}
