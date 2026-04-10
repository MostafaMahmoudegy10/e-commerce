package org.stylehub.backend.e_commerce.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleIllegalArgument(Exception ex) {
        return ResponseEntity.badRequest().body(
                Map.of(
                        "message", ex.getMessage(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "timestamp", System.currentTimeMillis()
                )
        );
    }
}
