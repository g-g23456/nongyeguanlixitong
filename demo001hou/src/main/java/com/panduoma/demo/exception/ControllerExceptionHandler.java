package com.panduoma.demo.exception;


import com.panduoma.demo.response.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        return Result.error(e.getMessage());
    }
}
