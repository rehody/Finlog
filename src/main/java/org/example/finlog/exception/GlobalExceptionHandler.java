package org.example.finlog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == LocalDate.class) {
            String msg = "Invalid value or date format for the parameter " + ex.getName() + ". Expected format: yyyy-MM-dd";
            return ResponseEntity.badRequest().body(msg);
        }

        if (Objects.requireNonNull(ex.getRequiredType()).isEnum()) {
            String allowedValues = Arrays.toString(ex.getRequiredType().getEnumConstants());
            String msg = "Invalid value for the parameter " + ex.getName() + ". Allowed values: " + allowedValues;
            return ResponseEntity.badRequest().body(msg);
        }

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(msg);
    }
}
