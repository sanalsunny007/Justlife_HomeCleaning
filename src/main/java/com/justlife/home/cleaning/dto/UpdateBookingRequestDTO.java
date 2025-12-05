package com.justlife.home.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequestDTO {

    @Schema(description = "New start timestamp for the booking",
            example = "2025-12-04T14:00:00")
    @NotNull
    private LocalDateTime newStartDateTime;
}
