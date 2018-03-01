package com.mfg.Exception;

/**
 * Created by I309908 on 5/18/2017.
 */
public class RequestErrorException extends Exception {
    public RequestErrorException() {
    }

    public RequestErrorException(String message) {
        super(message);
    }

    public RequestErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestErrorException(Throwable cause) {
        super(cause);
    }
}
