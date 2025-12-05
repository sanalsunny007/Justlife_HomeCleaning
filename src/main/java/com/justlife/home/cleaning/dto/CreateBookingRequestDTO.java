package com.justlife.home.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequestDTO {

    @Schema(description = "Start timestamp of the appointment",
            example = "2025-12-04T10:00:00")
    @NotNull
    private LocalDateTime startDateTime;

    @Schema(description = "Duration of booking in hours (allowed: 2 or 4)", example = "2")
    @Min(2)
    @Max(4)
    private Integer durationHours;

    @Schema(description = "Number of cleaners required (1 to 3)", example = "3")
    @Min(1)
    @Max(3)
    private Integer cleanerCount;

    @Schema(description = "Customer name for the appointment", example = "Sanal Sunny")
    private String customerName;
}
