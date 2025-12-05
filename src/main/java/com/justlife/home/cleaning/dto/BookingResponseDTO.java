package com.justlife.home.cleaning.dto;

import com.justlife.home.cleaning.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponseDTO {
    @Schema(description = "Booking ID", example = "1")
    private Long id;

    @Schema(description = "Start timestamp of the booking", example = "2025-12-04T10:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "End timestamp of the booking", example = "2025-12-04T12:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "Duration in hours", example = "2")
    private Integer durationHours;

    @Schema(description = "Number of cleaners requested", example = "2")
    private Integer requiredCleanerCount;

    @Schema(description = "Customer name", example = "John Doe")
    private String customerName;

    @Schema(description = "Booking status", example = "CONFIRMED")
    private BookingStatus status;

    @Schema(description = "List of assigned cleaner IDs", example = "[3, 5]")
    private List<Long> cleanerIds;

    @Schema(description = "Vehicle ID assigned to this booking", example = "1")
    private Long vehicleId;
}
