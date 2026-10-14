package com.riwi.eventifyt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Anotacion PROPIA de validacion: se usa igual que @NotBlank o @Size.
// @Constraint dice que clase hace el trabajo de verdad: NoPastEventsValidator
@Constraint(validatedBy = NoPastEventsValidator.class)
@Target(ElementType.FIELD)              // se pone sobre atributos
@Retention(RetentionPolicy.RUNTIME)     // debe existir mientras corre la app
public @interface NoPastEvents {

    // Estos 3 atributos son obligatorios en toda anotacion de validacion
    String message() default "No se pueden registrar eventos con fecha anterior a hoy";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
