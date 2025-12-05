package com.justlife.home.cleaning.controller;

import com.justlife.home.cleaning.dto.CleanerAvailabilityDTO;
import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.dto.ErrorResponseDTO;
import com.justlife.home.cleaning.service.AvailabilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(
        name = "Availability API",
        description = "Provides cleaner availability for full days or specific time slots"
)
@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    // -------------------------------------------------------------------------
    // Availability for a whole date
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Get availability for a specific date",
            description = """
                    Returns all cleaners with their free time windows for the given date. 
                    Excludes Fridays and respects working hours (08:00–22:00) and break rules.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of cleaners and their free time windows",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = CleanerAvailabilityDTO.class)))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid date or validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @GetMapping
    public List<CleanerAvailabilityDTO> getAvailabilityForDate(
            @Parameter(
                    description = "Date to check (YYYY-MM-DD)",
                    example = "2025-12-05",
                    required = true
            )
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return availabilityService.getAvailabilityForDate(date);
    }

    // -------------------------------------------------------------------------
    // Availability for a time slot
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Get available cleaners for a specific timeslot",
            description = """
                    Returns only cleaners available for the exact appointment slot.
                    Duration must be 2 or 4 hours.
                    Cleaners must not have overlapping bookings and must respect break rules.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cleaners available for the given timeslot",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = CleanerDTO.class)))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request (duration, working hours, non-working day)",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @GetMapping("/slot")
    public List<CleanerDTO> getAvailableCleaners(
            @Parameter(
                    description = "Appointment date",
                    example = "2025-12-04",
                    required = true
            )
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @Parameter(
                    description = "Start time (HH:mm)",
                    example = "10:00",
                    required = true
            )
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime start,

            @Parameter(
                    description = "Duration in hours (allowed values: 2 or 4)",
                    example = "2",
                    required = true
            )
            @RequestParam("durationHours")
            int durationHours
    ) {
        return availabilityService.getAvailableCleaners(date, start, durationHours);
    }
}
