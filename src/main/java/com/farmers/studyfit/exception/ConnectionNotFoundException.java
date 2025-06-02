package com.farmers.studyfit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConnectionNotFoundException extends RuntimeException{
    public ConnectionNotFoundException(String message) {
        super(message);
    }

}