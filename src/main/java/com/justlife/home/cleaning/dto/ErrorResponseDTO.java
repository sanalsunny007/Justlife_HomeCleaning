package com.justlife.home.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Standard error response structure")
public class ErrorResponseDTO {

    @Schema(example = "1002", description = "Numeric error code")
    private int errorCode;

    @Schema(example = "Duration must be exactly 2 or 4 hours", description = "Error message")
    private String message;

}
