package com.farmers.studyfit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String code;

    public static ErrorResponse of(HttpStatus status, String message, String code) {
        return new ErrorResponse(status.value(), message, code);
    }
}

