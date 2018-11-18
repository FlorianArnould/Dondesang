package fr.socket.florian.dondesang.loader.web;

import org.json.JSONObject;

public class WebPostResult extends WebResult {
    private final JSONObject _json;

    WebPostResult(JSONObject json, long date) {
        super(date);
        _json = json;
    }

    public JSONObject getJSON() {
        return _json;
    }
}
