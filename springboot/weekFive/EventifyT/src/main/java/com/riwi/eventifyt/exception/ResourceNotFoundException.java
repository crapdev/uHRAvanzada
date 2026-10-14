package com.riwi.eventifyt.exception;

// Se lanza cuando se busca algo que no existe (evento, lugar, categoria) → 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
