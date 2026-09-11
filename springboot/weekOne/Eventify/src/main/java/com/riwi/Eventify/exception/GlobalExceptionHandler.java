package com.riwi.Eventify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice //
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEventException.class) // Ejecuta este metodo cuando ocurra una InvalidEventException
    public ResponseEntity<Map<String, String>> handleInvalidEvent(InvalidEventException exception){
        Map<String, String> error = Map.of("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidVenueException.class)
    public ResponseEntity<Map<String, String>> handleInvalidVenue(InvalidVenueException exception){
        Map<String, String> error = Map.of("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
