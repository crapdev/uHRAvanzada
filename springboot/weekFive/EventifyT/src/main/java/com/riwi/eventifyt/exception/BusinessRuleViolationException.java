package com.riwi.eventifyt.exception;

// Se lanza cuando se rompe una regla del negocio
// (ej. eliminar un lugar que tiene eventos, o un id en el body distinto al de la URL) → 400
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
