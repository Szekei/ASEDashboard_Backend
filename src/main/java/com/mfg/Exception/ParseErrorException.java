package com.mfg.Exception;

/**
 * Created by I309908 on 5/19/2017.
 */
public class ParseErrorException extends Exception{
    public ParseErrorException() {
    }

    public ParseErrorException(String message) {
        super(message);
    }

    public ParseErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseErrorException(Throwable cause) {
        super(cause);
    }
}
