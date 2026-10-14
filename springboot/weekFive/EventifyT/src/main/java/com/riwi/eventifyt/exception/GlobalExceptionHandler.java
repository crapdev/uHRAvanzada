package com.riwi.eventifyt.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Atrapa las excepciones de los controladores REST (JSON) y las convierte en una respuesta
 * con el formato estandar RFC 7807 "Problem Details". Ejemplo de respuesta:
 * {
 *   "type": "https://eventify.com/errors/not-found",
 *   "title": "Recurso no encontrado",
 *   "status": 404,
 *   "detail": "El evento con ID 9999 no existe",
 *   "instance": "/api/events/9999"
 * }
 * Spring ya trae la clase ProblemDetail; solo hay que llenarla y devolverla.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.riwi.eventifyt.controller.api")
public class GlobalExceptionHandler {

    private static final String ERRORS_URL = "https://eventify.com/errors/";

    // 400: fallo alguna validacion de @Valid / @Validated en un DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        // Mapa campo → mensaje, ej: {"name": "El nombre del evento es obligatorio"}
        // Si un campo rompe varias reglas (ej. vacio → @NotBlank y @Size), se juntan sus mensajes
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            String message = error.getDefaultMessage();
            if (errors.containsKey(field)) {
                errors.put(field, errors.get(field) + ". " + message);
            } else {
                errors.put(field, message);
            }
        }

        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "validation-error", "Datos inválidos",
                "La solicitud tiene campos inválidos", request);
        problem.setProperty("errors", errors);   // campo extra dentro del ProblemDetail
        return problem;
    }

    // 400: el JSON esta mal escrito o un valor tiene formato incorrecto (ej. "date": "hola")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "malformed-request", "Formato inválido",
                "El cuerpo de la petición no es un JSON válido o algún campo tiene un formato incorrecto", request);
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "not-found", "Recurso no encontrado", exception.getMessage(), request);
    }

    // 409
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException exception, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "duplicate-resource", "Recurso duplicado", exception.getMessage(), request);
    }

    // 400
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleViolationException exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "business-rule", "Regla de negocio violada", exception.getMessage(), request);
    }

    // 500: red de seguridad para cualquier error que no esperabamos
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), exception);  // el detalle real va al log...
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Error interno",
                "Ocurrió un error inesperado. Intenta de nuevo más tarde", request); // ...y al cliente, un mensaje generico
    }

    /* -- Privates -- */

    // Arma el ProblemDetail con sus 5 campos estandar
    private ProblemDetail build(HttpStatus status, String type, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail); // status + detail
        problem.setType(URI.create(ERRORS_URL + type));                          // type
        problem.setTitle(title);                                                  // title
        problem.setInstance(URI.create(request.getRequestURI()));                 // instance: la URL que fallo
        return problem;
    }
}
