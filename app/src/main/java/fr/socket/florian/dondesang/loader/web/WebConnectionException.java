package fr.socket.florian.dondesang.loader.web;

public class WebConnectionException extends Exception {
    WebConnectionException(String message, Exception e) {
        super(message, e);
    }
}
