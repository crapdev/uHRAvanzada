package com.riwi.eventifyt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice //
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidException.class) // Ejecuta este metodo cuando ocurra una InvalidEventException
    public ResponseEntity<Map<String, String>> handleInvalidEvent(InvalidException exception){
        Map<String, String> error = Map.of("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Se ejecuta cuando ocurra un ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException exception){
        Map<String, String> error = Map.of("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
