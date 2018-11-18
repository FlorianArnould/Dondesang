package fr.socket.florian.dondesang.loader.web;

abstract class WebResult {
    private final long _date;

    WebResult(long date) {
        _date = date;
    }

    public long getDate() {
        return _date;
    }
}
