package fr.socket.florian.dondesang.loader.web;

import android.support.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebConnection {
    private final Map<String, String> _cookies;

    public WebConnection() {
        _cookies = new HashMap<>();
    }

    private static void setHeaders(@NonNull HttpsURLConnection conn) {
        conn.setRequestProperty("Host", "donneurs.efs.sante.fr");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        conn.setRequestProperty("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3");
        conn.setRequestProperty("Origin", "https://donneurs.efs.sante.fr");
        conn.setRequestProperty("Referer", "https://donneurs.efs.sante.fr/Profil");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
    }

    private void setCookies(@NonNull HttpsURLConnection conn) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : _cookies.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("; ");
        }
        conn.setRequestProperty("Cookie", builder.toString());
        Log.d("cookies", builder.toString());
    }

    public void setCookie(String name, String value) {
        _cookies.put(name, value);
    }

    private void interpretSetCookie(@NonNull HttpsURLConnection conn) {
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                Log.d("cookie", cookie);
                String[] baseCookie = cookie.split(";")[0].split("=");
                if (baseCookie.length > 1) {
                    _cookies.put(baseCookie[0], baseCookie[1]);
                }
            }
        }
    }

    public WebGetResult get(String url) throws WebConnectionException {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            setHeaders(conn);
            setCookies(conn);
            conn.connect();
            Log.d("GET", String.valueOf(conn.getResponseCode()));
            interpretSetCookie(conn);
            StringBuilder result = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } finally {
                conn.disconnect();
            }
            return new WebGetResult(Jsoup.parse(result.toString()), conn.getDate());
        } catch (IOException e) {
            throw new WebConnectionException("An error occurred when loading " + url + " : " + e.getMessage(), e);
        }
    }

    public WebPostResult post(String url, String parameters) throws WebConnectionException {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            setHeaders(conn);
            setCookies(conn);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            try (OutputStream out = conn.getOutputStream()) {
                out.write(parameters.getBytes());
                out.flush();
            }
            conn.connect();
            Log.d("POST", String.valueOf(conn.getResponseCode()));
            interpretSetCookie(conn);
            StringBuilder result = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } finally {
                conn.disconnect();
            }
            return new WebPostResult(new JSONObject(result.toString()), conn.getDate());
        } catch (IOException e) {
            throw new WebConnectionException("An error occurred when requesting " + url + " with " + parameters + " : " + e.getMessage(), e);
        } catch (JSONException e) {
            throw new WebConnectionException("Cannot parse the response when requesting " + url + " with " + parameters + " : " + e.getMessage(), e);
        }
    }
}
