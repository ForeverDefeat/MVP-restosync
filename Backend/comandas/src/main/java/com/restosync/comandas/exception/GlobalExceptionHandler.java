package com.restosync.comandas.exception;

import com.restosync.comandas.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
 
import java.util.LinkedHashMap;
import java.util.Map;
 
/**
 * Manejador global de excepciones.
 * Transforma cada excepción en una respuesta ApiResponse estandarizada.
 *
 * Mapa de excepciones → HTTP status:
 *   ResourceNotFoundException          → 404
 *   BusinessException                  → 409
 *   InvalidStateTransitionException    → 422
 *   UnauthorizedRoleException          → 403
 *   AccessDeniedException              → 403
 *   BadCredentialsException            → 401
 *   DisabledException                  → 401
 *   AuthenticationException            → 401
 *   MethodArgumentNotValidException    → 400
 *   MissingServletRequestParameterException → 400
 *   MethodArgumentTypeMismatchException → 400
 *   Exception (fallback)               → 500
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
 
    // ── 404 Not Found ─────────────────────────────────────────────────────────
 
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage(), 404));
    }
 
    // ── 409 Conflict ──────────────────────────────────────────────────────────
 
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        log.warn("Conflicto de negocio: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("BUSINESS_CONFLICT", ex.getMessage(), 409));
    }
 
    // ── 422 Unprocessable Entity ──────────────────────────────────────────────
 
    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTransition(InvalidStateTransitionException ex) {
        log.warn("Transición de estado inválida: {} → {}", ex.getFrom(), ex.getTo());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error("INVALID_STATE_TRANSITION", ex.getMessage(), 422));
    }
 
    // ── 403 Forbidden ─────────────────────────────────────────────────────────
 
    @ExceptionHandler(UnauthorizedRoleException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedRole(UnauthorizedRoleException ex) {
        log.warn("Rol no autorizado — rol: {}, acción: {}", ex.getRole(), ex.getAction());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("UNAUTHORIZED_ROLE", ex.getMessage(), 403));
    }
 
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado por Spring Security: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("ACCESS_DENIED", "No tienes permiso para realizar esta acción", 403));
    }
 
    // ── 401 Unauthorized ──────────────────────────────────────────────────────
 
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("INVALID_CREDENTIALS", "Email o contraseña incorrectos", 401));
    }
 
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(DisabledException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("ACCOUNT_DISABLED", "La cuenta de usuario está desactivada", 401));
    }
 
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        log.warn("Error de autenticación: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("AUTHENTICATION_ERROR", "Error de autenticación", 401));
    }

    @ExceptionHandler(TooManyLoginAttemptsException.class)
    public ResponseEntity<ApiResponse<Void>> handleTooManyLoginAttempts(TooManyLoginAttemptsException ex) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error("TOO_MANY_LOGIN_ATTEMPTS", ex.getMessage(), 429));
    }

    // ── 400 Bad Request ───────────────────────────────────────────────────────
 
    /**
     * Errores de @Valid: retorna un mapa campo → mensaje de validación.
     * El campo "data" del ApiResponse contiene los errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
 
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field   = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });
 
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .error("VALIDATION_ERROR")
                        .message("Error de validación en los datos enviados")
                        .data(fieldErrors)
                        .status(400)
                        .build());
    }
 
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {
 
        String message = String.format("El parámetro '%s' es obligatorio", ex.getParameterName());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("MISSING_PARAMETER", message, 400));
    }
 
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
 
        String message = String.format(
                "El valor '%s' no es válido para el parámetro '%s'",
                ex.getValue(), ex.getName()
        );
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("TYPE_MISMATCH", message, 400));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            fieldErrors.put(path.substring(path.lastIndexOf('.') + 1), violation.getMessage());
        });

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .error("VALIDATION_ERROR")
                        .message("Error de validacion en los parametros enviados")
                        .data(fieldErrors)
                        .status(400)
                        .build());
    }
 
    // ── 500 Internal Server Error (fallback) ──────────────────────────────────
 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Error inesperado no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "Error interno del servidor", 500));
    }
}
