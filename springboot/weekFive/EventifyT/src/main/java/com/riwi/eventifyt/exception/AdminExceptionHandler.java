package com.riwi.eventifyt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// Only handles exceptions thrown by admin panel controllers (HTML views)
@ControllerAdvice(basePackages = "com.riwi.eventifyt.controller.admin")
public class AdminExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "admin/error/not-found";
    }
}
