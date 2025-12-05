package com.justlife.home.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@Schema(description = "Validation error details for DTO field-level errors")
public class FieldValidationErrorResponseDTO {

    @Schema(example = "40001", description = "Validation error code")
    private int errorCode;

    @Schema(example = "Validation failed")
    private String message;

    @Schema(description = "Field-specific validation errors")
    private Map<String, String> fieldErrors;

    private LocalDateTime timestamp;
}
