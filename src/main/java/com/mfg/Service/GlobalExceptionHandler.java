package com.mfg.Service;

/**
 * Created by I309908 on 7/17/2017.
 */

import com.mfg.Model.BasicResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Global exception handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Exception handler of all exceptions thrown from controller
     * @param e Exception
     * @return BasicResponse
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BasicResponse exceptionHandler(Exception e){
        BasicResponse response = new BasicResponse();
        response.setStatus("Fail");
        response.setMessage(e.getMessage());
        return response;
    }
}
