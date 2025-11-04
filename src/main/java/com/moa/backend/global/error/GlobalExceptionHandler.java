package com.moa.backend.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = (ex.getMessage() == null || ex.getMessage().isBlank())
            ? errorCode.getDefaultMessage()
            : ex.getMessage();

        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse(errorCode.getDefaultMessage());

        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message
            ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage()
            ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage()
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception ex) {
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getDefaultMessage()
            ));
    }
}

