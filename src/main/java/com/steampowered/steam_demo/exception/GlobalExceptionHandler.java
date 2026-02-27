package com.steampowered.steam_demo.exception;

import com.steampowered.steam_demo.exception.domain.AlreadyOwnedException;
import com.steampowered.steam_demo.exception.domain.ApiDomainException;
import com.steampowered.steam_demo.exception.domain.UsernameAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiDomainException.class)
    public ResponseEntity<ApiError> handleApiDomainException(ApiDomainException ex, HttpServletRequest request) {
        return buildError(ex.getStatus(), ex.getMessage(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatusCode statusCode = ex.getStatusCode();
        String message = ex.getReason() != null ? ex.getReason() : "Request failed";
        return buildError(statusCode, message, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value for parameter: " + ex.getName();
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Malformed request body", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Validation failed");

        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("Validation failed");
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = extractConstraintMessage(ex);
        return buildError(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request);
    }

    private String extractConstraintMessage(DataIntegrityViolationException ex) {
        String details = extractCauseMessage(ex).toLowerCase();
        if (details.contains("uk_users_username") || details.contains("users_username_key")) {
            return new UsernameAlreadyExistsException().getMessage();
        }
        if (details.contains("uk_library_items_user_game")) {
            return new AlreadyOwnedException().getMessage();
        }
        if (details.contains("library_items") && details.contains("user_id") && details.contains("game_id")) {
            return new AlreadyOwnedException().getMessage();
        }
        return "Data integrity violation";
    }

    private String extractCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : "";
    }

    private ResponseEntity<ApiError> buildError(HttpStatusCode statusCode, String message, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String reasonPhrase = status != null ? status.getReasonPhrase() : "Error";

        ApiError apiError = new ApiError(
                Instant.now(),
                statusCode.value(),
                reasonPhrase,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(statusCode).body(apiError);
    }
}
