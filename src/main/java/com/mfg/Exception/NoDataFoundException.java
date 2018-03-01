package com.mfg.Exception;

/**
 * Created by I309908 on 4/25/2017.
 */
public class NoDataFoundException extends Exception {

    public NoDataFoundException(){}

    public NoDataFoundException(String message){
        super(message);
    }

    public NoDataFoundException(Throwable cause){
        super(cause);
    }

    public NoDataFoundException (String message, Throwable cause) {
        super (message, cause);
    }
}
