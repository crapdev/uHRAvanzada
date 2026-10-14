package com.riwi.eventifyt.exception;

// Se lanza cuando se intenta crear algo que ya existe (ej. un lugar con el mismo nombre) → 409
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
