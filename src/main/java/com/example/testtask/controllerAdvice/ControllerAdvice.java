package com.example.testtask.controllerAdvice;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                ex.getFieldError().getDefaultMessage() // "Сумма должна быть положительной"
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleInputException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
