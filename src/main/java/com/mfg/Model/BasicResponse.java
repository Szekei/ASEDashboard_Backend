package com.mfg.Model;

/**
 * Created by I309908 on 4/18/2017.
 */
public class BasicResponse {
    private String status;
    private String message;

    public BasicResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
