package com.inimai.devjourney.exception;

import java.util.HashMap;
import java.util.Map;
//spring calls this class when it sees an exception, it will look for a method with @ExceptionHandler annotation that matches the exception type and call that method
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        //binding result is a list of errors, we iterate through them and put them in the map
        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return errors;
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public Map<String,String> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String,String> error = new HashMap<>();

        error.put("message", ex.getMessage());

        return error;
    }
}