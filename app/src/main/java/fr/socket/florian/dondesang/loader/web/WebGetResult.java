package fr.socket.florian.dondesang.loader.web;

import org.jsoup.nodes.Document;

public class WebGetResult extends WebResult {
    private final Document _document;

    WebGetResult(Document document, long date) {
        super(date);
        _document = document;
    }

    public Document getDocument() {
        return _document;
    }
}
