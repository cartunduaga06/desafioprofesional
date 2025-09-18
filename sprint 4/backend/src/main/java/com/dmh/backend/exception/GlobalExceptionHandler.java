package com.dmh.backend.exception;

import com.dmh.backend.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;

/**
 * Global exception handler to translate exceptions into meaningful HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GenericResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GenericResponse> handleUserNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse(ex.getMessage()));
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<GenericResponse> handleNoSuchElement(java.util.NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse(ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<GenericResponse> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new GenericResponse(ex.getMostSpecificCause().getMessage()));
    }

    /**
     * Maneja excepciones de acceso denegado devolviendo un código 403 Forbidden.
     * Esto ocurre cuando el usuario no tiene permisos suficientes para acceder a un recurso.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Access denied"));
    }

    /**
     * Maneja excepciones de fondos insuficientes devolviendo un código 410 (Gone).
     * Según los requisitos de la historia de usuario, este código indica que la operación no puede completarse porque
     * los recursos necesarios (fondos) ya no están disponibles.
     */
    @ExceptionHandler(com.dmh.backend.exception.InsufficientFundsException.class)
    public ResponseEntity<GenericResponse> handleInsufficientFunds(com.dmh.backend.exception.InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(new GenericResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericResponse("Invalid email or password"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleGeneralException(Exception ex) {
        // Log exception here in a real application
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse("An unexpected error occurred"));
    }
}
