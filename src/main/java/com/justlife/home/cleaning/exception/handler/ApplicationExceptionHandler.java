package com.justlife.home.cleaning.exception.handler;

import com.justlife.home.cleaning.dto.ErrorResponseDTO;
import com.justlife.home.cleaning.dto.FieldValidationErrorResponseDTO;
import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.exception.NoAvailableCleanersException;
import com.justlife.home.cleaning.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    /**
     * Resource Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Business rule violations (cleaner not available etc.)
     */
    @ExceptionHandler(NoAvailableCleanersException.class)
    public ResponseEntity<?> handleBusiness(NoAvailableCleanersException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Booking validation errors (duration, cleaner count etc.)
     */
    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<?> handleBookingValidation(BookingValidationException ex) {

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * DTO field validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleFieldValidation(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        FieldValidationErrorResponseDTO response = FieldValidationErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(ApplicationErrorCode.VALIDATION_FAILED.getCode())
                .message("Validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Fallback error handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternal(Exception ex) {
        log.error("Unexpected error:", ex);

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(ApplicationErrorCode.INTERNAL_ERROR.getCode())
                .message("Something went wrong. Please try again later.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
