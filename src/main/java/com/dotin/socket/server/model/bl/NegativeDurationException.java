package com.dotin.socket.server.model.bl;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class NegativeDurationException extends Exception {
    public NegativeDurationException(String message) {
        super(message);
    }
    public NegativeDurationException() {
    }
}
