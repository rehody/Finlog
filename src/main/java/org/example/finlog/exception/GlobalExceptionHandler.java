package org.example.finlog.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.finlog.DTO.ErrorResponse;
import org.example.finlog.enums.ErrorCode;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnExpectedError(
            Exception ex,
            WebRequest request
    ) {
        log.error("Unexpected error", ex);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                "Internal server error",
                request.getDescription(false)

        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request
    ) {
        log.debug("Type mismatch for parameter '{}': {}", ex.getName(), ex.getValue());

        String message;
        if (ex.getRequiredType() == LocalDate.class) {
            message = "Invalid value or date format for the parameter " +
                    ex.getName() + ". Expected format: yyyy-MM-dd";
        } else if (Objects.requireNonNull(ex.getRequiredType()).isEnum()) {
            String allowedValues = Arrays.toString(ex.getRequiredType().getEnumConstants());
            message = "Invalid value for the parameter " +
                    ex.getName() + ". Allowed values: " + allowedValues;
        } else {
            message = ex.getMessage();
        }

        ErrorResponse response = new ErrorResponse(
                ErrorCode.BAD_REQUEST,
                message,
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", msg);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR,
                msg,
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            WebRequest request
    ) {
        log.debug("Not found: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ErrorCode.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExist(
            UserAlreadyExistsException ex,
            WebRequest request
    ) {
        log.warn("Registration attempt failed: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ErrorCode.CONFLICT,
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request
    ) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ErrorCode.FORBIDDEN,
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(
            OptimisticLockingFailureException ex,
            WebRequest request
    ) {
        log.warn("Concurrency conflict detected: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ErrorCode.CONFLICT,
                ex.getMessage(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
