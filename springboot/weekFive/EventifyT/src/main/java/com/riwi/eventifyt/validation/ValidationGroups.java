package com.riwi.eventifyt.validation;

import jakarta.validation.groups.Default;

// Grupos de validacion: permiten aplicar reglas distintas segun la operacion.
// Ej: el id NO se envia al crear, pero SI es obligatorio al actualizar.
//
// Extienden de Default para que, al validar un grupo, TAMBIEN se revisen las reglas
// "normales" (las que no tienen groups = ...), como @NotBlank o @Size.
public interface ValidationGroups {

    interface OnCreate extends Default {}

    interface OnUpdate extends Default {}
}
