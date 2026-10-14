package com.riwi.eventifyt.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

// La logica de @NoPastEvents: recibe la fecha y responde true (valida) o false (invalida)
public class NoPastEventsValidator implements ConstraintValidator<NoPastEvents, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        // Si la fecha viene vacia no es asunto nuestro: de eso se encarga @NotNull
        if (date == null) {
            return true;
        }
        // Hoy SI se permite; ayer o antes, NO
        return !date.isBefore(LocalDate.now());
    }
}
